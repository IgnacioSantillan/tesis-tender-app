import { BadRequestException } from '@nestjs/common';
import { SPIN_SPEED_RPMS, SpinSpeedRpm } from '../energy/energy-prediction-types';
import { SaveWasherRequestDto } from './dto/save-washer-request.dto';
import { WASHER_ENERGY_LABELS, WasherEnergyLabel } from './dto/washer-energy-label';
import { WASHER_TYPES, WasherType } from './dto/washer-type';

export interface ValidWasherInput {
  name: string;
  type: WasherType;
  capacityKg: number | null;
  energyLabel: WasherEnergyLabel | null;
  waterUsageLiters: number | null;
  defaultSpinRpm: SpinSpeedRpm | null;
  isPrimary: boolean;
}

export function validateWasherInput(input: SaveWasherRequestDto): ValidWasherInput {
  const name = validateName(input.name);
  const type = validateType(input.type);

  return {
    name,
    type,
    capacityKg: validateOptionalPositiveNumber(input.capacityKg, 'capacityKg'),
    energyLabel: validateEnergyLabel(input.energyLabel),
    waterUsageLiters: validateOptionalPositiveNumber(input.waterUsageLiters, 'waterUsageLiters'),
    defaultSpinRpm: validateOptionalSpinRpm(input.defaultSpinRpm),
    isPrimary: input.isPrimary ?? false,
  };
}

function validateName(value: unknown): string {
  if (typeof value !== 'string' || value.trim().length === 0) {
    throw new BadRequestException('name is required');
  }

  const normalized = value.trim();

  if (normalized.length > 80) {
    throw new BadRequestException('name must be 80 characters or fewer');
  }

  return normalized;
}

function validateType(value: unknown): WasherType {
  if (typeof value !== 'string') {
    throw new BadRequestException('type must be a supported washer type');
  }

  const normalized = normalizeWasherType(value);

  if (!normalized) {
    throw new BadRequestException('type must be a supported washer type');
  }

  return normalized;
}

function normalizeWasherType(value: string): WasherType | null {
  const normalized = value
    .trim()
    .normalize('NFD')
    .replace(/\p{Diacritic}/gu, '')
    .toUpperCase()
    .replace(/[^A-Z0-9]+/g, '_')
    .replace(/^_+|_+$/g, '');

  if (WASHER_TYPES.includes(normalized as WasherType)) {
    return normalized as WasherType;
  }

  const aliases: Record<string, WasherType> = {
    FRONT: 'FRONT_LOAD',
    FRONTLOAD: 'FRONT_LOAD',
    FRONT_LOADING: 'FRONT_LOAD',
    FRONT_LOAD_WASHER: 'FRONT_LOAD',
    CARGA_FRONTAL: 'FRONT_LOAD',
    LAVARROPAS_CARGA_FRONTAL: 'FRONT_LOAD',
    TOP: 'TOP_LOAD',
    TOPLOAD: 'TOP_LOAD',
    TOP_LOADING: 'TOP_LOAD',
    TOP_LOAD_WASHER: 'TOP_LOAD',
    CARGA_SUPERIOR: 'TOP_LOAD',
    LAVARROPAS_CARGA_SUPERIOR: 'TOP_LOAD',
    WASHERDRYER: 'WASHER_DRYER',
    WASHER_DRYER_COMBO: 'WASHER_DRYER',
    DRYER: 'WASHER_DRYER',
    SECARROPAS: 'WASHER_DRYER',
    LAVARROPAS_SECARROPAS: 'WASHER_DRYER',
    LAVASECARROPAS: 'WASHER_DRYER',
    OTRO: 'OTHER',
  };

  return aliases[normalized] ?? null;
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

function validateEnergyLabel(value: unknown): WasherEnergyLabel | null {
  if (value === undefined || value === null) {
    return null;
  }

  if (typeof value !== 'string') {
    throw new BadRequestException('energyLabel must be text');
  }

  const normalized = value.trim().toUpperCase();

  if (normalized.length === 0) {
    return null;
  }

  if (!WASHER_ENERGY_LABELS.includes(normalized as WasherEnergyLabel)) {
    throw new BadRequestException('energyLabel must be a supported washer energy label');
  }

  return normalized as WasherEnergyLabel;
}

function validateOptionalSpinRpm(value: unknown): SpinSpeedRpm | null {
  if (value === undefined || value === null) {
    return null;
  }

  if (typeof value !== 'number' || !Number.isInteger(value) || !SPIN_SPEED_RPMS.includes(value as SpinSpeedRpm)) {
    throw new BadRequestException('defaultSpinRpm must be a supported spin speed');
  }

  return value as SpinSpeedRpm;
}
