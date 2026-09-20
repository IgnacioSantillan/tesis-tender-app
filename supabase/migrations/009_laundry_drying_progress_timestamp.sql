-- TenderApp drying progress timestamp.
-- Purpose: distinguish washing start from drying start so Dashboard progress can
-- represent elapsed drying time instead of weather suitability.
-- Safe to run more than once.

alter table public.laundry_loads
  add column if not exists drying_started_at timestamptz;

comment on column public.laundry_loads.drying_started_at is
  'Timestamp captured when a laundry load enters DRYING status. Used for elapsed drying progress.';

-- Optional manual verification after running:
-- select column_name, data_type
-- from information_schema.columns
-- where table_schema = 'public'
--   and table_name = 'laundry_loads'
--   and column_name = 'drying_started_at';
