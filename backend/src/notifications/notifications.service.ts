import { Injectable, InternalServerErrorException, Logger, NotFoundException } from '@nestjs/common';
import { AuthenticatedUser } from '../auth/authenticated-user';
import { DeviceRegistrationResponseDto } from './dto/device-registration-response.dto';
import { DispatchDueNotificationsRequestDto } from './dto/dispatch-due-notifications-request.dto';
import { DispatchDueNotificationsResponseDto } from './dto/dispatch-due-notifications-response.dto';
import { RegisterDeviceRequestDto } from './dto/register-device-request.dto';
import { TestPushRequestDto } from './dto/test-push-request.dto';
import { TestPushResponseDto } from './dto/test-push-response.dto';
import { FirebaseAdminMessagingGateway } from './firebase-admin-messaging.gateway';
import {
  DeviceRegistrationRow,
  NotificationEventCategory,
  NotificationEventStatus,
  NotificationsSupabaseDataSource,
} from './notifications.supabase-data-source';
import { validateDeviceRegistrationInput } from './notifications.validation';

@Injectable()
export class NotificationsService {
  private readonly logger = new Logger(NotificationsService.name);

  constructor(
    private readonly dataSource: NotificationsSupabaseDataSource,
    private readonly messagingGateway: FirebaseAdminMessagingGateway,
  ) {}

  async registerDevice(user: AuthenticatedUser, body: RegisterDeviceRequestDto): Promise<DeviceRegistrationResponseDto> {
    const input = validateDeviceRegistrationInput(body);
    const { data, error } = await this.dataSource.upsertDeviceRegistration({
      userId: user.id,
      deviceToken: input.deviceToken,
      platform: input.platform,
      pushProvider: input.pushProvider,
      notificationOptIn: input.notificationOptIn,
      appVersion: input.appVersion,
    });

    if (error || !data) {
      this.logger.warn(`Unable to register notification device: ${formatOperationalError(error)}`);
      throw new InternalServerErrorException('Unable to register notification device');
    }

    return mapDeviceRegistrationRow(data);
  }

  async sendTestPush(user: AuthenticatedUser, body: TestPushRequestDto): Promise<TestPushResponseDto> {
    const { data, error } = await this.dataSource.findLatestActiveFcmRegistration(user.id);

    if (error) {
      this.logger.warn(`Unable to load notification device: ${formatOperationalError(error)}`);
      throw new InternalServerErrorException('Unable to load notification device');
    }

    if (!data?.fcm_registration_token) {
      throw new NotFoundException('No active FCM device registration found for user');
    }

    const providerMessageId = await this.messagingGateway.sendToToken({
      token: data.fcm_registration_token,
      title: normalizeText(body.title, 'TenderApp test'),
      body: normalizeText(body.body, 'Push channel is ready.'),
      data: {
        category: 'SYSTEM_TEST',
        source: 'BACKEND_PUSH_003',
        deviceRegistrationId: data.id,
      },
    });

    return {
      deviceRegistrationId: data.id,
      providerMessageId,
      status: 'SENT',
    };
  }

  async dispatchLaundryLoadCompleted(user: AuthenticatedUser, laundryLoadId: string): Promise<DomainPushDispatchResult> {
    return this.dispatchDomainPush(user, {
      category: 'DRYING_COMPLETE',
      laundryLoadId,
      content: {
        titleKey: 'notification_drying_complete_title',
        bodyKey: 'notification_drying_complete_message',
        title: 'Laundry should be ready',
        body: 'Check whether the load is dry and ready to pick up.',
      },
      payload: {
        laundryLoadId,
        event: 'LAUNDRY_LOAD_COMPLETED',
      },
    });
  }

  async scheduleIdealHangingTime(
    user: AuthenticatedUser,
    laundryLoadId: string,
    scheduledFor: string | null = null,
    windowEnd: string | null = null,
  ): Promise<NotificationAutomationResult> {
    return this.recordPendingDomainNotification(user.id, {
      category: 'IDEAL_HANGING_TIME',
      laundryLoadId,
      content: {
        titleKey: 'notification_ideal_hanging_time_title',
        bodyKey: 'notification_ideal_hanging_time_message',
        title: 'Best time to hang laundry',
        body: 'Weather looks suitable for hanging this load.',
      },
      payload: {
        laundryLoadId,
        event: 'IDEAL_HANGING_TIME_PENDING',
        recommendedHangWindowStart: scheduledFor,
        recommendedHangWindowEnd: windowEnd,
      },
      scheduledFor,
    });
  }

  async scheduleDryingComplete(
    user: AuthenticatedUser,
    laundryLoadId: string,
    scheduledFor: string | null = null,
  ): Promise<NotificationAutomationResult> {
    return this.recordPendingDomainNotification(user.id, {
      category: 'DRYING_COMPLETE',
      laundryLoadId,
      content: {
        titleKey: 'notification_drying_complete_title',
        bodyKey: 'notification_drying_complete_message',
        title: 'Laundry should be ready',
        body: 'Check whether the load is dry and ready to pick up.',
      },
      payload: {
        laundryLoadId,
        event: 'DRYING_COMPLETE_PENDING',
      },
      scheduledFor,
    });
  }

  async cancelPendingLaundryNotifications(
    user: AuthenticatedUser,
    laundryLoadId: string,
    categories: NotificationEventCategory[] = ['IDEAL_HANGING_TIME', 'DRYING_COMPLETE', 'RAIN_RISK'],
  ): Promise<NotificationAutomationResult> {
    const { data, error } = await this.dataSource.cancelPendingEventsForLaundryLoad(user.id, laundryLoadId, categories);

    if (error) {
      this.logger.warn(`Unable to cancel pending notification events for ${laundryLoadId}: ${formatOperationalError(error)}`);
      return { status: 'FAILED', errorCode: 'PENDING_NOTIFICATION_CANCEL_FAILED' };
    }

    return { status: 'CANCELLED', affectedEvents: data?.length ?? 0 };
  }

  async dispatchDueNotifications(
    body: DispatchDueNotificationsRequestDto = {},
  ): Promise<DispatchDueNotificationsResponseDto> {
    const limit = normalizeDispatchLimit(body.limit);
    const { data, error } = await this.dataSource.listDuePendingEvents(new Date().toISOString(), limit);

    if (error) {
      this.logger.warn(`Unable to load due notification events: ${formatOperationalError(error)}`);
      throw new InternalServerErrorException('Unable to load due notification events');
    }

    const summary: DispatchDueNotificationsResponseDto = {
      processed: 0,
      sent: 0,
      skipped: 0,
      failed: 0,
    };

    for (const event of data ?? []) {
      summary.processed += 1;
      const result = await this.dispatchPendingEvent(event);
      if (result === 'SENT') summary.sent += 1;
      if (result === 'SKIPPED') summary.skipped += 1;
      if (result === 'FAILED') summary.failed += 1;
    }

    return summary;
  }

  private async dispatchDomainPush(
    user: AuthenticatedUser,
    request: DomainPushDispatchRequest,
  ): Promise<DomainPushDispatchResult> {
    const { data, error } = await this.dataSource.findLatestActiveFcmRegistration(user.id);

    if (error) {
      await this.recordDomainNotification(user.id, request, {
        status: 'FAILED',
        deviceRegistrationId: null,
        providerMessageId: null,
        errorCode: 'DEVICE_LOOKUP_FAILED',
        sentAt: null,
      });
      return { status: 'FAILED', errorCode: 'DEVICE_LOOKUP_FAILED' };
    }

    if (!data?.fcm_registration_token) {
      await this.recordDomainNotification(user.id, request, {
        status: 'SKIPPED',
        deviceRegistrationId: null,
        providerMessageId: null,
        errorCode: 'NO_ACTIVE_FCM_DEVICE',
        sentAt: null,
      });
      return { status: 'SKIPPED', errorCode: 'NO_ACTIVE_FCM_DEVICE' };
    }

    try {
      const providerMessageId = await this.messagingGateway.sendToToken({
        token: data.fcm_registration_token,
        title: request.content.title,
        body: request.content.body,
        data: {
          category: request.category,
          source: 'BACKEND_PUSH_004',
          laundryLoadId: request.laundryLoadId ?? '',
          notificationTitleKey: request.content.titleKey,
          notificationBodyKey: request.content.bodyKey,
          deviceRegistrationId: data.id,
        },
      });

      await this.recordDomainNotification(user.id, request, {
        status: 'SENT',
        deviceRegistrationId: data.id,
        providerMessageId,
        errorCode: null,
        sentAt: new Date().toISOString(),
      });

      return { status: 'SENT', deviceRegistrationId: data.id, providerMessageId };
    } catch (sendError) {
      this.logger.warn(`Domain push dispatch failed for ${request.category}: ${formatOperationalError(sendError)}`);
      await this.recordDomainNotification(user.id, request, {
        status: 'FAILED',
        deviceRegistrationId: data.id,
        providerMessageId: null,
        errorCode: 'FCM_SEND_FAILED',
        sentAt: null,
      });
      return { status: 'FAILED', deviceRegistrationId: data.id, errorCode: 'FCM_SEND_FAILED' };
    }
  }

  private async dispatchPendingEvent(
    event: {
      id: string;
      user_id: string;
      laundry_load_id: string | null;
      category: NotificationEventCategory;
      title_key: string | null;
      body_key: string | null;
      payload: Record<string, unknown>;
    },
  ): Promise<'SENT' | 'SKIPPED' | 'FAILED'> {
    const { data: registration, error } = await this.dataSource.findLatestActiveFcmRegistration(event.user_id);

    if (error) {
      await this.updatePendingEventOutcome(event.id, {
        status: 'FAILED',
        deviceRegistrationId: null,
        providerMessageId: null,
        errorCode: 'DEVICE_LOOKUP_FAILED',
        sentAt: null,
      });
      return 'FAILED';
    }

    if (!registration?.fcm_registration_token) {
      await this.updatePendingEventOutcome(event.id, {
        status: 'SKIPPED',
        deviceRegistrationId: null,
        providerMessageId: null,
        errorCode: 'NO_ACTIVE_FCM_DEVICE',
        sentAt: null,
      });
      return 'SKIPPED';
    }

    const content = resolveNotificationContent(event.category, event.title_key, event.body_key);

    try {
      const providerMessageId = await this.messagingGateway.sendToToken({
        token: registration.fcm_registration_token,
        title: content.title,
        body: content.body,
        data: {
          category: event.category,
          source: 'BACKEND_NOTIF_DUE_001',
          laundryLoadId: event.laundry_load_id ?? '',
          notificationTitleKey: content.titleKey,
          notificationBodyKey: content.bodyKey,
          deviceRegistrationId: registration.id,
          notificationEventId: event.id,
          ...stringifyPayloadValues(event.payload),
        },
      });

      await this.updatePendingEventOutcome(event.id, {
        status: 'SENT',
        deviceRegistrationId: registration.id,
        providerMessageId,
        errorCode: null,
        sentAt: new Date().toISOString(),
      });
      return 'SENT';
    } catch (sendError) {
      this.logger.warn(`Due notification dispatch failed for ${event.id}: ${formatOperationalError(sendError)}`);
      await this.updatePendingEventOutcome(event.id, {
        status: 'FAILED',
        deviceRegistrationId: registration.id,
        providerMessageId: null,
        errorCode: 'FCM_SEND_FAILED',
        sentAt: null,
      });
      return 'FAILED';
    }
  }

  private async updatePendingEventOutcome(
    eventId: string,
    outcome: {
      status: NotificationEventStatus;
      deviceRegistrationId: string | null;
      providerMessageId: string | null;
      errorCode: string | null;
      sentAt: string | null;
    },
  ): Promise<void> {
    const { error } = await this.dataSource.updateNotificationEventDispatch({
      eventId,
      status: outcome.status,
      deviceRegistrationId: outcome.deviceRegistrationId,
      providerMessageId: outcome.providerMessageId,
      errorCode: outcome.errorCode,
      sentAt: outcome.sentAt,
    });

    if (error) {
      this.logger.warn(`Unable to update notification event ${eventId}: ${formatOperationalError(error)}`);
    }
  }

  private async recordDomainNotification(
    userId: string,
    request: DomainPushDispatchRequest,
    outcome: {
      status: NotificationEventStatus;
      deviceRegistrationId: string | null;
      providerMessageId: string | null;
      errorCode: string | null;
      sentAt: string | null;
    },
  ): Promise<void> {
    const { error } = await this.dataSource.createNotificationEvent({
      userId,
      deviceRegistrationId: outcome.deviceRegistrationId,
      laundryLoadId: request.laundryLoadId,
      category: request.category,
      titleKey: request.content.titleKey,
      bodyKey: request.content.bodyKey,
      payload: request.payload,
      status: outcome.status,
      providerMessageId: outcome.providerMessageId,
      errorCode: outcome.errorCode,
      scheduledFor: null,
      sentAt: outcome.sentAt,
    });

    if (error) {
      this.logger.warn(`Unable to audit notification event ${request.category}: ${formatOperationalError(error)}`);
    }
  }

  private async recordPendingDomainNotification(
    userId: string,
    request: DomainPushDispatchRequest,
  ): Promise<NotificationAutomationResult> {
    const { error } = await this.dataSource.createNotificationEvent({
      userId,
      deviceRegistrationId: null,
      laundryLoadId: request.laundryLoadId,
      category: request.category,
      titleKey: request.content.titleKey,
      bodyKey: request.content.bodyKey,
      payload: request.payload,
      status: 'PENDING',
      providerMessageId: null,
      errorCode: null,
      scheduledFor: request.scheduledFor ?? null,
      sentAt: null,
    });

    if (error) {
      this.logger.warn(`Unable to schedule notification event ${request.category}: ${formatOperationalError(error)}`);
      return { status: 'FAILED', errorCode: 'PENDING_NOTIFICATION_CREATE_FAILED' };
    }

    return { status: 'PENDING' };
  }
}

export interface DomainPushDispatchResult {
  status: 'SENT' | 'FAILED' | 'SKIPPED';
  deviceRegistrationId?: string;
  providerMessageId?: string;
  errorCode?: string;
}

export interface NotificationAutomationResult {
  status: 'PENDING' | 'CANCELLED' | 'FAILED';
  affectedEvents?: number;
  errorCode?: string;
}

interface DomainPushDispatchRequest {
  category: NotificationEventCategory;
  laundryLoadId: string | null;
  content: {
    titleKey: string;
    bodyKey: string;
    title: string;
    body: string;
  };
  payload: Record<string, unknown>;
  scheduledFor?: string | null;
}

function normalizeText(value: string | null | undefined, fallback: string): string {
  const normalized = value?.trim();
  return normalized && normalized.length > 0 ? normalized : fallback;
}

function normalizeDispatchLimit(value: number | undefined): number {
  if (typeof value !== 'number' || !Number.isInteger(value)) {
    return DEFAULT_DUE_DISPATCH_LIMIT;
  }

  return Math.min(Math.max(value, MIN_DUE_DISPATCH_LIMIT), MAX_DUE_DISPATCH_LIMIT);
}

function resolveNotificationContent(
  category: NotificationEventCategory,
  titleKey: string | null,
  bodyKey: string | null,
): {
  titleKey: string;
  bodyKey: string;
  title: string;
  body: string;
} {
  const fallback = NOTIFICATION_CONTENT_BY_CATEGORY[category] ?? NOTIFICATION_CONTENT_BY_CATEGORY.SYSTEM_TEST;
  return {
    titleKey: titleKey ?? fallback.titleKey,
    bodyKey: bodyKey ?? fallback.bodyKey,
    title: fallback.title,
    body: fallback.body,
  };
}

function stringifyPayloadValues(payload: Record<string, unknown>): Record<string, string> {
  return Object.fromEntries(
    Object.entries(payload)
      .filter(([key, value]) => key.trim().length > 0 && value !== undefined && value !== null)
      .map(([key, value]) => [key, typeof value === 'string' ? value : JSON.stringify(value)]),
  );
}

function formatOperationalError(error: unknown): string {
  if (!error) {
    return 'unknown';
  }

  if (error instanceof Error) {
    return `${error.name}: ${error.message}`;
  }

  if (typeof error !== 'object') {
    return String(error);
  }

  const maybeError = error as {
    code?: unknown;
    message?: unknown;
    details?: unknown;
    hint?: unknown;
    name?: unknown;
    status?: unknown;
  };
  const fields = [
    ['name', maybeError.name],
    ['code', maybeError.code],
    ['status', maybeError.status],
    ['message', maybeError.message],
    ['details', maybeError.details],
    ['hint', maybeError.hint],
  ]
    .filter(([, value]) => value !== undefined && value !== null && String(value).trim().length > 0)
    .map(([key, value]) => `${key}=${String(value)}`);

  return fields.length > 0 ? fields.join(' ') : 'unrecognized error object';
}

function mapDeviceRegistrationRow(row: DeviceRegistrationRow): DeviceRegistrationResponseDto {
  return {
    id: row.id,
    platform: row.platform,
    pushProvider: row.push_provider,
    status: row.notification_opt_in && !row.disabled_at ? 'ENABLED' : 'DISABLED',
    registeredAt: row.created_at,
    appVersion: row.app_version,
  };
}

const MIN_DUE_DISPATCH_LIMIT = 1;
const MAX_DUE_DISPATCH_LIMIT = 50;
const DEFAULT_DUE_DISPATCH_LIMIT = 10;

const NOTIFICATION_CONTENT_BY_CATEGORY: Record<NotificationEventCategory, {
  titleKey: string;
  bodyKey: string;
  title: string;
  body: string;
}> = {
  IDEAL_HANGING_TIME: {
    titleKey: 'notification_ideal_hanging_time_title',
    bodyKey: 'notification_ideal_hanging_time_message',
    title: 'Best time to hang laundry',
    body: 'Weather looks suitable for hanging this load.',
  },
  DRYING_COMPLETE: {
    titleKey: 'notification_drying_complete_title',
    bodyKey: 'notification_drying_complete_message',
    title: 'Laundry should be ready',
    body: 'Check whether the load is dry and ready to pick up.',
  },
  RAIN_RISK: {
    titleKey: 'notification_rain_risk_title',
    bodyKey: 'notification_rain_risk_message',
    title: 'Rain risk changed',
    body: 'Check the weather before leaving laundry outside.',
  },
  SYSTEM_TEST: {
    titleKey: 'notification_system_test_title',
    bodyKey: 'notification_system_test_message',
    title: 'TenderApp test',
    body: 'Push channel is ready.',
  },
};
