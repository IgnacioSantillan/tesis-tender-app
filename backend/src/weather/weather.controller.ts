import { Controller, Get, Query, Req, UseGuards } from '@nestjs/common';
import { ApiBearerAuth, ApiOkResponse, ApiOperation, ApiTags } from '@nestjs/swagger';
import { AuthGuard } from '../auth/auth.guard';
import { AuthenticatedRequest } from '../auth/authenticated-request';
import { ApiProtectedErrorResponses } from '../common/api-error-responses.decorator';
import { WeatherSnapshotResponseDto } from './dto/weather-snapshot-response.dto';
import { WeatherService } from './weather.service';

@ApiTags('weather')
@ApiBearerAuth()
@ApiProtectedErrorResponses()
@UseGuards(AuthGuard)
@Controller('weather')
export class WeatherController {
  constructor(private readonly weatherService: WeatherService) {}

  @Get('current')
  @ApiOperation({ summary: 'Return current normalized weather for a location' })
  @ApiOkResponse({ type: WeatherSnapshotResponseDto })
  current(
    @Req() request: AuthenticatedRequest,
    @Query('locationId') locationId: string | undefined,
  ): Promise<WeatherSnapshotResponseDto> {
    return this.weatherService.getCurrentWeather(locationId, requireUser(request));
  }

  @Get('forecast')
  @ApiOperation({ summary: 'Return normalized forecast window for a location' })
  @ApiOkResponse({ type: WeatherSnapshotResponseDto, isArray: true })
  forecast(
    @Req() request: AuthenticatedRequest,
    @Query('locationId') locationId: string | undefined,
  ): Promise<WeatherSnapshotResponseDto[]> {
    return this.weatherService.getForecast(locationId, requireUser(request));
  }
}

function requireUser(request: AuthenticatedRequest) {
  if (!request.user) {
    throw new Error('Authenticated user missing after guard execution');
  }

  return request.user;
}
