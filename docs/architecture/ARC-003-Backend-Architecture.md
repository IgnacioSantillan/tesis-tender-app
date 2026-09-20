---
id: ARC-003
title: Backend Architecture
category: Architecture Description
version: 0.1.0
status: Draft
language: English
author: Juan Santillán
created: 2026-07-05
updated: 2026-07-05
tags: []
related: ['ADR-004', 'REQ-001', 'REQ-003']
consumers: ['BACKEND-000', 'API-000']
---

# Backend Architecture

## Purpose

Define the backend role, boundaries and initial module structure for TenderApp.

## Educational Layer

A backend-for-frontend or application backend can isolate the mobile app from infrastructure details, centralize security, expose stable APIs and coordinate third-party services.

## TenderApp Adaptation

TenderApp will use a simple backend, preferably NestJS, acting as the mobile API layer. Supabase provides authentication/database capabilities, but the Android app should not depend on Supabase schema details for core business behavior.

## Engineering Notes

The backend should remain intentionally small. It must not become a complex microservice architecture. Express is acceptable if simplicity wins, but NestJS is preferred for modularity and TypeScript consistency.

## AI Guidance

When generating backend code, AI agents must avoid exposing service role keys to the client, must preserve API boundaries and must generate OpenAPI-compatible contracts when possible.

## Traceability

Supports REQ-001, REQ-003, REQ-005, REQ-006 and ADR-004.

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
