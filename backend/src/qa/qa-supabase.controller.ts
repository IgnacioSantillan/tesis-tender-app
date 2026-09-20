import { Controller, Get, UseGuards } from '@nestjs/common';
import { ApiHeader, ApiOkResponse, ApiOperation, ApiSecurity, ApiTags } from '@nestjs/swagger';
import { ApiProtectedErrorResponses } from '../common/api-error-responses.decorator';
import { QaSupabaseExamplesResponseDto } from './dto/qa-supabase-examples-response.dto';
import { QaSupabaseStatusResponseDto } from './dto/qa-supabase-status-response.dto';
import { QaKeyGuard } from './qa-key.guard';
import { QaSupabaseService } from './qa-supabase.service';

@ApiTags('qa')
@ApiSecurity('qa-key')
@ApiHeader({
  name: 'x-qa-key',
  required: true,
  description: 'Protected QA diagnostics key. Do not expose it in thesis screenshots.',
})
@ApiProtectedErrorResponses()
@UseGuards(QaKeyGuard)
@Controller('qa/supabase')
export class QaSupabaseController {
  constructor(private readonly qaSupabaseService: QaSupabaseService) {}

  @Get('status')
  @ApiOperation({ summary: 'Return protected redacted Supabase table diagnostics for QA.' })
  @ApiOkResponse({ type: QaSupabaseStatusResponseDto })
  status(): Promise<QaSupabaseStatusResponseDto> {
    return this.qaSupabaseService.getStatus();
  }

  @Get('examples')
  @ApiOperation({ summary: 'Return synthetic Supabase-related examples for thesis/API documentation.' })
  @ApiOkResponse({ type: QaSupabaseExamplesResponseDto })
  examples(): QaSupabaseExamplesResponseDto {
    return this.qaSupabaseService.getExamples();
  }
}
