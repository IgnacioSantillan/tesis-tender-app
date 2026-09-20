import { ApiProperty } from '@nestjs/swagger';

export class DispatchDueNotificationsResponseDto {
  @ApiProperty({ example: 2 })
  processed!: number;

  @ApiProperty({ example: 1 })
  sent!: number;

  @ApiProperty({ example: 1 })
  skipped!: number;

  @ApiProperty({ example: 0 })
  failed!: number;
}
