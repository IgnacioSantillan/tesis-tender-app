import { ApiProperty } from '@nestjs/swagger';
import { DRYING_LOCATION_IDS, DryingLocationId } from '../../drying-locations/drying-location-types';
import {
  ESTIMATED_WASHING_COST_CONFIDENCES,
  ESTIMATED_WASHING_COST_LEVELS,
  EstimatedWashingCostConfidence,
  EstimatedWashingCostLevel,
  LOAD_SIZES,
  LoadSize,
  SPIN_SPEED_RPMS,
  SpinSpeedRpm,
} from '../../energy/energy-prediction-types';
import { CLOTHING_TYPES, ClothingType, LAUNDRY_LOAD_STATUSES, LaundryLoadStatus, WASHING_PROGRAMS, WashingProgram } from './laundry-load-types';

export class LaundryLoadResponseDto {
  @ApiProperty({ example: 'load-1' })
  id!: string;

  @ApiProperty({ example: 'washer-1', nullable: true })
  washerId!: string | null;

  @ApiProperty({ enum: CLOTHING_TYPES, example: 'MIXED' })
  clothingType!: ClothingType;

  @ApiProperty({ enum: WASHING_PROGRAMS, example: 'NORMAL' })
  washingProgram!: WashingProgram;

  @ApiProperty({ enum: LAUNDRY_LOAD_STATUSES, example: 'PLANNED' })
  status!: LaundryLoadStatus;

  @ApiProperty({ example: 'home' })
  locationId!: string;

  @ApiProperty({ enum: DRYING_LOCATION_IDS, example: 'PATIO' })
  dryingLocationId!: DryingLocationId;

  @ApiProperty({ enum: SPIN_SPEED_RPMS, example: 1200, nullable: true })
  spinRpm!: SpinSpeedRpm | null;

  @ApiProperty({ enum: LOAD_SIZES, example: 'MEDIUM', nullable: true })
  loadSize!: LoadSize | null;

  @ApiProperty({ example: 0.84, nullable: true })
  estimatedWashingEnergyKwh!: number | null;

  @ApiProperty({ example: 52, nullable: true })
  estimatedWashingWaterLiters!: number | null;

  @ApiProperty({ example: null, nullable: true })
  estimatedWashingCostAmount!: number | null;

  @ApiProperty({ example: null, nullable: true })
  estimatedWashingCostCurrency!: string | null;

  @ApiProperty({ enum: ESTIMATED_WASHING_COST_LEVELS, example: 'MEDIUM', nullable: true })
  estimatedWashingCostLevel!: EstimatedWashingCostLevel | null;

  @ApiProperty({ enum: ESTIMATED_WASHING_COST_CONFIDENCES, example: 'LOW', nullable: true })
  estimatedWashingCostConfidence!: EstimatedWashingCostConfidence | null;

  @ApiProperty({ example: '2026-07-05T14:00:00Z' })
  createdAt!: string;

  @ApiProperty({ example: null, nullable: true })
  startedAt!: string | null;

  @ApiProperty({ example: null, nullable: true })
  dryingStartedAt!: string | null;

  @ApiProperty({ example: 180, nullable: true })
  dryingEstimatedMinutesAtStart!: number | null;

  @ApiProperty({ example: null, nullable: true })
  dryingEstimatedPickupAt!: string | null;

  @ApiProperty({ example: null, nullable: true })
  completedAt!: string | null;

  @ApiProperty({ example: null, nullable: true, type: 'object' })
  prediction!: null;
}
