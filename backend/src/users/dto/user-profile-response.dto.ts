import { ApiProperty } from '@nestjs/swagger';

export class UserProfileResponseDto {
  @ApiProperty({ example: 'user-1' })
  id!: string;

  @ApiProperty({ example: 'user@example.com', nullable: true })
  email!: string | null;

  @ApiProperty({ example: 'Juan', nullable: true })
  displayName!: string | null;

  @ApiProperty({ example: 'home', nullable: true })
  defaultLocationId!: string | null;
}
