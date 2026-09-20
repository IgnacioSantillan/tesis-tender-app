import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { DEVICE_PLATFORMS, DevicePlatform, PUSH_PROVIDERS, PushProvider } from './notification-device-types';

export class RegisterDeviceRequestDto {
  @ApiProperty({ example: 'fcm-token-from-android-client' })
  deviceToken!: string;

  @ApiProperty({ enum: DEVICE_PLATFORMS, example: 'ANDROID' })
  platform!: DevicePlatform;

  @ApiProperty({ enum: PUSH_PROVIDERS, example: 'FCM' })
  pushProvider!: PushProvider;

  @ApiProperty({ example: true })
  notificationOptIn!: boolean;

  @ApiPropertyOptional({ example: '1.0.0' })
  appVersion?: string | null;
}
