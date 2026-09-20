import { BadRequestException } from '@nestjs/common';
import { DryingLocationId, normalizeDryingLocationId } from '../drying-locations/drying-location-types';
import { LOAD_SIZES, LoadSize, SPIN_SPEED_RPMS, SpinSpeedRpm } from '../energy/energy-prediction-types';
import { CreateLaundryLoadRequestDto } from './dto/create-laundry-load-request.dto';
import { CLOTHING_TYPES, ClothingType, LAUNDRY_LOAD_STATUSES, LaundryLoadStatus, WASHING_PROGRAMS, WashingProgram } from './dto/laundry-load-types';

export interface ValidLaundryLoadInput {
  washerId: string | null;
  clothingType: ClothingType;
  washingProgram: WashingProgram;
  locationId: string;
  dryingLocationId: DryingLocationId;
  spinRpm: SpinSpeedRpm | null;
  loadSize: LoadSize | null;
}

export function validateCreateLaundryLoadInput(input: CreateLaundryLoadRequestDto): ValidLaundryLoadInput {
  return {
    washerId: validateOptionalId(input.washerId, 'washerId'),
    clothingType: validateEnum(input.clothingType, CLOTHING_TYPES, 'clothingType'),
    washingProgram: validateEnum(input.washingProgram, WASHING_PROGRAMS, 'washingProgram'),
    locationId: validateRequiredText(input.locationId, 'locationId'),
    dryingLocationId: validateDryingLocationId(input.dryingLocationId),
    spinRpm: validateOptionalSpinRpm(input.spinRpm),
    loadSize: validateOptionalLoadSize(input.loadSize),
  };
}

export function validateLaundryLoadStatus(value: unknown): LaundryLoadStatus {
  return validateEnum(value, LAUNDRY_LOAD_STATUSES, 'status');
}

function validateRequiredText(value: unknown, field: string): string {
  if (typeof value !== 'string' || value.trim().length === 0) {
    throw new BadRequestException(`${field} is required`);
  }

  return value.trim();
}

function validateOptionalId(value: unknown, field: string): string | null {
  if (value === undefined || value === null) {
    return null;
  }

  return validateRequiredText(value, field);
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

function validateEnum<T extends string>(value: unknown, values: readonly T[], field: string): T {
  if (typeof value !== 'string' || !values.includes(value as T)) {
    throw new BadRequestException(`${field} is not supported`);
  }

  return value as T;
}
