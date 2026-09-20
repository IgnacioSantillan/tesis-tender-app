export type WeatherProviderName = 'mock' | 'open-meteo' | 'met-no';

export interface WeatherProviderConfig {
  provider: WeatherProviderName;
  openMeteoBaseUrl: string;
  metNoBaseUrl: string;
  metNoUserAgent: string;
}

export function getWeatherProviderConfig(env: NodeJS.ProcessEnv = process.env): WeatherProviderConfig {
  return {
    provider: parseWeatherProvider(env.WEATHER_PROVIDER),
    openMeteoBaseUrl: normalizeBaseUrl(env.OPEN_METEO_BASE_URL || 'https://api.open-meteo.com/v1'),
    metNoBaseUrl: normalizeBaseUrl(env.MET_NO_BASE_URL || 'https://api.met.no/weatherapi/locationforecast/2.0'),
    metNoUserAgent: normalizeUserAgent(env.MET_NO_USER_AGENT),
  };
}

function parseWeatherProvider(value: string | undefined): WeatherProviderName {
  const normalized = value?.trim().toLowerCase();

  if (!normalized) {
    return 'mock';
  }

  if (normalized === 'mock' || normalized === 'open-meteo' || normalized === 'met-no') {
    return normalized;
  }

  throw new Error('WEATHER_PROVIDER must be mock, open-meteo or met-no');
}

function normalizeBaseUrl(value: string): string {
  const trimmed = value.trim().replace(/\/+$/, '');

  if (!trimmed.startsWith('https://')) {
    throw new Error('Weather provider base URL must use https');
  }

  return trimmed;
}

function normalizeUserAgent(value: string | undefined): string {
  const trimmed = value?.trim();
  return trimmed && trimmed.length > 0
    ? trimmed
    : 'TenderApp/0.1 weather-mvp thesis-project';
}
