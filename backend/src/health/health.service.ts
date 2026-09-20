import { Injectable } from '@nestjs/common';
import { getAppConfig } from '../config/environment';
import { SupabaseService } from '../supabase/supabase.service';
import { HealthResponseDto } from './dto/health-response.dto';
import { SupabaseHealthResponseDto } from './dto/supabase-health-response.dto';

@Injectable()
export class HealthService {
  constructor(private readonly supabaseService: SupabaseService) {}

  getHealth(): HealthResponseDto {
    const config = getAppConfig();

    return {
      status: 'ok',
      version: config.apiVersion,
    };
  }

  async getSupabaseHealth(): Promise<SupabaseHealthResponseDto> {
    const checkedAt = new Date().toISOString();

    try {
      await this.supabaseService.verifyConnectivity();

      return {
        status: 'ok',
        projectHost: this.supabaseService.getProjectHost(),
        checkedAt,
        message: null,
      };
    } catch {
      return {
        status: 'unavailable',
        projectHost: this.supabaseService.getProjectHost(),
        checkedAt,
        message: 'Supabase connectivity check failed.',
      };
    }
  }
}
