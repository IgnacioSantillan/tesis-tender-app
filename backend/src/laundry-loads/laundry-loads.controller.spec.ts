import { LaundryLoadsController } from './laundry-loads.controller';

const request = {
  headers: {},
  user: {
    id: 'user-1',
    email: 'user@example.com',
  },
};

describe('LaundryLoadsController', () => {
  it('lists laundry loads for the authenticated user', async () => {
    const laundryLoadsService = {
      listLaundryLoads: jest.fn().mockResolvedValue([]),
    };
    const controller = new LaundryLoadsController(laundryLoadsService as never);

    await expect(controller.list(request)).resolves.toEqual([]);
    expect(laundryLoadsService.listLaundryLoads).toHaveBeenCalledWith(request.user);
  });

  it('creates a laundry load for the authenticated user', async () => {
    const load = {
      id: 'load-1',
      washerId: 'washer-1',
      clothingType: 'MIXED',
      washingProgram: 'NORMAL',
      status: 'PLANNED',
      locationId: 'home',
      dryingLocationId: 'PATIO',
      createdAt: '2026-07-05T14:00:00Z',
      startedAt: null,
      dryingStartedAt: null,
      completedAt: null,
      prediction: null,
    };
    const laundryLoadsService = {
      createLaundryLoad: jest.fn().mockResolvedValue(load),
    };
    const controller = new LaundryLoadsController(laundryLoadsService as never);

    await expect(
      controller.create(request, {
        washerId: 'washer-1',
        clothingType: 'MIXED',
        washingProgram: 'NORMAL',
        locationId: 'home',
        dryingLocationId: 'PATIO',
      }),
    ).resolves.toEqual(load);
  });
});
