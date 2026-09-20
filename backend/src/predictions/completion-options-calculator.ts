import {
  WASHING_PROGRAMS,
  WASHING_PROGRAM_DURATION_MINUTES,
  WashingProgram,
} from '../laundry-loads/dto/laundry-load-types';
import { WeatherSnapshotResponseDto } from '../weather/dto/weather-snapshot-response.dto';
import { CompletionOptionsResponseDto, CompletionProgramOptionResponseDto } from './dto/completion-options-response.dto';
import { DryingPredictionCalculator, DryingCalculationInput, DryingCalculationResult } from './drying-prediction-calculator';
import { ValidCompletionOptionsInput } from './completion-options.validation';

type DryingCalculator = {
  calculate(input: DryingCalculationInput): DryingCalculationResult;
};

export class CompletionOptionsCalculator {
  constructor(private readonly dryingCalculator: DryingCalculator = new DryingPredictionCalculator()) {}

  calculate(
    input: ValidCompletionOptionsInput,
    forecast: WeatherSnapshotResponseDto[],
    generatedAt = new Date(),
  ): CompletionOptionsResponseDto {
    const normalizedForecast = normalizeForecast(forecast);
    if (normalizedForecast.length === 0) {
      throw new Error('Completion options require at least one forecast snapshot');
    }

    const forecastCoverageEndsAt = calculateForecastCoverageEndsAt(normalizedForecast);
    const options = WASHING_PROGRAMS.map((program) =>
      this.calculateProgramOption(input, program, normalizedForecast, forecastCoverageEndsAt),
    );

    return {
      generatedAt: generatedAt.toISOString(),
      plannedStartAt: input.plannedStartAt.toISOString(),
      targetReadyAt: input.targetReadyAt.toISOString(),
      weatherSource: normalizedForecast[0].source,
      isStale: normalizedForecast.some((snapshot) => snapshot.isStale),
      forecastCoverageEndsAt: forecastCoverageEndsAt?.toISOString() ?? null,
      recommendedPrograms: options.filter((option) => option.feasible).map((option) => option.program),
      options,
    };
  }

  private calculateProgramOption(
    input: ValidCompletionOptionsInput,
    program: WashingProgram,
    forecast: WeatherSnapshotResponseDto[],
    forecastCoverageEndsAt: Date | null,
  ): CompletionProgramOptionResponseDto {
    const washingMinutes = WASHING_PROGRAM_DURATION_MINUTES[program];
    const washingEndsAt = addMinutes(input.plannedStartAt, washingMinutes);
    const programForecast = forecastFrom(forecast, washingEndsAt);
    const weather = programForecast[0];
    const calculation = this.dryingCalculator.calculate({
      clothingType: input.clothingType,
      washingProgram: program,
      dryingMethod: input.dryingMethod,
      dryingLocationId: input.dryingLocationId,
      weather,
      forecast: programForecast,
      earliestHangAt: washingEndsAt,
      spinRpm: input.spinRpm,
      loadSize: input.loadSize,
      washerEnergyLabel: input.washerEnergyLabel,
      washerCapacityKg: input.washerCapacityKg,
      waterUsageLiters: input.waterUsageLiters,
    });
    const recommendedHangAt = new Date(calculation.recommendedHangAt);
    const dryingStartsAt =
      recommendedHangAt.getTime() > washingEndsAt.getTime() ? recommendedHangAt : washingEndsAt;
    const estimatedReadyAt = addMinutes(dryingStartsAt, calculation.estimatedDryingMinutes);
    const totalElapsedMinutes = differenceInMinutes(estimatedReadyAt, input.plannedStartAt);
    const marginMinutes = differenceInMinutes(input.targetReadyAt, estimatedReadyAt);

    return {
      program,
      washingMinutes,
      washingEndsAt: washingEndsAt.toISOString(),
      dryingStartsAt: dryingStartsAt.toISOString(),
      estimatedDryingMinutes: calculation.estimatedDryingMinutes,
      estimatedReadyAt: estimatedReadyAt.toISOString(),
      totalElapsedMinutes,
      marginMinutes,
      feasible: marginMinutes >= 0,
      usesForecastExtrapolation:
        forecastCoverageEndsAt === null || estimatedReadyAt.getTime() > forecastCoverageEndsAt.getTime(),
      verdict: calculation.verdict,
      suitabilityScore: calculation.suitabilityScore,
    };
  }
}

function normalizeForecast(forecast: WeatherSnapshotResponseDto[]): WeatherSnapshotResponseDto[] {
  return forecast
    .filter((snapshot) => Number.isFinite(new Date(snapshot.forecastFor).getTime()))
    .sort((left, right) => new Date(left.forecastFor).getTime() - new Date(right.forecastFor).getTime());
}

function forecastFrom(
  forecast: WeatherSnapshotResponseDto[],
  washingEndsAt: Date,
): WeatherSnapshotResponseDto[] {
  const future = forecast.filter(
    (snapshot) => new Date(snapshot.forecastFor).getTime() >= washingEndsAt.getTime(),
  );
  return future.length > 0 ? future : [forecast[forecast.length - 1]];
}

function calculateForecastCoverageEndsAt(forecast: WeatherSnapshotResponseDto[]): Date | null {
  const lastTime = new Date(forecast[forecast.length - 1]?.forecastFor ?? '').getTime();
  if (!Number.isFinite(lastTime)) {
    return null;
  }

  const previousTime = new Date(forecast[forecast.length - 2]?.forecastFor ?? '').getTime();
  const inferredStep =
    Number.isFinite(previousTime) && lastTime > previousTime
      ? lastTime - previousTime
      : DEFAULT_FORECAST_STEP_MILLISECONDS;
  return new Date(lastTime + inferredStep);
}

function addMinutes(instant: Date, minutes: number): Date {
  return new Date(instant.getTime() + minutes * MILLISECONDS_PER_MINUTE);
}

function differenceInMinutes(later: Date, earlier: Date): number {
  return Math.round((later.getTime() - earlier.getTime()) / MILLISECONDS_PER_MINUTE);
}

const MILLISECONDS_PER_MINUTE = 60_000;
const DEFAULT_FORECAST_STEP_MILLISECONDS = 60 * MILLISECONDS_PER_MINUTE;
