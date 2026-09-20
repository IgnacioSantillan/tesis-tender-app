import { MetNoWeatherProvider } from './met-no-weather.provider';

describe('MetNoWeatherProvider', () => {
  const originalMetNoBaseUrl = process.env.MET_NO_BASE_URL;
  const originalMetNoUserAgent = process.env.MET_NO_USER_AGENT;

  beforeEach(() => {
    process.env.MET_NO_BASE_URL = 'https://api.example.test/weatherapi/locationforecast/2.0';
    process.env.MET_NO_USER_AGENT = 'TenderApp tests';
  });

  afterEach(() => {
    jest.restoreAllMocks();
    restoreEnvValue('MET_NO_BASE_URL', originalMetNoBaseUrl);
    restoreEnvValue('MET_NO_USER_AGENT', originalMetNoUserAgent);
  });

  it('maps MET Norway compact forecast into TenderApp weather snapshots', async () => {
    const fetcher = jest.spyOn(globalThis, 'fetch').mockResolvedValue({
      ok: true,
      json: async () => ({
        properties: {
          timeseries: [
            {
              time: '2026-07-25T20:00:00Z',
              data: {
                instant: {
                  details: {
                    air_temperature: 12.3,
                    relative_humidity: 92.3,
                    wind_speed: 3.4,
                    cloud_area_fraction: 65.6,
                  },
                },
                next_1_hours: {
                  summary: {
                    symbol_code: 'fog',
                  },
                  details: {
                    precipitation_amount: 0,
                  },
                },
              },
            },
            {
              time: '2026-07-25T21:00:00Z',
              data: {
                instant: {
                  details: {
                    air_temperature: 11.5,
                    relative_humidity: 95,
                    wind_speed: 3.9,
                    cloud_area_fraction: 85.9,
                  },
                },
                next_1_hours: {
                  summary: {
                    symbol_code: 'lightrain',
                  },
                  details: {
                    precipitation_amount: 0.8,
                  },
                },
              },
            },
          ],
        },
      }),
    } as Response);
    const provider = new MetNoWeatherProvider();

    const forecast = await provider.getForecast(homeLocation());

    expect(forecast).toHaveLength(2);
    expect(forecast[0]).toMatchObject({
      source: 'MET_NO',
      forecastFor: '2026-07-25T20:00:00.000Z',
      condition: 'CLOUDY',
      temperatureCelsius: 12.3,
      humidityPercent: 92,
      windSpeedKph: 12.2,
      rainProbabilityPercent: 0,
      precipitationMillimeters: 0,
      cloudCoverPercent: 66,
      isStale: false,
    });
    expect(forecast[1]).toMatchObject({
      condition: 'RAIN',
      rainProbabilityPercent: 40,
      precipitationMillimeters: 0.8,
    });
    expect(fetcher).toHaveBeenCalledWith(
      expect.stringContaining('/compact?lat=-34.6037&lon=-58.3816'),
      expect.objectContaining({
        headers: {
          'User-Agent': 'TenderApp tests',
        },
      }),
    );
  });

  it('fails when MET Norway returns an HTTP error', async () => {
    jest.spyOn(globalThis, 'fetch').mockResolvedValue({ ok: false, status: 429 } as Response);
    const provider = new MetNoWeatherProvider();

    await expect(provider.getCurrentWeather(homeLocation())).rejects.toThrow(
      'MET Norway request failed with status 429',
    );
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

function restoreEnvValue(key: string, value: string | undefined): void {
  if (value === undefined) {
    delete process.env[key];
  } else {
    process.env[key] = value;
  }
}
