import { ApiProperty } from '@nestjs/swagger';
import { DRYING_LOCATION_IDS, DryingLocationId } from '../../drying-locations/drying-location-types';
import {
  ESTIMATED_WASHING_COST_CONFIDENCES,
  ESTIMATED_WASHING_COST_LEVELS,
  EstimatedWashingCostConfidence,
  EstimatedWashingCostLevel,
} from '../../energy/energy-prediction-types';
import { WeatherSnapshotResponseDto } from '../../weather/dto/weather-snapshot-response.dto';
import { DRYING_METHODS, DRYING_VERDICTS, DryingMethod, DryingVerdict } from './drying-prediction-types';

export class EstimatedWashingCostResponseDto {
  @ApiProperty({ example: null, nullable: true })
  amount!: number | null;

  @ApiProperty({ example: null, nullable: true })
  currency!: string | null;

  @ApiProperty({ enum: ESTIMATED_WASHING_COST_LEVELS, example: 'MEDIUM' })
  level!: EstimatedWashingCostLevel;

  @ApiProperty({ enum: ESTIMATED_WASHING_COST_CONFIDENCES, example: 'LOW' })
  confidence!: EstimatedWashingCostConfidence;

  @ApiProperty({ example: 0.84, nullable: true })
  estimatedEnergyKwh!: number | null;

  @ApiProperty({ example: 52, nullable: true })
  estimatedWaterLiters!: number | null;
}

export class DryingHourlySlotResponseDto {
  @ApiProperty({ example: '2026-07-05T15:00:00.000Z' })
  forecastFor!: string;

  @ApiProperty({ enum: DRYING_VERDICTS, example: 'GOOD' })
  verdict!: DryingVerdict;

  @ApiProperty({ example: 82 })
  suitabilityScore!: number;

  @ApiProperty({ example: 24.0 })
  temperatureCelsius!: number;

  @ApiProperty({ example: 48 })
  humidityPercent!: number;

  @ApiProperty({ example: 18.0 })
  windSpeedKph!: number;

  @ApiProperty({ example: 8 })
  rainProbabilityPercent!: number;
}

export class DryingPredictionResponseDto {
  @ApiProperty({ enum: DRYING_VERDICTS, example: 'GOOD' })
  verdict!: DryingVerdict;

  @ApiProperty({ enum: DRYING_METHODS, example: 'OUTDOOR' })
  dryingMethod!: DryingMethod;

  @ApiProperty({ enum: DRYING_LOCATION_IDS, example: 'PATIO' })
  dryingLocationId!: DryingLocationId;

  @ApiProperty({ example: 'Patio' })
  dryingLocationLabel!: string;

  @ApiProperty({ example: 180 })
  estimatedDryingMinutes!: number;

  @ApiProperty({ example: '2026-07-05T15:00:00.000Z' })
  recommendedHangAt!: string;

  @ApiProperty({ example: '2026-07-05T15:00:00.000Z' })
  recommendedHangWindowStart!: string;

  @ApiProperty({ example: '2026-07-05T17:00:00.000Z' })
  recommendedHangWindowEnd!: string;

  @ApiProperty({ example: '2026-07-05T18:00:00.000Z' })
  estimatedPickupAt!: string;

  @ApiProperty({ example: 82 })
  suitabilityScore!: number;

  @ApiProperty({ example: 'Weather is favorable for drying: low rain risk and useful wind.' })
  reason!: string;

  @ApiProperty({ type: () => EstimatedWashingCostResponseDto, nullable: true })
  estimatedCost!: EstimatedWashingCostResponseDto | null;

  @ApiProperty({ type: () => DryingHourlySlotResponseDto, isArray: true })
  hourlySlots!: DryingHourlySlotResponseDto[];

  @ApiProperty({ type: () => WeatherSnapshotResponseDto })
  weatherSnapshot!: WeatherSnapshotResponseDto;
}
