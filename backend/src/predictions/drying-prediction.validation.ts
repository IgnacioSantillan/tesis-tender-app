import { BadRequestException } from '@nestjs/common';
import { DryingLocationId, normalizeDryingLocationId } from '../drying-locations/drying-location-types';
import { LOAD_SIZES, LoadSize, SPIN_SPEED_RPMS, SpinSpeedRpm } from '../energy/energy-prediction-types';
import { CLOTHING_TYPES, ClothingType, WASHING_PROGRAMS, WashingProgram } from '../laundry-loads/dto/laundry-load-types';
import { WASHER_ENERGY_LABELS, WasherEnergyLabel } from '../washers/dto/washer-energy-label';
import { CreateDryingPredictionRequestDto } from './dto/create-drying-prediction-request.dto';
import { DRYING_METHODS, DryingMethod } from './dto/drying-prediction-types';

export interface ValidDryingPredictionInput {
  laundryLoadId: string;
  clothingType: ClothingType;
  washingProgram: WashingProgram;
  dryingMethod: DryingMethod;
  locationId: string;
  dryingLocationId: DryingLocationId;
  spinRpm: SpinSpeedRpm | null;
  loadSize: LoadSize | null;
  washerEnergyLabel: WasherEnergyLabel | null;
  washerCapacityKg: number | null;
  waterUsageLiters: number | null;
}

export function validateDryingPredictionInput(input: CreateDryingPredictionRequestDto): ValidDryingPredictionInput {
  return {
    laundryLoadId: validateRequiredText(input.laundryLoadId, 'laundryLoadId'),
    clothingType: validateEnum(input.clothingType, CLOTHING_TYPES, 'clothingType'),
    washingProgram: validateEnum(input.washingProgram, WASHING_PROGRAMS, 'washingProgram'),
    dryingMethod: validateEnum(input.dryingMethod, DRYING_METHODS, 'dryingMethod'),
    locationId: validateRequiredText(input.locationId, 'locationId'),
    dryingLocationId: validateDryingLocationId(input.dryingLocationId),
    spinRpm: validateOptionalSpinRpm(input.spinRpm),
    loadSize: validateOptionalLoadSize(input.loadSize),
    washerEnergyLabel: validateEnergyLabel(input.washerEnergyLabel),
    washerCapacityKg: validateOptionalPositiveNumber(input.washerCapacityKg, 'washerCapacityKg'),
    waterUsageLiters: validateOptionalPositiveNumber(input.waterUsageLiters, 'waterUsageLiters'),
  };
}

function validateRequiredText(value: unknown, field: string): string {
  if (typeof value !== 'string' || value.trim().length === 0) {
    throw new BadRequestException(`${field} is required`);
  }

  return value.trim();
}

function validateDryingLocationId(value: unknown): DryingLocationId {
  if (value !== undefined && value !== null && typeof value !== 'string') {
    throw new BadRequestException('dryingLocationId is not supported');
  }

  const dryingLocationId = normalizeDryingLocationId(value);
  if (!dryingLocationId) {
    throw new BadRequestException('dryingLocationId is not supported');
  }

  return dryingLocationId;
}

function validateOptionalSpinRpm(value: unknown): SpinSpeedRpm | null {
  if (value === undefined || value === null) {
    return null;
  }

  if (typeof value !== 'number' || !Number.isInteger(value) || !SPIN_SPEED_RPMS.includes(value as SpinSpeedRpm)) {
    throw new BadRequestException('spinRpm is not supported');
  }

  return value as SpinSpeedRpm;
}

function validateOptionalLoadSize(value: unknown): LoadSize | null {
  if (value === undefined || value === null) {
    return null;
  }

  return validateEnum(value, LOAD_SIZES, 'loadSize');
}

function validateEnergyLabel(value: unknown): WasherEnergyLabel | null {
  if (value === undefined || value === null) {
    return null;
  }

  if (typeof value !== 'string') {
    throw new BadRequestException('washerEnergyLabel must be text');
  }

  const normalized = value.trim().toUpperCase();
  if (normalized.length === 0) {
    return null;
  }

  if (!WASHER_ENERGY_LABELS.includes(normalized as WasherEnergyLabel)) {
    throw new BadRequestException('washerEnergyLabel is not supported');
  }

  return normalized as WasherEnergyLabel;
}

function validateOptionalPositiveNumber(value: unknown, field: string): number | null {
  if (value === undefined || value === null) {
    return null;
  }

  if (typeof value !== 'number' || !Number.isFinite(value) || value <= 0) {
    throw new BadRequestException(`${field} must be a positive number`);
  }

  return value;
}

function validateEnum<T extends string>(value: unknown, values: readonly T[], field: string): T {
  if (typeof value !== 'string' || !values.includes(value as T)) {
    throw new BadRequestException(`${field} is not supported`);
  }

  return value as T;
}
