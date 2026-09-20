---
id: QA-005
title: MVP Deployment Review Gate
category: Quality Gate
version: 1.2.0
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


# QA-005 — MVP Deployment Review Gate

## Purpose
Define the quality gate before declaring the deployed MVP ready for thesis demonstration.

## Checklist

### Security
- [x] No service role key in Android
- [x] `.env` not committed to repository
- [x] Supabase anon key read from build config, not hardcoded
- [ ] RLS policies audited end-to-end (defined in schema, not independently verified)
- [ ] Session persistence reviewed for production readiness (uses deprecated API)

### Infrastructure
- [x] Backend deployed to Render
- [x] Backend public URL reachable from browser
- [ ] Swagger/OpenAPI reachable from non-browser client
- [ ] Cold start latency documented for demo (Render free tier)

### Android
- [x] App builds (`assembleDebug` + `testDebugUnitTest` pass)
- [x] Backend URL configurable by build variant
- [x] App navigates from auth to dashboard after login
- [x] Dashboard loads without crashing
- [x] History loads laundry loads from backend
- [x] New Load creates a record via backend
- [ ] Screenshots captured from device/emulator

### Backend
- [x] `GET /api/v1/health` returns `status: ok`
- [x] `GET /api/v1/health/supabase` returns `status: ok`
- [x] Domain endpoints registered (users, washers, laundry-loads, weather, predictions, notifications)
- [x] Auth guard validates Supabase bearer token
- [x] Defensive response mapping for legacy data

### Supabase
- [x] `001_initial_schema.sql` applied
- [x] `002_demo_data_normalization.sql` applied (2026-07-08)
- [x] RLS enabled on `profiles`, `washers`, `laundry_loads`, `user_locations`
- [x] Test user available for manual smoke validation
- [x] Seed data independently verified through manual Android smoke flow

### Integration
- [x] Android reaches public backend
- [x] Backend reaches Supabase
- [x] Protected bearer-token call returns user-owned data
- [x] Smoke test result recorded in INTEGRATION-EVIDENCE-001
- [x] Manual Android MVP smoke test passed (`MVP-SMOKE-TEST-001`)

### Evidence
- [x] Smoke baseline documented (INTEGRATION-EVIDENCE-001)
- [x] Demo data normalized (SUPABASE-DATA-001)
- [x] Manual Android MVP smoke documented (MVP-SMOKE-TEST-001)
- [ ] Screenshots captured from device/emulator
- [ ] End-to-end demo script reviewed by thesis author

---

## Verification Notes

**Date:** 2026-07-08

The core deployment pipeline is functional. The backend is live at `https://tesis-t85s.onrender.com` and both health endpoints (`/api/v1/health` and `/api/v1/health/supabase`) return `status: ok`. The Android app builds cleanly, reaches the deployed backend, and the full data path from Android through the backend to Supabase has been exercised manually: `GET /api/v1/laundry-loads` with a valid bearer token returns real rows from Supabase.

Schema migrations `001_initial_schema.sql` and `002_demo_data_normalization.sql` are applied. RLS is enabled on `profiles`, `washers`, `laundry_loads`, and `user_locations`; policies are defined in the schema but have not been audited by an independent reviewer. No service role key is present in the Android client. The `.env` file is excluded from the repository; `.env.example` is committed.

On 2026-07-09, the thesis author reported a successful manual Android MVP smoke test: login OK, backend OK, Dashboard OK, Settings OK with washers 3 and 4 observed, New Load OK and History OK. This confirms the deployed Android -> Backend -> Supabase path for the main MVP demo flow.

Remaining open items are tracked below. Push notifications are out of scope for the MVP demo (registration boundary only).

---

## Open Items Before Demo

The following items are pending and must be resolved or explicitly accepted as known gaps before the thesis demonstration:

1. **RLS audit** — Row-Level Security policies are defined in schema but have not been verified end-to-end by an independent test or automated check.
2. **Session persistence** — The current implementation uses a deprecated API; production readiness has not been reviewed.
3. **Swagger reachability** — `/api/v1/docs` has not been confirmed reachable from a non-browser client.
4. **Cold start latency** — Render free tier cold-start behaviour has not been measured or documented for the demo context.
5. **Screenshots** — No screenshots from a physical device or emulator have been captured and attached to the evidence record.
6. **Demo script review** — The end-to-end demo script (`MVP-DEMO-SCRIPT-001`) has not been reviewed by the thesis author.

---

## Changelog

| Version | Date | Change |
|---|---|---|
| 1.2.0 | 2026-07-09 | Recorded successful manual Android MVP smoke test and closed seed/test-user verification items for MVP demo scope. |
| 1.1.0 | 2026-07-08 | Expanded checklist with current verification state; added Verification Notes, Open Items, and Changelog sections. |
| 1.0.0 | 2026-07-06 | Original gate definition. |
