import { InternalServerErrorException } from '@nestjs/common';
import { UsersService } from './users.service';

describe('UsersService', () => {
  it('returns the authenticated user profile enriched with profile data', async () => {
    const service = new UsersService(
      createSupabaseServiceMock({
        profileData: { display_name: 'Juan' },
        primaryLocationData: { id: 'home', label: 'Home', latitude: null, longitude: null, is_primary: true, updated_at: 'now' },
      }),
    );

    await expect(
      service.getCurrentProfile({
        id: 'user-1',
        email: 'user@example.com',
      }),
    ).resolves.toEqual({
      id: 'user-1',
      email: 'user@example.com',
      displayName: 'Juan',
      defaultLocationId: 'home',
    });
  });

  it('falls back to auth identity when no profile row exists', async () => {
    const service = new UsersService(createSupabaseServiceMock({ profileData: null, primaryLocationData: null }));

    await expect(
      service.getCurrentProfile({
        id: 'user-1',
        email: null,
      }),
    ).resolves.toEqual({
      id: 'user-1',
      email: null,
      displayName: null,
      defaultLocationId: null,
    });
  });

  it('hides Supabase details when profile lookup fails', async () => {
    const service = new UsersService(createSupabaseServiceMock({ profileError: new Error('table missing') }));

    await expect(
      service.getCurrentProfile({
        id: 'user-1',
        email: 'user@example.com',
      }),
    ).rejects.toBeInstanceOf(InternalServerErrorException);
  });

  it('saves household location as the primary user location', async () => {
    const service = new UsersService(
      createSupabaseServiceMock({
        savedLocationData: {
          id: 'current-location',
          label: 'Mi ubicacion actual',
          latitude: '-34.6037',
          longitude: '-58.3816',
          is_primary: true,
          updated_at: '2026-07-12T12:00:00.000Z',
        },
      }),
    );

    await expect(
      service.saveHouseholdLocation(
        { id: 'user-1', email: 'user@example.com' },
        {
          locationId: 'current-location',
          label: ' Mi ubicacion actual ',
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
  });

  it('hides Supabase details when location persistence fails', async () => {
    const service = new UsersService(createSupabaseServiceMock({ resetLocationError: new Error('boom') }));

    await expect(
      service.saveHouseholdLocation(
        { id: 'user-1', email: 'user@example.com' },
        {
          locationId: 'current-location',
          label: 'Mi ubicacion actual',
          latitude: -34.6037,
          longitude: -58.3816,
        },
      ),
    ).rejects.toBeInstanceOf(InternalServerErrorException);
  });
});

interface SupabaseMockOptions {
  profileData?: unknown;
  profileError?: Error | null;
  primaryLocationData?: unknown;
  primaryLocationError?: Error | null;
  resetLocationError?: Error | null;
  savedLocationData?: unknown;
  savedLocationError?: Error | null;
}

function createSupabaseServiceMock(options: SupabaseMockOptions): never {
  const from = jest.fn((table: string) => {
    if (table === 'profiles') {
      const maybeSingle = jest.fn().mockResolvedValue({
        data: options.profileData ?? null,
        error: options.profileError ?? null,
      });
      const eq = jest.fn().mockReturnValue({ maybeSingle });
      const select = jest.fn().mockReturnValue({ eq });

      return { select };
    }

    if (table === 'user_locations') {
      const maybeSingle = jest.fn().mockResolvedValue({
        data: options.primaryLocationData ?? null,
        error: options.primaryLocationError ?? null,
      });
      const secondEq = jest.fn().mockReturnValue({ maybeSingle });
      const firstEq = jest.fn().mockReturnValue({ eq: secondEq });
      const selectForRead = jest.fn().mockReturnValue({ eq: firstEq });
      const eqForUpdate = jest.fn().mockResolvedValue({ error: options.resetLocationError ?? null });
      const update = jest.fn().mockReturnValue({ eq: eqForUpdate });
      const single = jest.fn().mockResolvedValue({
        data: options.savedLocationData ?? null,
        error: options.savedLocationError ?? null,
      });
      const selectForUpsert = jest.fn().mockReturnValue({ single });
      const upsert = jest.fn().mockReturnValue({ select: selectForUpsert });

      return { select: selectForRead, update, upsert };
    }

    throw new Error(`Unexpected table ${table}`);
  });

  return {
    getClient: () => ({
      from,
    }),
  } as never;
}
