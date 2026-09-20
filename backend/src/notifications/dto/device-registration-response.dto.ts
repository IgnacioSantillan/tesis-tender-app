import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import {
  DEVICE_PLATFORMS,
  DEVICE_REGISTRATION_STATUSES,
  DevicePlatform,
  DeviceRegistrationStatus,
  PUSH_PROVIDERS,
  PushProvider,
} from './notification-device-types';

export class DeviceRegistrationResponseDto {
  @ApiProperty({ example: 'device-user-1-android' })
  id!: string;

  @ApiProperty({ enum: DEVICE_PLATFORMS, example: 'ANDROID' })
  platform!: DevicePlatform;

  @ApiProperty({ enum: PUSH_PROVIDERS, example: 'FCM' })
  pushProvider!: PushProvider;

  @ApiProperty({ enum: DEVICE_REGISTRATION_STATUSES, example: 'ENABLED' })
  status!: DeviceRegistrationStatus;

  @ApiProperty({ example: '2026-07-06T12:00:00.000Z' })
  registeredAt!: string;

  @ApiPropertyOptional({ example: '1.0.0', nullable: true })
  appVersion!: string | null;
}
