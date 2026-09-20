import { WashersController } from './washers.controller';

const request = {
  headers: {},
  user: {
    id: 'user-1',
    email: 'user@example.com',
  },
};

describe('WashersController', () => {
  it('lists washers for the authenticated user', async () => {
    const washersService = {
      listWashers: jest.fn().mockResolvedValue([]),
    };
    const controller = new WashersController(washersService as never);

    await expect(controller.list(request)).resolves.toEqual([]);
    expect(washersService.listWashers).toHaveBeenCalledWith(request.user);
  });

  it('creates a washer for the authenticated user', async () => {
    const washer = {
      id: 'washer-1',
      name: 'Main washer',
      type: 'FRONT_LOAD',
      capacityKg: null,
      energyLabel: null,
      waterUsageLiters: null,
      defaultSpinRpm: null,
      isPrimary: false,
    };
    const washersService = {
      createWasher: jest.fn().mockResolvedValue(washer),
    };
    const controller = new WashersController(washersService as never);

    await expect(controller.create(request, { name: 'Main washer', type: 'FRONT_LOAD' })).resolves.toEqual(washer);
  });
});
