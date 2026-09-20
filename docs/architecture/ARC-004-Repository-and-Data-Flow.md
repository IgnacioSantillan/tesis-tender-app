---
id: ARC-004
title: Repository and Data Flow
category: Architecture Description
version: 0.1.0
status: Draft
language: English
author: Juan Santillán
created: 2026-07-05
updated: 2026-07-05
tags: []
related: ['ADR-001', 'ADR-005', 'ARC-002']
consumers: ['ANDROID-000', 'TEST-000']
---

# Repository and Data Flow

## Purpose

Define how repositories coordinate local cache, remote APIs and domain models.

## Educational Layer

The Repository Pattern abstracts data access and allows the rest of the application to work with domain-oriented operations instead of concrete persistence APIs. A repository may wrap one or multiple data sources.

## TenderApp Adaptation

Room is the local source of immediate truth for UI observation. Remote data refreshes update Room. ViewModels consume repository flows and do not decide whether data comes from cache, network or synchronization.

## Engineering Notes

Repository is not a DAO and not an API client. It is the boundary where data freshness, mapping, errors and synchronization policies are coordinated.

## AI Guidance

Never call Retrofit directly from a ViewModel. Never call Room directly from a composable. Repository APIs should expose clear operations such as observeActiveLoad(), refreshForecast(), saveWasher() and calculateDryingPrediction().

## Traceability

Directly supports REQ-003, REQ-004, REQ-005 and NFR-001.

## References

- ISO/IEC/IEEE 42010: Architecture description.
- ISO/IEC/IEEE 29148: Requirements engineering.
- Android Developers: Guide to app architecture.
- OpenAPI Specification.
- Michael Nygard: Documenting Architecture Decisions.

## Open Questions

- To be reviewed by Juan during the FR2 review cycle.

## Changelog

- 0.1.0 (2026-07-05): Initial FR2 draft.
