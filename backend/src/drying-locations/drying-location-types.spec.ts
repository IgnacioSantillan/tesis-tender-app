import {
  DEFAULT_DRYING_LOCATION_ID,
  getDryingLocationProfile,
  normalizeDryingLocationId,
} from './drying-location-types';

describe('drying location types', () => {
  it('normalizes supported aliases', () => {
    expect(normalizeDryingLocationId(' interior ')).toBe('INDOOR');
    expect(normalizeDryingLocationId('balcon')).toBe('BALCONY');
    expect(normalizeDryingLocationId('tender')).toBe('OUTDOOR_LINE');
    expect(normalizeDryingLocationId('lavadero')).toBe('LAUNDRY_ROOM');
  });

  it('defaults missing values to the MVP patio location', () => {
    expect(normalizeDryingLocationId(undefined)).toBe(DEFAULT_DRYING_LOCATION_ID);
    expect(normalizeDryingLocationId('')).toBe(DEFAULT_DRYING_LOCATION_ID);
  });

  it('exposes prediction factors for each profile', () => {
    expect(getDryingLocationProfile('PATIO')).toMatchObject({
      defaultDryingMethod: 'OUTDOOR',
      weatherExposure: 'HIGH',
    });
    expect(getDryingLocationProfile('LAUNDRY_ROOM')).toMatchObject({
      defaultDryingMethod: 'INDOOR',
      weatherExposure: 'LOW',
    });
  });
});
