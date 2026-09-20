export const WASHER_TYPES = ['FRONT_LOAD', 'TOP_LOAD', 'WASHER_DRYER', 'OTHER'] as const;

export type WasherType = (typeof WASHER_TYPES)[number];
