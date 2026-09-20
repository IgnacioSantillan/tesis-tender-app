-- TenderApp canonical notification event schema reset.
-- Purpose: replace an empty legacy notification_events table with the PB-020
-- canonical backend-owned push audit schema.
--
-- Safe guard: this migration refuses to run if notification_events contains
-- rows, because it drops and recreates the table.

create extension if not exists pgcrypto;

do $$
declare
  existing_rows bigint := 0;
begin
  if to_regclass('public.notification_events') is not null then
    execute 'select count(*) from public.notification_events' into existing_rows;

    if existing_rows > 0 then
      raise exception
        'notification_events contains % rows; export or migrate data before resetting schema',
        existing_rows;
    end if;

    drop table public.notification_events;
  end if;
end $$;

create table public.notification_events (
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

create index notification_events_user_created_at_idx
  on public.notification_events(user_id, created_at desc);

create index notification_events_device_created_at_idx
  on public.notification_events(device_registration_id, created_at desc);

create index notification_events_status_scheduled_for_idx
  on public.notification_events(status, scheduled_for);

alter table public.notification_events enable row level security;

create policy "notification_events select own rows"
  on public.notification_events
  for select
  to authenticated
  using (auth.uid() is not null and auth.uid() = user_id);

comment on table public.notification_events is
  'Canonical notification scheduling and delivery audit events for TenderApp PB-020.';

-- Optional verification:
-- select column_name, is_nullable, data_type
-- from information_schema.columns
-- where table_schema = 'public'
--   and table_name = 'notification_events'
-- order by ordinal_position;
