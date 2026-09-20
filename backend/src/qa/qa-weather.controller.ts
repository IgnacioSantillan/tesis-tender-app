import { Controller, Get, Query, UseGuards } from '@nestjs/common';
import { ApiHeader, ApiOkResponse, ApiOperation, ApiQuery, ApiSecurity, ApiTags } from '@nestjs/swagger';
import { ApiProtectedErrorResponses } from '../common/api-error-responses.decorator';
import { QaWeatherProviderStatusResponseDto } from './dto/qa-weather-provider-status-response.dto';
import { QaKeyGuard } from './qa-key.guard';
import { QaWeatherService } from './qa-weather.service';

@ApiTags('qa')
@ApiSecurity('qa-key')
@ApiHeader({
  name: 'x-qa-key',
  required: true,
  description: 'Protected QA diagnostics key. Do not expose it in thesis screenshots.',
})
@ApiProtectedErrorResponses()
@UseGuards(QaKeyGuard)
@Controller('qa/weather')
export class QaWeatherController {
  constructor(private readonly qaWeatherService: QaWeatherService) {}

  @Get('provider-status')
  @ApiOperation({ summary: 'Return protected weather provider diagnostics for QA.' })
  @ApiQuery({ name: 'locationId', required: false, example: 'home' })
  @ApiOkResponse({ type: QaWeatherProviderStatusResponseDto })
  providerStatus(
    @Query('locationId') locationId?: string,
  ): Promise<QaWeatherProviderStatusResponseDto> {
    return this.qaWeatherService.getProviderStatus(locationId?.trim() || 'home');
  }
}
