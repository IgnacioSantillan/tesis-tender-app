import { NotificationsService } from './notifications.service';

describe('NotificationsService', () => {
  it('persists and returns an enabled registration when the user opted in', async () => {
    const dataSource = {
      upsertDeviceRegistration: jest.fn().mockResolvedValue({
        data: {
          id: 'registration-1',
          platform: 'ANDROID',
          push_provider: 'FCM',
          notification_opt_in: true,
          app_version: '1.0.0',
          disabled_at: null,
          created_at: '2026-07-10T12:00:00.000Z',
        },
        error: null,
      }),
    };
    const service = new NotificationsService(dataSource as never, createMessagingGateway() as never);

    const response = await service.registerDevice(
      { id: 'User_1', email: 'user@example.com' },
      {
        deviceToken: 'token-123',
        platform: 'ANDROID',
        pushProvider: 'FCM',
        notificationOptIn: true,
        appVersion: '1.0.0',
      },
    );

    expect(response).toMatchObject({
      id: 'registration-1',
      platform: 'ANDROID',
      pushProvider: 'FCM',
      status: 'ENABLED',
      appVersion: '1.0.0',
      registeredAt: '2026-07-10T12:00:00.000Z',
    });
    expect(dataSource.upsertDeviceRegistration).toHaveBeenCalledWith({
      userId: 'User_1',
      deviceToken: 'token-123',
      platform: 'ANDROID',
      pushProvider: 'FCM',
      notificationOptIn: true,
      appVersion: '1.0.0',
    });
  });

  it('keeps the registration disabled when the user did not opt in', async () => {
    const dataSource = {
      upsertDeviceRegistration: jest.fn().mockResolvedValue({
        data: {
          id: 'registration-1',
          platform: 'ANDROID',
          push_provider: 'FCM',
          notification_opt_in: false,
          app_version: null,
          disabled_at: '2026-07-10T12:00:00.000Z',
          created_at: '2026-07-10T12:00:00.000Z',
        },
        error: null,
      }),
    };
    const service = new NotificationsService(dataSource as never, createMessagingGateway() as never);

    const response = await service.registerDevice(
      { id: 'user-1', email: null },
      {
        deviceToken: 'token-123',
        platform: 'ANDROID',
        pushProvider: 'FCM',
        notificationOptIn: false,
      },
    );

    expect(response.status).toBe('DISABLED');
  });

  it('fails with a backend error when persistence fails', async () => {
    const service = new NotificationsService({
      upsertDeviceRegistration: jest.fn().mockResolvedValue({ data: null, error: new Error('database unavailable') }),
    } as never, createMessagingGateway() as never);

    await expect(
      service.registerDevice(
        { id: 'user-1', email: null },
        {
          deviceToken: 'token-123',
          platform: 'ANDROID',
          pushProvider: 'FCM',
          notificationOptIn: true,
        },
      ),
    ).rejects.toThrow('Unable to register notification device');
  });

  it('sends a test push to the latest active registration', async () => {
    const dataSource = {
      findLatestActiveFcmRegistration: jest.fn().mockResolvedValue({
        data: {
          id: 'registration-1',
          platform: 'ANDROID',
          push_provider: 'FCM',
          fcm_registration_token: 'fcm-token-123',
          notification_opt_in: true,
          app_version: '1.0.0',
          disabled_at: null,
          created_at: '2026-07-10T12:00:00.000Z',
        },
        error: null,
      }),
    };
    const messagingGateway = createMessagingGateway('projects/tesis-tender-app/messages/1');
    const service = new NotificationsService(dataSource as never, messagingGateway as never);

    await expect(
      service.sendTestPush(
        { id: 'user-1', email: 'user@example.com' },
        { title: 'Smoke', body: 'Ready' },
      ),
    ).resolves.toEqual({
      deviceRegistrationId: 'registration-1',
      providerMessageId: 'projects/tesis-tender-app/messages/1',
      status: 'SENT',
    });

    expect(dataSource.findLatestActiveFcmRegistration).toHaveBeenCalledWith('user-1');
    expect(messagingGateway.sendToToken).toHaveBeenCalledWith({
      token: 'fcm-token-123',
      title: 'Smoke',
      body: 'Ready',
      data: {
        category: 'SYSTEM_TEST',
        source: 'BACKEND_PUSH_003',
        deviceRegistrationId: 'registration-1',
      },
    });
  });

  it('fails test push when the user has no active registered device', async () => {
    const service = new NotificationsService({
      findLatestActiveFcmRegistration: jest.fn().mockResolvedValue({ data: null, error: null }),
    } as never, createMessagingGateway() as never);

    await expect(
      service.sendTestPush({ id: 'user-1', email: null }, {}),
    ).rejects.toThrow('No active FCM device registration found for user');
  });

  it('dispatches and audits a drying-complete domain push', async () => {
    const dataSource = {
      findLatestActiveFcmRegistration: jest.fn().mockResolvedValue({
        data: {
          id: 'registration-1',
          platform: 'ANDROID',
          push_provider: 'FCM',
          fcm_registration_token: 'fcm-token-123',
          notification_opt_in: true,
          app_version: '1.0.0',
          disabled_at: null,
          created_at: '2026-07-10T12:00:00.000Z',
        },
        error: null,
      }),
      createNotificationEvent: jest.fn().mockResolvedValue({ data: { id: 'event-1' }, error: null }),
    };
    const messagingGateway = createMessagingGateway('projects/tesis-tender-app/messages/2');
    const service = new NotificationsService(dataSource as never, messagingGateway as never);

    await expect(
      service.dispatchLaundryLoadCompleted({ id: 'user-1', email: 'user@example.com' }, 'load-1'),
    ).resolves.toEqual({
      status: 'SENT',
      deviceRegistrationId: 'registration-1',
      providerMessageId: 'projects/tesis-tender-app/messages/2',
    });

    expect(messagingGateway.sendToToken).toHaveBeenCalledWith({
      token: 'fcm-token-123',
      title: 'Laundry should be ready',
      body: 'Check whether the load is dry and ready to pick up.',
      data: {
        category: 'DRYING_COMPLETE',
        source: 'BACKEND_PUSH_004',
        laundryLoadId: 'load-1',
        notificationTitleKey: 'notification_drying_complete_title',
        notificationBodyKey: 'notification_drying_complete_message',
        deviceRegistrationId: 'registration-1',
      },
    });
    expect(dataSource.createNotificationEvent).toHaveBeenCalledWith(
      expect.objectContaining({
        userId: 'user-1',
        deviceRegistrationId: 'registration-1',
        laundryLoadId: 'load-1',
        category: 'DRYING_COMPLETE',
        status: 'SENT',
        providerMessageId: 'projects/tesis-tender-app/messages/2',
        errorCode: null,
        scheduledFor: null,
      }),
    );
  });

  it('audits a skipped drying-complete push when the user has no active device', async () => {
    const dataSource = {
      findLatestActiveFcmRegistration: jest.fn().mockResolvedValue({ data: null, error: null }),
      createNotificationEvent: jest.fn().mockResolvedValue({ data: { id: 'event-1' }, error: null }),
    };
    const messagingGateway = createMessagingGateway();
    const service = new NotificationsService(dataSource as never, messagingGateway as never);

    await expect(
      service.dispatchLaundryLoadCompleted({ id: 'user-1', email: null }, 'load-1'),
    ).resolves.toEqual({
      status: 'SKIPPED',
      errorCode: 'NO_ACTIVE_FCM_DEVICE',
    });

    expect(messagingGateway.sendToToken).not.toHaveBeenCalled();
    expect(dataSource.createNotificationEvent).toHaveBeenCalledWith(
      expect.objectContaining({
        userId: 'user-1',
        deviceRegistrationId: null,
        laundryLoadId: 'load-1',
        category: 'DRYING_COMPLETE',
        status: 'SKIPPED',
        errorCode: 'NO_ACTIVE_FCM_DEVICE',
        scheduledFor: null,
      }),
    );
  });

  it('creates a pending ideal-hanging notification event', async () => {
    const dataSource = {
      createNotificationEvent: jest.fn().mockResolvedValue({ data: { id: 'event-1' }, error: null }),
    };
    const service = new NotificationsService(dataSource as never, createMessagingGateway() as never);

    await expect(
      service.scheduleIdealHangingTime(
        { id: 'user-1', email: null },
        'load-1',
        '2026-07-10T12:00:00.000Z',
        '2026-07-10T14:00:00.000Z',
      ),
    ).resolves.toEqual({ status: 'PENDING' });

    expect(dataSource.createNotificationEvent).toHaveBeenCalledWith(
      expect.objectContaining({
        userId: 'user-1',
        deviceRegistrationId: null,
        laundryLoadId: 'load-1',
        category: 'IDEAL_HANGING_TIME',
        status: 'PENDING',
        providerMessageId: null,
        errorCode: null,
        scheduledFor: '2026-07-10T12:00:00.000Z',
        sentAt: null,
      }),
    );
  });

  it('cancels pending notification events for a laundry load', async () => {
    const dataSource = {
      cancelPendingEventsForLaundryLoad: jest.fn().mockResolvedValue({
        data: [{ id: 'event-1' }, { id: 'event-2' }],
        error: null,
      }),
    };
    const service = new NotificationsService(dataSource as never, createMessagingGateway() as never);

    await expect(
      service.cancelPendingLaundryNotifications({ id: 'user-1', email: null }, 'load-1', ['DRYING_COMPLETE']),
    ).resolves.toEqual({ status: 'CANCELLED', affectedEvents: 2 });

    expect(dataSource.cancelPendingEventsForLaundryLoad).toHaveBeenCalledWith('user-1', 'load-1', ['DRYING_COMPLETE']);
  });

  it('dispatches due pending notification events and marks them as sent', async () => {
    jest.useFakeTimers().setSystemTime(new Date('2026-07-25T22:21:00.000Z'));
    const dataSource = {
      listDuePendingEvents: jest.fn().mockResolvedValue({
        data: [
          {
            id: 'event-1',
            user_id: 'user-1',
            laundry_load_id: 'load-1',
            category: 'DRYING_COMPLETE',
            title_key: 'notification_drying_complete_title',
            body_key: 'notification_drying_complete_message',
            payload: { laundryLoadId: 'load-1' },
            status: 'PENDING',
          },
        ],
        error: null,
      }),
      findLatestActiveFcmRegistration: jest.fn().mockResolvedValue({
        data: {
          id: 'registration-1',
          platform: 'ANDROID',
          push_provider: 'FCM',
          fcm_registration_token: 'fcm-token-123',
          notification_opt_in: true,
          app_version: '1.0.0',
          disabled_at: null,
          created_at: '2026-07-10T12:00:00.000Z',
        },
        error: null,
      }),
      updateNotificationEventDispatch: jest.fn().mockResolvedValue({ data: { id: 'event-1' }, error: null }),
    };
    const messagingGateway = createMessagingGateway('projects/tesis-tender-app/messages/due-1');
    const service = new NotificationsService(dataSource as never, messagingGateway as never);

    await expect(service.dispatchDueNotifications({ limit: 5 })).resolves.toEqual({
      processed: 1,
      sent: 1,
      skipped: 0,
      failed: 0,
    });

    expect(dataSource.listDuePendingEvents).toHaveBeenCalledWith('2026-07-25T22:21:00.000Z', 5);
    expect(messagingGateway.sendToToken).toHaveBeenCalledWith(
      expect.objectContaining({
        token: 'fcm-token-123',
        title: 'Laundry should be ready',
        body: 'Check whether the load is dry and ready to pick up.',
        data: expect.objectContaining({
          category: 'DRYING_COMPLETE',
          source: 'BACKEND_NOTIF_DUE_001',
          laundryLoadId: 'load-1',
          notificationEventId: 'event-1',
        }),
      }),
    );
    expect(dataSource.updateNotificationEventDispatch).toHaveBeenCalledWith({
      eventId: 'event-1',
      status: 'SENT',
      deviceRegistrationId: 'registration-1',
      providerMessageId: 'projects/tesis-tender-app/messages/due-1',
      errorCode: null,
      sentAt: '2026-07-25T22:21:00.000Z',
    });
    jest.useRealTimers();
  });

  it('marks due pending notification events as skipped when no active device exists', async () => {
    const dataSource = {
      listDuePendingEvents: jest.fn().mockResolvedValue({
        data: [
          {
            id: 'event-1',
            user_id: 'user-1',
            laundry_load_id: 'load-1',
            category: 'DRYING_COMPLETE',
            title_key: null,
            body_key: null,
            payload: {},
            status: 'PENDING',
          },
        ],
        error: null,
      }),
      findLatestActiveFcmRegistration: jest.fn().mockResolvedValue({ data: null, error: null }),
      updateNotificationEventDispatch: jest.fn().mockResolvedValue({ data: { id: 'event-1' }, error: null }),
    };
    const messagingGateway = createMessagingGateway();
    const service = new NotificationsService(dataSource as never, messagingGateway as never);

    await expect(service.dispatchDueNotifications()).resolves.toEqual({
      processed: 1,
      sent: 0,
      skipped: 1,
      failed: 0,
    });

    expect(messagingGateway.sendToToken).not.toHaveBeenCalled();
    expect(dataSource.updateNotificationEventDispatch).toHaveBeenCalledWith(
      expect.objectContaining({
        eventId: 'event-1',
        status: 'SKIPPED',
        deviceRegistrationId: null,
        errorCode: 'NO_ACTIVE_FCM_DEVICE',
      }),
    );
  });
});

function createMessagingGateway(messageId = 'projects/test/messages/1') {
  return {
    sendToToken: jest.fn().mockResolvedValue(messageId),
  };
}
