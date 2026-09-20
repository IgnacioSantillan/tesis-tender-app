import { InternalServerErrorException, NotFoundException } from '@nestjs/common';
import { LaundryLoadRow } from './laundry-loads.mapper';
import { LaundryLoadsService } from './laundry-loads.service';
import { LaundryLoadsSupabaseDataSource } from './laundry-loads.supabase-data-source';

const user = {
  id: 'user-1',
  email: 'user@example.com',
};

const loadRow: LaundryLoadRow = {
  id: 'load-1',
  washer_id: 'washer-1',
  clothing_type: 'MIXED',
  washing_program: 'NORMAL',
  status: 'PLANNED',
  location_id: 'home',
  drying_location_id: 'PATIO',
  spin_rpm: 1200,
  load_size: 'MEDIUM',
  estimated_washing_energy_kwh: null,
  estimated_washing_water_liters: null,
  estimated_washing_cost_amount: null,
  estimated_washing_cost_currency: null,
  estimated_washing_cost_level: null,
  estimated_washing_cost_confidence: null,
  created_at: '2026-07-05T14:00:00Z',
  started_at: null,
  drying_started_at: null,
  drying_estimated_minutes_at_start: null,
  drying_estimated_pickup_at: null,
  completed_at: null,
};

const loadResponse = {
  id: 'load-1',
  washerId: 'washer-1',
  clothingType: 'MIXED',
  washingProgram: 'NORMAL',
  status: 'PLANNED',
  locationId: 'home',
  dryingLocationId: 'PATIO',
  spinRpm: 1200,
  loadSize: 'MEDIUM',
  estimatedWashingEnergyKwh: null,
  estimatedWashingWaterLiters: null,
  estimatedWashingCostAmount: null,
  estimatedWashingCostCurrency: null,
  estimatedWashingCostLevel: null,
  estimatedWashingCostConfidence: null,
  createdAt: '2026-07-05T14:00:00.000Z',
  startedAt: null,
  dryingStartedAt: null,
  dryingEstimatedMinutesAtStart: null,
  dryingEstimatedPickupAt: null,
  completedAt: null,
  prediction: null,
};

describe('LaundryLoadsService', () => {
  it('lists laundry loads owned by the authenticated user', async () => {
    const dataSource = createDataSourceMock({ listResult: { data: [loadRow], error: null } });
    const notificationsService = createNotificationsServiceMock();
    const service = new LaundryLoadsService(dataSource as unknown as LaundryLoadsSupabaseDataSource, notificationsService as never, createPredictionsServiceMock() as never);

    await expect(service.listLaundryLoads(user)).resolves.toEqual([loadResponse]);
    expect(dataSource.listByUser).toHaveBeenCalledWith('user-1');
  });

  it('normalizes legacy lower-case Supabase values for Android clients', async () => {
    const legacyRow = {
      ...loadRow,
      clothing_type: 'light',
      washing_program: 'quick',
      status: 'washing',
      location_id: null,
      created_at: '2026-07-07T20:36:41.28169+00:00',
      started_at: '2026-07-07T20:16:41.28169+00:00',
    };
    const dataSource = createDataSourceMock({ listResult: { data: [legacyRow], error: null } });
    const notificationsService = createNotificationsServiceMock();
    const service = new LaundryLoadsService(dataSource as unknown as LaundryLoadsSupabaseDataSource, notificationsService as never, createPredictionsServiceMock() as never);

    await expect(service.listLaundryLoads(user)).resolves.toEqual([
      {
        ...loadResponse,
        clothingType: 'LIGHT_CLOTHES',
        washingProgram: 'QUICK',
        status: 'WASHING',
        locationId: 'unknown',
        createdAt: '2026-07-07T20:36:41.281Z',
        startedAt: '2026-07-07T20:16:41.281Z',
      },
    ]);
  });

  it('creates a planned laundry load for the authenticated user', async () => {
    const energyRow = {
      ...loadRow,
      estimated_washing_energy_kwh: 0.84,
      estimated_washing_water_liters: 52,
      estimated_washing_cost_level: 'MEDIUM',
      estimated_washing_cost_confidence: 'MEDIUM',
    };
    const dataSource = createDataSourceMock({
      createResult: { data: loadRow, error: null },
      updateEnergyResult: { data: energyRow, error: null },
    });
    const notificationsService = createNotificationsServiceMock();
    const service = new LaundryLoadsService(dataSource as unknown as LaundryLoadsSupabaseDataSource, notificationsService as never, createPredictionsServiceMock() as never);

    await expect(
      service.createLaundryLoad(user, {
        washerId: 'washer-1',
        clothingType: 'MIXED',
        washingProgram: 'NORMAL',
        locationId: 'home',
        dryingLocationId: 'BALCONY',
        spinRpm: 1400,
        loadSize: 'LARGE',
      }),
    ).resolves.toEqual({
      ...loadResponse,
      estimatedWashingEnergyKwh: 0.84,
      estimatedWashingWaterLiters: 52,
      estimatedWashingCostLevel: 'MEDIUM',
      estimatedWashingCostConfidence: 'MEDIUM',
    });
    expect(dataSource.create).toHaveBeenCalledWith({
      userId: 'user-1',
      washerId: 'washer-1',
      clothingType: 'MIXED',
      washingProgram: 'NORMAL',
      locationId: 'home',
      dryingLocationId: 'BALCONY',
      spinRpm: 1400,
      loadSize: 'LARGE',
    });
    expect(dataSource.updateEnergyEstimate).toHaveBeenCalledWith({
      userId: 'user-1',
      loadId: 'load-1',
      estimatedWashingEnergyKwh: 0.84,
      estimatedWashingWaterLiters: 52,
      estimatedWashingCostAmount: null,
      estimatedWashingCostCurrency: null,
      estimatedWashingCostLevel: 'MEDIUM',
      estimatedWashingCostConfidence: 'MEDIUM',
    });
    expect(notificationsService.scheduleIdealHangingTime).toHaveBeenCalledWith(
      user,
      'load-1',
      '2026-07-10T12:00:00.000Z',
      '2026-07-10T14:00:00.000Z',
    );
  });

  it('does not fail load creation when ideal hanging notification scheduling fails', async () => {
    const energyRow = {
      ...loadRow,
      estimated_washing_energy_kwh: 0.84,
      estimated_washing_water_liters: 52,
      estimated_washing_cost_level: 'MEDIUM',
      estimated_washing_cost_confidence: 'MEDIUM',
    };
    const dataSource = createDataSourceMock({
      createResult: { data: loadRow, error: null },
      updateEnergyResult: { data: energyRow, error: null },
    });
    const notificationsService = createNotificationsServiceMock();
    notificationsService.scheduleIdealHangingTime.mockRejectedValueOnce(new Error('notification unavailable'));
    const service = new LaundryLoadsService(dataSource as unknown as LaundryLoadsSupabaseDataSource, notificationsService as never, createPredictionsServiceMock() as never);

    await expect(
      service.createLaundryLoad(user, {
        washerId: 'washer-1',
        clothingType: 'MIXED',
        washingProgram: 'NORMAL',
        locationId: 'home',
        dryingLocationId: 'BALCONY',
        spinRpm: 1400,
        loadSize: 'LARGE',
      }),
    ).resolves.toEqual({
      ...loadResponse,
      estimatedWashingEnergyKwh: 0.84,
      estimatedWashingWaterLiters: 52,
      estimatedWashingCostLevel: 'MEDIUM',
      estimatedWashingCostConfidence: 'MEDIUM',
    });
    expect(dataSource.updateEnergyEstimate).toHaveBeenCalled();
    expect(notificationsService.scheduleIdealHangingTime).toHaveBeenCalled();
  });

  it('returns not found when updating a load outside the user scope', async () => {
    const service = new LaundryLoadsService(
      createDataSourceMock({ updateResult: { data: null, error: null } }) as unknown as LaundryLoadsSupabaseDataSource,
      createNotificationsServiceMock() as never,
      createPredictionsServiceMock() as never,
    );

    await expect(service.updateLaundryLoadStatus(user, 'load-2', { status: 'DRYING' })).rejects.toBeInstanceOf(
      NotFoundException,
    );
  });

  it('hides Supabase errors behind backend errors', async () => {
    const service = new LaundryLoadsService(
      createDataSourceMock({ listResult: { data: null, error: new Error('boom') } }) as unknown as LaundryLoadsSupabaseDataSource,
      createNotificationsServiceMock() as never,
      createPredictionsServiceMock() as never,
    );

    await expect(service.listLaundryLoads(user)).rejects.toBeInstanceOf(InternalServerErrorException);
  });

  it('dispatches a drying-complete push attempt when a load becomes completed', async () => {
    const completedRow = {
      ...loadRow,
      status: 'COMPLETED',
      completed_at: '2026-07-10T12:00:00.000Z',
    };
    const dataSource = createDataSourceMock({ updateResult: { data: completedRow, error: null } });
    const notificationsService = createNotificationsServiceMock();
    const service = new LaundryLoadsService(dataSource as unknown as LaundryLoadsSupabaseDataSource, notificationsService as never, createPredictionsServiceMock() as never);

    await expect(service.updateLaundryLoadStatus(user, 'load-1', { status: 'COMPLETED' })).resolves.toEqual({
      ...loadResponse,
      status: 'COMPLETED',
      completedAt: '2026-07-10T12:00:00.000Z',
    });

    expect(notificationsService.dispatchLaundryLoadCompleted).toHaveBeenCalledWith(user, 'load-1');
    expect(notificationsService.cancelPendingLaundryNotifications).toHaveBeenCalledWith(user, 'load-1');
  });

  it('schedules drying-complete notification when a load starts drying', async () => {
    jest.useFakeTimers().setSystemTime(new Date('2026-07-10T11:00:00.000Z'));
    const dryingRow = {
      ...loadRow,
      status: 'DRYING',
      drying_started_at: '2026-07-10T11:00:00.000Z',
      drying_estimated_minutes_at_start: 120,
      drying_estimated_pickup_at: '2026-07-10T13:00:00.000Z',
    };
    const dataSource = createDataSourceMock({ updateResult: { data: dryingRow, error: null } });
    const notificationsService = createNotificationsServiceMock();
    const predictionsService = createPredictionsServiceMock({
      estimatedDryingMinutes: 120,
      estimatedPickupAt: '2026-07-10T13:00:00.000Z',
    });
    const service = new LaundryLoadsService(dataSource as unknown as LaundryLoadsSupabaseDataSource, notificationsService as never, predictionsService as never);

    await service.updateLaundryLoadStatus(user, 'load-1', { status: 'DRYING' });

    expect(dataSource.updateStatus).toHaveBeenCalledWith({
      userId: 'user-1',
      loadId: 'load-1',
      status: 'DRYING',
      startedAt: undefined,
      dryingStartedAt: '2026-07-10T11:00:00.000Z',
      dryingEstimatedMinutesAtStart: 120,
      dryingEstimatedPickupAt: '2026-07-10T13:00:00.000Z',
      completedAt: undefined,
    });
    expect(notificationsService.cancelPendingLaundryNotifications).toHaveBeenCalledWith(user, 'load-1', [
      'IDEAL_HANGING_TIME',
    ]);
    expect(notificationsService.scheduleDryingComplete).toHaveBeenCalledWith(user, 'load-1', '2026-07-10T13:00:00.000Z');
    expect(predictionsService.calculateDryingPrediction).toHaveBeenCalledWith(
      expect.objectContaining({
        laundryLoadId: 'load-1',
        dryingLocationId: 'PATIO',
      }),
      user,
    );
    jest.useRealTimers();
  });

  it('does not dispatch a drying-complete push for non-completed status changes', async () => {
    const dryingRow = {
      ...loadRow,
      status: 'DRYING',
    };
    const dataSource = createDataSourceMock({ updateResult: { data: dryingRow, error: null } });
    const notificationsService = createNotificationsServiceMock();
    const service = new LaundryLoadsService(dataSource as unknown as LaundryLoadsSupabaseDataSource, notificationsService as never, createPredictionsServiceMock() as never);

    await service.updateLaundryLoadStatus(user, 'load-1', { status: 'DRYING' });

    expect(notificationsService.dispatchLaundryLoadCompleted).not.toHaveBeenCalled();
  });
});

function createNotificationsServiceMock() {
  return {
    dispatchLaundryLoadCompleted: jest.fn().mockResolvedValue({ status: 'SENT' }),
    scheduleIdealHangingTime: jest.fn().mockResolvedValue({ status: 'PENDING' }),
    scheduleDryingComplete: jest.fn().mockResolvedValue({ status: 'PENDING' }),
    cancelPendingLaundryNotifications: jest.fn().mockResolvedValue({ status: 'CANCELLED', affectedEvents: 1 }),
  };
}

function createPredictionsServiceMock(result: {
  estimatedDryingMinutes: number;
  estimatedPickupAt: string;
  recommendedHangWindowStart?: string;
  recommendedHangWindowEnd?: string;
  estimatedCost?: {
    amount: number | null;
    currency: string | null;
    level: 'LOW' | 'MEDIUM' | 'HIGH';
    confidence: 'LOW' | 'MEDIUM' | 'HIGH';
    estimatedEnergyKwh: number | null;
    estimatedWaterLiters: number | null;
  };
} = {
  estimatedDryingMinutes: 180,
  recommendedHangWindowStart: '2026-07-10T12:00:00.000Z',
  recommendedHangWindowEnd: '2026-07-10T14:00:00.000Z',
  estimatedPickupAt: '2026-07-10T14:00:00.000Z',
  estimatedCost: {
    amount: null,
    currency: null,
    level: 'MEDIUM',
    confidence: 'MEDIUM',
    estimatedEnergyKwh: 0.84,
    estimatedWaterLiters: 52,
  },
}) {
  return {
    calculateDryingPrediction: jest.fn().mockResolvedValue(result),
  };
}

function createDataSourceMock(results: {
  listResult?: { data: typeof loadRow[] | null; error: unknown };
  createResult?: { data: typeof loadRow | null; error: unknown };
  findResult?: { data: typeof loadRow | null; error: unknown };
  updateResult?: { data: typeof loadRow | null; error: unknown };
  updateEnergyResult?: { data: typeof loadRow | null; error: unknown };
}): jest.Mocked<Pick<LaundryLoadsSupabaseDataSource, 'listByUser' | 'findByUserAndId' | 'create' | 'updateStatus' | 'updateEnergyEstimate'>> {
  return {
    listByUser: jest.fn().mockResolvedValue(results.listResult ?? { data: [], error: null }),
    findByUserAndId: jest.fn().mockResolvedValue(results.findResult ?? { data: loadRow, error: null }),
    create: jest.fn().mockResolvedValue(results.createResult ?? { data: loadRow, error: null }),
    updateStatus: jest.fn().mockResolvedValue(results.updateResult ?? { data: loadRow, error: null }),
    updateEnergyEstimate: jest.fn().mockResolvedValue(results.updateEnergyResult ?? { data: loadRow, error: null }),
  };
}
