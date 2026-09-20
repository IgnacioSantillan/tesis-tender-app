---
id: MVP-DEPLOY-001
title: Deployed MVP Checklist
category: MVP Checklist
version: 1.1.0
status: Approved
language: English
author: Juan Santillán
created: 2026-07-06
updated: 2026-07-09
related:
  - FR5
  - FR6
tags:
  - implementation
  - integration
  - deployment
---


# MVP-DEPLOY-001 — Deployed MVP Checklist

## Purpose
Define when TenderApp stops being only a mockup and becomes a deployed MVP.

## Checklist

### Android
- [x] App builds.
- [x] Dashboard opens.
- [ ] Backend URL configurable.
- [x] App calls backend.
- [x] App shows weather/verdict or prediction data.

### Backend
- [x] Backend deployed to Render/Railway.
- [x] `/health` works.
- [x] `/health/supabase` works.
- [ ] Swagger/OpenAPI available or documented.
- [x] Supabase adapter implemented.

### Supabase
- [x] Schema applied.
- [ ] RLS enabled.
- [x] Test user exists.
- [x] Seed data available.
- [x] Demo data normalization SQL applied (2026-07-08).
- [ ] Service key backend-only.

### Integration
- [x] Android reaches public backend.
- [x] Backend reaches Supabase.
- [x] First vertical slice demonstrated.
- [x] Implementation log updated.
- [x] Manual Android MVP smoke test passed (2026-07-09).
- [ ] Real push smoke test executed (`QA-PUSH-001`).

## Acceptance Criteria
A reviewer can run the app, call the backend through Android, and observe a useful laundry recommendation.

## Current Verification Notes

Date: 2026-07-08.

- Android UI prototype evidence checklist was prepared in `docs/09-implementation/android/ANDROID-UI-008-UI-Evidence-Package.md`.
- Deployed smoke baseline was attempted in `docs/09-implementation/integration/INTEGRATION-EVIDENCE-001-Deployed-Smoke-Baseline.md`.
- Render public endpoints were not reachable from the local Codex shell during this attempt.
- Browser evidence provided by the thesis author confirms `/api/v1/health` and `/api/v1/health/supabase` return safe success responses.
- Android manual validation confirms the app reaches the public backend.
- The MVP schema migration `supabase/migrations/001_initial_schema.sql` was applied manually in Supabase SQL Editor after the `NOT VALID` constraint adjustment.
- Protected `GET /api/v1/laundry-loads` returned real Supabase rows after schema migration.
- Defensive backend response mapping now normalizes legacy lower-case laundry-load values for Android.
- `supabase/migrations/002_demo_data_normalization.sql` was applied manually on 2026-07-08. All laundry_loads rows have valid enums and location_id = 'home'. user_locations table created with 'home' row per demo user. laundry_loads_status_check constraint re-added as VALID after resolving a spike row with status='small'.
- PB-018 now tracks the remaining realistic MVP layer: connected laundry creation, weather-driven dashboard, centralized weather location and lightweight visual indicators.
- 2026-07-09 manual Android MVP smoke test passed and is recorded in `docs/09-implementation/integration/MVP-SMOKE-TEST-001-Manual-Android-Smoke-Test.md`.
- Validated flow: login, backend reachability, Dashboard, Settings, washer creation/listing (washers 3 and 4 observed), New Load and History.
- Next pending: screenshot capture for thesis evidence (ANDROID-VISUAL-001 / MVP-DEMO evidence package).
- 2026-07-10: Real push notification runbook prepared in `docs/09-implementation/integration/QA-PUSH-001-Real-Push-Smoke-Test.md`. Execution remains pending because it requires deployed `BACKEND-PUSH-003`, Firebase Admin credentials in Render, a signed-in Android device and redacted evidence capture.
- Render free-tier cold start must be considered during smoke validation: the first request after inactivity can be slow and should not be misclassified as a backend failure until health checks are retried after the instance wakes.
