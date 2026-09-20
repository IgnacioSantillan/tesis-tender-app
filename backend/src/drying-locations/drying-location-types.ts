import type { DryingMethod } from '../predictions/dto/drying-prediction-types';

export const DRYING_LOCATION_IDS = ['INDOOR', 'BALCONY', 'OUTDOOR_LINE', 'PATIO', 'LAUNDRY_ROOM'] as const;

export type DryingLocationId = (typeof DRYING_LOCATION_IDS)[number];

export const DRYING_WEATHER_EXPOSURES = ['LOW', 'MEDIUM', 'HIGH'] as const;

export type DryingWeatherExposure = (typeof DRYING_WEATHER_EXPOSURES)[number];

export interface DryingLocationProfile {
  id: DryingLocationId;
  label: string;
  defaultDryingMethod: DryingMethod;
  weatherExposure: DryingWeatherExposure;
  airflowFactor: number;
  rainExposureFactor: number;
  humidityRetentionFactor: number;
}

export const DEFAULT_DRYING_LOCATION_ID: DryingLocationId = 'PATIO';

export const DRYING_LOCATION_PROFILES: Record<DryingLocationId, DryingLocationProfile> = {
  INDOOR: {
    id: 'INDOOR',
    label: 'Interior',
    defaultDryingMethod: 'INDOOR',
    weatherExposure: 'LOW',
    airflowFactor: 0.75,
    rainExposureFactor: 0.1,
    humidityRetentionFactor: 1.25,
  },
  BALCONY: {
    id: 'BALCONY',
    label: 'Balcony',
    defaultDryingMethod: 'OUTDOOR',
    weatherExposure: 'MEDIUM',
    airflowFactor: 0.9,
    rainExposureFactor: 0.55,
    humidityRetentionFactor: 1.05,
  },
  OUTDOOR_LINE: {
    id: 'OUTDOOR_LINE',
    label: 'Outdoor line',
    defaultDryingMethod: 'OUTDOOR',
    weatherExposure: 'HIGH',
    airflowFactor: 1.2,
    rainExposureFactor: 1,
    humidityRetentionFactor: 0.95,
  },
  PATIO: {
    id: 'PATIO',
    label: 'Patio',
    defaultDryingMethod: 'OUTDOOR',
    weatherExposure: 'HIGH',
    airflowFactor: 1.05,
    rainExposureFactor: 0.85,
    humidityRetentionFactor: 1,
  },
  LAUNDRY_ROOM: {
    id: 'LAUNDRY_ROOM',
    label: 'Laundry room',
    defaultDryingMethod: 'INDOOR',
    weatherExposure: 'LOW',
    airflowFactor: 0.65,
    rainExposureFactor: 0,
    humidityRetentionFactor: 1.35,
  },
};

const DRYING_LOCATION_ALIASES: Record<string, DryingLocationId> = {
  HOME: DEFAULT_DRYING_LOCATION_ID,
  INTERIOR: 'INDOOR',
  INDOOR: 'INDOOR',
  BALCON: 'BALCONY',
  BALCONY: 'BALCONY',
  TENDER: 'OUTDOOR_LINE',
  OUTDOOR: 'OUTDOOR_LINE',
  OUTDOOR_LINE: 'OUTDOOR_LINE',
  PATIO: 'PATIO',
  LAVADERO: 'LAUNDRY_ROOM',
  LAUNDRY: 'LAUNDRY_ROOM',
  LAUNDRY_ROOM: 'LAUNDRY_ROOM',
};

export function normalizeDryingLocationId(value: string | null | undefined): DryingLocationId | null {
  const normalized = value?.trim().toUpperCase();
  if (!normalized) {
    return DEFAULT_DRYING_LOCATION_ID;
  }

  return DRYING_LOCATION_ALIASES[normalized] ?? null;
}

export function getDryingLocationProfile(value: DryingLocationId): DryingLocationProfile {
  return DRYING_LOCATION_PROFILES[value];
}
