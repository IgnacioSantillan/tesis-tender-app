import { ApiProperty } from '@nestjs/swagger';

export class TestPushResponseDto {
  @ApiProperty({ example: 'registration-1' })
  deviceRegistrationId!: string;

  @ApiProperty({ example: 'projects/tesis-tender-app/messages/1234567890' })
  providerMessageId!: string;

  @ApiProperty({ example: 'SENT' })
  status!: 'SENT';
}
