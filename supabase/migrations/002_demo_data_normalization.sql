-- TenderApp demo data normalization.
-- Purpose: make existing MVP/demo rows suitable for connected Android screenshots.
-- Safe to run more than once.

create extension if not exists pgcrypto;

-- Existing demo/spike databases may already have VALID enum constraints.
-- Drop them before cleanup so legacy rows can be normalized safely, then
-- recreate and validate them after the data is aligned.
alter table public.washers
  drop constraint if exists washers_type_check;

alter table public.laundry_loads
  drop constraint if exists laundry_loads_clothing_type_check,
  drop constraint if exists laundry_loads_washing_program_check,
  drop constraint if exists laundry_loads_status_check;

-- 1. Normalize washer rows to the backend enum contract.
update public.washers
set
  name = case
    when name is null or length(trim(name)) = 0 then 'Lavarropas demo'
    else trim(name)
  end,
  type = case upper(trim(coalesce(type, '')))
    when 'FRONT_LOAD' then 'FRONT_LOAD'
    when 'FRONT' then 'FRONT_LOAD'
    when 'FRONTLOAD' then 'FRONT_LOAD'
    when 'TOP_LOAD' then 'TOP_LOAD'
    when 'TOP' then 'TOP_LOAD'
    when 'TOPLOAD' then 'TOP_LOAD'
    when 'WASHER_DRYER' then 'WASHER_DRYER'
    when 'WASHERDRYER' then 'WASHER_DRYER'
    when 'DRYER' then 'WASHER_DRYER'
    when 'OTHER' then 'OTHER'
    else 'FRONT_LOAD'
  end,
  capacity_kg = coalesce(capacity_kg, 7.0),
  energy_label = coalesce(nullif(trim(energy_label), ''), 'A'),
  water_usage_liters = coalesce(water_usage_liters, 45.0),
  is_primary = coalesce(is_primary, false),
  updated_at = now();

-- 2. Ensure users with laundry rows have at least one washer for demo linking.
insert into public.washers (
  user_id,
  name,
  type,
  capacity_kg,
  energy_label,
  water_usage_liters,
  is_primary
)
select distinct
  laundry_loads.user_id,
  'Lavarropas demo',
  'FRONT_LOAD',
  7.0,
  'A',
  45.0,
  true
from public.laundry_loads
where laundry_loads.user_id is not null
  and not exists (
    select 1
    from public.washers
    where washers.user_id = laundry_loads.user_id
  );

-- 3. Keep exactly one primary washer per user with washer rows.
with ranked_washers as (
  select
    id,
    row_number() over (
      partition by user_id
      order by is_primary desc, created_at desc, id
    ) as row_number_for_user
  from public.washers
  where user_id is not null
)
update public.washers
set is_primary = ranked_washers.row_number_for_user = 1,
    updated_at = now()
from ranked_washers
where washers.id = ranked_washers.id;

-- 4. Normalize laundry rows to the backend enum contract and stable MVP location.
update public.laundry_loads
set
  clothing_type = case upper(trim(coalesce(clothing_type, '')))
    when 'LIGHT' then 'LIGHT_CLOTHES'
    when 'LIGHT_CLOTHES' then 'LIGHT_CLOTHES'
    when 'HEAVY' then 'HEAVY_CLOTHES'
    when 'HEAVY_CLOTHES' then 'HEAVY_CLOTHES'
    when 'BEDDING' then 'BEDDING'
    when 'DELICATES' then 'DELICATES'
    when 'DELICATE' then 'DELICATES'
    when 'MIXED' then 'MIXED'
    else 'MIXED'
  end,
  washing_program = case upper(trim(coalesce(washing_program, '')))
    when 'QUICK' then 'QUICK'
    when 'NORMAL' then 'NORMAL'
    when 'ECO' then 'ECO'
    when 'DELICATE' then 'DELICATE'
    else 'NORMAL'
  end,
  status = case upper(trim(coalesce(status, '')))
    when 'PLANNED' then 'PLANNED'
    when 'WASHING' then 'WASHING'
    when 'DRYING' then 'DRYING'
    when 'COMPLETED' then 'COMPLETED'
    when 'CANCELLED' then 'CANCELLED'
    else 'PLANNED'
  end,
  location_id = case
    when location_id is null or length(trim(location_id)) = 0 or upper(trim(location_id)) = 'UNKNOWN' then 'home'
    else trim(location_id)
  end;

-- 5. Link orphan laundry rows to the user's primary washer when possible.
with primary_washers as (
  select distinct on (user_id)
    user_id,
    id
  from public.washers
  where user_id is not null
  order by user_id, is_primary desc, created_at desc, id
)
update public.laundry_loads
set washer_id = primary_washers.id
from primary_washers
where laundry_loads.user_id = primary_washers.user_id
  and (
    laundry_loads.washer_id is null
    or not exists (
      select 1
      from public.washers
      where washers.id = laundry_loads.washer_id
        and washers.user_id = laundry_loads.user_id
    )
  );

-- 6. Add a minimal user location table for MVP demo evidence and future settings persistence.
create table if not exists public.user_locations (
  user_id uuid not null references auth.users(id) on delete cascade,
  id text not null,
  label text not null,
  latitude numeric,
  longitude numeric,
  is_primary boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  primary key (user_id, id)
);

alter table public.user_locations enable row level security;

do $$
begin
  if not exists (
    select 1 from pg_policies
    where schemaname = 'public'
      and tablename = 'user_locations'
      and policyname = 'user_locations select own rows'
  ) then
    create policy "user_locations select own rows"
      on public.user_locations
      for select
      to authenticated
      using (auth.uid() is not null and auth.uid() = user_id);
  end if;

  if not exists (
    select 1 from pg_policies
    where schemaname = 'public'
      and tablename = 'user_locations'
      and policyname = 'user_locations insert own rows'
  ) then
    create policy "user_locations insert own rows"
      on public.user_locations
      for insert
      to authenticated
      with check (auth.uid() is not null and auth.uid() = user_id);
  end if;

  if not exists (
    select 1 from pg_policies
    where schemaname = 'public'
      and tablename = 'user_locations'
      and policyname = 'user_locations update own rows'
  ) then
    create policy "user_locations update own rows"
      on public.user_locations
      for update
      to authenticated
      using (auth.uid() is not null and auth.uid() = user_id)
      with check (auth.uid() is not null and auth.uid() = user_id);
  end if;

  if not exists (
    select 1 from pg_policies
    where schemaname = 'public'
      and tablename = 'user_locations'
      and policyname = 'user_locations delete own rows'
  ) then
    create policy "user_locations delete own rows"
      on public.user_locations
      for delete
      to authenticated
      using (auth.uid() is not null and auth.uid() = user_id);
  end if;
end $$;

insert into public.user_locations (
  user_id,
  id,
  label,
  latitude,
  longitude,
  is_primary
)
select distinct
  source_users.user_id,
  'home',
  'Home patio',
  -34.6037,
  -58.3816,
  true
from (
  select user_id from public.washers where user_id is not null
  union
  select user_id from public.laundry_loads where user_id is not null
) as source_users
on conflict (user_id, id) do update
set
  label = excluded.label,
  latitude = excluded.latitude,
  longitude = excluded.longitude,
  is_primary = true,
  updated_at = now();

-- 7. Recreate and validate enum constraints after cleanup.
alter table public.washers
  add constraint washers_type_check
  check (type in ('FRONT_LOAD', 'TOP_LOAD', 'WASHER_DRYER', 'OTHER'));

alter table public.laundry_loads
  add constraint laundry_loads_clothing_type_check
  check (clothing_type in ('LIGHT_CLOTHES', 'HEAVY_CLOTHES', 'BEDDING', 'DELICATES', 'MIXED'));

alter table public.laundry_loads
  add constraint laundry_loads_washing_program_check
  check (washing_program in ('QUICK', 'NORMAL', 'ECO', 'DELICATE'));

alter table public.laundry_loads
  add constraint laundry_loads_status_check
  check (status in ('PLANNED', 'WASHING', 'DRYING', 'COMPLETED', 'CANCELLED'));

-- Optional manual inspection after running:
-- select clothing_type, washing_program, status, location_id, count(*)
-- from public.laundry_loads
-- group by clothing_type, washing_program, status, location_id
-- order by count(*) desc;
--
-- select user_id, count(*) as washers, count(*) filter (where is_primary) as primary_washers
-- from public.washers
-- group by user_id;
