import { ApiProperty } from '@nestjs/swagger';
import { WeatherLocationDto } from './weather-location.dto';
import { WEATHER_CONDITIONS, WEATHER_DATA_SOURCES, WeatherCondition, WeatherDataSource } from './weather-types';

export class WeatherSnapshotResponseDto {
  @ApiProperty({ type: () => WeatherLocationDto })
  location!: WeatherLocationDto;

  @ApiProperty({ enum: WEATHER_DATA_SOURCES, example: 'OPEN_METEO' })
  source!: WeatherDataSource;

  @ApiProperty({ example: '2026-07-05T14:00:00Z' })
  capturedAt!: string;

  @ApiProperty({ example: '2026-07-05T15:00:00Z' })
  forecastFor!: string;

  @ApiProperty({ enum: WEATHER_CONDITIONS, example: 'CLEAR' })
  condition!: WeatherCondition;

  @ApiProperty({ example: 24.0 })
  temperatureCelsius!: number;

  @ApiProperty({ example: 48 })
  humidityPercent!: number;

  @ApiProperty({ example: 18.0 })
  windSpeedKph!: number;

  @ApiProperty({ example: 8 })
  rainProbabilityPercent!: number;

  @ApiProperty({ example: 0.2, description: 'Expected precipitation amount for the snapshot window.' })
  precipitationMillimeters!: number;

  @ApiProperty({ example: 20 })
  cloudCoverPercent!: number;

  @ApiProperty({ example: 60, description: 'Minutes between capture time and forecast target time.' })
  forecastLeadMinutes!: number;

  @ApiProperty({ example: false })
  isStale!: boolean;
}
