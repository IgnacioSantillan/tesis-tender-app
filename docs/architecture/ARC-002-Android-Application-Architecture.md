---
id: ARC-002
title: Android Application Architecture
category: Architecture Description
version: 1.0.0
status: Draft
language: English
author: Juan Santillán
created: 2026-07-05
updated: 2026-07-05
tags: []
related: ['ADR-001', 'ADR-003', 'REQ-001', 'REQ-004']
consumers: ['ANDROID-000', 'PROMPT-000', 'AG-000']
---

# Android Application Architecture

## Purpose

Define the Android architecture that will guide implementation in Kotlin and Jetpack Compose.

## Educational Layer

Modern Android architecture usually separates UI, state management, data access and background work. Google recommends a layered architecture with UI layer, data layer and optional domain layer depending on complexity.

## TenderApp Adaptation

TenderApp uses Compose + ViewModel + StateFlow for UI state, repositories for data access, Room as local cache, Retrofit/OkHttp for backend communication, Hilt for dependency injection and WorkManager for scheduled background checks.

## Engineering Notes

Use Cases are optional. They should be introduced only for non-trivial business operations such as drying prediction, synchronization and notification scheduling.

## Increment and Kanban Context

This architecture is used across the five increments. Android foundation and navigation support Increment 1; authentication and household configuration support Increment 2; loads and recommendation screens support Increment 3; state, progress and notifications support Increment 4; and regression evidence and closure support Increment 5. Each change moves through the shared Kanban states from detection to validation and closure.

## AI Guidance

Do not place business logic inside composables. Do not expose Room entities directly to UI if mapping is required. Prefer immutable UI state models and StateFlow.

## Traceability

Implements REQ-001 to REQ-008 and NFR-001 to NFR-004. Feeds ANDROID-000 and TEST-000.

## References

- ISO/IEC/IEEE 42010: Architecture description.
- ISO/IEC/IEEE 29148: Requirements engineering.
- Android Developers: Guide to app architecture.
- OpenAPI Specification.
- Michael Nygard: Documenting Architecture Decisions.

## Open Questions

- To be reviewed by Juan during the FR2 review cycle.

## Changelog

- 1.0.0 (2026-09-19): Curated for the five-increment Kanban baseline and academic review.
