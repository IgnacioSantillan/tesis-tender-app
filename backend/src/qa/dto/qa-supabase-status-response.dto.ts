import { ApiProperty } from '@nestjs/swagger';

export class QaSupabaseTableStatusDto {
  @ApiProperty({ example: 'washers' })
  table!: string;

  @ApiProperty({ enum: ['ok', 'unavailable'], example: 'ok' })
  status!: 'ok' | 'unavailable';

  @ApiProperty({ example: 3, nullable: true })
  rowCount!: number | null;

  @ApiProperty({ example: null, nullable: true })
  message!: string | null;
}

export class QaSupabaseStatusResponseDto {
  @ApiProperty({ enum: ['ok', 'degraded'], example: 'ok' })
  status!: 'ok' | 'degraded';

  @ApiProperty({ example: 'rozbtrmvldvsjdudnmxo.supabase.co' })
  projectHost!: string;

  @ApiProperty({ example: '2026-07-18T12:00:00.000Z' })
  checkedAt!: string;

  @ApiProperty({ type: () => QaSupabaseTableStatusDto, isArray: true })
  tables!: QaSupabaseTableStatusDto[];
}
