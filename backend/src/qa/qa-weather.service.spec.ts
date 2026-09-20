import { QaWeatherService } from './qa-weather.service';

describe('QaWeatherService', () => {
  it('reports ok when the weather service returns a real secondary-provider snapshot', async () => {
    const weatherService = {
      getForecast: jest.fn().mockResolvedValue([
        {
          location: { id: 'home' },
          source: 'MET_NO',
          isStale: false,
          forecastFor: '2026-07-25T20:00:00.000Z',
          temperatureCelsius: 12.3,
          humidityPercent: 92,
          windSpeedKph: 12.2,
          rainProbabilityPercent: 0,
          precipitationMillimeters: 0,
          cloudCoverPercent: 66,
        },
      ]),
    };
    const service = new QaWeatherService(weatherService as never);

    await expect(service.getProviderStatus('home')).resolves.toMatchObject({
      status: 'ok',
      locationId: 'home',
      source: 'MET_NO',
      isStale: false,
      temperatureCelsius: 12.3,
      message: 'Provider returned a real weather snapshot.',
    });
  });

  it('reports degraded when the weather service returns stale fallback data', async () => {
    const weatherService = {
      getForecast: jest.fn().mockResolvedValue([
        {
          location: { id: 'home' },
          source: 'MOCK',
          isStale: true,
          forecastFor: '2026-07-25T20:00:00.000Z',
          temperatureCelsius: 22,
          humidityPercent: 58,
          windSpeedKph: 12,
          rainProbabilityPercent: 10,
          precipitationMillimeters: 0,
          cloudCoverPercent: 25,
        },
      ]),
    };
    const service = new QaWeatherService(weatherService as never);

    await expect(service.getProviderStatus('home')).resolves.toMatchObject({
      status: 'degraded',
      source: 'MOCK',
      isStale: true,
      message: 'Weather service is using fallback data.',
    });
  });
});
