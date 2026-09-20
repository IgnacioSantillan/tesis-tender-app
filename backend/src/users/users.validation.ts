import { BadRequestException } from '@nestjs/common';
import { SaveUserLocationRequestDto } from './dto/save-user-location-request.dto';

export interface ValidUserLocationInput {
  id: string;
  label: string;
  latitude: number | null;
  longitude: number | null;
}

export function validateUserLocationInput(input: SaveUserLocationRequestDto): ValidUserLocationInput {
  return {
    id: validateLocationId(input.locationId),
    label: validateLabel(input.label),
    latitude: validateCoordinate(input.latitude, 'latitude', -90, 90),
    longitude: validateCoordinate(input.longitude, 'longitude', -180, 180),
  };
}

function validateLocationId(value: unknown): string {
  if (typeof value !== 'string' || value.trim().length === 0) {
    throw new BadRequestException('locationId is required');
  }

  const normalized = value.trim();

  if (normalized.length > 64) {
    throw new BadRequestException('locationId must be 64 characters or fewer');
  }

  if (!/^[a-zA-Z0-9_-]+$/.test(normalized)) {
    throw new BadRequestException('locationId must contain only letters, numbers, underscores or hyphens');
  }

  return normalized;
}

function validateLabel(value: unknown): string {
  if (typeof value !== 'string' || value.trim().length === 0) {
    throw new BadRequestException('label is required');
  }

  const normalized = value.trim();

  if (normalized.length > 120) {
    throw new BadRequestException('label must be 120 characters or fewer');
  }

  return normalized;
}

function validateCoordinate(value: unknown, field: string, min: number, max: number): number | null {
  if (value === undefined || value === null) {
    return null;
  }

  if (typeof value !== 'number' || !Number.isFinite(value) || value < min || value > max) {
    throw new BadRequestException(`${field} must be a valid coordinate`);
  }

  return value;
}
