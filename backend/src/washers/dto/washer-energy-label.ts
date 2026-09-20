export const WASHER_ENERGY_LABELS = [
  'A+++',
  'A++',
  'A+',
  'A',
  'B',
  'C',
  'D',
  'E',
  'F',
  'G',
] as const;

export type WasherEnergyLabel = (typeof WASHER_ENERGY_LABELS)[number];
