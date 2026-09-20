import { Injectable } from '@nestjs/common';
import { SupabaseService } from '../supabase/supabase.service';
import { DevicePlatform, PushProvider } from './dto/notification-device-types';

export interface UpsertDeviceRegistrationRecord {
  userId: string;
  deviceToken: string;
  platform: DevicePlatform;
  pushProvider: PushProvider;
  notificationOptIn: boolean;
  appVersion: string | null;
}

export interface DeviceRegistrationRow {
  id: string;
  platform: DevicePlatform;
  push_provider: PushProvider;
  fcm_registration_token?: string | null;
  notification_opt_in: boolean;
  app_version: string | null;
  disabled_at: string | null;
  created_at: string;
}

export interface CreateNotificationEventRecord {
  userId: string;
  deviceRegistrationId: string | null;
  laundryLoadId: string | null;
  category: NotificationEventCategory;
  titleKey: string;
  bodyKey: string;
  payload: Record<string, unknown>;
  status: NotificationEventStatus;
  providerMessageId: string | null;
  errorCode: string | null;
  scheduledFor: string | null;
  sentAt: string | null;
}

export interface NotificationEventRow {
  id: string;
  user_id: string;
  device_registration_id: string | null;
  laundry_load_id: string | null;
  category: NotificationEventCategory;
  title_key: string | null;
  body_key: string | null;
  payload: Record<string, unknown>;
  status: NotificationEventStatus;
  provider_message_id: string | null;
  error_code: string | null;
  scheduled_for: string | null;
  sent_at: string | null;
  created_at: string;
}

export interface UpdateNotificationEventDispatchRecord {
  eventId: string;
  status: NotificationEventStatus;
  deviceRegistrationId: string | null;
  providerMessageId: string | null;
  errorCode: string | null;
  sentAt: string | null;
}

export type NotificationEventCategory = 'IDEAL_HANGING_TIME' | 'DRYING_COMPLETE' | 'RAIN_RISK' | 'SYSTEM_TEST';
export type NotificationEventStatus = 'PENDING' | 'SENT' | 'FAILED' | 'SKIPPED' | 'CANCELLED';

export interface NotificationQueryResult<T> {
  data: T | null;
  error: unknown;
}

const DEVICE_REGISTRATION_COLUMNS =
  'id,platform,push_provider,notification_opt_in,app_version,disabled_at,created_at';

const ACTIVE_DEVICE_REGISTRATION_COLUMNS =
  'id,platform,push_provider,fcm_registration_token,notification_opt_in,app_version,disabled_at,created_at';

const NOTIFICATION_EVENT_COLUMNS =
  'id,user_id,device_registration_id,laundry_load_id,category,title_key,body_key,payload,status,provider_message_id,error_code,scheduled_for,sent_at,created_at';

@Injectable()
export class NotificationsSupabaseDataSource {
  constructor(private readonly supabaseService: SupabaseService) {}

  async upsertDeviceRegistration(
    record: UpsertDeviceRegistrationRecord,
  ): Promise<NotificationQueryResult<DeviceRegistrationRow>> {
    const now = new Date().toISOString();
    const client = this.supabaseService.getClient();
    const payload = {
      user_id: record.userId,
      platform: record.platform,
      push_provider: record.pushProvider,
      fcm_registration_token: record.deviceToken,
      notification_opt_in: record.notificationOptIn,
      app_version: record.appVersion,
      last_seen_at: now,
      disabled_at: record.notificationOptIn ? null : now,
      updated_at: now,
    };

    const { data: existingRegistration, error: lookupError } = await client
      .from('device_push_registrations')
      .select('id')
      .eq('fcm_registration_token', record.deviceToken)
      .maybeSingle();

    if (lookupError) {
      return { data: null, error: lookupError };
    }

    if (existingRegistration?.id) {
      const { data, error } = await client
        .from('device_push_registrations')
        .update(payload)
        .eq('id', existingRegistration.id)
        .select(DEVICE_REGISTRATION_COLUMNS)
        .single();

      return { data: data as DeviceRegistrationRow | null, error };
    }

    const { data, error } = await client
      .from('device_push_registrations')
      .insert(payload)
      .select(DEVICE_REGISTRATION_COLUMNS)
      .single();

    return { data: data as DeviceRegistrationRow | null, error };
  }

  async findLatestActiveFcmRegistration(userId: string): Promise<NotificationQueryResult<DeviceRegistrationRow>> {
    const { data, error } = await this.supabaseService
      .getClient()
      .from('device_push_registrations')
      .select(ACTIVE_DEVICE_REGISTRATION_COLUMNS)
      .eq('user_id', userId)
      .eq('platform', 'ANDROID')
      .eq('push_provider', 'FCM')
      .eq('notification_opt_in', true)
      .is('disabled_at', null)
      .not('fcm_registration_token', 'is', null)
      .order('last_seen_at', { ascending: false })
      .limit(1)
      .maybeSingle();

    return { data: data as DeviceRegistrationRow | null, error };
  }

  async createNotificationEvent(
    record: CreateNotificationEventRecord,
  ): Promise<NotificationQueryResult<NotificationEventRow>> {
    const { data, error } = await this.supabaseService
      .getClient()
      .from('notification_events')
      .insert({
        user_id: record.userId,
        device_registration_id: record.deviceRegistrationId,
        laundry_load_id: record.laundryLoadId,
        category: record.category,
        source: 'BACKEND',
        title_key: record.titleKey,
        body_key: record.bodyKey,
        payload: record.payload,
        status: record.status,
        provider_message_id: record.providerMessageId,
        error_code: record.errorCode,
        scheduled_for: record.scheduledFor,
        sent_at: record.sentAt,
      })
      .select(NOTIFICATION_EVENT_COLUMNS)
      .single();

    return { data: data as NotificationEventRow | null, error };
  }

  async cancelPendingEventsForLaundryLoad(
    userId: string,
    laundryLoadId: string,
    categories: NotificationEventCategory[],
  ): Promise<NotificationQueryResult<NotificationEventRow[]>> {
    let query = this.supabaseService
      .getClient()
      .from('notification_events')
      .update({ status: 'CANCELLED' })
      .eq('user_id', userId)
      .eq('laundry_load_id', laundryLoadId)
      .eq('status', 'PENDING');

    if (categories.length > 0) {
      query = query.in('category', categories);
    }

    const { data, error } = await query.select(NOTIFICATION_EVENT_COLUMNS);
    return { data: data as NotificationEventRow[] | null, error };
  }

  async listDuePendingEvents(
    nowIso: string,
    limit: number,
  ): Promise<NotificationQueryResult<NotificationEventRow[]>> {
    const { data, error } = await this.supabaseService
      .getClient()
      .from('notification_events')
      .select(NOTIFICATION_EVENT_COLUMNS)
      .eq('status', 'PENDING')
      .not('scheduled_for', 'is', null)
      .lte('scheduled_for', nowIso)
      .order('scheduled_for', { ascending: true })
      .limit(limit);

    return { data: data as NotificationEventRow[] | null, error };
  }

  async updateNotificationEventDispatch(
    record: UpdateNotificationEventDispatchRecord,
  ): Promise<NotificationQueryResult<NotificationEventRow>> {
    const { data, error } = await this.supabaseService
      .getClient()
      .from('notification_events')
      .update({
        status: record.status,
        device_registration_id: record.deviceRegistrationId,
        provider_message_id: record.providerMessageId,
        error_code: record.errorCode,
        sent_at: record.sentAt,
      })
      .eq('id', record.eventId)
      .eq('status', 'PENDING')
      .select(NOTIFICATION_EVENT_COLUMNS)
      .maybeSingle();

    return { data: data as NotificationEventRow | null, error };
  }
}
