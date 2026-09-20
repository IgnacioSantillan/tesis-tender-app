export const WEATHER_CONDITIONS = ['CLEAR', 'PARTLY_CLOUDY', 'CLOUDY', 'RAIN', 'STORM', 'UNKNOWN'] as const;

export type WeatherCondition = (typeof WEATHER_CONDITIONS)[number];

export const WEATHER_DATA_SOURCES = ['OPEN_METEO', 'MET_NO', 'MOCK'] as const;

export type WeatherDataSource = (typeof WEATHER_DATA_SOURCES)[number];
