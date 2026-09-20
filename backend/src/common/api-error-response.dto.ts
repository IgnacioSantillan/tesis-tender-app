import { ApiProperty } from '@nestjs/swagger';

export class ApiErrorResponseDto {
  @ApiProperty({ example: 'BAD_REQUEST' })
  code!: string;

  @ApiProperty({ example: 'locationId is required' })
  message!: string;

  @ApiProperty({ example: 'request-123' })
  traceId!: string;
}
