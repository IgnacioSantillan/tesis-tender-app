import { DryingLocationId, normalizeDryingLocationId } from '../drying-locations/drying-location-types';
import {
  ESTIMATED_WASHING_COST_CONFIDENCES,
  ESTIMATED_WASHING_COST_LEVELS,
  EstimatedWashingCostConfidence,
  EstimatedWashingCostLevel,
  LOAD_SIZES,
  LoadSize,
  SPIN_SPEED_RPMS,
  SpinSpeedRpm,
} from '../energy/energy-prediction-types';
import { LaundryLoadResponseDto } from './dto/laundry-load-response.dto';
import { ClothingType, LaundryLoadStatus, WashingProgram } from './dto/laundry-load-types';

export interface LaundryLoadRow {
  id: string;
  washer_id: string | null;
  clothing_type: string | null;
  washing_program: string | null;
  status: string | null;
  location_id: string | null;
  drying_location_id: string | null;
  spin_rpm: number | null;
  load_size: string | null;
  estimated_washing_energy_kwh: number | null;
  estimated_washing_water_liters: number | null;
  estimated_washing_cost_amount: number | null;
  estimated_washing_cost_currency: string | null;
  estimated_washing_cost_level: string | null;
  estimated_washing_cost_confidence: string | null;
  created_at: string;
  started_at: string | null;
  drying_started_at: string | null;
  drying_estimated_minutes_at_start: number | null;
  drying_estimated_pickup_at: string | null;
  completed_at: string | null;
}

export function mapLaundryLoadRow(row: LaundryLoadRow): LaundryLoadResponseDto {
  return {
    id: row.id,
    washerId: row.washer_id,
    clothingType: normalizeClothingType(row.clothing_type),
    washingProgram: normalizeWashingProgram(row.washing_program),
    status: normalizeStatus(row.status),
    locationId: normalizeLocationId(row.location_id),
    dryingLocationId: normalizeDryingLocation(row.drying_location_id),
    spinRpm: normalizeSpinRpm(row.spin_rpm),
    loadSize: normalizeLoadSize(row.load_size),
    estimatedWashingEnergyKwh: normalizeOptionalNumber(row.estimated_washing_energy_kwh),
    estimatedWashingWaterLiters: normalizeOptionalNumber(row.estimated_washing_water_liters),
    estimatedWashingCostAmount: normalizeOptionalNumber(row.estimated_washing_cost_amount),
    estimatedWashingCostCurrency: normalizeCurrency(row.estimated_washing_cost_currency),
    estimatedWashingCostLevel: normalizeCostLevel(row.estimated_washing_cost_level),
    estimatedWashingCostConfidence: normalizeCostConfidence(row.estimated_washing_cost_confidence),
    createdAt: normalizeTimestamp(row.created_at),
    startedAt: row.started_at ? normalizeTimestamp(row.started_at) : null,
    dryingStartedAt: row.drying_started_at ? normalizeTimestamp(row.drying_started_at) : null,
    dryingEstimatedMinutesAtStart: normalizeOptionalNumber(row.drying_estimated_minutes_at_start),
    dryingEstimatedPickupAt: row.drying_estimated_pickup_at ? normalizeTimestamp(row.drying_estimated_pickup_at) : null,
    completedAt: row.completed_at ? normalizeTimestamp(row.completed_at) : null,
    prediction: null,
  };
}

function normalizeClothingType(value: string | null): ClothingType {
  const normalized = normalizeToken(value);
  const aliases: Record<string, ClothingType> = {
    LIGHT: 'LIGHT_CLOTHES',
    LIGHT_CLOTHES: 'LIGHT_CLOTHES',
    HEAVY: 'HEAVY_CLOTHES',
    HEAVY_CLOTHES: 'HEAVY_CLOTHES',
    BEDDING: 'BEDDING',
    DELICATES: 'DELICATES',
    MIXED: 'MIXED',
  };

  return aliases[normalized] ?? 'MIXED';
}

function normalizeWashingProgram(value: string | null): WashingProgram {
  const normalized = normalizeToken(value);
  const aliases: Record<string, WashingProgram> = {
    QUICK: 'QUICK',
    NORMAL: 'NORMAL',
    ECO: 'ECO',
    DELICATE: 'DELICATE',
  };

  return aliases[normalized] ?? 'NORMAL';
}

function normalizeStatus(value: string | null): LaundryLoadStatus {
  const normalized = normalizeToken(value);
  const aliases: Record<string, LaundryLoadStatus> = {
    PLANNED: 'PLANNED',
    WASHING: 'WASHING',
    DRYING: 'DRYING',
    COMPLETED: 'COMPLETED',
    CANCELLED: 'CANCELLED',
  };

  return aliases[normalized] ?? 'PLANNED';
}

function normalizeLocationId(value: string | null): string {
  const trimmed = value?.trim();
  return trimmed && trimmed.length > 0 ? trimmed : 'unknown';
}

function normalizeDryingLocation(value: string | null): DryingLocationId {
  return normalizeDryingLocationId(value) ?? 'PATIO';
}

function normalizeSpinRpm(value: number | null): SpinSpeedRpm | null {
  return typeof value === 'number' && SPIN_SPEED_RPMS.includes(value as SpinSpeedRpm) ? (value as SpinSpeedRpm) : null;
}

function normalizeLoadSize(value: string | null): LoadSize | null {
  const normalized = normalizeToken(value);
  return LOAD_SIZES.includes(normalized as LoadSize) ? (normalized as LoadSize) : null;
}

function normalizeCostLevel(value: string | null): EstimatedWashingCostLevel | null {
  const normalized = normalizeToken(value);
  return ESTIMATED_WASHING_COST_LEVELS.includes(normalized as EstimatedWashingCostLevel)
    ? (normalized as EstimatedWashingCostLevel)
    : null;
}

function normalizeCostConfidence(value: string | null): EstimatedWashingCostConfidence | null {
  const normalized = normalizeToken(value);
  return ESTIMATED_WASHING_COST_CONFIDENCES.includes(normalized as EstimatedWashingCostConfidence)
    ? (normalized as EstimatedWashingCostConfidence)
    : null;
}

function normalizeCurrency(value: string | null): string | null {
  const normalized = normalizeToken(value);
  return /^[A-Z]{3}$/.test(normalized) ? normalized : null;
}

function normalizeOptionalNumber(value: number | null): number | null {
  return typeof value === 'number' && Number.isFinite(value) ? value : null;
}

function normalizeTimestamp(value: string): string {
  const timestamp = new Date(value);
  return Number.isNaN(timestamp.getTime()) ? value : timestamp.toISOString();
}

function normalizeToken(value: string | null): string {
  return value?.trim().toUpperCase() ?? '';
}
