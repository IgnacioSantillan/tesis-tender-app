import type { DryingLocationId } from '../drying-locations/drying-location-types';
import type { LoadSize, SpinSpeedRpm } from '../energy/energy-prediction-types';
import type { ClothingType, WashingProgram } from '../laundry-loads/dto/laundry-load-types';
import type { WeatherSnapshotResponseDto } from '../weather/dto/weather-snapshot-response.dto';
import {
  DryingCalculationInput,
  DryingPredictionCalculator,
} from './drying-prediction-calculator';
import type { DryingMethod, DryingVerdict } from './dto/drying-prediction-types';

interface CoreValidationScenario {
  name: string;
  input: Omit<DryingCalculationInput, 'weather'>;
  weather: Partial<WeatherSnapshotResponseDto>;
  expected: {
    suitabilityScore: number;
    verdict: DryingVerdict;
    estimatedDryingMinutes: number;
  };
}

const FAVORABLE_WEATHER: Partial<WeatherSnapshotResponseDto> = {
  rainProbabilityPercent: 5,
  humidityPercent: 35,
  temperatureCelsius: 27,
  windSpeedKph: 12,
  cloudCoverPercent: 10,
};

const RAINY_WEATHER: Partial<WeatherSnapshotResponseDto> = {
  rainProbabilityPercent: 80,
  humidityPercent: 40,
  temperatureCelsius: 26,
  windSpeedKph: 14,
  cloudCoverPercent: 20,
};

const CORE_SCENARIOS: CoreValidationScenario[] = [
  {
    name: 'favorable outdoor conditions',
    input: predictionInput({
      clothingType: 'LIGHT_CLOTHES',
      dryingMethod: 'OUTDOOR',
      dryingLocationId: 'OUTDOOR_LINE',
    }),
    weather: FAVORABLE_WEATHER,
    expected: {
      suitabilityScore: 92,
      verdict: 'GOOD',
      estimatedDryingMinutes: 100,
    },
  },
  {
    name: 'high humidity degrades score and drying time',
    input: predictionInput({
      clothingType: 'LIGHT_CLOTHES',
      dryingMethod: 'OUTDOOR',
      dryingLocationId: 'OUTDOOR_LINE',
    }),
    weather: {
      ...FAVORABLE_WEATHER,
      humidityPercent: 90,
      windSpeedKph: 8,
      cloudCoverPercent: 20,
    },
    expected: {
      suitabilityScore: 64,
      verdict: 'CAUTION',
      estimatedDryingMinutes: 158,
    },
  },
  {
    name: 'outdoor rain applies the negative verdict override',
    input: predictionInput({
      clothingType: 'MIXED',
      dryingMethod: 'OUTDOOR',
      dryingLocationId: 'OUTDOOR_LINE',
    }),
    weather: RAINY_WEATHER,
    expected: {
      suitabilityScore: 17,
      verdict: 'BAD',
      estimatedDryingMinutes: 230,
    },
  },
  {
    name: 'indoor rain remains viable without the outdoor override',
    input: predictionInput({
      clothingType: 'MIXED',
      dryingMethod: 'INDOOR',
      dryingLocationId: 'INDOOR',
    }),
    weather: RAINY_WEATHER,
    expected: {
      suitabilityScore: 88,
      verdict: 'GOOD',
      estimatedDryingMinutes: 437,
    },
  },
  {
    name: 'heavy large load with low spin takes longer',
    input: predictionInput({
      clothingType: 'HEAVY_CLOTHES',
      dryingMethod: 'OUTDOOR',
      dryingLocationId: 'OUTDOOR_LINE',
      spinRpm: 600,
      loadSize: 'LARGE',
    }),
    weather: FAVORABLE_WEATHER,
    expected: {
      suitabilityScore: 92,
      verdict: 'GOOD',
      estimatedDryingMinutes: 303,
    },
  },
  {
    name: 'light small load with high spin dries faster',
    input: predictionInput({
      clothingType: 'LIGHT_CLOTHES',
      dryingMethod: 'OUTDOOR',
      dryingLocationId: 'OUTDOOR_LINE',
      spinRpm: 1400,
      loadSize: 'SMALL',
    }),
    weather: FAVORABLE_WEATHER,
    expected: {
      suitabilityScore: 92,
      verdict: 'GOOD',
      estimatedDryingMinutes: 76,
    },
  },
];

describe('DryingPredictionCalculator core validation', () => {
  const calculator = new DryingPredictionCalculator();

  beforeAll(() => {
    jest.useFakeTimers().setSystemTime(new Date('2026-07-05T14:00:00.000Z'));
  });

  afterAll(() => {
    jest.useRealTimers();
  });

  it.each(CORE_SCENARIOS)(
    '$name',
    ({ input, weather: weatherOverrides, expected }) => {
      const result = calculator.calculate({
        ...input,
        weather: weather(weatherOverrides),
      });

      expect(result.suitabilityScore).toBe(expected.suitabilityScore);
      expect(result.verdict).toBe(expected.verdict);
      expect(result.estimatedDryingMinutes).toBe(expected.estimatedDryingMinutes);
    },
  );

  it('preserves monotonic sensitivity for humidity, clothing, spin and load size', () => {
    const favorable = calculate(calculator, {
      clothingType: 'LIGHT_CLOTHES',
      spinRpm: 1200,
      loadSize: 'MEDIUM',
      weatherOverrides: FAVORABLE_WEATHER,
    });
    const humid = calculate(calculator, {
      clothingType: 'LIGHT_CLOTHES',
      spinRpm: 1200,
      loadSize: 'MEDIUM',
      weatherOverrides: {
        ...FAVORABLE_WEATHER,
        humidityPercent: 90,
      },
    });
    const heavy = calculate(calculator, {
      clothingType: 'HEAVY_CLOTHES',
      spinRpm: 1200,
      loadSize: 'MEDIUM',
      weatherOverrides: FAVORABLE_WEATHER,
    });
    const lowSpin = calculate(calculator, {
      clothingType: 'MIXED',
      spinRpm: 600,
      loadSize: 'MEDIUM',
      weatherOverrides: FAVORABLE_WEATHER,
    });
    const highSpin = calculate(calculator, {
      clothingType: 'MIXED',
      spinRpm: 1400,
      loadSize: 'MEDIUM',
      weatherOverrides: FAVORABLE_WEATHER,
    });
    const smallLoad = calculate(calculator, {
      clothingType: 'MIXED',
      spinRpm: 1200,
      loadSize: 'SMALL',
      weatherOverrides: FAVORABLE_WEATHER,
    });
    const largeLoad = calculate(calculator, {
      clothingType: 'MIXED',
      spinRpm: 1200,
      loadSize: 'LARGE',
      weatherOverrides: FAVORABLE_WEATHER,
    });

    expect(humid.suitabilityScore).toBeLessThan(favorable.suitabilityScore);
    expect(humid.estimatedDryingMinutes).toBeGreaterThan(favorable.estimatedDryingMinutes);
    expect(heavy.estimatedDryingMinutes).toBeGreaterThan(favorable.estimatedDryingMinutes);
    expect(lowSpin.estimatedDryingMinutes).toBeGreaterThan(highSpin.estimatedDryingMinutes);
    expect(largeLoad.estimatedDryingMinutes).toBeGreaterThan(smallLoad.estimatedDryingMinutes);
  });
});

function predictionInput(
  overrides: Partial<{
    clothingType: ClothingType;
    washingProgram: WashingProgram;
    dryingMethod: DryingMethod;
    dryingLocationId: DryingLocationId;
    spinRpm: SpinSpeedRpm;
    loadSize: LoadSize;
  }>,
): Omit<DryingCalculationInput, 'weather'> {
  return {
    clothingType: 'MIXED',
    washingProgram: 'NORMAL',
    dryingMethod: 'OUTDOOR',
    dryingLocationId: 'OUTDOOR_LINE',
    ...overrides,
  };
}

function calculate(
  calculator: DryingPredictionCalculator,
  input: {
    clothingType: ClothingType;
    spinRpm: SpinSpeedRpm;
    loadSize: LoadSize;
    weatherOverrides: Partial<WeatherSnapshotResponseDto>;
  },
) {
  return calculator.calculate({
    clothingType: input.clothingType,
    washingProgram: 'NORMAL',
    dryingMethod: 'OUTDOOR',
    dryingLocationId: 'OUTDOOR_LINE',
    spinRpm: input.spinRpm,
    loadSize: input.loadSize,
    weather: weather(input.weatherOverrides),
  });
}

function weather(overrides: Partial<WeatherSnapshotResponseDto>): WeatherSnapshotResponseDto {
  return {
    location: {
      id: 'home',
      label: 'Home outdoor line',
      latitude: null,
      longitude: null,
    },
    source: 'MOCK',
    capturedAt: '2026-07-05T14:00:00.000Z',
    forecastFor: '2026-07-05T15:00:00.000Z',
    condition: 'CLEAR',
    temperatureCelsius: 24,
    humidityPercent: 48,
    windSpeedKph: 18,
    rainProbabilityPercent: 8,
    precipitationMillimeters: 0,
    cloudCoverPercent: 20,
    forecastLeadMinutes: 60,
    isStale: false,
    ...overrides,
  };
}
