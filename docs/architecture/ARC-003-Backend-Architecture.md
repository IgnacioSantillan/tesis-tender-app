---
id: ARC-003
title: Backend Architecture
category: Architecture Description
version: 1.0.0
status: Curated
language: English
author: Juan Santillán
created: 2026-07-05
updated: 2026-07-05
tags: []
related: ['ADR-004', 'REQ-001', 'REQ-003']
---

# Backend Architecture

## Purpose

Define the backend role, boundaries and initial module structure for TenderApp.

## TenderApp Adaptation

TenderApp will use a simple backend, preferably NestJS, acting as the mobile API layer. Supabase provides authentication/database capabilities, but the Android app should not depend on Supabase schema details for core business behavior.

## Engineering Notes

The backend should remain intentionally small. It must not become a complex microservice architecture. Express is acceptable if simplicity wins, but NestJS is preferred for modularity and TypeScript consistency.

## Increment and Kanban Context

The backend evolves through five increments: foundation and health checks in Increment 1; authenticated household configuration in Increment 2; loads, weather and prediction in Increment 3; state changes and notification automation in Increment 4; and integration, regression and evidence closure in Increment 5. Work is reviewed through the shared Kanban states and remains within the declared MVP scope.

## Traceability

Supports REQ-001, REQ-003, REQ-005, REQ-006 and ADR-004.

## References

- ISO/IEC/IEEE 42010: Architecture description.
- ISO/IEC/IEEE 29148: Requirements engineering.
- Android Developers: Guide to app architecture.
- OpenAPI Specification.
- Michael Nygard: Documenting Architecture Decisions.

## Open Questions

- To be reviewed against the curated five-increment Kanban baseline.

## Changelog

- 1.0.0 (2026-09-19): Curated for the five-increment Kanban baseline and academic review.


