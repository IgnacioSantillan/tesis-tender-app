import { Body, Controller, Post, Req, UseGuards } from '@nestjs/common';
import {
  ApiBadRequestResponse,
  ApiBearerAuth,
  ApiOkResponse,
  ApiOperation,
  ApiTags,
} from '@nestjs/swagger';
import { AuthGuard } from '../auth/auth.guard';
import { AuthenticatedRequest } from '../auth/authenticated-request';
import { ApiProtectedErrorResponses } from '../common/api-error-responses.decorator';
import { CompletionOptionsResponseDto } from './dto/completion-options-response.dto';
import { CreateCompletionOptionsRequestDto } from './dto/create-completion-options-request.dto';
import { CreateDryingPredictionRequestDto } from './dto/create-drying-prediction-request.dto';
import { DryingPredictionResponseDto } from './dto/drying-prediction-response.dto';
import { PredictionsService } from './predictions.service';

@ApiTags('predictions')
@ApiBearerAuth()
@ApiProtectedErrorResponses()
@UseGuards(AuthGuard)
@Controller('predictions')
export class PredictionsController {
  constructor(private readonly predictionsService: PredictionsService) {}

  @Post('drying')
  @ApiOperation({ summary: 'Calculate a rule-based drying prediction' })
  @ApiOkResponse({ type: DryingPredictionResponseDto })
  drying(
    @Req() request: AuthenticatedRequest,
    @Body() body: CreateDryingPredictionRequestDto,
  ): Promise<DryingPredictionResponseDto> {
    return this.predictionsService.calculateDryingPrediction(body, requireUser(request));
  }

  @Post('completion-options')
  @ApiOperation({
    summary: 'Compare washing programs against a desired laundry ready time',
    description:
      'Evaluates the canonical QUICK, NORMAL, ECO and DELICATE durations with one weather forecast and returns every option, including infeasible ones.',
  })
  @ApiOkResponse({
    type: CompletionOptionsResponseDto,
    description: 'All program options. An empty recommendedPrograms array means no program is expected to finish on time.',
  })
  @ApiBadRequestResponse({
    description:
      'The request is invalid, a timestamp lacks Z/UTC offset, or targetReadyAt is not after plannedStartAt.',
  })
  completionOptions(
    @Req() request: AuthenticatedRequest,
    @Body() body: CreateCompletionOptionsRequestDto,
  ): Promise<CompletionOptionsResponseDto> {
    return this.predictionsService.calculateCompletionOptions(body, requireUser(request));
  }
}

function requireUser(request: AuthenticatedRequest) {
  if (!request.user) {
    throw new Error('Authenticated user missing after guard execution');
  }

  return request.user;
}
