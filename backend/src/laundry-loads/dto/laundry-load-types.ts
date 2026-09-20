export const CLOTHING_TYPES = ['LIGHT_CLOTHES', 'HEAVY_CLOTHES', 'BEDDING', 'DELICATES', 'MIXED'] as const;
export const WASHING_PROGRAMS = ['QUICK', 'NORMAL', 'ECO', 'DELICATE'] as const;
export const LAUNDRY_LOAD_STATUSES = ['PLANNED', 'WASHING', 'DRYING', 'COMPLETED', 'CANCELLED'] as const;

export type ClothingType = (typeof CLOTHING_TYPES)[number];
export type WashingProgram = (typeof WASHING_PROGRAMS)[number];
export type LaundryLoadStatus = (typeof LAUNDRY_LOAD_STATUSES)[number];

export const WASHING_PROGRAM_DURATION_MINUTES: Readonly<Record<WashingProgram, number>> = {
  QUICK: 30,
  NORMAL: 60,
  ECO: 90,
  DELICATE: 45,
};
