import { ApiProperty } from '@nestjs/swagger';
import { WEATHER_DATA_SOURCES, WeatherDataSource } from '../../weather/dto/weather-types';

export class QaWeatherProviderStatusResponseDto {
  @ApiProperty({ enum: ['ok', 'degraded'], example: 'ok' })
  status!: 'ok' | 'degraded';

  @ApiProperty({ example: 'home' })
  locationId!: string;

  @ApiProperty({ enum: WEATHER_DATA_SOURCES, example: 'MET_NO' })
  source!: WeatherDataSource;

  @ApiProperty({ example: false })
  isStale!: boolean;

  @ApiProperty({ example: '2026-07-25T20:00:00.000Z' })
  checkedAt!: string;

  @ApiProperty({ example: '2026-07-25T20:00:00.000Z' })
  forecastFor!: string;

  @ApiProperty({ example: 12.3 })
  temperatureCelsius!: number;

  @ApiProperty({ example: 92 })
  humidityPercent!: number;

  @ApiProperty({ example: 12.2 })
  windSpeedKph!: number;

  @ApiProperty({ example: 0 })
  rainProbabilityPercent!: number;

  @ApiProperty({ example: 0 })
  precipitationMillimeters!: number;

  @ApiProperty({ example: 66 })
  cloudCoverPercent!: number;

  @ApiProperty({
    example: 'Provider returned a real weather snapshot.',
    nullable: true,
  })
  message!: string | null;
}
