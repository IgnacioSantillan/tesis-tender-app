-- TenderApp drying location normalization.
-- Purpose: separate weather/household location from physical drying location.
-- Safe to run more than once.

do $$
begin
  if not exists (
    select 1
    from information_schema.columns
    where table_schema = 'public'
      and table_name = 'laundry_loads'
      and column_name = 'drying_location_id'
  ) then
    alter table public.laundry_loads
      add column drying_location_id text not null default 'PATIO';
  end if;
end $$;

do $$
begin
  if exists (
    select 1
    from information_schema.table_constraints
    where table_schema = 'public'
      and table_name = 'laundry_loads'
      and constraint_name = 'laundry_loads_drying_location_id_check'
  ) then
    alter table public.laundry_loads
      drop constraint laundry_loads_drying_location_id_check;
  end if;

  alter table public.laundry_loads
    add constraint laundry_loads_drying_location_id_check
    check (drying_location_id in ('INDOOR', 'BALCONY', 'OUTDOOR_LINE', 'PATIO', 'LAUNDRY_ROOM'));
end $$;

comment on column public.laundry_loads.drying_location_id is
  'Physical drying location used by prediction and notification automation. Distinct from weather/household location.';

-- Optional manual verification after running:
-- select column_name, data_type, column_default, is_nullable
-- from information_schema.columns
-- where table_schema = 'public'
--   and table_name = 'laundry_loads'
--   and column_name in ('location_id', 'drying_location_id')
-- order by column_name;
