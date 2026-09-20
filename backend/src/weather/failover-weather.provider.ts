import { Injectable, Logger } from '@nestjs/common';
import { WeatherSnapshotResponseDto } from './dto/weather-snapshot-response.dto';
import { MetNoWeatherProvider } from './met-no-weather.provider';
import { OpenMeteoWeatherProvider } from './open-meteo-weather.provider';
import { ResolvedWeatherLocation } from './weather-location-registry';
import { WeatherProvider } from './weather-provider';

@Injectable()
export class FailoverWeatherProvider implements WeatherProvider {
  private readonly logger = new Logger(FailoverWeatherProvider.name);

  constructor(
    private readonly openMeteoProvider: OpenMeteoWeatherProvider,
    private readonly metNoProvider: MetNoWeatherProvider,
  ) {}

  async getCurrentWeather(location: ResolvedWeatherLocation): Promise<WeatherSnapshotResponseDto> {
    try {
      return await this.openMeteoProvider.getCurrentWeather(location);
    } catch (error) {
      this.logger.warn(`Open-Meteo current weather failed; trying MET Norway: ${formatError(error)}`);
      return this.metNoProvider.getCurrentWeather(location);
    }
  }

  async getForecast(location: ResolvedWeatherLocation): Promise<WeatherSnapshotResponseDto[]> {
    try {
      return await this.openMeteoProvider.getForecast(location);
    } catch (error) {
      this.logger.warn(`Open-Meteo forecast failed; trying MET Norway: ${formatError(error)}`);
      return this.metNoProvider.getForecast(location);
    }
  }
}

function formatError(error: unknown): string {
  if (error instanceof Error) {
    return error.message;
  }

  try {
    return JSON.stringify(error);
  } catch {
    return String(error);
  }
}
