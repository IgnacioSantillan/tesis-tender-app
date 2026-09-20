import { WASHING_PROGRAMS } from '../laundry-loads/dto/laundry-load-types';
import { WeatherSnapshotResponseDto } from '../weather/dto/weather-snapshot-response.dto';
import { CompletionOptionsCalculator } from './completion-options-calculator';
import {
  DryingCalculationInput,
  DryingCalculationResult,
  DryingPredictionCalculator,
} from './drying-prediction-calculator';
import { ValidCompletionOptionsInput } from './completion-options.validation';

describe('CompletionOptionsCalculator', () => {
  it('treats an exact target boundary as feasible', () => {
    const calculator = new CompletionOptionsCalculator(fixedDryingCalculator(60));
    const result = calculator.calculate(
      input({
        plannedStartAt: new Date('2026-07-25T20:00:00.000Z'),
        targetReadyAt: new Date('2026-07-25T21:30:00.000Z'),
      }),
      hourlyForecast(),
      new Date('2026-07-25T19:59:00.000Z'),
    );

    expect(result.options[0]).toMatchObject({
      program: 'QUICK',
      washingMinutes: 30,
      washingEndsAt: '2026-07-25T20:30:00.000Z',
      dryingStartsAt: '2026-07-25T20:30:00.000Z',
      estimatedReadyAt: '2026-07-25T21:30:00.000Z',
      totalElapsedMinutes: 90,
      marginMinutes: 0,
      feasible: true,
    });
    expect(result.recommendedPrograms).toEqual(['QUICK']);
  });

  it('returns 200-compatible results with no recommendation when no program arrives on time', () => {
    const calculator = new CompletionOptionsCalculator(fixedDryingCalculator(60));
    const result = calculator.calculate(
      input({
        plannedStartAt: new Date('2026-07-25T20:00:00.000Z'),
        targetReadyAt: new Date('2026-07-25T20:45:00.000Z'),
      }),
      hourlyForecast(),
    );

    expect(result.recommendedPrograms).toEqual([]);
    expect(result.options).toHaveLength(4);
    expect(result.options.every((option) => !option.feasible && option.marginMinutes < 0)).toBe(true);
  });

  it('preserves the real instant across midnight and explicit offsets', () => {
    const calculator = new CompletionOptionsCalculator(fixedDryingCalculator(60));
    const result = calculator.calculate(
      input({
        plannedStartAt: new Date('2026-07-25T20:00:00-03:00'),
        targetReadyAt: new Date('2026-07-26T01:00:00-03:00'),
      }),
      hourlyForecast('2026-07-25T23:00:00.000Z'),
    );

    expect(result.plannedStartAt).toBe('2026-07-25T23:00:00.000Z');
    expect(result.targetReadyAt).toBe('2026-07-26T04:00:00.000Z');
    expect(result.options[0].washingEndsAt).toBe('2026-07-25T23:30:00.000Z');
    expect(result.options[0].estimatedReadyAt).toBe('2026-07-26T00:30:00.000Z');
    expect(result.options[0].marginMinutes).toBe(210);
  });

  it('uses forecast snapshots at or after each program washing end', () => {
    const calculate = jest.fn((calculationInput: DryingCalculationInput) =>
      dryingResult(calculationInput, 60),
    );
    const calculator = new CompletionOptionsCalculator({ calculate });
    const forecast = [
      snapshot('2026-07-25T20:00:00.000Z'),
      snapshot('2026-07-25T20:30:00.000Z'),
      snapshot('2026-07-25T21:00:00.000Z'),
      snapshot('2026-07-25T21:30:00.000Z'),
    ];

    calculator.calculate(
      input({
        plannedStartAt: new Date('2026-07-25T20:00:00.000Z'),
        targetReadyAt: new Date('2026-07-26T01:00:00.000Z'),
      }),
      forecast,
    );

    expect(calculate).toHaveBeenCalledTimes(4);
    const forecastStarts = calculate.mock.calls.map(
      ([calculationInput]) => calculationInput.forecast?.[0].forecastFor,
    );
    expect(forecastStarts).toEqual([
      '2026-07-25T20:30:00.000Z',
      '2026-07-25T21:00:00.000Z',
      '2026-07-25T21:30:00.000Z',
      '2026-07-25T21:00:00.000Z',
    ]);
    expect(calculate.mock.calls.map(([calculationInput]) => calculationInput.earliestHangAt)).toEqual([
      new Date('2026-07-25T20:30:00.000Z'),
      new Date('2026-07-25T21:00:00.000Z'),
      new Date('2026-07-25T21:30:00.000Z'),
      new Date('2026-07-25T20:45:00.000Z'),
    ]);
  });

  it('includes the wait for the recommended hanging window in total elapsed time', () => {
    const calculator = new CompletionOptionsCalculator(new DryingPredictionCalculator());
    const result = calculator.calculate(
      input({
        clothingType: 'LIGHT_CLOTHES',
        plannedStartAt: new Date('2026-07-25T15:00:00.000Z'),
        targetReadyAt: new Date('2026-07-25T20:00:00.000Z'),
      }),
      [
        snapshot('2026-07-25T15:00:00.000Z'),
        snapshot('2026-07-25T16:00:00.000Z'),
        snapshot('2026-07-25T17:00:00.000Z'),
      ],
    );

    const quick = result.options.find((option) => option.program === 'QUICK');
    expect(quick).toBeDefined();
    expect(quick?.washingEndsAt).toBe('2026-07-25T15:30:00.000Z');
    expect(quick?.dryingStartsAt).toBe('2026-07-25T16:00:00.000Z');
    expect(quick?.totalElapsedMinutes).toBe(
      30 + 30 + (quick?.estimatedDryingMinutes ?? 0),
    );
  });

  it('keeps the four canonical programs in deterministic order', () => {
    const calculator = new CompletionOptionsCalculator(fixedDryingCalculator(60));
    const result = calculator.calculate(input(), hourlyForecast());

    expect(result.options.map((option) => option.program)).toEqual(WASHING_PROGRAMS);
    expect(result.options.map((option) => option.washingMinutes)).toEqual([30, 60, 90, 45]);
  });
});

function fixedDryingCalculator(estimatedDryingMinutes: number) {
  return {
    calculate: (calculationInput: DryingCalculationInput) =>
      dryingResult(calculationInput, estimatedDryingMinutes),
  };
}

function dryingResult(
  calculationInput: DryingCalculationInput,
  estimatedDryingMinutes: number,
): DryingCalculationResult {
  const earliestHangAt = new Date(calculationInput.earliestHangAt as Date);
  const pickupAt = new Date(earliestHangAt.getTime() + estimatedDryingMinutes * 60_000);

  return {
    verdict: 'GOOD',
    dryingMethod: calculationInput.dryingMethod,
    dryingLocationId: calculationInput.dryingLocationId,
    dryingLocationLabel: 'Patio',
    estimatedDryingMinutes,
    recommendedHangAt: earliestHangAt.toISOString(),
    recommendedHangWindowStart: earliestHangAt.toISOString(),
    recommendedHangWindowEnd: new Date(earliestHangAt.getTime() + 60 * 60_000).toISOString(),
    estimatedPickupAt: pickupAt.toISOString(),
    suitabilityScore: 82,
    reason: 'Controlled test result.',
    estimatedCost: {
      amount: null,
      currency: null,
      level: 'MEDIUM',
      confidence: 'LOW',
      estimatedEnergyKwh: 0.9,
      estimatedWaterLiters: 52,
    },
    hourlySlots: [],
  };
}

function input(
  overrides: Partial<ValidCompletionOptionsInput> = {},
): ValidCompletionOptionsInput {
  return {
    laundryLoadId: 'load-1',
    clothingType: 'MIXED',
    dryingMethod: 'OUTDOOR',
    locationId: 'home',
    dryingLocationId: 'PATIO',
    spinRpm: 1200,
    loadSize: 'MEDIUM',
    washerEnergyLabel: 'A',
    washerCapacityKg: 7,
    waterUsageLiters: 45,
    plannedStartAt: new Date('2026-07-25T20:00:00.000Z'),
    targetReadyAt: new Date('2026-07-26T01:00:00.000Z'),
    ...overrides,
  };
}

function hourlyForecast(start = '2026-07-25T20:00:00.000Z'): WeatherSnapshotResponseDto[] {
  const startTime = new Date(start).getTime();
  return Array.from({ length: 6 }, (_, index) =>
    snapshot(new Date(startTime + index * 60 * 60_000).toISOString()),
  );
}

function snapshot(forecastFor: string): WeatherSnapshotResponseDto {
  return {
    location: { id: 'home', label: 'Home patio', latitude: -24.78, longitude: -65.41 },
    source: 'OPEN_METEO',
    capturedAt: '2026-07-25T19:55:00.000Z',
    forecastFor,
    condition: 'CLEAR',
    temperatureCelsius: 24,
    humidityPercent: 48,
    windSpeedKph: 18,
    rainProbabilityPercent: 8,
    precipitationMillimeters: 0,
    cloudCoverPercent: 20,
    forecastLeadMinutes: 60,
    isStale: false,
  };
}
