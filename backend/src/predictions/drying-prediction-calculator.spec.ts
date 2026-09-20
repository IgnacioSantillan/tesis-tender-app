import { DryingPredictionCalculator } from './drying-prediction-calculator';

describe('DryingPredictionCalculator', () => {
  const calculator = new DryingPredictionCalculator();

  it('returns a good verdict for favorable outdoor weather', () => {
    const prediction = calculator.calculate({
      clothingType: 'LIGHT_CLOTHES',
      washingProgram: 'NORMAL',
      dryingMethod: 'OUTDOOR',
      dryingLocationId: 'OUTDOOR_LINE',
      weather: weather({
        rainProbabilityPercent: 5,
        humidityPercent: 35,
        temperatureCelsius: 27,
        windSpeedKph: 12,
        cloudCoverPercent: 10,
      }),
    });

    expect(prediction.verdict).toBe('GOOD');
    expect(prediction.dryingLocationId).toBe('OUTDOOR_LINE');
    expect(prediction.suitabilityScore).toBeGreaterThanOrEqual(70);
    expect(prediction.reason).toContain('favorable');
  });

  it('applies the rain risk override for outdoor drying', () => {
    const prediction = calculator.calculate({
      clothingType: 'MIXED',
      washingProgram: 'NORMAL',
      dryingMethod: 'OUTDOOR',
      dryingLocationId: 'OUTDOOR_LINE',
      weather: weather({
        rainProbabilityPercent: 80,
        humidityPercent: 40,
        temperatureCelsius: 26,
        windSpeedKph: 14,
        cloudCoverPercent: 20,
      }),
    });

    expect(prediction.verdict).toBe('BAD');
    expect(prediction.reason).toContain('Rain risk');
  });

  it('keeps indoor drying possible when rain risk is high', () => {
    const prediction = calculator.calculate({
      clothingType: 'MIXED',
      washingProgram: 'NORMAL',
      dryingMethod: 'INDOOR',
      dryingLocationId: 'INDOOR',
      weather: weather({
        rainProbabilityPercent: 80,
        humidityPercent: 55,
        temperatureCelsius: 22,
        windSpeedKph: 8,
        cloudCoverPercent: 70,
      }),
    });

    expect(prediction.verdict).not.toBe('BAD');
    expect(prediction.dryingMethod).toBe('INDOOR');
  });

  it('uses drying location factors to estimate different drying durations', () => {
    const outdoor = calculator.calculate({
      clothingType: 'MIXED',
      washingProgram: 'NORMAL',
      dryingMethod: 'OUTDOOR',
      dryingLocationId: 'OUTDOOR_LINE',
      weather: weather({ humidityPercent: 55, windSpeedKph: 18 }),
    });
    const laundryRoom = calculator.calculate({
      clothingType: 'MIXED',
      washingProgram: 'NORMAL',
      dryingMethod: 'INDOOR',
      dryingLocationId: 'LAUNDRY_ROOM',
      weather: weather({ humidityPercent: 55, windSpeedKph: 18 }),
    });

    expect(laundryRoom.estimatedDryingMinutes).toBeGreaterThan(outdoor.estimatedDryingMinutes);
    expect(laundryRoom.reason).toContain('Laundry room');
  });

  it('uses forecast segments to account for worsening drying conditions', () => {
    const stableWeather = weather({ humidityPercent: 45, windSpeedKph: 18 });
    const worseningForecast = [
      stableWeather,
      weather({
        forecastFor: '2026-07-05T16:00:00.000Z',
        humidityPercent: 88,
        temperatureCelsius: 13,
        windSpeedKph: 2,
        cloudCoverPercent: 90,
      }),
      weather({
        forecastFor: '2026-07-05T17:00:00.000Z',
        humidityPercent: 92,
        temperatureCelsius: 12,
        windSpeedKph: 1,
        cloudCoverPercent: 95,
      }),
    ];

    const pointEstimate = calculator.calculate({
      clothingType: 'MIXED',
      washingProgram: 'NORMAL',
      dryingMethod: 'OUTDOOR',
      dryingLocationId: 'PATIO',
      weather: stableWeather,
    });
    const segmentedEstimate = calculator.calculate({
      clothingType: 'MIXED',
      washingProgram: 'NORMAL',
      dryingMethod: 'OUTDOOR',
      dryingLocationId: 'PATIO',
      weather: stableWeather,
      forecast: worseningForecast,
    });

    expect(segmentedEstimate.estimatedDryingMinutes).toBeGreaterThan(pointEstimate.estimatedDryingMinutes);
  });

  it('estimates drying from the recommended hanging window when waiting for better weather', () => {
    const unfavorableWeather = weather({
      forecastFor: '2026-07-05T15:00:00.000Z',
      rainProbabilityPercent: 65,
      humidityPercent: 94,
      temperatureCelsius: 10,
      windSpeedKph: 1,
      cloudCoverPercent: 95,
    });
    const favorableWeather = weather({
      forecastFor: '2026-07-05T16:00:00.000Z',
      rainProbabilityPercent: 5,
      humidityPercent: 35,
      temperatureCelsius: 27,
      windSpeedKph: 14,
      cloudCoverPercent: 10,
    });
    const favorableWeatherLater = weather({
      forecastFor: '2026-07-05T17:00:00.000Z',
      rainProbabilityPercent: 5,
      humidityPercent: 38,
      temperatureCelsius: 26,
      windSpeedKph: 13,
      cloudCoverPercent: 12,
    });

    const delayedPrediction = calculator.calculate({
      clothingType: 'LIGHT_CLOTHES',
      washingProgram: 'QUICK',
      dryingMethod: 'OUTDOOR',
      dryingLocationId: 'PATIO',
      earliestHangAt: '2026-07-05T15:00:00.000Z',
      weather: unfavorableWeather,
      forecast: [unfavorableWeather, favorableWeather, favorableWeatherLater],
    });
    const predictionStartingInRecommendedWindow = calculator.calculate({
      clothingType: 'LIGHT_CLOTHES',
      washingProgram: 'QUICK',
      dryingMethod: 'OUTDOOR',
      dryingLocationId: 'PATIO',
      earliestHangAt: '2026-07-05T16:00:00.000Z',
      weather: favorableWeather,
      forecast: [favorableWeather, favorableWeatherLater],
    });

    expect(delayedPrediction.recommendedHangAt).toBe('2026-07-05T16:00:00.000Z');
    expect(delayedPrediction.estimatedDryingMinutes).toBe(
      predictionStartingInRecommendedWindow.estimatedDryingMinutes,
    );
    expect(delayedPrediction.estimatedPickupAt).toBe(
      predictionStartingInRecommendedWindow.estimatedPickupAt,
    );
  });

  it('does not return a pickup time in the past when the current forecast slot already started', () => {
    jest.useFakeTimers().setSystemTime(new Date('2026-07-05T15:25:00.000Z'));

    const prediction = calculator.calculate({
      clothingType: 'LIGHT_CLOTHES',
      washingProgram: 'QUICK',
      dryingMethod: 'OUTDOOR',
      dryingLocationId: 'PATIO',
      weather: weather({
        forecastFor: '2026-07-05T15:00:00.000Z',
        humidityPercent: 35,
        temperatureCelsius: 27,
        windSpeedKph: 14,
      }),
      forecast: [
        weather({
          forecastFor: '2026-07-05T15:00:00.000Z',
          humidityPercent: 35,
          temperatureCelsius: 27,
          windSpeedKph: 14,
        }),
        weather({
          forecastFor: '2026-07-05T16:00:00.000Z',
          humidityPercent: 40,
          temperatureCelsius: 25,
          windSpeedKph: 12,
        }),
      ],
    });

    expect(prediction.recommendedHangAt).toBe('2026-07-05T15:25:00.000Z');
    expect(prediction.recommendedHangWindowStart).toBe('2026-07-05T15:25:00.000Z');
    expect(new Date(prediction.estimatedPickupAt).getTime()).toBeGreaterThan(Date.now());

    jest.useRealTimers();
  });

  it('reduces estimated drying time for high spin and small loads', () => {
    const lowSpinLargeLoad = calculator.calculate({
      clothingType: 'MIXED',
      washingProgram: 'NORMAL',
      dryingMethod: 'OUTDOOR',
      dryingLocationId: 'PATIO',
      spinRpm: 600,
      loadSize: 'LARGE',
      weather: weather({ humidityPercent: 55 }),
    });
    const highSpinSmallLoad = calculator.calculate({
      clothingType: 'MIXED',
      washingProgram: 'NORMAL',
      dryingMethod: 'OUTDOOR',
      dryingLocationId: 'PATIO',
      spinRpm: 1400,
      loadSize: 'SMALL',
      weather: weather({ humidityPercent: 55 }),
    });

    expect(highSpinSmallLoad.estimatedDryingMinutes).toBeLessThan(lowSpinLargeLoad.estimatedDryingMinutes);
    expect(highSpinSmallLoad.reason).toContain('High spin speed');
    expect(lowSpinLargeLoad.reason).toContain('Low spin speed');
  });

  it('estimates relative washing cost from program, efficiency and load size', () => {
    const efficientSmallEco = calculator.calculate({
      clothingType: 'MIXED',
      washingProgram: 'ECO',
      dryingMethod: 'OUTDOOR',
      dryingLocationId: 'PATIO',
      loadSize: 'SMALL',
      washerEnergyLabel: 'A+++',
      waterUsageLiters: 40,
      weather: weather({}),
    });
    const inefficientLargeNormal = calculator.calculate({
      clothingType: 'MIXED',
      washingProgram: 'NORMAL',
      dryingMethod: 'OUTDOOR',
      dryingLocationId: 'PATIO',
      loadSize: 'LARGE',
      washerEnergyLabel: 'G',
      waterUsageLiters: 60,
      weather: weather({}),
    });

    expect(efficientSmallEco.estimatedCost.level).toBe('LOW');
    expect(efficientSmallEco.estimatedCost.confidence).toBe('HIGH');
    expect(inefficientLargeNormal.estimatedCost.level).toBe('HIGH');
    expect(inefficientLargeNormal.estimatedCost.estimatedEnergyKwh).toBeGreaterThan(
      efficientSmallEco.estimatedCost.estimatedEnergyKwh ?? 0,
    );
    expect(inefficientLargeNormal.estimatedCost.amount).toBeNull();
    expect(inefficientLargeNormal.estimatedCost.currency).toBeNull();
  });

  it('keeps cost estimation low confidence when washer metadata is missing', () => {
    const prediction = calculator.calculate({
      clothingType: 'MIXED',
      washingProgram: 'NORMAL',
      dryingMethod: 'OUTDOOR',
      dryingLocationId: 'PATIO',
      weather: weather({}),
    });

    expect(prediction.estimatedCost.level).toBe('MEDIUM');
    expect(prediction.estimatedCost.confidence).toBe('LOW');
    expect(prediction.estimatedCost.estimatedEnergyKwh).not.toBeNull();
    expect(prediction.reason).toContain('confidence low');
  });
});

function weather(overrides: Partial<ReturnType<typeof weatherBase>>) {
  return {
    ...weatherBase(),
    ...overrides,
  };
}

function weatherBase() {
  return {
    location: { id: 'home', label: 'Home patio', latitude: null, longitude: null },
    source: 'MOCK' as const,
    capturedAt: '2026-07-05T14:00:00.000Z',
    forecastFor: '2026-07-05T15:00:00.000Z',
    condition: 'CLEAR' as const,
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
