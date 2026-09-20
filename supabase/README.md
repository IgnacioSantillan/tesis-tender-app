# TenderApp Supabase Operations

This folder stores manual Supabase SQL migrations used by the TenderApp MVP.

## Current Migration Order

Run migrations in order:

1. `migrations/001_initial_schema.sql`
2. `migrations/002_demo_data_normalization.sql`
3. `migrations/003_push_notifications.sql`
4. `migrations/004_notification_events_canonical_reset.sql`
5. `migrations/005_drying_locations.sql`
6. `migrations/006_energy_prediction_inputs.sql`

## Notification Events Canonical Reset

`004_notification_events_canonical_reset.sql` exists because the live Supabase project had an empty legacy `notification_events` table with older required columns:

- `type`
- `title`
- `body`
- lowercase `status` values

The backend PB-020 push implementation uses the cleaner canonical event audit model:

- `category`
- `source`
- `title_key`
- `body_key`
- `payload`
- uppercase lifecycle status values: `PENDING`, `SENT`, `FAILED`, `SKIPPED`, `CANCELLED`
- provider audit fields: `provider_message_id`, `error_code`, `sent_at`

Because the table was confirmed empty during implementation, the chosen approach is to reset `notification_events` to the canonical model instead of keeping a permanent legacy compatibility layer.

The migration refuses to run if existing rows are present.

## Manual Verification

After applying migration 004, run:

```sql
select column_name, is_nullable, data_type
from information_schema.columns
where table_schema = 'public'
  and table_name = 'notification_events'
order by ordinal_position;
```

Expected: no `type`, `title` or `body` columns.

Then verify constraints:

```sql
select conname
from pg_constraint
where conrelid = 'public.notification_events'::regclass
order by conname;
```

Expected constraints include:

- `notification_events_category_check`
- `notification_events_source_check`
- `notification_events_status_check`
- `notification_events_payload_object_check`

## Energy Prediction Inputs

`006_energy_prediction_inputs.sql` prepares the database for `ENERGY-PRED-001`.

It adds nullable fields for:

- habitual washer spin speed;
- cycle spin speed;
- load size;
- approximate washing energy, water and cost snapshots.

The migration is backward-compatible and uses controlled checks with `not valid` where appropriate, so existing MVP/demo rows are not broken by the new contract.

Manual verification:

```sql
select table_name, column_name, data_type, is_nullable
from information_schema.columns
where table_schema = 'public'
  and (
    (table_name = 'washers' and column_name = 'default_spin_rpm')
    or (
      table_name = 'laundry_loads'
      and column_name in (
        'spin_rpm',
        'load_size',
        'estimated_washing_energy_kwh',
        'estimated_washing_water_liters',
        'estimated_washing_cost_amount',
        'estimated_washing_cost_currency',
        'estimated_washing_cost_level',
        'estimated_washing_cost_confidence'
      )
    )
  )
order by table_name, column_name;
```

## Evidence Rule

Screenshots or SQL output used for thesis evidence must not include service role keys, access tokens, FCM registration tokens or private user data.
