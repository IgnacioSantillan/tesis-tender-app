import { OpenMeteoWeatherProvider } from './open-meteo-weather.provider';

describe('OpenMeteoWeatherProvider', () => {
  const originalOpenMeteoBaseUrl = process.env.OPEN_METEO_BASE_URL;

  beforeEach(() => {
    process.env.OPEN_METEO_BASE_URL = 'https://api.example.test/v1';
  });

  afterEach(() => {
    jest.restoreAllMocks();
    if (originalOpenMeteoBaseUrl === undefined) {
      delete process.env.OPEN_METEO_BASE_URL;
    } else {
      process.env.OPEN_METEO_BASE_URL = originalOpenMeteoBaseUrl;
    }
  });

  it('maps current Open-Meteo response into TenderApp weather snapshot', async () => {
    const fetcher = jest.spyOn(globalThis, 'fetch').mockResolvedValue({
      ok: true,
      json: async () => ({
        current: {
          time: '2026-07-08T12:00',
          temperature_2m: 18.5,
          relative_humidity_2m: 72,
          precipitation: 0,
          cloud_cover: 35,
          wind_speed_10m: 14.2,
          weather_code: 2,
        },
        hourly: {
          time: ['2026-07-08T12:00'],
          precipitation: [0.2],
          precipitation_probability: [12],
          temperature_2m: [18.5],
          relative_humidity_2m: [72],
          wind_speed_10m: [14.2],
          cloud_cover: [35],
          weather_code: [2],
        },
      }),
    } as Response);
    const provider = new OpenMeteoWeatherProvider();

    await expect(provider.getCurrentWeather(homeLocation())).resolves.toMatchObject({
      location: {
        id: 'home',
        label: 'Home patio',
        latitude: -34.6037,
        longitude: -58.3816,
      },
      source: 'OPEN_METEO',
      forecastFor: '2026-07-08T12:00:00.000Z',
      condition: 'PARTLY_CLOUDY',
      temperatureCelsius: 18.5,
      humidityPercent: 72,
      windSpeedKph: 14.2,
      rainProbabilityPercent: 12,
      precipitationMillimeters: 0.2,
      cloudCoverPercent: 35,
      isStale: false,
    });
    expect(fetcher).toHaveBeenCalledWith(expect.stringContaining('/forecast?'));
    expect(fetcher.mock.calls[0][0]).toContain('latitude=-34.6037');
    expect(fetcher.mock.calls[0][0]).toContain('longitude=-58.3816');
  });

  it('maps an hourly forecast window', async () => {
    jest.spyOn(globalThis, 'fetch').mockResolvedValue({
      ok: true,
      json: async () => ({
        current: {},
        hourly: {
          time: ['2026-07-08T12:00', '2026-07-08T13:00'],
          precipitation: [0, 1.4],
          precipitation_probability: [5, 20],
          temperature_2m: [19, 20],
          relative_humidity_2m: [60, 58],
          wind_speed_10m: [10, 11],
          cloud_cover: [10, 50],
          weather_code: [0, 61],
        },
      }),
    } as Response);
    const provider = new OpenMeteoWeatherProvider();

    const forecast = await provider.getForecast(homeLocation());

    expect(forecast).toHaveLength(2);
    expect(forecast[0]).toMatchObject({ condition: 'CLEAR', rainProbabilityPercent: 5, precipitationMillimeters: 0 });
    expect(forecast[1]).toMatchObject({ condition: 'RAIN', rainProbabilityPercent: 20, precipitationMillimeters: 1.4 });
  });

  it('fails when Open-Meteo returns an HTTP error', async () => {
    jest.spyOn(globalThis, 'fetch').mockResolvedValue({ ok: false, status: 503 } as Response);
    const provider = new OpenMeteoWeatherProvider();

    await expect(provider.getCurrentWeather(homeLocation())).rejects.toThrow('Open-Meteo request failed with status 503');
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
