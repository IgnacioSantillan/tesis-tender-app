import { Injectable } from '@nestjs/common';
import { WeatherSnapshotResponseDto } from './dto/weather-snapshot-response.dto';
import { WeatherCondition } from './dto/weather-types';
import { ResolvedWeatherLocation } from './weather-location-registry';
import { WeatherProvider } from './weather-provider';
import { getWeatherProviderConfig } from './weather-provider.config';

interface MetNoCompactResponse {
  properties?: {
    timeseries?: MetNoTimeSeries[];
  };
}

interface MetNoTimeSeries {
  time?: string;
  data?: {
    instant?: {
      details?: {
        air_temperature?: number;
        relative_humidity?: number;
        wind_speed?: number;
        cloud_area_fraction?: number;
      };
    };
    next_1_hours?: MetNoForecastPeriod;
    next_6_hours?: MetNoForecastPeriod;
  };
}

interface MetNoForecastPeriod {
  summary?: {
    symbol_code?: string;
  };
  details?: {
    precipitation_amount?: number;
  };
}

@Injectable()
export class MetNoWeatherProvider implements WeatherProvider {
  private readonly baseUrl = getWeatherProviderConfig().metNoBaseUrl;
  private readonly userAgent = getWeatherProviderConfig().metNoUserAgent;
  private readonly fetcher: typeof fetch = fetch;

  async getCurrentWeather(location: ResolvedWeatherLocation): Promise<WeatherSnapshotResponseDto> {
    const forecast = await this.getForecast(location);
    const current = forecast[0];

    if (!current) {
      throw new Error('MET Norway locationforecast response is missing forecast data');
    }

    return current;
  }

  async getForecast(location: ResolvedWeatherLocation): Promise<WeatherSnapshotResponseDto[]> {
    const forecast = await this.fetchForecast(location.latitude, location.longitude);
    const timeseries = forecast.properties?.timeseries;

    if (!timeseries?.length) {
      throw new Error('MET Norway locationforecast response is missing timeseries data');
    }

    return timeseries.slice(0, 6).map((entry) => mapTimeSeriesToSnapshot(entry, location));
  }

  private async fetchForecast(latitude: number, longitude: number): Promise<MetNoCompactResponse> {
    const params = new URLSearchParams({
      lat: latitude.toFixed(4),
      lon: longitude.toFixed(4),
    });

    const response = await this.fetcher(`${this.baseUrl}/compact?${params.toString()}`, {
      headers: {
        'User-Agent': this.userAgent,
      },
    });

    if (!response.ok) {
      throw new Error(`MET Norway request failed with status ${response.status}`);
    }

    return (await response.json()) as MetNoCompactResponse;
  }
}

function mapTimeSeriesToSnapshot(
  entry: MetNoTimeSeries,
  location: ResolvedWeatherLocation,
): WeatherSnapshotResponseDto {
  const capturedAt = new Date().toISOString();
  const forecastFor = toIsoTimestamp(entry.time);
  const instant = entry.data?.instant?.details;
  const period = entry.data?.next_1_hours ?? entry.data?.next_6_hours;
  const precipitationMillimeters = normalizePrecipitation(period?.details?.precipitation_amount);

  return {
    location,
    source: 'MET_NO',
    capturedAt,
    forecastFor,
    condition: mapSymbolCode(period?.summary?.symbol_code),
    temperatureCelsius: instant?.air_temperature ?? 0,
    humidityPercent: Math.round(instant?.relative_humidity ?? 0),
    windSpeedKph: metersPerSecondToKph(instant?.wind_speed),
    rainProbabilityPercent: precipitationToRisk(precipitationMillimeters),
    precipitationMillimeters,
    cloudCoverPercent: Math.round(instant?.cloud_area_fraction ?? 0),
    forecastLeadMinutes: calculateForecastLeadMinutes(capturedAt, forecastFor),
    isStale: false,
  };
}

function toIsoTimestamp(value: string | undefined): string {
  if (!value) {
    return new Date().toISOString();
  }

  const parsed = new Date(value);
  return Number.isNaN(parsed.getTime()) ? new Date().toISOString() : parsed.toISOString();
}

function metersPerSecondToKph(value: number | undefined): number {
  if (value === undefined || !Number.isFinite(value)) {
    return 0;
  }

  return Number((value * 3.6).toFixed(1));
}

function normalizePrecipitation(value: number | undefined): number {
  if (value === undefined || !Number.isFinite(value)) {
    return 0;
  }

  return Math.max(Number(value.toFixed(1)), 0);
}

function precipitationToRisk(value: number): number {
  if (value <= 0) {
    return 0;
  }

  return Math.min(100, Math.round(value * 50));
}

function calculateForecastLeadMinutes(capturedAt: string, forecastFor: string): number {
  const captured = new Date(capturedAt).getTime();
  const forecast = new Date(forecastFor).getTime();

  if (Number.isNaN(captured) || Number.isNaN(forecast)) {
    return 0;
  }

  return Math.max(Math.round((forecast - captured) / 60_000), 0);
}

function mapSymbolCode(symbolCode: string | undefined): WeatherCondition {
  const normalized = symbolCode?.toLowerCase() ?? '';

  if (normalized.includes('thunder')) {
    return 'STORM';
  }

  if (normalized.includes('rain') || normalized.includes('sleet') || normalized.includes('snow')) {
    return 'RAIN';
  }

  if (normalized.includes('cloudy')) {
    return normalized.includes('partly') ? 'PARTLY_CLOUDY' : 'CLOUDY';
  }

  if (normalized.includes('fair')) {
    return 'PARTLY_CLOUDY';
  }

  if (normalized.includes('clearsky')) {
    return 'CLEAR';
  }

  if (normalized.includes('fog')) {
    return 'CLOUDY';
  }

  return 'UNKNOWN';
}
