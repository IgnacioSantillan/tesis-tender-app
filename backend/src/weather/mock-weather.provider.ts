import { Injectable } from '@nestjs/common';
import { WeatherSnapshotResponseDto } from './dto/weather-snapshot-response.dto';
import { ResolvedWeatherLocation } from './weather-location-registry';
import { WeatherProvider } from './weather-provider';

@Injectable()
export class MockWeatherProvider implements WeatherProvider {
  async getCurrentWeather(location: ResolvedWeatherLocation): Promise<WeatherSnapshotResponseDto> {
    return createSnapshot(location, 0);
  }

  async getForecast(location: ResolvedWeatherLocation): Promise<WeatherSnapshotResponseDto[]> {
    return [createSnapshot(location, 0), createSnapshot(location, 2), createSnapshot(location, 4)];
  }
}

function createSnapshot(location: ResolvedWeatherLocation, hourOffset: number): WeatherSnapshotResponseDto {
  const capturedAt = new Date('2026-07-05T14:00:00.000Z');
  const forecastFor = new Date(capturedAt.getTime() + hourOffset * 60 * 60 * 1000);

  return {
    location: {
      id: location.id,
      label: location.label,
      latitude: location.latitude,
      longitude: location.longitude,
    },
    source: 'MOCK',
    capturedAt: capturedAt.toISOString(),
    forecastFor: forecastFor.toISOString(),
    condition: hourOffset >= 4 ? 'PARTLY_CLOUDY' : 'CLEAR',
    temperatureCelsius: 24 - hourOffset * 0.5,
    humidityPercent: 48 + hourOffset,
    windSpeedKph: 18,
    rainProbabilityPercent: hourOffset >= 4 ? 18 : 8,
    precipitationMillimeters: hourOffset >= 4 ? 0.4 : 0,
    cloudCoverPercent: hourOffset >= 4 ? 35 : 20,
    forecastLeadMinutes: hourOffset * 60,
    isStale: false,
  };
}
