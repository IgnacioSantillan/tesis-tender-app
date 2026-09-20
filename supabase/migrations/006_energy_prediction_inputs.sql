-- TenderApp energy-aware prediction inputs.
-- Purpose: add nullable washer spin-speed and laundry cost-support fields for
-- ENERGY-PRED-001 / SUPABASE-ENERGY-001 without breaking existing data.
-- Safe to run more than once.

alter table public.washers
  add column if not exists default_spin_rpm integer;

alter table public.laundry_loads
  add column if not exists spin_rpm integer,
  add column if not exists load_size text,
  add column if not exists estimated_washing_energy_kwh numeric(8, 3),
  add column if not exists estimated_washing_water_liters numeric(8, 2),
  add column if not exists estimated_washing_cost_amount numeric(12, 2),
  add column if not exists estimated_washing_cost_currency text,
  add column if not exists estimated_washing_cost_level text,
  add column if not exists estimated_washing_cost_confidence text;

do $$
begin
  if not exists (
    select 1
    from pg_constraint
    where conrelid = 'public.washers'::regclass
      and conname = 'washers_default_spin_rpm_check'
  ) then
    alter table public.washers
      add constraint washers_default_spin_rpm_check
      check (
        default_spin_rpm is null
        or default_spin_rpm in (600, 800, 1000, 1200, 1400, 1600)
      ) not valid;
  end if;

  if not exists (
    select 1
    from pg_constraint
    where conrelid = 'public.laundry_loads'::regclass
      and conname = 'laundry_loads_spin_rpm_check'
  ) then
    alter table public.laundry_loads
      add constraint laundry_loads_spin_rpm_check
      check (
        spin_rpm is null
        or spin_rpm in (600, 800, 1000, 1200, 1400, 1600)
      ) not valid;
  end if;

  if not exists (
    select 1
    from pg_constraint
    where conrelid = 'public.laundry_loads'::regclass
      and conname = 'laundry_loads_load_size_check'
  ) then
    alter table public.laundry_loads
      add constraint laundry_loads_load_size_check
      check (
        load_size is null
        or load_size in ('SMALL', 'MEDIUM', 'LARGE')
      ) not valid;
  end if;

  if not exists (
    select 1
    from pg_constraint
    where conrelid = 'public.laundry_loads'::regclass
      and conname = 'laundry_loads_estimated_washing_energy_kwh_check'
  ) then
    alter table public.laundry_loads
      add constraint laundry_loads_estimated_washing_energy_kwh_check
      check (
        estimated_washing_energy_kwh is null
        or estimated_washing_energy_kwh >= 0
      ) not valid;
  end if;

  if not exists (
    select 1
    from pg_constraint
    where conrelid = 'public.laundry_loads'::regclass
      and conname = 'laundry_loads_estimated_washing_water_liters_check'
  ) then
    alter table public.laundry_loads
      add constraint laundry_loads_estimated_washing_water_liters_check
      check (
        estimated_washing_water_liters is null
        or estimated_washing_water_liters >= 0
      ) not valid;
  end if;

  if not exists (
    select 1
    from pg_constraint
    where conrelid = 'public.laundry_loads'::regclass
      and conname = 'laundry_loads_estimated_washing_cost_amount_check'
  ) then
    alter table public.laundry_loads
      add constraint laundry_loads_estimated_washing_cost_amount_check
      check (
        estimated_washing_cost_amount is null
        or estimated_washing_cost_amount >= 0
      ) not valid;
  end if;

  if not exists (
    select 1
    from pg_constraint
    where conrelid = 'public.laundry_loads'::regclass
      and conname = 'laundry_loads_estimated_washing_cost_currency_check'
  ) then
    alter table public.laundry_loads
      add constraint laundry_loads_estimated_washing_cost_currency_check
      check (
        estimated_washing_cost_currency is null
        or estimated_washing_cost_currency ~ '^[A-Z]{3}$'
      ) not valid;
  end if;

  if not exists (
    select 1
    from pg_constraint
    where conrelid = 'public.laundry_loads'::regclass
      and conname = 'laundry_loads_estimated_washing_cost_level_check'
  ) then
    alter table public.laundry_loads
      add constraint laundry_loads_estimated_washing_cost_level_check
      check (
        estimated_washing_cost_level is null
        or estimated_washing_cost_level in ('LOW', 'MEDIUM', 'HIGH', 'UNKNOWN')
      ) not valid;
  end if;

  if not exists (
    select 1
    from pg_constraint
    where conrelid = 'public.laundry_loads'::regclass
      and conname = 'laundry_loads_estimated_washing_cost_confidence_check'
  ) then
    alter table public.laundry_loads
      add constraint laundry_loads_estimated_washing_cost_confidence_check
      check (
        estimated_washing_cost_confidence is null
        or estimated_washing_cost_confidence in ('LOW', 'MEDIUM', 'HIGH')
      ) not valid;
  end if;
end $$;

comment on column public.washers.default_spin_rpm is
  'Habitual washer spin speed used as a smart default for energy-aware drying predictions.';

comment on column public.laundry_loads.spin_rpm is
  'Spin speed selected for the laundry cycle. Overrides washer.default_spin_rpm when present.';

comment on column public.laundry_loads.load_size is
  'Controlled load size used by drying duration and approximate washing consumption heuristics.';

comment on column public.laundry_loads.estimated_washing_energy_kwh is
  'Approximate estimated electrical consumption snapshot for the washing cycle.';

comment on column public.laundry_loads.estimated_washing_water_liters is
  'Approximate estimated water consumption snapshot for the washing cycle.';

comment on column public.laundry_loads.estimated_washing_cost_amount is
  'Optional approximate monetary cost snapshot when a tariff is configured.';

comment on column public.laundry_loads.estimated_washing_cost_currency is
  'ISO-like three-letter currency code for estimated_washing_cost_amount.';

comment on column public.laundry_loads.estimated_washing_cost_level is
  'Relative washing cost level used when exact tariffs are unavailable.';

comment on column public.laundry_loads.estimated_washing_cost_confidence is
  'Confidence level for the approximate washing cost snapshot.';

-- Optional manual verification after running:
-- select table_name, column_name, data_type, is_nullable
-- from information_schema.columns
-- where table_schema = 'public'
--   and (
--     (table_name = 'washers' and column_name = 'default_spin_rpm')
--     or (
--       table_name = 'laundry_loads'
--       and column_name in (
--         'spin_rpm',
--         'load_size',
--         'estimated_washing_energy_kwh',
--         'estimated_washing_water_liters',
--         'estimated_washing_cost_amount',
--         'estimated_washing_cost_currency',
--         'estimated_washing_cost_level',
--         'estimated_washing_cost_confidence'
--       )
--     )
--   )
-- order by table_name, column_name;
--
-- select conname, convalidated
-- from pg_constraint
-- where conrelid in ('public.washers'::regclass, 'public.laundry_loads'::regclass)
--   and (
--     conname like '%spin_rpm%'
--     or conname like '%estimated_washing%'
--     or conname = 'laundry_loads_load_size_check'
--   )
-- order by conname;
