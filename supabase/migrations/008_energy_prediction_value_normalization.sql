-- TenderApp energy-aware value normalization.
-- Purpose: normalize legacy nullable energy fields that may have been inserted
-- before SUPABASE-ENERGY-001 constraints were applied.
-- Safe to run more than once.

update public.laundry_loads
set load_size = upper(trim(load_size))
where load_size is not null
  and upper(trim(load_size)) in ('SMALL', 'MEDIUM', 'LARGE')
  and load_size <> upper(trim(load_size));

update public.laundry_loads
set load_size = null
where load_size is not null
  and upper(trim(load_size)) not in ('SMALL', 'MEDIUM', 'LARGE');

update public.laundry_loads
set estimated_washing_cost_level = upper(trim(estimated_washing_cost_level))
where estimated_washing_cost_level is not null
  and upper(trim(estimated_washing_cost_level)) in ('LOW', 'MEDIUM', 'HIGH', 'UNKNOWN')
  and estimated_washing_cost_level <> upper(trim(estimated_washing_cost_level));

update public.laundry_loads
set estimated_washing_cost_level = null
where estimated_washing_cost_level is not null
  and upper(trim(estimated_washing_cost_level)) not in ('LOW', 'MEDIUM', 'HIGH', 'UNKNOWN');

update public.laundry_loads
set estimated_washing_cost_confidence = upper(trim(estimated_washing_cost_confidence))
where estimated_washing_cost_confidence is not null
  and upper(trim(estimated_washing_cost_confidence)) in ('LOW', 'MEDIUM', 'HIGH')
  and estimated_washing_cost_confidence <> upper(trim(estimated_washing_cost_confidence));

update public.laundry_loads
set estimated_washing_cost_confidence = null
where estimated_washing_cost_confidence is not null
  and upper(trim(estimated_washing_cost_confidence)) not in ('LOW', 'MEDIUM', 'HIGH');

update public.laundry_loads
set estimated_washing_cost_currency = upper(trim(estimated_washing_cost_currency))
where estimated_washing_cost_currency is not null
  and upper(trim(estimated_washing_cost_currency)) ~ '^[A-Z]{3}$'
  and estimated_washing_cost_currency <> upper(trim(estimated_washing_cost_currency));

update public.laundry_loads
set estimated_washing_cost_currency = null
where estimated_washing_cost_currency is not null
  and upper(trim(estimated_washing_cost_currency)) !~ '^[A-Z]{3}$';

-- Optional manual verification after running:
-- select id, load_size, estimated_washing_cost_level, estimated_washing_cost_confidence, estimated_washing_cost_currency
-- from public.laundry_loads
-- where (load_size is not null and load_size not in ('SMALL', 'MEDIUM', 'LARGE'))
--    or (estimated_washing_cost_level is not null and estimated_washing_cost_level not in ('LOW', 'MEDIUM', 'HIGH', 'UNKNOWN'))
--    or (estimated_washing_cost_confidence is not null and estimated_washing_cost_confidence not in ('LOW', 'MEDIUM', 'HIGH'))
--    or (estimated_washing_cost_currency is not null and estimated_washing_cost_currency !~ '^[A-Z]{3}$');
