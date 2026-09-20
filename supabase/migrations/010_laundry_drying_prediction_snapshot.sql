-- TenderApp drying prediction snapshot.
-- Purpose: keep Dashboard progress stable after a load enters DRYING by storing
-- the drying estimate captured at that transition.
-- Safe to run more than once.

alter table public.laundry_loads
  add column if not exists drying_estimated_minutes_at_start integer,
  add column if not exists drying_estimated_pickup_at timestamptz;

comment on column public.laundry_loads.drying_estimated_minutes_at_start is
  'Drying minutes estimated when the load entered DRYING status. Used as stable progress basis.';

comment on column public.laundry_loads.drying_estimated_pickup_at is
  'Estimated pickup timestamp calculated when the load entered DRYING status.';

-- Optional manual verification after running:
-- select column_name, data_type
-- from information_schema.columns
-- where table_schema = 'public'
--   and table_name = 'laundry_loads'
--   and column_name in ('drying_estimated_minutes_at_start', 'drying_estimated_pickup_at')
-- order by column_name;
