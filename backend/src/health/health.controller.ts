import { Controller, Get } from '@nestjs/common';
import { ApiOkResponse, ApiOperation, ApiTags } from '@nestjs/swagger';
import { ApiPublicErrorResponses } from '../common/api-error-responses.decorator';
import { HealthResponseDto } from './dto/health-response.dto';
import { SupabaseHealthResponseDto } from './dto/supabase-health-response.dto';
import { HealthService } from './health.service';

@ApiTags('health')
@ApiPublicErrorResponses()
@Controller('health')
export class HealthController {
  constructor(private readonly healthService: HealthService) {}

  @Get()
  @ApiOperation({ summary: 'Return backend health status' })
  @ApiOkResponse({ type: HealthResponseDto })
  health(): HealthResponseDto {
    return this.healthService.getHealth();
  }

  @Get('supabase')
  @ApiOperation({ summary: 'Return backend Supabase connectivity status' })
  @ApiOkResponse({ type: SupabaseHealthResponseDto })
  supabase(): Promise<SupabaseHealthResponseDto> {
    return this.healthService.getSupabaseHealth();
  }
}
