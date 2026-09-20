import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { DRYING_LOCATION_IDS, DryingLocationId } from '../../drying-locations/drying-location-types';
import { LOAD_SIZES, LoadSize, SPIN_SPEED_RPMS, SpinSpeedRpm } from '../../energy/energy-prediction-types';
import { CLOTHING_TYPES, ClothingType } from '../../laundry-loads/dto/laundry-load-types';
import { WASHER_ENERGY_LABELS, WasherEnergyLabel } from '../../washers/dto/washer-energy-label';
import { DRYING_METHODS, DryingMethod } from './drying-prediction-types';

export class CreateCompletionOptionsRequestDto {
  @ApiProperty({ example: 'load-1' })
  laundryLoadId!: string;

  @ApiProperty({ enum: CLOTHING_TYPES, example: 'MIXED' })
  clothingType!: ClothingType;

  @ApiProperty({ enum: DRYING_METHODS, example: 'OUTDOOR' })
  dryingMethod!: DryingMethod;

  @ApiProperty({ example: 'home' })
  locationId!: string;

  @ApiPropertyOptional({ enum: DRYING_LOCATION_IDS, example: 'PATIO' })
  dryingLocationId?: DryingLocationId | null;

  @ApiPropertyOptional({ enum: SPIN_SPEED_RPMS, example: 1200, nullable: true })
  spinRpm?: SpinSpeedRpm | null;

  @ApiPropertyOptional({ enum: LOAD_SIZES, example: 'MEDIUM', nullable: true })
  loadSize?: LoadSize | null;

  @ApiPropertyOptional({ enum: WASHER_ENERGY_LABELS, example: 'A', nullable: true })
  washerEnergyLabel?: WasherEnergyLabel | null;

  @ApiPropertyOptional({ example: 7, nullable: true })
  washerCapacityKg?: number | null;

  @ApiPropertyOptional({ example: 45, nullable: true })
  waterUsageLiters?: number | null;

  @ApiPropertyOptional({
    example: '2026-07-25T20:00:00-03:00',
    description: 'Planned washing start as an ISO 8601 instant with Z or an explicit UTC offset. Defaults to server time.',
  })
  plannedStartAt?: string;

  @ApiProperty({
    example: '2026-07-26T01:00:00-03:00',
    description: 'Desired ready time as an ISO 8601 instant with Z or an explicit UTC offset.',
  })
  targetReadyAt!: string;
}
