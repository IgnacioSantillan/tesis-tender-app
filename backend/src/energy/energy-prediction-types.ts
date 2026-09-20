export const SPIN_SPEED_RPMS = [600, 800, 1000, 1200, 1400, 1600] as const;
export const LOAD_SIZES = ['SMALL', 'MEDIUM', 'LARGE'] as const;
export const ESTIMATED_WASHING_COST_LEVELS = ['LOW', 'MEDIUM', 'HIGH', 'UNKNOWN'] as const;
export const ESTIMATED_WASHING_COST_CONFIDENCES = ['LOW', 'MEDIUM', 'HIGH'] as const;

export type SpinSpeedRpm = (typeof SPIN_SPEED_RPMS)[number];
export type LoadSize = (typeof LOAD_SIZES)[number];
export type EstimatedWashingCostLevel = (typeof ESTIMATED_WASHING_COST_LEVELS)[number];
export type EstimatedWashingCostConfidence = (typeof ESTIMATED_WASHING_COST_CONFIDENCES)[number];
