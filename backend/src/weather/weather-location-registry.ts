import { WeatherLocationDto } from './dto/weather-location.dto';

export interface ResolvedWeatherLocation extends WeatherLocationDto {
  latitude: number;
  longitude: number;
}

const KNOWN_LOCATIONS: Record<string, ResolvedWeatherLocation> = {
  home: {
    id: 'home',
    label: 'Home patio',
    latitude: -34.6037,
    longitude: -58.3816,
  },
};

export function resolveWeatherLocation(locationId: string): ResolvedWeatherLocation {
  const normalized = locationId.trim().toLowerCase();
  return KNOWN_LOCATIONS[normalized] ?? {
    id: locationId,
    label: locationId,
    latitude: -34.6037,
    longitude: -58.3816,
  };
}
