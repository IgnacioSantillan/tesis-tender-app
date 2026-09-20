import { ApiProperty } from '@nestjs/swagger';

export class QaSyntheticWasherDto {
  @ApiProperty({ example: '00000000-0000-4000-8000-000000000101' })
  id!: string;

  @ApiProperty({ example: 'Lavarropas de ejemplo' })
  name!: string;

  @ApiProperty({ example: 'FRONT_LOAD' })
  type!: string;

  @ApiProperty({ example: 7 })
  capacityKg!: number;

  @ApiProperty({ example: 'A' })
  energyLabel!: string;

  @ApiProperty({ example: 45 })
  waterUsageLiters!: number;

  @ApiProperty({ example: 1200 })
  defaultSpinRpm!: number;
}

export class QaSyntheticLaundryLoadDto {
  @ApiProperty({ example: '00000000-0000-4000-8000-000000000201' })
  id!: string;

  @ApiProperty({ example: 'MIXED' })
  clothingType!: string;

  @ApiProperty({ example: 'ECO' })
  washingProgram!: string;

  @ApiProperty({ example: 'DRYING' })
  status!: string;

  @ApiProperty({ example: 'home' })
  locationId!: string;

  @ApiProperty({ example: 'PATIO' })
  dryingLocationId!: string;
}

export class QaSyntheticUserLocationDto {
  @ApiProperty({ example: 'home' })
  id!: string;

  @ApiProperty({ example: 'Hogar de ejemplo' })
  label!: string;

  @ApiProperty({ example: -34.6037 })
  latitude!: number;

  @ApiProperty({ example: -58.3816 })
  longitude!: number;
}

export class QaSupabaseExamplesResponseDto {
  @ApiProperty({ example: 'synthetic' })
  source!: 'synthetic';

  @ApiProperty({ example: 'These examples are fake and do not contain persisted user data.' })
  note!: string;

  @ApiProperty({ type: () => QaSyntheticWasherDto })
  washer!: QaSyntheticWasherDto;

  @ApiProperty({ type: () => QaSyntheticLaundryLoadDto })
  laundryLoad!: QaSyntheticLaundryLoadDto;

  @ApiProperty({ type: () => QaSyntheticUserLocationDto })
  userLocation!: QaSyntheticUserLocationDto;
}
