import { WeatherSnapshotResponseDto } from './dto/weather-snapshot-response.dto';
import { ResolvedWeatherLocation } from './weather-location-registry';

export const WEATHER_PROVIDER = Symbol('WEATHER_PROVIDER');

export interface WeatherProvider {
  getCurrentWeather(location: ResolvedWeatherLocation): Promise<WeatherSnapshotResponseDto>;
  getForecast(location: ResolvedWeatherLocation): Promise<WeatherSnapshotResponseDto[]>;
}
