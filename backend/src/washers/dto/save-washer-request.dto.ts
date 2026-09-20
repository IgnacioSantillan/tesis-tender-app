import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { SPIN_SPEED_RPMS, SpinSpeedRpm } from '../../energy/energy-prediction-types';
import { WASHER_ENERGY_LABELS, WasherEnergyLabel } from './washer-energy-label';
import { WASHER_TYPES, WasherType } from './washer-type';

export class SaveWasherRequestDto {
  @ApiProperty({ example: 'Main washer' })
  name!: string;

  @ApiProperty({ enum: WASHER_TYPES, example: 'FRONT_LOAD' })
  type!: WasherType;

  @ApiPropertyOptional({ example: 7.0, nullable: true })
  capacityKg?: number | null;

  @ApiPropertyOptional({ enum: WASHER_ENERGY_LABELS, example: 'A', nullable: true })
  energyLabel?: WasherEnergyLabel | null;

  @ApiPropertyOptional({ example: 45.0, nullable: true })
  waterUsageLiters?: number | null;

  @ApiPropertyOptional({ enum: SPIN_SPEED_RPMS, example: 1200, nullable: true })
  defaultSpinRpm?: SpinSpeedRpm | null;

  @ApiPropertyOptional({ example: true })
  isPrimary?: boolean;
}
