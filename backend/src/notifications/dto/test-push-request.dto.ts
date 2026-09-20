import { ApiPropertyOptional } from '@nestjs/swagger';

export class TestPushRequestDto {
  @ApiPropertyOptional({ example: 'TenderApp test' })
  title?: string | null;

  @ApiPropertyOptional({ example: 'Push channel is ready.' })
  body?: string | null;
}
