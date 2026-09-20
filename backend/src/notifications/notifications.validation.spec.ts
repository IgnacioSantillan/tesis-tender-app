import { BadRequestException } from '@nestjs/common';
import { validateDeviceRegistrationInput } from './notifications.validation';

describe('validateDeviceRegistrationInput', () => {
  it('normalizes valid device registration input', () => {
    expect(
      validateDeviceRegistrationInput({
        deviceToken: ' token-123 ',
        platform: 'ANDROID',
        pushProvider: 'FCM',
        notificationOptIn: true,
        appVersion: ' 1.0.0 ',
      }),
    ).toEqual({
      deviceToken: 'token-123',
      platform: 'ANDROID',
      pushProvider: 'FCM',
      notificationOptIn: true,
      appVersion: '1.0.0',
    });
  });

  it('rejects missing device tokens', () => {
    expect(() =>
      validateDeviceRegistrationInput({
        deviceToken: '',
        platform: 'ANDROID',
        pushProvider: 'FCM',
        notificationOptIn: true,
      }),
    ).toThrow(BadRequestException);
  });

  it('rejects unsupported platforms', () => {
    expect(() =>
      validateDeviceRegistrationInput({
        deviceToken: 'token-123',
        platform: 'IOS' as never,
        pushProvider: 'FCM',
        notificationOptIn: true,
      }),
    ).toThrow(BadRequestException);
  });

  it('requires an explicit notification opt-in value', () => {
    expect(() =>
      validateDeviceRegistrationInput({
        deviceToken: 'token-123',
        platform: 'ANDROID',
        pushProvider: 'FCM',
        notificationOptIn: 'yes' as never,
      }),
    ).toThrow(BadRequestException);
  });
});
