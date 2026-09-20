import { NotificationsController } from './notifications.controller';

describe('NotificationsController', () => {
  it('delegates device registration to the service using the authenticated user', async () => {
    const response = { id: 'device-user-1-android' };
    const service = {
      registerDevice: jest.fn().mockResolvedValue(response),
      sendTestPush: jest.fn(),
    };
    const controller = new NotificationsController(service as never);
    const request = {
      headers: {},
      user: { id: 'user-1', email: 'user@example.com' },
    };
    const body = {
      deviceToken: 'token-123',
      platform: 'ANDROID' as const,
      pushProvider: 'FCM' as const,
      notificationOptIn: true,
    };

    await expect(controller.registerDevice(request, body)).resolves.toBe(response);
    expect(service.registerDevice).toHaveBeenCalledWith(request.user, body);
  });

  it('fails when the guard did not attach a user', () => {
    const controller = new NotificationsController({ registerDevice: jest.fn(), sendTestPush: jest.fn() } as never);

    expect(() =>
      controller.registerDevice(
        { headers: {} },
        {
          deviceToken: 'token-123',
          platform: 'ANDROID',
          pushProvider: 'FCM',
          notificationOptIn: true,
        },
      ),
    ).toThrow('Authenticated user missing after guard execution');
  });

  it('delegates test push to the service using the authenticated user', async () => {
    const response = {
      deviceRegistrationId: 'registration-1',
      providerMessageId: 'projects/test/messages/1',
      status: 'SENT',
    };
    const service = {
      registerDevice: jest.fn(),
      sendTestPush: jest.fn().mockResolvedValue(response),
    };
    const controller = new NotificationsController(service as never);
    const request = {
      headers: {},
      user: { id: 'user-1', email: 'user@example.com' },
    };
    const body = { title: 'Smoke', body: 'Ready' };

    await expect(controller.sendTestPush(request, body)).resolves.toBe(response);
    expect(service.sendTestPush).toHaveBeenCalledWith(request.user, body);
  });
});
