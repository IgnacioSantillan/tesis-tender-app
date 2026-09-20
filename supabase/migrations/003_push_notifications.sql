-- TenderApp real push notification schema.
-- Purpose: support backend-owned Android push registration and notification event audit.
-- Safe to run more than once.

create extension if not exists pgcrypto;

create table if not exists public.device_push_registrations (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users(id) on delete cascade,
  platform text not null default 'ANDROID',
  push_provider text not null default 'FCM',
  firebase_installation_id text,
  fcm_registration_token text,
  notification_opt_in boolean not null default true,
  app_version text,
  device_label text,
  last_seen_at timestamptz not null default now(),
  disabled_at timestamptz,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint device_push_registrations_platform_check
    check (platform in ('ANDROID')),
  constraint device_push_registrations_provider_check
    check (push_provider in ('FCM')),
  constraint device_push_registrations_recipient_check
    check (
      firebase_installation_id is not null
      or fcm_registration_token is not null
    )
);

alter table public.device_push_registrations
  add column if not exists user_id uuid references auth.users(id) on delete cascade,
  add column if not exists platform text not null default 'ANDROID',
  add column if not exists push_provider text not null default 'FCM',
  add column if not exists firebase_installation_id text,
  add column if not exists fcm_registration_token text,
  add column if not exists notification_opt_in boolean not null default true,
  add column if not exists app_version text,
  add column if not exists device_label text,
  add column if not exists last_seen_at timestamptz not null default now(),
  add column if not exists disabled_at timestamptz,
  add column if not exists created_at timestamptz not null default now(),
  add column if not exists updated_at timestamptz not null default now();

create table if not exists public.notification_events (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users(id) on delete cascade,
  device_registration_id uuid references public.device_push_registrations(id) on delete set null,
  laundry_load_id uuid references public.laundry_loads(id) on delete set null,
  category text not null,
  source text not null default 'BACKEND',
  title_key text,
  body_key text,
  payload jsonb not null default '{}'::jsonb,
  status text not null default 'PENDING',
  provider_message_id text,
  error_code text,
  scheduled_for timestamptz,
  sent_at timestamptz,
  created_at timestamptz not null default now(),
  constraint notification_events_category_check
    check (category in ('IDEAL_HANGING_TIME', 'DRYING_COMPLETE', 'RAIN_RISK', 'SYSTEM_TEST')),
  constraint notification_events_source_check
    check (source in ('ANDROID', 'BACKEND', 'SYSTEM')),
  constraint notification_events_status_check
    check (status in ('PENDING', 'SENT', 'FAILED', 'SKIPPED', 'CANCELLED')),
  constraint notification_events_payload_object_check
    check (jsonb_typeof(payload) = 'object')
);

alter table public.notification_events
  add column if not exists user_id uuid references auth.users(id) on delete cascade,
  add column if not exists device_registration_id uuid references public.device_push_registrations(id) on delete set null,
  add column if not exists laundry_load_id uuid references public.laundry_loads(id) on delete set null,
  add column if not exists category text,
  add column if not exists source text not null default 'BACKEND',
  add column if not exists title_key text,
  add column if not exists body_key text,
  add column if not exists payload jsonb not null default '{}'::jsonb,
  add column if not exists status text not null default 'PENDING',
  add column if not exists provider_message_id text,
  add column if not exists error_code text,
  add column if not exists scheduled_for timestamptz,
  add column if not exists sent_at timestamptz,
  add column if not exists created_at timestamptz not null default now();

do $$
begin
  if not exists (
    select 1 from pg_constraint
    where conname = 'device_push_registrations_platform_check'
      and conrelid = 'public.device_push_registrations'::regclass
  ) then
    alter table public.device_push_registrations
      add constraint device_push_registrations_platform_check
      check (platform in ('ANDROID')) not valid;
  end if;

  if not exists (
    select 1 from pg_constraint
    where conname = 'device_push_registrations_provider_check'
      and conrelid = 'public.device_push_registrations'::regclass
  ) then
    alter table public.device_push_registrations
      add constraint device_push_registrations_provider_check
      check (push_provider in ('FCM')) not valid;
  end if;

  if not exists (
    select 1 from pg_constraint
    where conname = 'device_push_registrations_recipient_check'
      and conrelid = 'public.device_push_registrations'::regclass
  ) then
    alter table public.device_push_registrations
      add constraint device_push_registrations_recipient_check
      check (
        firebase_installation_id is not null
        or fcm_registration_token is not null
      ) not valid;
  end if;

  if not exists (
    select 1 from pg_constraint
    where conname = 'notification_events_category_check'
      and conrelid = 'public.notification_events'::regclass
  ) then
    alter table public.notification_events
      add constraint notification_events_category_check
      check (category in ('IDEAL_HANGING_TIME', 'DRYING_COMPLETE', 'RAIN_RISK', 'SYSTEM_TEST')) not valid;
  end if;

  if not exists (
    select 1 from pg_constraint
    where conname = 'notification_events_source_check'
      and conrelid = 'public.notification_events'::regclass
  ) then
    alter table public.notification_events
      add constraint notification_events_source_check
      check (source in ('ANDROID', 'BACKEND', 'SYSTEM')) not valid;
  end if;

  if not exists (
    select 1 from pg_constraint
    where conname = 'notification_events_status_check'
      and conrelid = 'public.notification_events'::regclass
  ) then
    alter table public.notification_events
      add constraint notification_events_status_check
      check (status in ('PENDING', 'SENT', 'FAILED', 'SKIPPED', 'CANCELLED')) not valid;
  end if;

  if not exists (
    select 1 from pg_constraint
    where conname = 'notification_events_payload_object_check'
      and conrelid = 'public.notification_events'::regclass
  ) then
    alter table public.notification_events
      add constraint notification_events_payload_object_check
      check (jsonb_typeof(payload) = 'object') not valid;
  end if;
end $$;

create index if not exists device_push_registrations_user_id_last_seen_idx
  on public.device_push_registrations(user_id, last_seen_at desc);

create index if not exists device_push_registrations_active_user_idx
  on public.device_push_registrations(user_id, push_provider, platform)
  where disabled_at is null and notification_opt_in = true;

create unique index if not exists device_push_registrations_fid_user_provider_uq
  on public.device_push_registrations(user_id, push_provider, firebase_installation_id)
  where firebase_installation_id is not null;

create unique index if not exists device_push_registrations_fcm_token_uq
  on public.device_push_registrations(fcm_registration_token)
  where fcm_registration_token is not null;

create index if not exists notification_events_user_created_at_idx
  on public.notification_events(user_id, created_at desc);

create index if not exists notification_events_device_created_at_idx
  on public.notification_events(device_registration_id, created_at desc);

create index if not exists notification_events_status_scheduled_for_idx
  on public.notification_events(status, scheduled_for);

alter table public.device_push_registrations enable row level security;
alter table public.notification_events enable row level security;

-- Device push registrations are backend-owned because they contain sensitive
-- provider recipient identifiers. The backend uses the Supabase service role,
-- while direct authenticated client access remains blocked by the absence of
-- authenticated policies on this table.

do $$
begin
  if not exists (
    select 1 from pg_policies
    where schemaname = 'public'
      and tablename = 'notification_events'
      and policyname = 'notification_events select own rows'
  ) then
    create policy "notification_events select own rows"
      on public.notification_events
      for select
      to authenticated
      using (auth.uid() is not null and auth.uid() = user_id);
  end if;
end $$;

comment on table public.device_push_registrations is
  'Backend-owned push recipient registrations for TenderApp Android devices.';

comment on table public.notification_events is
  'Notification scheduling and delivery audit events for TenderApp.';

-- Optional manual verification after running:
-- select table_name
-- from information_schema.tables
-- where table_schema = 'public'
--   and table_name in ('device_push_registrations', 'notification_events')
-- order by table_name;
--
-- select schemaname, tablename, policyname, cmd
-- from pg_policies
-- where schemaname = 'public'
--   and tablename in ('device_push_registrations', 'notification_events')
-- order by tablename, policyname;
