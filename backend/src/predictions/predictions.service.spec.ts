import { PredictionsService } from './predictions.service';

describe('PredictionsService', () => {
  afterEach(() => {
    jest.useRealTimers();
  });

  it('calculates a drying prediction using normalized weather', async () => {
    const weather = weatherSnapshot();
    const weatherService = {
      getCurrentWeather: jest.fn().mockResolvedValue(weather),
      getForecast: jest.fn().mockResolvedValue([weather]),
    };
    const energyMetadataDataSource = {
      resolveForLaundryLoad: jest.fn().mockResolvedValue({
        spinRpm: null,
        loadSize: null,
        washerEnergyLabel: null,
        washerCapacityKg: null,
        waterUsageLiters: null,
      }),
    };
    const service = new PredictionsService(weatherService as never, energyMetadataDataSource as never);

    await expect(
      service.calculateDryingPrediction(
        {
          laundryLoadId: 'load-1',
          clothingType: 'MIXED',
          washingProgram: 'NORMAL',
          dryingMethod: 'OUTDOOR',
          locationId: 'home',
          dryingLocationId: 'BALCONY',
          spinRpm: 1200,
          loadSize: 'MEDIUM',
          washerEnergyLabel: 'A',
          washerCapacityKg: 7,
          waterUsageLiters: 45,
        },
        { id: 'user-1', email: 'user@example.com' },
      ),
    ).resolves.toMatchObject({
      verdict: 'GOOD',
      dryingMethod: 'OUTDOOR',
      dryingLocationId: 'BALCONY',
      dryingLocationLabel: 'Balcony',
      estimatedCost: {
        amount: null,
        currency: null,
        level: 'MEDIUM',
        confidence: 'HIGH',
      },
      weatherSnapshot: weather,
    });
    expect(weatherService.getForecast).toHaveBeenCalledWith('home', {
      id: 'user-1',
      email: 'user@example.com',
    });
    expect(weatherService.getCurrentWeather).not.toHaveBeenCalled();
    expect(energyMetadataDataSource.resolveForLaundryLoad).toHaveBeenCalledWith('user-1', 'load-1');
  });

  it('enriches dashboard predictions with washer metadata when request energy fields are missing', async () => {
    const weather = weatherSnapshot();
    const weatherService = {
      getCurrentWeather: jest.fn().mockResolvedValue(weather),
      getForecast: jest.fn().mockResolvedValue([weather]),
    };
    const energyMetadataDataSource = {
      resolveForLaundryLoad: jest.fn().mockResolvedValue({
        spinRpm: 1400,
        loadSize: 'SMALL',
        washerEnergyLabel: 'A+++',
        washerCapacityKg: 5,
        waterUsageLiters: 38,
      }),
    };
    const service = new PredictionsService(weatherService as never, energyMetadataDataSource as never);

    await expect(
      service.calculateDryingPrediction(
        {
          laundryLoadId: 'load-2',
          clothingType: 'MIXED',
          washingProgram: 'NORMAL',
          dryingMethod: 'OUTDOOR',
          locationId: 'home',
          dryingLocationId: 'PATIO',
        },
        { id: 'user-1', email: 'user@example.com' },
      ),
    ).resolves.toMatchObject({
      estimatedCost: {
        confidence: 'HIGH',
        level: 'LOW',
        estimatedWaterLiters: 32.3,
      },
    });
  });

  it('keeps explicit request energy fields over inferred washer metadata', async () => {
    const weather = weatherSnapshot();
    const weatherService = {
      getCurrentWeather: jest.fn().mockResolvedValue(weather),
      getForecast: jest.fn().mockResolvedValue([weather]),
    };
    const energyMetadataDataSource = {
      resolveForLaundryLoad: jest.fn().mockResolvedValue({
        spinRpm: 1400,
        loadSize: 'LARGE',
        washerEnergyLabel: 'A+++',
        washerCapacityKg: 8,
        waterUsageLiters: 38,
      }),
    };
    const service = new PredictionsService(weatherService as never, energyMetadataDataSource as never);

    await expect(
      service.calculateDryingPrediction(
        {
          laundryLoadId: 'load-3',
          clothingType: 'MIXED',
          washingProgram: 'NORMAL',
          dryingMethod: 'OUTDOOR',
          locationId: 'home',
          dryingLocationId: 'PATIO',
          spinRpm: 800,
          loadSize: 'LARGE',
          washerEnergyLabel: 'G',
          washerCapacityKg: 8,
          waterUsageLiters: 70,
        },
        { id: 'user-1', email: 'user@example.com' },
      ),
    ).resolves.toMatchObject({
      estimatedCost: {
        confidence: 'HIGH',
        level: 'HIGH',
        estimatedWaterLiters: 82.6,
      },
    });
  });

  it('evaluates all completion options with one forecast request', async () => {
    jest.useFakeTimers().setSystemTime(new Date('2026-07-25T20:00:00.000Z'));
    const forecast = [
      weatherSnapshot({ forecastFor: '2026-07-25T20:00:00.000Z' }),
      weatherSnapshot({ forecastFor: '2026-07-25T21:00:00.000Z' }),
      weatherSnapshot({ forecastFor: '2026-07-25T22:00:00.000Z' }),
      weatherSnapshot({ forecastFor: '2026-07-25T23:00:00.000Z' }),
    ];
    const weatherService = {
      getCurrentWeather: jest.fn(),
      getForecast: jest.fn().mockResolvedValue(forecast),
    };
    const energyMetadataDataSource = {
      resolveForLaundryLoad: jest.fn().mockResolvedValue({
        spinRpm: 1200,
        loadSize: 'MEDIUM',
        washerEnergyLabel: 'A',
        washerCapacityKg: 7,
        waterUsageLiters: 45,
      }),
    };
    const service = new PredictionsService(weatherService as never, energyMetadataDataSource as never);

    const result = await service.calculateCompletionOptions(
      {
        laundryLoadId: 'load-1',
        clothingType: 'MIXED',
        dryingMethod: 'OUTDOOR',
        locationId: 'home',
        plannedStartAt: '2026-07-25T20:00:00.000Z',
        targetReadyAt: '2026-07-26T02:00:00.000Z',
      },
      { id: 'user-1', email: 'user@example.com' },
    );

    expect(result.options).toHaveLength(4);
    expect(result.options.map((option) => option.program)).toEqual([
      'QUICK',
      'NORMAL',
      'ECO',
      'DELICATE',
    ]);
    expect(weatherService.getForecast).toHaveBeenCalledTimes(1);
    expect(weatherService.getForecast).toHaveBeenCalledWith('home', {
      id: 'user-1',
      email: 'user@example.com',
    });
    expect(weatherService.getCurrentWeather).not.toHaveBeenCalled();
    expect(energyMetadataDataSource.resolveForLaundryLoad).toHaveBeenCalledTimes(1);

  });
});

function weatherSnapshot(overrides: Record<string, unknown> = {}) {
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
    ...overrides,
  };
}
