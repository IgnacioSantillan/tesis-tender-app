-- TenderApp initial Supabase schema.
-- This migration is aligned with the current NestJS backend DTOs and data sources.
-- It is intentionally small for the MVP vertical slice.

create extension if not exists pgcrypto;

create table if not exists public.profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  email text,
  display_name text,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);
 
create table if not exists public.washers (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users(id) on delete cascade,
  name text not null,
  type text not null,
  capacity_kg numeric,
  energy_label text,
  water_usage_liters numeric,
  is_primary boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists public.laundry_loads (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users(id) on delete cascade,
  washer_id uuid references public.washers(id) on delete set null,
  clothing_type text not null,
  washing_program text not null,
  status text not null default 'PLANNED',
  location_id text not null,
  created_at timestamptz not null default now(),
  started_at timestamptz,
  completed_at timestamptz
);

alter table public.washers
  add column if not exists user_id uuid references auth.users(id) on delete cascade,
  add column if not exists name text,
  add column if not exists type text,
  add column if not exists capacity_kg numeric,
  add column if not exists energy_label text,
  add column if not exists water_usage_liters numeric,
  add column if not exists is_primary boolean not null default false,
  add column if not exists created_at timestamptz not null default now(),
  add column if not exists updated_at timestamptz not null default now();

alter table public.laundry_loads
  add column if not exists user_id uuid references auth.users(id) on delete cascade,
  add column if not exists washer_id uuid references public.washers(id) on delete set null,
  add column if not exists clothing_type text,
  add column if not exists washing_program text,
  add column if not exists status text not null default 'PLANNED',
  add column if not exists location_id text,
  add column if not exists created_at timestamptz not null default now(),
  add column if not exists started_at timestamptz,
  add column if not exists completed_at timestamptz;

do $$
begin
  if not exists (
    select 1 from pg_constraint where conname = 'washers_type_check'
  ) then
    alter table public.washers
      add constraint washers_type_check
      check (type in ('FRONT_LOAD', 'TOP_LOAD', 'WASHER_DRYER', 'OTHER')) not valid;
  end if;

  if not exists (
    select 1 from pg_constraint where conname = 'laundry_loads_clothing_type_check'
  ) then
    alter table public.laundry_loads
      add constraint laundry_loads_clothing_type_check
      check (clothing_type in ('LIGHT_CLOTHES', 'HEAVY_CLOTHES', 'BEDDING', 'DELICATES', 'MIXED')) not valid;
  end if;

  if not exists (
    select 1 from pg_constraint where conname = 'laundry_loads_washing_program_check'
  ) then
    alter table public.laundry_loads
      add constraint laundry_loads_washing_program_check
      check (washing_program in ('QUICK', 'NORMAL', 'ECO', 'DELICATE')) not valid;
  end if;

  if not exists (
    select 1 from pg_constraint where conname = 'laundry_loads_status_check'
  ) then
    alter table public.laundry_loads
      add constraint laundry_loads_status_check
      check (status in ('PLANNED', 'WASHING', 'DRYING', 'COMPLETED', 'CANCELLED')) not valid;
  end if;
end $$;

-- Existing spike data may not match the backend enum values yet.
-- The constraints above are added as NOT VALID so new rows are protected
-- while historical rows can be inspected and normalized before validation.
--
-- Useful inspection queries:
-- select id, clothing_type from public.laundry_loads
-- where clothing_type is not null
--   and clothing_type not in ('LIGHT_CLOTHES', 'HEAVY_CLOTHES', 'BEDDING', 'DELICATES', 'MIXED');
--
-- select id, washing_program from public.laundry_loads
-- where washing_program is not null
--   and washing_program not in ('QUICK', 'NORMAL', 'ECO', 'DELICATE');
--
-- select id, status from public.laundry_loads
-- where status is not null
--   and status not in ('PLANNED', 'WASHING', 'DRYING', 'COMPLETED', 'CANCELLED');

create index if not exists profiles_email_idx on public.profiles(email);
create index if not exists washers_user_id_created_at_idx on public.washers(user_id, created_at desc);
create index if not exists laundry_loads_user_id_created_at_idx on public.laundry_loads(user_id, created_at desc);
create index if not exists laundry_loads_washer_id_idx on public.laundry_loads(washer_id);

alter table public.profiles enable row level security;
alter table public.washers enable row level security;
alter table public.laundry_loads enable row level security;

do $$
begin
  if not exists (
    select 1 from pg_policies where schemaname = 'public' and tablename = 'profiles' and policyname = 'profiles select own row'
  ) then
    create policy "profiles select own row"
      on public.profiles
      for select
      to authenticated
      using (auth.uid() is not null and auth.uid() = id);
  end if;

  if not exists (
    select 1 from pg_policies where schemaname = 'public' and tablename = 'profiles' and policyname = 'profiles update own row'
  ) then
    create policy "profiles update own row"
      on public.profiles
      for update
      to authenticated
      using (auth.uid() is not null and auth.uid() = id)
      with check (auth.uid() is not null and auth.uid() = id);
  end if;

  if not exists (
    select 1 from pg_policies where schemaname = 'public' and tablename = 'washers' and policyname = 'washers select own rows'
  ) then
    create policy "washers select own rows"
      on public.washers
      for select
      to authenticated
      using (auth.uid() is not null and auth.uid() = user_id);
  end if;

  if not exists (
    select 1 from pg_policies where schemaname = 'public' and tablename = 'washers' and policyname = 'washers insert own rows'
  ) then
    create policy "washers insert own rows"
      on public.washers
      for insert
      to authenticated
      with check (auth.uid() is not null and auth.uid() = user_id);
  end if;

  if not exists (
    select 1 from pg_policies where schemaname = 'public' and tablename = 'washers' and policyname = 'washers update own rows'
  ) then
    create policy "washers update own rows"
      on public.washers
      for update
      to authenticated
      using (auth.uid() is not null and auth.uid() = user_id)
      with check (auth.uid() is not null and auth.uid() = user_id);
  end if;

  if not exists (
    select 1 from pg_policies where schemaname = 'public' and tablename = 'washers' and policyname = 'washers delete own rows'
  ) then
    create policy "washers delete own rows"
      on public.washers
      for delete
      to authenticated
      using (auth.uid() is not null and auth.uid() = user_id);
  end if;

  if not exists (
    select 1 from pg_policies where schemaname = 'public' and tablename = 'laundry_loads' and policyname = 'laundry_loads select own rows'
  ) then
    create policy "laundry_loads select own rows"
      on public.laundry_loads
      for select
      to authenticated
      using (auth.uid() is not null and auth.uid() = user_id);
  end if;

  if not exists (
    select 1 from pg_policies where schemaname = 'public' and tablename = 'laundry_loads' and policyname = 'laundry_loads insert own rows'
  ) then
    create policy "laundry_loads insert own rows"
      on public.laundry_loads
      for insert
      to authenticated
      with check (auth.uid() is not null and auth.uid() = user_id);
  end if;

  if not exists (
    select 1 from pg_policies where schemaname = 'public' and tablename = 'laundry_loads' and policyname = 'laundry_loads update own rows'
  ) then
    create policy "laundry_loads update own rows"
      on public.laundry_loads
      for update
      to authenticated
      using (auth.uid() is not null and auth.uid() = user_id)
      with check (auth.uid() is not null and auth.uid() = user_id);
  end if;

  if not exists (
    select 1 from pg_policies where schemaname = 'public' and tablename = 'laundry_loads' and policyname = 'laundry_loads delete own rows'
  ) then
    create policy "laundry_loads delete own rows"
      on public.laundry_loads
      for delete
      to authenticated
      using (auth.uid() is not null and auth.uid() = user_id);
  end if;
end $$;
