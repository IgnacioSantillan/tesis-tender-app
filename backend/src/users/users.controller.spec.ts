import { UsersController } from './users.controller';

describe('UsersController', () => {
  it('returns the current user profile', async () => {
    const usersService = {
      getCurrentProfile: jest.fn().mockResolvedValue({
        id: 'user-1',
        email: 'user@example.com',
        displayName: 'Juan',
        defaultLocationId: null,
      }),
    };
    const controller = new UsersController(usersService as never);

    await expect(
      controller.me({
        headers: {},
        user: {
          id: 'user-1',
          email: 'user@example.com',
        },
      }),
    ).resolves.toEqual({
      id: 'user-1',
      email: 'user@example.com',
      displayName: 'Juan',
      defaultLocationId: null,
    });
  });

  it('persists the current user household location', async () => {
    const usersService = {
      saveHouseholdLocation: jest.fn().mockResolvedValue({
        id: 'current-location',
        label: 'Mi ubicacion actual',
        latitude: -34.6037,
        longitude: -58.3816,
        isPrimary: true,
        updatedAt: '2026-07-12T12:00:00.000Z',
      }),
    };
    const controller = new UsersController(usersService as never);

    await expect(
      controller.saveHouseholdLocation(
        {
          headers: {},
          user: {
            id: 'user-1',
            email: 'user@example.com',
          },
        },
        {
          locationId: 'current-location',
          label: 'Mi ubicacion actual',
          latitude: -34.6037,
          longitude: -58.3816,
        },
      ),
    ).resolves.toEqual({
      id: 'current-location',
      label: 'Mi ubicacion actual',
      latitude: -34.6037,
      longitude: -58.3816,
      isPrimary: true,
      updatedAt: '2026-07-12T12:00:00.000Z',
    });

    expect(usersService.saveHouseholdLocation).toHaveBeenCalledWith(
      {
        id: 'user-1',
        email: 'user@example.com',
      },
      {
        locationId: 'current-location',
        label: 'Mi ubicacion actual',
        latitude: -34.6037,
        longitude: -58.3816,
      },
    );
  });
});
