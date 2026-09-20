---
id: BR-006
title: Offline Fallback
category: Business Rule
version: 0.1.0
status: Curated
language: English
author: Juan Santillán
created: 2026-07-05
updated: 2026-07-05
tags:
  - business-rule
  - fr1
related:
  - NFR-002
---

# BR-006 — Offline Fallback

## Purpose

Define a rule that constrains TenderApp behavior: Offline Fallback.

## TenderApp Adaptation

When fresh weather data is unavailable, the app may show cached predictions but must visibly indicate reduced confidence.

## Engineering Notes

This rule must be implemented in either prediction logic, repositories, notification scheduling, or UI explanation depending on scope.

## Traceability

Related artifacts: NFR-002. Future links: ALG, TEST, UI, API.

## References

- ISO/IEC/IEEE 29148 — Requirements engineering.
- Business rules and domain modeling literature.

## Open Questions

- None at this stage.

## Changelog

- 0.1.0: Initial FR1 draft.


