import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';

export class SaveUserLocationRequestDto {
  @ApiProperty({ example: 'current-location' })
  locationId!: string;

  @ApiProperty({ example: 'Mi ubicacion actual' })
  label!: string;

  @ApiPropertyOptional({ example: -34.6037, nullable: true })
  latitude?: number | null;

  @ApiPropertyOptional({ example: -58.3816, nullable: true })
  longitude?: number | null;
}
