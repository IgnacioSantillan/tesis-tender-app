import { Injectable, ServiceUnavailableException } from '@nestjs/common';
import { AuthenticatedUser } from '../auth/authenticated-user';
import { WeatherService } from '../weather/weather.service';
import { CompletionOptionsCalculator } from './completion-options-calculator';
import {
  validateCompletionOptionsInput,
  ValidCompletionOptionsInput,
} from './completion-options.validation';
import { CompletionOptionsResponseDto } from './dto/completion-options-response.dto';
import { CreateCompletionOptionsRequestDto } from './dto/create-completion-options-request.dto';
import { CreateDryingPredictionRequestDto } from './dto/create-drying-prediction-request.dto';
import { DryingPredictionResponseDto } from './dto/drying-prediction-response.dto';
import { DryingPredictionCalculator } from './drying-prediction-calculator';
import { PredictionEnergyMetadataDataSource } from './prediction-energy-metadata.data-source';
import { validateDryingPredictionInput, ValidDryingPredictionInput } from './drying-prediction.validation';

@Injectable()
export class PredictionsService {
  private readonly calculator = new DryingPredictionCalculator();
  private readonly completionOptionsCalculator = new CompletionOptionsCalculator(this.calculator);

  constructor(
    private readonly weatherService: WeatherService,
    private readonly energyMetadataDataSource: PredictionEnergyMetadataDataSource,
  ) {}

  async calculateDryingPrediction(
    body: CreateDryingPredictionRequestDto,
    user?: AuthenticatedUser,
  ): Promise<DryingPredictionResponseDto> {
    const input = validateDryingPredictionInput(body);
    const enrichedInput = await this.enrichEnergyInputs(input, user);
    const forecast = await this.weatherService.getForecast(input.locationId, user);
    const weather = forecast[0] ?? (await this.weatherService.getCurrentWeather(input.locationId, user));
    const calculation = this.calculator.calculate({
      clothingType: enrichedInput.clothingType,
      washingProgram: enrichedInput.washingProgram,
      dryingMethod: enrichedInput.dryingMethod,
      dryingLocationId: enrichedInput.dryingLocationId,
      weather,
      forecast,
      spinRpm: enrichedInput.spinRpm,
      loadSize: enrichedInput.loadSize,
      washerEnergyLabel: enrichedInput.washerEnergyLabel,
      washerCapacityKg: enrichedInput.washerCapacityKg,
      waterUsageLiters: enrichedInput.waterUsageLiters,
    });

    return {
      ...calculation,
      weatherSnapshot: weather,
    };
  }

  async calculateCompletionOptions(
    body: CreateCompletionOptionsRequestDto,
    user: AuthenticatedUser,
  ): Promise<CompletionOptionsResponseDto> {
    const input = validateCompletionOptionsInput(body);
    const enrichedInput = await this.enrichCompletionEnergyInputs(input, user);
    const forecast = await this.weatherService.getForecast(input.locationId, user);

    if (forecast.length === 0) {
      throw new ServiceUnavailableException('Weather forecast is unavailable');
    }

    return this.completionOptionsCalculator.calculate(enrichedInput, forecast);
  }

  private async enrichEnergyInputs(
    input: ValidDryingPredictionInput,
    user?: AuthenticatedUser,
  ): Promise<ValidDryingPredictionInput> {
    if (!user) {
      return input;
    }

    const metadata = await this.energyMetadataDataSource.resolveForLaundryLoad(user.id, input.laundryLoadId);

    return {
      ...input,
      spinRpm: input.spinRpm ?? metadata.spinRpm,
      loadSize: input.loadSize ?? metadata.loadSize,
      washerEnergyLabel: input.washerEnergyLabel ?? metadata.washerEnergyLabel,
      washerCapacityKg: input.washerCapacityKg ?? metadata.washerCapacityKg,
      waterUsageLiters: input.waterUsageLiters ?? metadata.waterUsageLiters,
    };
  }

  private async enrichCompletionEnergyInputs(
    input: ValidCompletionOptionsInput,
    user: AuthenticatedUser,
  ): Promise<ValidCompletionOptionsInput> {
    const metadata = await this.energyMetadataDataSource.resolveForLaundryLoad(user.id, input.laundryLoadId);

    return {
      ...input,
      spinRpm: input.spinRpm ?? metadata.spinRpm,
      loadSize: input.loadSize ?? metadata.loadSize,
      washerEnergyLabel: input.washerEnergyLabel ?? metadata.washerEnergyLabel,
      washerCapacityKg: input.washerCapacityKg ?? metadata.washerCapacityKg,
      waterUsageLiters: input.waterUsageLiters ?? metadata.waterUsageLiters,
    };
  }
}
