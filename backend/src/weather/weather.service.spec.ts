import { BadRequestException } from '@nestjs/common';
import { WeatherService } from './weather.service';

describe('WeatherService', () => {
  afterEach(() => {
    jest.useRealTimers();
  });

  it('resolves current weather from forecast with normalized location id', async () => {
    const provider = {
      getCurrentWeather: jest.fn(),
      getForecast: jest.fn().mockResolvedValue([{ location: { id: 'home' } }]),
    };
    const service = new WeatherService(provider, createSupabaseServiceMock({ data: null }));

    await expect(service.getCurrentWeather(' home ')).resolves.toEqual({ location: { id: 'home' } });
    expect(provider.getForecast).toHaveBeenCalledWith({
      id: 'home',
      label: 'Home patio',
      latitude: -34.6037,
      longitude: -58.3816,
    });
    expect(provider.getCurrentWeather).not.toHaveBeenCalled();
  });

  it('resolves authenticated home location from the primary user location', async () => {
    const provider = {
      getCurrentWeather: jest.fn(),
      getForecast: jest.fn().mockResolvedValue([{ location: { id: 'current-location' } }]),
    };
    const service = new WeatherService(
      provider,
      createSupabaseServiceMock({
        data: {
          id: 'current-location',
          label: 'Mi ubicacion actual',
          latitude: '-34.6037',
          longitude: '-58.3816',
        },
      }),
    );

    await service.getCurrentWeather('home', { id: 'user-1', email: 'user@example.com' });

    expect(provider.getForecast).toHaveBeenCalledWith({
      id: 'current-location',
      label: 'Mi ubicacion actual',
      latitude: -34.6037,
      longitude: -58.3816,
    });
  });

  it('rejects missing location id', async () => {
    const service = new WeatherService(
      {
        getCurrentWeather: jest.fn(),
        getForecast: jest.fn(),
      },
      createSupabaseServiceMock({ data: null }),
    );

    await expect(service.getForecast('')).rejects.toBeInstanceOf(BadRequestException);
  });

  it('falls back to the static location when user location lookup fails', async () => {
    const provider = {
      getCurrentWeather: jest.fn(),
      getForecast: jest.fn().mockResolvedValue([{ location: { id: 'home' } }]),
    };
    const service = new WeatherService(
      provider,
      createSupabaseServiceMock({ data: null, error: new Error('supabase unavailable') }),
    );

    await service.getCurrentWeather('home', { id: 'user-1', email: 'user@example.com' });

    expect(provider.getForecast).toHaveBeenCalledWith({
      id: 'home',
      label: 'Home patio',
      latitude: -34.6037,
      longitude: -58.3816,
    });
  });

  it('returns stale backend fallback weather when the provider fails', async () => {
    const service = new WeatherService(
      {
        getCurrentWeather: jest.fn(),
        getForecast: jest.fn().mockRejectedValue(new Error('provider unavailable')),
      },
      createSupabaseServiceMock({ data: null }),
    );

    await expect(service.getCurrentWeather('home')).resolves.toMatchObject({
      location: {
        id: 'home',
        label: 'Home patio',
      },
      source: 'MOCK',
      isStale: true,
    });
  });

  it('caches forecast by resolved location to avoid provider request bursts', async () => {
    const provider = {
      getCurrentWeather: jest.fn(),
      getForecast: jest.fn().mockResolvedValue([{ location: { id: 'home' } }]),
    };
    const service = new WeatherService(provider, createSupabaseServiceMock({ data: null }));

    await service.getForecast('home');
    await service.getCurrentWeather('home');

    expect(provider.getForecast).toHaveBeenCalledTimes(1);
  });

  it('briefly caches stale fallback weather when provider fails repeatedly', async () => {
    const provider = {
      getCurrentWeather: jest.fn(),
      getForecast: jest.fn().mockRejectedValue(new Error('provider unavailable')),
    };
    const service = new WeatherService(provider, createSupabaseServiceMock({ data: null }));

    await service.getForecast('home');
    await service.getCurrentWeather('home');

    expect(provider.getForecast).toHaveBeenCalledTimes(1);
  });

  it('keeps rate-limited fallback cached longer than transient fallback', async () => {
    jest.useFakeTimers().setSystemTime(new Date('2026-07-25T22:00:00.000Z'));
    const provider = {
      getCurrentWeather: jest.fn(),
      getForecast: jest.fn().mockRejectedValue(new Error('Open-Meteo request failed with status 429')),
    };
    const service = new WeatherService(provider, createSupabaseServiceMock({ data: null }));

    await service.getForecast('home');
    jest.advanceTimersByTime(10 * 60 * 1000);
    await service.getCurrentWeather('home');

    expect(provider.getForecast).toHaveBeenCalledTimes(1);
  });
});

function createSupabaseServiceMock(result: { data: unknown; error?: Error | null }): never {
  const maybeSingle = jest.fn().mockResolvedValue({ data: result.data, error: result.error ?? null });
  const secondEq = jest.fn().mockReturnValue({ maybeSingle });
  const firstEq = jest.fn().mockReturnValue({ eq: secondEq });
  const select = jest.fn().mockReturnValue({ eq: firstEq });
  const from = jest.fn().mockReturnValue({ select });

  return {
    getClient: () => ({ from }),
  } as never;
}
