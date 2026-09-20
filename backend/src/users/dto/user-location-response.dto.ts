import { ApiProperty } from '@nestjs/swagger';

export class UserLocationResponseDto {
  @ApiProperty({ example: 'current-location' })
  id!: string;

  @ApiProperty({ example: 'Mi ubicacion actual' })
  label!: string;

  @ApiProperty({ example: -34.6037, nullable: true })
  latitude!: number | null;

  @ApiProperty({ example: -58.3816, nullable: true })
  longitude!: number | null;

  @ApiProperty({ example: true })
  isPrimary!: boolean;

  @ApiProperty({ example: '2026-07-12T12:00:00.000Z' })
  updatedAt!: string;
}
