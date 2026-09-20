import { getWeatherProviderConfig } from './weather-provider.config';

describe('getWeatherProviderConfig', () => {
  it('defaults to mock provider', () => {
    expect(getWeatherProviderConfig({})).toEqual({
      provider: 'mock',
      openMeteoBaseUrl: 'https://api.open-meteo.com/v1',
      metNoBaseUrl: 'https://api.met.no/weatherapi/locationforecast/2.0',
      metNoUserAgent: 'TenderApp/0.1 weather-mvp thesis-project',
    });
  });

  it('accepts open-meteo provider', () => {
    expect(
      getWeatherProviderConfig({
        WEATHER_PROVIDER: 'open-meteo',
        OPEN_METEO_BASE_URL: 'https://example.com/weather/',
      }),
    ).toEqual({
      provider: 'open-meteo',
      openMeteoBaseUrl: 'https://example.com/weather',
      metNoBaseUrl: 'https://api.met.no/weatherapi/locationforecast/2.0',
      metNoUserAgent: 'TenderApp/0.1 weather-mvp thesis-project',
    });
  });

  it('accepts met-no provider configuration', () => {
    expect(
      getWeatherProviderConfig({
        WEATHER_PROVIDER: 'met-no',
        MET_NO_BASE_URL: 'https://example.com/met-no/',
        MET_NO_USER_AGENT: ' TenderApp/1.0 contact@example.com ',
      }),
    ).toEqual({
      provider: 'met-no',
      openMeteoBaseUrl: 'https://api.open-meteo.com/v1',
      metNoBaseUrl: 'https://example.com/met-no',
      metNoUserAgent: 'TenderApp/1.0 contact@example.com',
    });
  });

  it('rejects unknown providers', () => {
    expect(() => getWeatherProviderConfig({ WEATHER_PROVIDER: 'other' })).toThrow(
      'WEATHER_PROVIDER must be mock, open-meteo or met-no',
    );
  });
});
