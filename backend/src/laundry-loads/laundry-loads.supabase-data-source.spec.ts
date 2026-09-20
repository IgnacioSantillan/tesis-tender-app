import { LaundryLoadsSupabaseDataSource } from './laundry-loads.supabase-data-source';

const loadRow = {
  id: 'load-1',
  washer_id: 'washer-1',
  clothing_type: 'MIXED',
  washing_program: 'NORMAL',
  status: 'PLANNED',
  location_id: 'home',
  drying_location_id: 'PATIO',
  spin_rpm: null,
  load_size: null,
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

describe('LaundryLoadsSupabaseDataSource', () => {
  it('lists laundry loads scoped by user id', async () => {
    const { dataSource, from } = createDataSource({ listResult: { data: [loadRow], error: null } });

    await expect(dataSource.listByUser('user-1')).resolves.toEqual({ data: [loadRow], error: null });
    expect(from).toHaveBeenCalledWith('laundry_loads');
  });

  it('creates laundry loads with explicit user ownership', async () => {
    const { dataSource, insert } = createDataSource({ createResult: { data: loadRow, error: null } });

    await expect(
      dataSource.create({
        userId: 'user-1',
        washerId: 'washer-1',
        clothingType: 'MIXED',
        washingProgram: 'NORMAL',
        locationId: 'home',
        dryingLocationId: 'BALCONY',
        spinRpm: 1200,
        loadSize: 'MEDIUM',
      }),
    ).resolves.toEqual({ data: loadRow, error: null });
    expect(insert).toHaveBeenCalledWith({
      user_id: 'user-1',
      washer_id: 'washer-1',
      clothing_type: 'MIXED',
      washing_program: 'NORMAL',
      status: 'PLANNED',
      location_id: 'home',
      drying_location_id: 'BALCONY',
      spin_rpm: 1200,
      load_size: 'MEDIUM',
    });
  });

  it('updates laundry load status inside the authenticated user scope', async () => {
    const { dataSource, update } = createDataSource({ updateResult: { data: loadRow, error: null } });

    await expect(
      dataSource.updateStatus({
        userId: 'user-1',
        loadId: 'load-1',
        status: 'COMPLETED',
        completedAt: '2026-07-05T16:00:00.000Z',
      }),
    ).resolves.toEqual({ data: loadRow, error: null });
    expect(update).toHaveBeenCalledWith({
      status: 'COMPLETED',
      started_at: undefined,
      drying_started_at: undefined,
      drying_estimated_minutes_at_start: undefined,
      drying_estimated_pickup_at: undefined,
      completed_at: '2026-07-05T16:00:00.000Z',
    });
  });

  it('persists drying start timestamp when a load enters drying status', async () => {
    const { dataSource, update } = createDataSource({ updateResult: { data: loadRow, error: null } });

    await dataSource.updateStatus({
      userId: 'user-1',
      loadId: 'load-1',
      status: 'DRYING',
      dryingStartedAt: '2026-07-05T15:10:00.000Z',
    });

    expect(update).toHaveBeenCalledWith({
      status: 'DRYING',
      started_at: undefined,
      drying_started_at: '2026-07-05T15:10:00.000Z',
      drying_estimated_minutes_at_start: undefined,
      drying_estimated_pickup_at: undefined,
      completed_at: undefined,
    });
  });

  it('persists drying prediction snapshot during status update', async () => {
    const { dataSource, update } = createDataSource({ updateResult: { data: loadRow, error: null } });

    await dataSource.updateStatus({
      userId: 'user-1',
      loadId: 'load-1',
      status: 'DRYING',
      dryingStartedAt: '2026-07-05T15:10:00.000Z',
      dryingEstimatedMinutesAtStart: 120,
      dryingEstimatedPickupAt: '2026-07-05T17:10:00.000Z',
    });

    expect(update).toHaveBeenCalledWith({
      status: 'DRYING',
      started_at: undefined,
      drying_started_at: '2026-07-05T15:10:00.000Z',
      drying_estimated_minutes_at_start: 120,
      drying_estimated_pickup_at: '2026-07-05T17:10:00.000Z',
      completed_at: undefined,
    });
  });

  it('normalizes legacy load size during status update so Supabase checks pass', async () => {
    const { dataSource, update } = createDataSource({
      lookupResult: { data: { load_size: 'small' }, error: null },
      updateResult: { data: { ...loadRow, load_size: 'SMALL' }, error: null },
    });

    await dataSource.updateStatus({
      userId: 'user-1',
      loadId: 'load-1',
      status: 'DRYING',
    });

    expect(update).toHaveBeenCalledWith({
      status: 'DRYING',
      started_at: undefined,
      drying_started_at: undefined,
      drying_estimated_minutes_at_start: undefined,
      drying_estimated_pickup_at: undefined,
      completed_at: undefined,
      load_size: 'SMALL',
    });
  });

  it('clears invalid legacy load size during status update', async () => {
    const { dataSource, update } = createDataSource({
      lookupResult: { data: { load_size: 'HUGE' }, error: null },
      updateResult: { data: { ...loadRow, load_size: null }, error: null },
    });

    await dataSource.updateStatus({
      userId: 'user-1',
      loadId: 'load-1',
      status: 'DRYING',
    });

    expect(update).toHaveBeenCalledWith({
      status: 'DRYING',
      started_at: undefined,
      drying_started_at: undefined,
      drying_estimated_minutes_at_start: undefined,
      drying_estimated_pickup_at: undefined,
      completed_at: undefined,
      load_size: null,
    });
  });

  it('persists washing energy estimate fields inside the authenticated user scope', async () => {
    const { dataSource, update } = createDataSource({ updateResult: { data: loadRow, error: null } });

    await dataSource.updateEnergyEstimate({
      userId: 'user-1',
      loadId: 'load-1',
      estimatedWashingEnergyKwh: 0.84,
      estimatedWashingWaterLiters: 52,
      estimatedWashingCostAmount: null,
      estimatedWashingCostCurrency: null,
      estimatedWashingCostLevel: 'MEDIUM',
      estimatedWashingCostConfidence: 'MEDIUM',
    });

    expect(update).toHaveBeenCalledWith({
      estimated_washing_energy_kwh: 0.84,
      estimated_washing_water_liters: 52,
      estimated_washing_cost_amount: null,
      estimated_washing_cost_currency: null,
      estimated_washing_cost_level: 'MEDIUM',
      estimated_washing_cost_confidence: 'MEDIUM',
    });
  });
});

function createDataSource(results: {
  listResult?: unknown;
  createResult?: unknown;
  lookupResult?: unknown;
  updateResult?: unknown;
}) {
  const order = jest.fn().mockResolvedValue(results.listResult ?? { data: [], error: null });
  const single = jest.fn().mockResolvedValue(results.createResult ?? { data: loadRow, error: null });
  const lookupMaybeSingle = jest.fn().mockResolvedValue(results.lookupResult ?? { data: { load_size: null }, error: null });
  const maybeSingle = jest.fn().mockResolvedValue(results.updateResult ?? { data: loadRow, error: null });

  const insert = jest.fn().mockReturnValue({ select: jest.fn().mockReturnValue({ single }) });
  const update = jest.fn().mockReturnValue({
    eq: jest.fn().mockReturnValue({
      eq: jest.fn().mockReturnValue({
        select: jest.fn().mockReturnValue({ maybeSingle }),
      }),
    }),
  });
  const lookupSelect = jest.fn().mockReturnValue({
    eq: jest.fn().mockReturnValue({
      eq: jest.fn().mockReturnValue({
        maybeSingle: lookupMaybeSingle,
      }),
    }),
  });
  const listSelect = jest.fn().mockReturnValue({
    eq: jest.fn().mockReturnValue({ order }),
  });
  const from = jest.fn().mockReturnValue({
    select: jest.fn((columns: string) => (columns === 'load_size' ? lookupSelect(columns) : listSelect(columns))),
    insert,
    update,
  });
  const dataSource = new LaundryLoadsSupabaseDataSource({
    getClient: () => ({ from }),
  } as never);

  return { dataSource, from, insert, update };
}
