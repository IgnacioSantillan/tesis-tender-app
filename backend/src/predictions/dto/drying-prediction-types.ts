export const DRYING_METHODS = ['OUTDOOR', 'INDOOR'] as const;
export const DRYING_VERDICTS = ['GOOD', 'CAUTION', 'BAD'] as const;

export type DryingMethod = (typeof DRYING_METHODS)[number];
export type DryingVerdict = (typeof DRYING_VERDICTS)[number];
