import { InternalServerErrorException, NotFoundException } from '@nestjs/common';
import { WashersService } from './washers.service';

const user = {
  id: 'user-1',
  email: 'user@example.com',
};

const washerRow = {
  id: 'washer-1',
  name: 'Main washer',
  type: 'FRONT_LOAD',
  capacity_kg: 7,
  energy_label: 'A',
  water_usage_liters: 45,
  default_spin_rpm: 1200,
  is_primary: true,
};

const washerResponse = {
  id: 'washer-1',
  name: 'Main washer',
  type: 'FRONT_LOAD',
  capacityKg: 7,
  energyLabel: 'A',
  waterUsageLiters: 45,
  defaultSpinRpm: 1200,
  isPrimary: true,
};

describe('WashersService', () => {
  it('lists washers owned by the authenticated user', async () => {
    const service = new WashersService(createSupabaseServiceMock({ listResult: { data: [washerRow], error: null } }));

    await expect(service.listWashers(user)).resolves.toEqual([washerResponse]);
  });

  it('creates a washer for the authenticated user', async () => {
    const service = new WashersService(createSupabaseServiceMock({ createResult: { data: washerRow, error: null } }));

    await expect(
      service.createWasher(user, {
        name: 'Main washer',
        type: 'FRONT_LOAD',
        capacityKg: 7,
        energyLabel: 'A',
        waterUsageLiters: 45,
        defaultSpinRpm: 1200,
        isPrimary: true,
      }),
    ).resolves.toEqual(washerResponse);
  });

  it('returns not found when updating a washer outside the user scope', async () => {
    const service = new WashersService(createSupabaseServiceMock({ updateResult: { data: null, error: null } }));

    await expect(
      service.updateWasher(user, 'washer-2', {
        name: 'Other washer',
        type: 'TOP_LOAD',
      }),
    ).rejects.toBeInstanceOf(NotFoundException);
  });

  it('retires a washer without deleting the historical row', async () => {
    const mock = createSupabaseServiceMock({ deleteResult: { data: { id: 'washer-1' }, error: null } });
    const service = new WashersService(mock);

    await expect(service.deleteWasher(user, 'washer-1')).resolves.toBeUndefined();
  });

  it('returns not found when retiring a missing or already retired washer', async () => {
    const service = new WashersService(createSupabaseServiceMock({ deleteResult: { data: null, error: null } }));

    await expect(service.deleteWasher(user, 'washer-2')).rejects.toBeInstanceOf(NotFoundException);
  });

  it('hides Supabase errors behind backend errors', async () => {
    const service = new WashersService(createSupabaseServiceMock({ listResult: { data: null, error: new Error('boom') } }));

    await expect(service.listWashers(user)).rejects.toBeInstanceOf(InternalServerErrorException);
  });
});

function createSupabaseServiceMock(results: {
  listResult?: unknown;
  createResult?: unknown;
  updateResult?: unknown;
  deleteResult?: unknown;
}): never {
  const order = jest.fn().mockResolvedValue(results.listResult ?? { data: [], error: null });
  const maybeSingleForUpdate = jest.fn().mockResolvedValue(results.updateResult ?? { data: washerRow, error: null });
  const maybeSingleForDelete = jest.fn().mockResolvedValue(results.deleteResult ?? { data: { id: 'washer-1' }, error: null });
  const single = jest.fn().mockResolvedValue(results.createResult ?? { data: washerRow, error: null });

  const selectAfterInsert = jest.fn().mockReturnValue({ single });
  const selectAfterUpdate = jest.fn().mockReturnValue({ maybeSingle: maybeSingleForUpdate });
  const selectAfterDelete = jest.fn().mockReturnValue({ maybeSingle: maybeSingleForDelete });

  const isForList = jest.fn().mockReturnValue({ order });
  const eqForList = jest.fn().mockReturnValue({ is: isForList });
  const isForUpdate = jest.fn().mockReturnValue({ select: selectAfterUpdate });
  const eqForUpdateUser = jest.fn().mockReturnValue({ is: isForUpdate });
  const eqForUpdateId = jest.fn().mockReturnValue({ eq: eqForUpdateUser });
  const isForDelete = jest.fn().mockReturnValue({ select: selectAfterDelete });
  const eqForDeleteUser = jest.fn().mockReturnValue({ is: isForDelete });
  const eqForDeleteId = jest.fn().mockReturnValue({ eq: eqForDeleteUser });

  const update = jest.fn().mockImplementation((payload: Record<string, unknown>) => {
    return payload.retired_at ? { eq: eqForDeleteId } : { eq: eqForUpdateId };
  });

  const from = jest.fn().mockReturnValue({
    select: jest.fn().mockReturnValue({ eq: eqForList }),
    insert: jest.fn().mockReturnValue({ select: selectAfterInsert }),
    update,
  });

  return {
    getClient: () => ({
      from,
    }),
  } as never;
}
