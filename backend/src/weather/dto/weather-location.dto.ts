import { ApiProperty } from '@nestjs/swagger';

export class WeatherLocationDto {
  @ApiProperty({ example: 'home' })
  id!: string;

  @ApiProperty({ example: 'Home patio' })
  label!: string;

  @ApiProperty({ example: null, nullable: true })
  latitude!: number | null;

  @ApiProperty({ example: null, nullable: true })
  longitude!: number | null;
}
