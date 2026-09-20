import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { DRYING_LOCATION_IDS, DryingLocationId } from '../../drying-locations/drying-location-types';
import { LOAD_SIZES, LoadSize, SPIN_SPEED_RPMS, SpinSpeedRpm } from '../../energy/energy-prediction-types';
import { CLOTHING_TYPES, ClothingType, WASHING_PROGRAMS, WashingProgram } from './laundry-load-types';

export class CreateLaundryLoadRequestDto {
  @ApiPropertyOptional({ example: 'washer-1', nullable: true })
  washerId?: string | null;

  @ApiProperty({ enum: CLOTHING_TYPES, example: 'MIXED' })
  clothingType!: ClothingType;

  @ApiProperty({ enum: WASHING_PROGRAMS, example: 'NORMAL' })
  washingProgram!: WashingProgram;

  @ApiProperty({ example: 'home' })
  locationId!: string;

  @ApiPropertyOptional({ enum: DRYING_LOCATION_IDS, example: 'PATIO' })
  dryingLocationId?: DryingLocationId | null;

  @ApiPropertyOptional({ enum: SPIN_SPEED_RPMS, example: 1200, nullable: true })
  spinRpm?: SpinSpeedRpm | null;

  @ApiPropertyOptional({ enum: LOAD_SIZES, example: 'MEDIUM', nullable: true })
  loadSize?: LoadSize | null;
}
