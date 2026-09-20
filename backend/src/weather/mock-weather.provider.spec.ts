import { MockWeatherProvider } from './mock-weather.provider';

describe('MockWeatherProvider', () => {
  it('returns a normalized current weather snapshot', async () => {
    const provider = new MockWeatherProvider();

    await expect(provider.getCurrentWeather(homeLocation())).resolves.toMatchObject({
      location: {
        id: 'home',
        label: 'Home patio',
      },
      source: 'MOCK',
      condition: 'CLEAR',
      precipitationMillimeters: 0,
      forecastLeadMinutes: 0,
      isStale: false,
    });
  });

  it('returns a forecast window', async () => {
    const provider = new MockWeatherProvider();

    const forecast = await provider.getForecast(homeLocation());

    expect(forecast).toHaveLength(3);
    expect(forecast[2].condition).toBe('PARTLY_CLOUDY');
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
