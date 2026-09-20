import { resolveWeatherLocation } from './weather-location-registry';

describe('resolveWeatherLocation', () => {
  it('resolves the MVP home location to coordinates', () => {
    expect(resolveWeatherLocation('home')).toEqual({
      id: 'home',
      label: 'Home patio',
      latitude: -34.6037,
      longitude: -58.3816,
    });
  });

  it('falls back to Buenos Aires coordinates for unknown MVP locations', () => {
    expect(resolveWeatherLocation('balcony')).toEqual({
      id: 'balcony',
      label: 'balcony',
      latitude: -34.6037,
      longitude: -58.3816,
    });
  });
});
