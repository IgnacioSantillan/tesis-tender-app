-- TenderApp washer retirement lifecycle.
-- Purpose: preserve historical laundry loads while hiding retired washers
-- from active selection and management lists.
-- Safe to run more than once.

alter table public.washers
  add column if not exists retired_at timestamptz;

create index if not exists washers_active_user_created_at_idx
  on public.washers(user_id, created_at desc)
  where retired_at is null;

comment on column public.washers.retired_at is
  'Timestamp set when a washer is retired from active use. Historical laundry loads keep their washer reference.';

-- Optional manual verification after running:
-- select column_name, data_type, is_nullable
-- from information_schema.columns
-- where table_schema = 'public'
--   and table_name = 'washers'
--   and column_name = 'retired_at';
--
-- select indexname
-- from pg_indexes
-- where schemaname = 'public'
--   and tablename = 'washers'
--   and indexname = 'washers_active_user_created_at_idx';
