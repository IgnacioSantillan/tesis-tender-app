import { Injectable } from '@nestjs/common';
import { SupabaseService } from '../supabase/supabase.service';
import { QaSupabaseExamplesResponseDto } from './dto/qa-supabase-examples-response.dto';
import { QaSupabaseStatusResponseDto, QaSupabaseTableStatusDto } from './dto/qa-supabase-status-response.dto';

const TABLES_TO_CHECK = [
  'washers',
  'laundry_loads',
  'user_locations',
  'device_push_registrations',
  'notification_events',
] as const;

@Injectable()
export class QaSupabaseService {
  constructor(private readonly supabaseService: SupabaseService) {}

  async getStatus(): Promise<QaSupabaseStatusResponseDto> {
    const tables = await Promise.all(TABLES_TO_CHECK.map((table) => this.checkTable(table)));
    const hasUnavailableTable = tables.some((table) => table.status === 'unavailable');

    return {
      status: hasUnavailableTable ? 'degraded' : 'ok',
      projectHost: this.supabaseService.getProjectHost(),
      checkedAt: new Date().toISOString(),
      tables,
    };
  }

  getExamples(): QaSupabaseExamplesResponseDto {
    return {
      source: 'synthetic',
      note: 'These examples are fake and do not contain persisted user data.',
      washer: {
        id: '00000000-0000-4000-8000-000000000101',
        name: 'Lavarropas de ejemplo',
        type: 'FRONT_LOAD',
        capacityKg: 7,
        energyLabel: 'A',
        waterUsageLiters: 45,
        defaultSpinRpm: 1200,
      },
      laundryLoad: {
        id: '00000000-0000-4000-8000-000000000201',
        clothingType: 'MIXED',
        washingProgram: 'ECO',
        status: 'DRYING',
        locationId: 'home',
        dryingLocationId: 'PATIO',
      },
      userLocation: {
        id: 'home',
        label: 'Hogar de ejemplo',
        latitude: -34.6037,
        longitude: -58.3816,
      },
    };
  }

  private async checkTable(table: string): Promise<QaSupabaseTableStatusDto> {
    const { count, error } = await this.supabaseService
      .getClient()
      .from(table)
      .select('*', { count: 'exact', head: true });

    if (error) {
      return {
        table,
        status: 'unavailable',
        rowCount: null,
        message: 'Table check failed.',
      };
    }

    return {
      table,
      status: 'ok',
      rowCount: count ?? 0,
      message: null,
    };
  }
}
