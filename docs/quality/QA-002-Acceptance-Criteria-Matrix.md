---
id: QA-002
title: Acceptance Criteria Matrix
category: Quality Matrix
version: 0.6.0
status: Draft
language: English
author: Juan Santillán
created: 2026-07-05
updated: 2026-07-25
tags: ['quality', 'acceptance']
related: ['BACKLOG-001', 'REQ-001', 'MVP-INCREMENTS-001']
---

# Acceptance Criteria Matrix

## Purpose

Map MVP backlog items to acceptance criteria.

## TenderApp Matrix

| Backlog ID | Acceptance Criteria |
|---|---|
| PB-001 | Android project builds; Compose preview works; navigation shell exists. |
| PB-002 | Backend starts locally; /health returns OK; environment config is externalized. |
| PB-003 | Supabase schema draft exists; no secrets committed; DB-001 updated. |
| PB-004 | User can authenticate; invalid auth is handled; session state is visible to UI. |
| PB-005 | User can create, view, edit and retire washers while preserving historical loads. |
| PB-006 | App receives weather data through backend, not direct provider coupling. |
| PB-007 | Prediction returns score, verdict, estimated drying time and reasoning. |
| PB-008 | User can create a laundry load using washer, program and clothing type. |
| PB-009 | Dashboard shows weather, verdict and current/recommended laundry status in under 3 seconds cognitively. |
| PB-010 | History shows previous loads and prediction outcomes. |
| PB-011 | Core data persists locally and supports basic offline read. |
| PB-012 | Reminder notification can be scheduled or simulated for MVP; user opt-in is respected; disabled alerts are not scheduled. |
| PB-013 | Errors show recoverable UI states. |
| PB-014 | Evidence screenshots and notes are stored for thesis use. |
| PB-019 | MVP automation is explicit: ideal hanging-time, rain-risk and pickup reminders are represented through local/simulated notifications or documented alert states; tests or manual evidence validate opt-in/opt-out behavior. |
| PB-020 | Android registers an FCM recipient through backend; backend stores device registration in Supabase; backend can dispatch or audit a real push attempt without exposing provider secrets to Android. |
| PB-021 | User can create a load with drying location; backend calculates a weather-based drying prediction; Dashboard shows active load, weather and recommendation; notification events are created, updated or cancelled according to load state; evidence is captured without secrets. |
| PB-022 | Functional MVP flow preserves lifecycle consistency: location, prediction, status updates, notifications, history and washer retirement work without deleting historical evidence. |
| PB-023 | Prediction supports controlled spin speed, load size, washer efficiency and approximate washing cost; missing values fall back safely; Dashboard can infer washer metadata from user-owned loads; tests and evidence document the heuristic nature of the result. |
| PB-024 | User can select when clothes must be ready; backend evaluates QUICK, DELICATE, NORMAL and ECO using washing plus weather-based drying time; each alternative exposes finish time, margin and feasibility; only feasible alternatives are recommended; invalid targets and stale UI responses are handled. |

## AI Guidance

AI agents must cite the relevant acceptance criteria when claiming a task is complete.

## Traceability

Consumes BACKLOG-001 and QA-001. Supports review and thesis evidence.

## References

- ISO/IEC/IEEE 29148 for requirements validation and traceability.
- Software acceptance testing practices.

## Open Questions

- Add automated test mapping after implementation begins.

## Changelog

| Version | Date | Change |
|---|---|---|
| 0.6.0 | 2026-07-25 | Added PB-024 acceptance criteria for target-ready washing and drying program comparison. |
| 0.5.0 | 2026-07-12 | Added PB-022 lifecycle criteria and clarified washer management as retirement instead of physical deletion. |
| 0.4.0 | 2026-07-12 | Added PB-023 acceptance criteria for energy-aware prediction and Dashboard washer metadata enrichment. |
| 0.3.0 | 2026-07-11 | Added PB-020 and PB-021 acceptance criteria for real push and Increment 4 validation. |
| 0.2.0 | 2026-07-09 | Expanded notification acceptance criteria and added PB-019 automation validation. |
| 0.1.0 | 2026-07-05 | Initial acceptance matrix. |
