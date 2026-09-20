export const DEVICE_PLATFORMS = ['ANDROID'] as const;
export const PUSH_PROVIDERS = ['FCM'] as const;
export const DEVICE_REGISTRATION_STATUSES = ['ENABLED', 'DISABLED'] as const;

export type DevicePlatform = (typeof DEVICE_PLATFORMS)[number];
export type PushProvider = (typeof PUSH_PROVIDERS)[number];
export type DeviceRegistrationStatus = (typeof DEVICE_REGISTRATION_STATUSES)[number];
