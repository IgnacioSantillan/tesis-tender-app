import { ApiProperty } from '@nestjs/swagger';
import { SPIN_SPEED_RPMS, SpinSpeedRpm } from '../../energy/energy-prediction-types';
import { WASHER_ENERGY_LABELS, WasherEnergyLabel } from './washer-energy-label';
import { WASHER_TYPES, WasherType } from './washer-type';

export class WasherResponseDto {
  @ApiProperty({ example: 'washer-1' })
  id!: string;

  @ApiProperty({ example: 'Main washer' })
  name!: string;

  @ApiProperty({ enum: WASHER_TYPES, example: 'FRONT_LOAD' })
  type!: WasherType;

  @ApiProperty({ example: 7.0, nullable: true })
  capacityKg!: number | null;

  @ApiProperty({ enum: WASHER_ENERGY_LABELS, example: 'A', nullable: true })
  energyLabel!: WasherEnergyLabel | null;

  @ApiProperty({ example: 45.0, nullable: true })
  waterUsageLiters!: number | null;

  @ApiProperty({ enum: SPIN_SPEED_RPMS, example: 1200, nullable: true })
  defaultSpinRpm!: SpinSpeedRpm | null;

  @ApiProperty({ example: true })
  isPrimary!: boolean;
}
