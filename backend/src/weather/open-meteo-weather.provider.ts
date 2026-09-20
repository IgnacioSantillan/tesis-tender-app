import { Injectable } from '@nestjs/common';
import { WeatherSnapshotResponseDto } from './dto/weather-snapshot-response.dto';
import { WeatherCondition } from './dto/weather-types';
import { ResolvedWeatherLocation } from './weather-location-registry';
import { WeatherProvider } from './weather-provider';
import { getWeatherProviderConfig } from './weather-provider.config';

interface OpenMeteoForecastResponse {
  current?: {
    time?: string;
    temperature_2m?: number;
    relative_humidity_2m?: number;
    precipitation?: number;
    cloud_cover?: number;
    wind_speed_10m?: number;
    weather_code?: number;
  };
  hourly?: {
    time?: string[];
    precipitation?: number[];
    precipitation_probability?: number[];
    temperature_2m?: number[];
    relative_humidity_2m?: number[];
    wind_speed_10m?: number[];
    cloud_cover?: number[];
    weather_code?: number[];
  };
}

@Injectable()
export class OpenMeteoWeatherProvider implements WeatherProvider {
  private readonly baseUrl = getWeatherProviderConfig().openMeteoBaseUrl;
  private readonly fetcher: typeof fetch = fetch;

  async getCurrentWeather(location: ResolvedWeatherLocation): Promise<WeatherSnapshotResponseDto> {
    const forecast = await this.fetchForecast(location.latitude, location.longitude, 1);
    const current = forecast.current;

    if (!current) {
      throw new Error('Open-Meteo current weather response is missing current data');
    }

    const capturedAt = new Date().toISOString();
    const forecastFor = toIsoTimestamp(current.time);

    return {
      location,
      source: 'OPEN_METEO',
      capturedAt,
      forecastFor,
      condition: mapWeatherCode(current.weather_code),
      temperatureCelsius: current.temperature_2m ?? 0,
      humidityPercent: Math.round(current.relative_humidity_2m ?? 0),
      windSpeedKph: current.wind_speed_10m ?? 0,
      rainProbabilityPercent:
        firstNumber(forecast.hourly?.precipitation_probability) ??
        precipitationToRisk(current.precipitation),
      precipitationMillimeters:
        firstFiniteNumber(forecast.hourly?.precipitation) ??
        normalizePrecipitation(current.precipitation),
      cloudCoverPercent: Math.round(current.cloud_cover ?? 0),
      forecastLeadMinutes: calculateForecastLeadMinutes(capturedAt, forecastFor),
      isStale: false,
    };
  }

  async getForecast(location: ResolvedWeatherLocation): Promise<WeatherSnapshotResponseDto[]> {
    const forecast = await this.fetchForecast(location.latitude, location.longitude, 1);
    const hourly = forecast.hourly;

    if (!hourly?.time?.length) {
      return [await this.getCurrentWeather(location)];
    }

    return hourly.time.slice(0, 6).map((time, index) => {
      const capturedAt = new Date().toISOString();
      const forecastFor = toIsoTimestamp(time);

      return {
        location,
        source: 'OPEN_METEO',
        capturedAt,
        forecastFor,
        condition: mapWeatherCode(hourly.weather_code?.[index]),
        temperatureCelsius: hourly.temperature_2m?.[index] ?? 0,
        humidityPercent: Math.round(hourly.relative_humidity_2m?.[index] ?? 0),
        windSpeedKph: hourly.wind_speed_10m?.[index] ?? 0,
        rainProbabilityPercent: Math.round(hourly.precipitation_probability?.[index] ?? 0),
        precipitationMillimeters: normalizePrecipitation(hourly.precipitation?.[index]),
        cloudCoverPercent: Math.round(hourly.cloud_cover?.[index] ?? 0),
        forecastLeadMinutes: calculateForecastLeadMinutes(capturedAt, forecastFor),
        isStale: false,
      };
    });
  }

  private async fetchForecast(
    latitude: number,
    longitude: number,
    forecastDays: number,
  ): Promise<OpenMeteoForecastResponse> {
    const params = new URLSearchParams({
      latitude: String(latitude),
      longitude: String(longitude),
      current:
        'temperature_2m,relative_humidity_2m,precipitation,cloud_cover,wind_speed_10m,weather_code',
      hourly:
        'precipitation,precipitation_probability,temperature_2m,relative_humidity_2m,wind_speed_10m,cloud_cover,weather_code',
      forecast_days: String(forecastDays),
      timezone: 'UTC',
    });

    const response = await this.fetcher(`${this.baseUrl}/forecast?${params.toString()}`);

    if (!response.ok) {
      throw new Error(`Open-Meteo request failed with status ${response.status}`);
    }

    return (await response.json()) as OpenMeteoForecastResponse;
  }
}

function toIsoTimestamp(value: string | undefined): string {
  if (!value) {
    return new Date().toISOString();
  }

  const normalized = value.endsWith('Z') ? value : `${value}Z`;
  const parsed = new Date(normalized);
  return Number.isNaN(parsed.getTime()) ? new Date().toISOString() : parsed.toISOString();
}

function firstNumber(values: number[] | undefined): number | null {
  const value = values?.find((candidate) => Number.isFinite(candidate));
  return value === undefined ? null : Math.round(value);
}

function firstFiniteNumber(values: number[] | undefined): number | null {
  const value = values?.find((candidate) => Number.isFinite(candidate));
  return value === undefined ? null : normalizePrecipitation(value);
}

function normalizePrecipitation(value: number | undefined): number {
  if (value === undefined || !Number.isFinite(value)) {
    return 0;
  }

  return Math.max(Number(value.toFixed(1)), 0);
}

function calculateForecastLeadMinutes(capturedAt: string, forecastFor: string): number {
  const captured = new Date(capturedAt).getTime();
  const forecast = new Date(forecastFor).getTime();

  if (Number.isNaN(captured) || Number.isNaN(forecast)) {
    return 0;
  }

  return Math.max(Math.round((forecast - captured) / 60_000), 0);
}

function precipitationToRisk(value: number | undefined): number {
  if (!value || value <= 0) {
    return 0;
  }

  return Math.min(100, Math.round(value * 50));
}

function mapWeatherCode(code: number | undefined): WeatherCondition {
  if (code === undefined) {
    return 'UNKNOWN';
  }

  if (code === 0) {
    return 'CLEAR';
  }

  if ([1, 2].includes(code)) {
    return 'PARTLY_CLOUDY';
  }

  if ([3, 45, 48].includes(code)) {
    return 'CLOUDY';
  }

  if ((code >= 51 && code <= 67) || (code >= 80 && code <= 82)) {
    return 'RAIN';
  }

  if (code >= 95) {
    return 'STORM';
  }

  return 'UNKNOWN';
}
