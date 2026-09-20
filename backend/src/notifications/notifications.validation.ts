import { BadRequestException } from '@nestjs/common';
import { RegisterDeviceRequestDto } from './dto/register-device-request.dto';
import { DEVICE_PLATFORMS, DevicePlatform, PUSH_PROVIDERS, PushProvider } from './dto/notification-device-types';

export interface ValidDeviceRegistrationInput {
  deviceToken: string;
  platform: DevicePlatform;
  pushProvider: PushProvider;
  notificationOptIn: boolean;
  appVersion: string | null;
}

export function validateDeviceRegistrationInput(input: RegisterDeviceRequestDto): ValidDeviceRegistrationInput {
  return {
    deviceToken: validateDeviceToken(input.deviceToken),
    platform: validateEnum(input.platform, DEVICE_PLATFORMS, 'platform'),
    pushProvider: validateEnum(input.pushProvider, PUSH_PROVIDERS, 'pushProvider'),
    notificationOptIn: validateBoolean(input.notificationOptIn, 'notificationOptIn'),
    appVersion: validateOptionalText(input.appVersion, 'appVersion'),
  };
}

function validateDeviceToken(value: unknown): string {
  if (typeof value !== 'string' || value.trim().length === 0) {
    throw new BadRequestException('deviceToken is required');
  }

  const normalized = value.trim();

  if (normalized.length > 4096) {
    throw new BadRequestException('deviceToken must be 4096 characters or fewer');
  }

  return normalized;
}

function validateBoolean(value: unknown, field: string): boolean {
  if (typeof value !== 'boolean') {
    throw new BadRequestException(`${field} must be boolean`);
  }

  return value;
}

function validateOptionalText(value: unknown, field: string): string | null {
  if (value === undefined || value === null) {
    return null;
  }

  if (typeof value !== 'string') {
    throw new BadRequestException(`${field} must be text`);
  }

  const normalized = value.trim();

  return normalized.length > 0 ? normalized : null;
}

function validateEnum<T extends string>(value: unknown, values: readonly T[], field: string): T {
  if (typeof value !== 'string' || !values.includes(value as T)) {
    throw new BadRequestException(`${field} is not supported`);
  }

  return value as T;
}
