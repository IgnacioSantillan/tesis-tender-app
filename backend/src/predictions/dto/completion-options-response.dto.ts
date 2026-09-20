import { ApiProperty } from '@nestjs/swagger';
import {
  WASHING_PROGRAMS,
  WashingProgram,
} from '../../laundry-loads/dto/laundry-load-types';
import { WEATHER_DATA_SOURCES, WeatherDataSource } from '../../weather/dto/weather-types';
import { DRYING_VERDICTS, DryingVerdict } from './drying-prediction-types';

export class CompletionProgramOptionResponseDto {
  @ApiProperty({ enum: WASHING_PROGRAMS, example: 'QUICK' })
  program!: WashingProgram;

  @ApiProperty({ example: 30 })
  washingMinutes!: number;

  @ApiProperty({ example: '2026-07-25T23:30:00.000Z' })
  washingEndsAt!: string;

  @ApiProperty({ example: '2026-07-26T00:00:00.000Z' })
  dryingStartsAt!: string;

  @ApiProperty({ example: 120 })
  estimatedDryingMinutes!: number;

  @ApiProperty({ example: '2026-07-26T02:00:00.000Z' })
  estimatedReadyAt!: string;

  @ApiProperty({ example: 180, description: 'Elapsed minutes from planned washing start until estimated ready time.' })
  totalElapsedMinutes!: number;

  @ApiProperty({
    example: 60,
    description: 'Minutes remaining before the target. Negative values indicate lateness.',
  })
  marginMinutes!: number;

  @ApiProperty({ example: true })
  feasible!: boolean;

  @ApiProperty({
    example: false,
    description: 'True when the estimated drying interval extends beyond available forecast coverage.',
  })
  usesForecastExtrapolation!: boolean;

  @ApiProperty({ enum: DRYING_VERDICTS, example: 'GOOD' })
  verdict!: DryingVerdict;

  @ApiProperty({ example: 82 })
  suitabilityScore!: number;
}

export class CompletionOptionsResponseDto {
  @ApiProperty({ example: '2026-07-25T22:55:00.000Z' })
  generatedAt!: string;

  @ApiProperty({ example: '2026-07-25T23:00:00.000Z' })
  plannedStartAt!: string;

  @ApiProperty({ example: '2026-07-26T04:00:00.000Z' })
  targetReadyAt!: string;

  @ApiProperty({ enum: WEATHER_DATA_SOURCES, example: 'OPEN_METEO' })
  weatherSource!: WeatherDataSource;

  @ApiProperty({ example: false })
  isStale!: boolean;

  @ApiProperty({ example: '2026-07-26T06:00:00.000Z', nullable: true })
  forecastCoverageEndsAt!: string | null;

  @ApiProperty({ enum: WASHING_PROGRAMS, isArray: true, example: ['QUICK', 'DELICATE'] })
  recommendedPrograms!: WashingProgram[];

  @ApiProperty({ type: () => CompletionProgramOptionResponseDto, isArray: true })
  options!: CompletionProgramOptionResponseDto[];
}
