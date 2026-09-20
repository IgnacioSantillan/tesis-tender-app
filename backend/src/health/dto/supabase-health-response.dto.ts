import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';

export const SUPABASE_HEALTH_STATUSES = ['ok', 'unavailable'] as const;

export type SupabaseHealthStatus = (typeof SUPABASE_HEALTH_STATUSES)[number];

export class SupabaseHealthResponseDto {
  @ApiProperty({ enum: SUPABASE_HEALTH_STATUSES, example: 'ok' })
  status!: SupabaseHealthStatus;

  @ApiProperty({ example: 'example.supabase.co' })
  projectHost!: string;

  @ApiProperty({ example: '2026-07-07T15:00:00.000Z' })
  checkedAt!: string;

  @ApiPropertyOptional({ example: 'Supabase connectivity check failed.', nullable: true })
  message!: string | null;
}
