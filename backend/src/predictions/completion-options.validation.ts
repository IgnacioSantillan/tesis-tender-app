import { BadRequestException } from '@nestjs/common';
import { DryingLocationId } from '../drying-locations/drying-location-types';
import { LoadSize, SpinSpeedRpm } from '../energy/energy-prediction-types';
import { ClothingType } from '../laundry-loads/dto/laundry-load-types';
import { WasherEnergyLabel } from '../washers/dto/washer-energy-label';
import { CreateCompletionOptionsRequestDto } from './dto/create-completion-options-request.dto';
import { DryingMethod } from './dto/drying-prediction-types';
import { validateDryingPredictionInput } from './drying-prediction.validation';

export interface ValidCompletionOptionsInput {
  laundryLoadId: string;
  clothingType: ClothingType;
  dryingMethod: DryingMethod;
  locationId: string;
  dryingLocationId: DryingLocationId;
  spinRpm: SpinSpeedRpm | null;
  loadSize: LoadSize | null;
  washerEnergyLabel: WasherEnergyLabel | null;
  washerCapacityKg: number | null;
  waterUsageLiters: number | null;
  plannedStartAt: Date;
  targetReadyAt: Date;
}

export function validateCompletionOptionsInput(
  input: CreateCompletionOptionsRequestDto,
  serverNow = new Date(),
): ValidCompletionOptionsInput {
  const common = validateDryingPredictionInput({
    ...input,
    washingProgram: 'QUICK',
  });
  const plannedStartAt =
    input.plannedStartAt === undefined
      ? validateServerNow(serverNow)
      : validateIsoInstant(input.plannedStartAt, 'plannedStartAt');
  const targetReadyAt = validateIsoInstant(input.targetReadyAt, 'targetReadyAt');

  if (targetReadyAt.getTime() <= plannedStartAt.getTime()) {
    throw new BadRequestException('targetReadyAt must be after plannedStartAt');
  }

  return {
    laundryLoadId: common.laundryLoadId,
    clothingType: common.clothingType,
    dryingMethod: common.dryingMethod,
    locationId: common.locationId,
    dryingLocationId: common.dryingLocationId,
    spinRpm: common.spinRpm,
    loadSize: common.loadSize,
    washerEnergyLabel: common.washerEnergyLabel,
    washerCapacityKg: common.washerCapacityKg,
    waterUsageLiters: common.waterUsageLiters,
    plannedStartAt,
    targetReadyAt,
  };
}

function validateIsoInstant(value: unknown, field: string): Date {
  if (typeof value !== 'string' || value.trim().length === 0) {
    throw new BadRequestException(`${field} is required`);
  }

  const normalized = value.trim();
  const match = ISO_INSTANT_WITH_ZONE.exec(normalized);
  if (!match) {
    throw new BadRequestException(`${field} must be an ISO 8601 instant with Z or UTC offset`);
  }

  validateCalendarComponents(match, field);
  const instant = new Date(normalized);
  if (!Number.isFinite(instant.getTime())) {
    throw new BadRequestException(`${field} must be a valid ISO 8601 instant`);
  }

  return instant;
}

function validateCalendarComponents(match: RegExpExecArray, field: string): void {
  const year = Number(match.groups?.year);
  const month = Number(match.groups?.month);
  const day = Number(match.groups?.day);
  const hour = Number(match.groups?.hour);
  const minute = Number(match.groups?.minute);
  const second = Number(match.groups?.second ?? 0);
  const zone = match.groups?.zone ?? '';

  const calendarDate = new Date(Date.UTC(year, month - 1, day));
  const calendarIsValid =
    calendarDate.getUTCFullYear() === year &&
    calendarDate.getUTCMonth() === month - 1 &&
    calendarDate.getUTCDate() === day;
  const timeIsValid = hour <= 23 && minute <= 59 && second <= 59;
  const offsetIsValid = validateOffset(zone);

  if (!calendarIsValid || !timeIsValid || !offsetIsValid) {
    throw new BadRequestException(`${field} must be a valid ISO 8601 instant`);
  }
}

function validateOffset(zone: string): boolean {
  if (zone === 'Z') {
    return true;
  }

  const [hours, minutes] = zone.slice(1).split(':').map(Number);
  return hours < 14 ? minutes <= 59 : hours === 14 && minutes === 0;
}

function validateServerNow(value: Date): Date {
  if (!Number.isFinite(value.getTime())) {
    throw new Error('Server clock returned an invalid instant');
  }

  return new Date(value.getTime());
}

const ISO_INSTANT_WITH_ZONE =
  /^(?<year>\d{4})-(?<month>\d{2})-(?<day>\d{2})T(?<hour>\d{2}):(?<minute>\d{2})(?::(?<second>\d{2})(?:\.\d{1,9})?)?(?<zone>Z|[+-]\d{2}:\d{2})$/;
