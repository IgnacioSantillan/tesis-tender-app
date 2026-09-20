import { ApiPropertyOptional } from '@nestjs/swagger';

export class DispatchDueNotificationsRequestDto {
  @ApiPropertyOptional({ example: 10, minimum: 1, maximum: 50 })
  limit?: number;
}
