import { FailoverWeatherProvider } from './failover-weather.provider';

describe('FailoverWeatherProvider', () => {
  it('returns Open-Meteo forecast when primary provider succeeds', async () => {
    const openMeteoProvider = {
      getCurrentWeather: jest.fn(),
      getForecast: jest.fn().mockResolvedValue([{ source: 'OPEN_METEO' }]),
    };
    const metNoProvider = {
      getCurrentWeather: jest.fn(),
      getForecast: jest.fn(),
    };
    const provider = new FailoverWeatherProvider(openMeteoProvider as never, metNoProvider as never);

    await expect(provider.getForecast(homeLocation())).resolves.toEqual([{ source: 'OPEN_METEO' }]);
    expect(metNoProvider.getForecast).not.toHaveBeenCalled();
  });

  it('uses MET Norway forecast when Open-Meteo is rate limited', async () => {
    const openMeteoProvider = {
      getCurrentWeather: jest.fn(),
      getForecast: jest.fn().mockRejectedValue(new Error('Open-Meteo request failed with status 429')),
    };
    const metNoProvider = {
      getCurrentWeather: jest.fn(),
      getForecast: jest.fn().mockResolvedValue([{ source: 'MET_NO' }]),
    };
    const provider = new FailoverWeatherProvider(openMeteoProvider as never, metNoProvider as never);

    await expect(provider.getForecast(homeLocation())).resolves.toEqual([{ source: 'MET_NO' }]);
    expect(metNoProvider.getForecast).toHaveBeenCalledWith(homeLocation());
  });

  it('uses MET Norway current weather when Open-Meteo current weather fails', async () => {
    const openMeteoProvider = {
      getCurrentWeather: jest.fn().mockRejectedValue(new Error('Open-Meteo unavailable')),
      getForecast: jest.fn(),
    };
    const metNoProvider = {
      getCurrentWeather: jest.fn().mockResolvedValue({ source: 'MET_NO' }),
      getForecast: jest.fn(),
    };
    const provider = new FailoverWeatherProvider(openMeteoProvider as never, metNoProvider as never);

    await expect(provider.getCurrentWeather(homeLocation())).resolves.toEqual({ source: 'MET_NO' });
    expect(metNoProvider.getCurrentWeather).toHaveBeenCalledWith(homeLocation());
  });
});

function homeLocation() {
  return {
    id: 'home',
    label: 'Home patio',
    latitude: -34.6037,
    longitude: -58.3816,
  };
}
