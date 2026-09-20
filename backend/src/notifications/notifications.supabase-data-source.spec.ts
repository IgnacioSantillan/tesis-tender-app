import { NotificationsSupabaseDataSource } from './notifications.supabase-data-source';

const registrationRow = {
  id: 'registration-1',
  platform: 'ANDROID',
  push_provider: 'FCM',
  notification_opt_in: true,
  app_version: '1.0.0',
  disabled_at: null,
  created_at: '2026-07-10T12:00:00.000Z',
};

describe('NotificationsSupabaseDataSource', () => {
  it('inserts new device registrations with explicit user ownership', async () => {
    const { dataSource, from, insert } = createDataSource({
      lookupRegistrationResult: { data: null, error: null },
      insertRegistrationResult: { data: registrationRow, error: null },
    });

    await expect(
      dataSource.upsertDeviceRegistration({
        userId: 'user-1',
        deviceToken: 'fcm-token-123',
        platform: 'ANDROID',
        pushProvider: 'FCM',
        notificationOptIn: true,
        appVersion: '1.0.0',
      }),
    ).resolves.toEqual({ data: registrationRow, error: null });

    expect(from).toHaveBeenCalledWith('device_push_registrations');
    expect(insert).toHaveBeenCalledWith(
      expect.objectContaining({
        user_id: 'user-1',
        platform: 'ANDROID',
        push_provider: 'FCM',
        fcm_registration_token: 'fcm-token-123',
        notification_opt_in: true,
        app_version: '1.0.0',
        disabled_at: null,
      }),
    );
  });

  it('updates existing device registrations by token', async () => {
    const { dataSource, update, updateEq } = createDataSource({
      lookupRegistrationResult: { data: { id: 'registration-1' }, error: null },
      updateRegistrationResult: {
        data: {
          ...registrationRow,
          notification_opt_in: false,
          disabled_at: '2026-07-10T12:00:00.000Z',
        },
        error: null,
      },
    });

    await dataSource.upsertDeviceRegistration({
      userId: 'user-1',
      deviceToken: 'fcm-token-123',
      platform: 'ANDROID',
      pushProvider: 'FCM',
      notificationOptIn: false,
      appVersion: null,
    });

    expect(update).toHaveBeenCalledWith(
      expect.objectContaining({
        user_id: 'user-1',
        fcm_registration_token: 'fcm-token-123',
        notification_opt_in: false,
        disabled_at: expect.any(String),
      }),
    );
    expect(updateEq).toHaveBeenCalledWith('id', 'registration-1');
  });

  it('returns lookup errors before writing device registrations', async () => {
    const lookupError = new Error('lookup failed');
    const { dataSource, insert, update } = createDataSource({
      lookupRegistrationResult: { data: null, error: lookupError },
    });

    await expect(
      dataSource.upsertDeviceRegistration({
        userId: 'user-1',
        deviceToken: 'fcm-token-123',
        platform: 'ANDROID',
        pushProvider: 'FCM',
        notificationOptIn: true,
        appVersion: null,
      }),
    ).resolves.toEqual({ data: null, error: lookupError });

    expect(insert).not.toHaveBeenCalled();
    expect(update).not.toHaveBeenCalled();
  });

  it('loads the latest active FCM registration for a user', async () => {
    const { dataSource, from, eq, is, not, order, limit, maybeSingle } = createDataSource({
      activeRegistrationResult: {
        data: {
          ...registrationRow,
          fcm_registration_token: 'fcm-token-123',
        },
        error: null,
      },
    });

    await expect(dataSource.findLatestActiveFcmRegistration('user-1')).resolves.toEqual({
      data: {
        ...registrationRow,
        fcm_registration_token: 'fcm-token-123',
      },
      error: null,
    });

    expect(from).toHaveBeenCalledWith('device_push_registrations');
    expect(eq).toHaveBeenCalledWith('user_id', 'user-1');
    expect(eq).toHaveBeenCalledWith('platform', 'ANDROID');
    expect(eq).toHaveBeenCalledWith('push_provider', 'FCM');
    expect(eq).toHaveBeenCalledWith('notification_opt_in', true);
    expect(is).toHaveBeenCalledWith('disabled_at', null);
    expect(not).toHaveBeenCalledWith('fcm_registration_token', 'is', null);
    expect(order).toHaveBeenCalledWith('last_seen_at', { ascending: false });
    expect(limit).toHaveBeenCalledWith(1);
    expect(maybeSingle).toHaveBeenCalled();
  });

  it('creates notification event audit rows', async () => {
    const notificationEventRow = {
      id: 'event-1',
      user_id: 'user-1',
      device_registration_id: 'registration-1',
      laundry_load_id: 'load-1',
      category: 'DRYING_COMPLETE',
      status: 'SENT',
      provider_message_id: 'projects/test/messages/1',
      error_code: null,
      created_at: '2026-07-10T12:00:00.000Z',
    };
    const { dataSource, from, insertNotificationEvent } = createDataSource({
      insertNotificationEventResult: { data: notificationEventRow, error: null },
    });

    await expect(
      dataSource.createNotificationEvent({
        userId: 'user-1',
        deviceRegistrationId: 'registration-1',
        laundryLoadId: 'load-1',
        category: 'DRYING_COMPLETE',
        titleKey: 'notification_drying_complete_title',
        bodyKey: 'notification_drying_complete_message',
        payload: { laundryLoadId: 'load-1' },
        status: 'SENT',
        providerMessageId: 'projects/test/messages/1',
        errorCode: null,
        scheduledFor: null,
        sentAt: '2026-07-10T12:00:00.000Z',
      }),
    ).resolves.toEqual({ data: notificationEventRow, error: null });

    expect(from).toHaveBeenCalledWith('notification_events');
    expect(insertNotificationEvent).toHaveBeenCalledWith(
      expect.objectContaining({
        user_id: 'user-1',
        device_registration_id: 'registration-1',
        laundry_load_id: 'load-1',
        category: 'DRYING_COMPLETE',
        source: 'BACKEND',
        title_key: 'notification_drying_complete_title',
        body_key: 'notification_drying_complete_message',
        status: 'SENT',
        provider_message_id: 'projects/test/messages/1',
        error_code: null,
        scheduled_for: null,
      }),
    );
  });

  it('cancels pending notification events for a laundry load', async () => {
    const { dataSource, from, updateNotificationEvent, notificationEq, notificationIn, notificationSelect } =
      createDataSource({
        cancelNotificationEventResult: { data: [{ id: 'event-1' }], error: null },
      });

    await expect(
      dataSource.cancelPendingEventsForLaundryLoad('user-1', 'load-1', ['IDEAL_HANGING_TIME', 'DRYING_COMPLETE']),
    ).resolves.toEqual({ data: [{ id: 'event-1' }], error: null });

    expect(from).toHaveBeenCalledWith('notification_events');
    expect(updateNotificationEvent).toHaveBeenCalledWith({ status: 'CANCELLED' });
    expect(notificationEq).toHaveBeenCalledWith('user_id', 'user-1');
    expect(notificationEq).toHaveBeenCalledWith('laundry_load_id', 'load-1');
    expect(notificationEq).toHaveBeenCalledWith('status', 'PENDING');
    expect(notificationIn).toHaveBeenCalledWith('category', ['IDEAL_HANGING_TIME', 'DRYING_COMPLETE']);
    expect(notificationSelect).toHaveBeenCalled();
  });

  it('lists due pending notification events ordered by scheduled time', async () => {
    const dueRows = [
      {
        id: 'event-1',
        user_id: 'user-1',
        device_registration_id: null,
        laundry_load_id: 'load-1',
        category: 'DRYING_COMPLETE',
        title_key: 'notification_drying_complete_title',
        body_key: 'notification_drying_complete_message',
        payload: { laundryLoadId: 'load-1' },
        status: 'PENDING',
        provider_message_id: null,
        error_code: null,
        scheduled_for: '2026-07-25T22:21:00.000Z',
        sent_at: null,
        created_at: '2026-07-25T20:52:06.000Z',
      },
    ];
    const { dataSource, from, dueEq, dueNot, dueLte, dueOrder, dueLimit } = createDataSource({
      dueNotificationEventResult: { data: dueRows, error: null },
    });

    await expect(dataSource.listDuePendingEvents('2026-07-25T22:22:00.000Z', 10)).resolves.toEqual({
      data: dueRows,
      error: null,
    });

    expect(from).toHaveBeenCalledWith('notification_events');
    expect(dueEq).toHaveBeenCalledWith('status', 'PENDING');
    expect(dueNot).toHaveBeenCalledWith('scheduled_for', 'is', null);
    expect(dueLte).toHaveBeenCalledWith('scheduled_for', '2026-07-25T22:22:00.000Z');
    expect(dueOrder).toHaveBeenCalledWith('scheduled_for', { ascending: true });
    expect(dueLimit).toHaveBeenCalledWith(10);
  });

  it('updates pending notification event dispatch outcome by id and pending status', async () => {
    const { dataSource, updateNotificationEvent, dispatchEq, dispatchSelect } = createDataSource({
      dispatchNotificationEventResult: { data: { id: 'event-1', status: 'SENT' }, error: null },
    });

    await expect(
      dataSource.updateNotificationEventDispatch({
        eventId: 'event-1',
        status: 'SENT',
        deviceRegistrationId: 'registration-1',
        providerMessageId: 'projects/test/messages/1',
        errorCode: null,
        sentAt: '2026-07-25T22:21:00.000Z',
      }),
    ).resolves.toEqual({ data: { id: 'event-1', status: 'SENT' }, error: null });

    expect(updateNotificationEvent).toHaveBeenCalledWith({
      status: 'SENT',
      device_registration_id: 'registration-1',
      provider_message_id: 'projects/test/messages/1',
      error_code: null,
      sent_at: '2026-07-25T22:21:00.000Z',
    });
    expect(dispatchEq).toHaveBeenCalledWith('id', 'event-1');
    expect(dispatchEq).toHaveBeenCalledWith('status', 'PENDING');
    expect(dispatchSelect).toHaveBeenCalled();
  });
});

function createDataSource(results: {
  lookupRegistrationResult?: unknown;
  insertRegistrationResult?: unknown;
  updateRegistrationResult?: unknown;
  activeRegistrationResult?: unknown;
  insertNotificationEventResult?: unknown;
  cancelNotificationEventResult?: unknown;
  dueNotificationEventResult?: unknown;
  dispatchNotificationEventResult?: unknown;
}) {
  const insertRegistrationSingle = jest
    .fn()
    .mockResolvedValue(results.insertRegistrationResult ?? { data: registrationRow, error: null });
  const insertSelect = jest.fn().mockReturnValue({ single: insertRegistrationSingle });
  const insert = jest.fn().mockReturnValue({ select: insertSelect });
  const updateSingle = jest
    .fn()
    .mockResolvedValue(results.updateRegistrationResult ?? { data: registrationRow, error: null });
  const updateSelect = jest.fn().mockReturnValue({ single: updateSingle });
  const updateEq = jest.fn().mockReturnValue({ select: updateSelect });
  const update = jest.fn().mockReturnValue({ eq: updateEq });
  const lookupMaybeSingle = jest
    .fn()
    .mockResolvedValue(results.lookupRegistrationResult ?? { data: null, error: null });
  const lookupEq = jest.fn().mockReturnValue({ maybeSingle: lookupMaybeSingle });
  const notificationEventSingle = jest
    .fn()
    .mockResolvedValue(results.insertNotificationEventResult ?? { data: null, error: null });
  const notificationInsertSelect = jest.fn().mockReturnValue({ single: notificationEventSingle });
  const insertNotificationEvent = jest.fn().mockReturnValue({ select: notificationInsertSelect });
  const notificationSelect = jest
    .fn()
    .mockResolvedValue(results.cancelNotificationEventResult ?? { data: [], error: null });
  const notificationIn = jest.fn().mockReturnValue({ select: notificationSelect });
  const notificationEq = jest.fn().mockReturnThis();
  notificationEq.mockReturnValue({ eq: notificationEq, in: notificationIn, select: notificationSelect });
  const dueLimit = jest.fn().mockResolvedValue(results.dueNotificationEventResult ?? { data: [], error: null });
  const dueOrder = jest.fn().mockReturnValue({ limit: dueLimit });
  const dueLte = jest.fn().mockReturnValue({ order: dueOrder });
  const dueNot = jest.fn().mockReturnValue({ lte: dueLte });
  const dueEq = jest.fn().mockReturnValue({ not: dueNot });
  const dueSelect = jest.fn().mockReturnValue({ eq: dueEq });
  const dispatchMaybeSingle = jest
    .fn()
    .mockResolvedValue(results.dispatchNotificationEventResult ?? { data: null, error: null });
  const dispatchSelect = jest.fn().mockReturnValue({ maybeSingle: dispatchMaybeSingle });
  const dispatchEq = jest.fn().mockReturnThis();
  dispatchEq.mockReturnValue({ eq: dispatchEq, select: dispatchSelect });
  const updateNotificationEvent = jest.fn().mockImplementation((payload: Record<string, unknown>) => {
    if ('sent_at' in payload || 'provider_message_id' in payload) {
      return { eq: dispatchEq };
    }

    return { eq: notificationEq };
  });
  const maybeSingle = jest.fn().mockResolvedValue(results.activeRegistrationResult ?? { data: null, error: null });
  const limit = jest.fn().mockReturnValue({ maybeSingle });
  const order = jest.fn().mockReturnValue({ limit });
  const not = jest.fn().mockReturnValue({ order });
  const is = jest.fn().mockReturnValue({ not });
  const eq = jest.fn().mockReturnThis();
  const selectForQuery = jest.fn().mockReturnValue({ eq });
  eq.mockReturnValue({ eq, is });
  const from = jest.fn().mockImplementation((table: string) => {
    if (table === 'notification_events') {
      return {
        insert: insertNotificationEvent,
        update: updateNotificationEvent,
        select: dueSelect,
      };
    }

    return {
      insert,
      update,
      select: jest.fn().mockImplementation((columns: string) => {
        if (columns === 'id') {
          return { eq: lookupEq };
        }

        return selectForQuery(columns);
      }),
    };
  });
  const dataSource = new NotificationsSupabaseDataSource({
    getClient: () => ({ from }),
  } as never);

  return {
    dataSource,
    from,
    insert,
    update,
    updateEq,
    insertNotificationEvent,
    updateNotificationEvent,
    notificationEq,
    notificationIn,
    notificationSelect,
    dueEq,
    dueNot,
    dueLte,
    dueOrder,
    dueLimit,
    dispatchEq,
    dispatchSelect,
    eq,
    is,
    not,
    order,
    limit,
    maybeSingle,
  };
}
