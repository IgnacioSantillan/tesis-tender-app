import { WeatherController } from './weather.controller';

describe('WeatherController', () => {
  it('returns current weather from the weather service', async () => {
    const weather = {
      location: { id: 'home', label: 'Home patio', latitude: null, longitude: null },
      source: 'MOCK',
      capturedAt: '2026-07-05T14:00:00.000Z',
      forecastFor: '2026-07-05T14:00:00.000Z',
      condition: 'CLEAR',
      temperatureCelsius: 24,
      humidityPercent: 48,
      windSpeedKph: 18,
      rainProbabilityPercent: 8,
      precipitationMillimeters: 0,
      cloudCoverPercent: 20,
      forecastLeadMinutes: 0,
      isStale: false,
    };
    const service = {
      getCurrentWeather: jest.fn().mockResolvedValue(weather),
      getForecast: jest.fn(),
    };
    const controller = new WeatherController(service as never);

    await expect(
      controller.current(
        {
          headers: {},
          user: { id: 'user-1', email: 'user@example.com' },
        },
        'home',
      ),
    ).resolves.toEqual(weather);

    expect(service.getCurrentWeather).toHaveBeenCalledWith('home', {
      id: 'user-1',
      email: 'user@example.com',
    });
  });
});
