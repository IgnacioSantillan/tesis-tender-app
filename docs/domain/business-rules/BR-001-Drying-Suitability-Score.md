---
id: BR-001
title: Drying Suitability Score
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
  - REQ-004
---

# BR-001 — Drying Suitability Score

## Purpose

Define a rule that constrains TenderApp behavior: Drying Suitability Score.

## TenderApp Adaptation

The system shall classify washing suitability as Good, Warning, or Bad using rain probability, humidity, wind, temperature, and drying location.

## Engineering Notes

This rule must be implemented in either prediction logic, repositories, notification scheduling, or UI explanation depending on scope.

## Traceability

Related artifacts: REQ-004. Future links: ALG, TEST, UI, API.

## References

- ISO/IEC/IEEE 29148 — Requirements engineering.
- Business rules and domain modeling literature.

## Open Questions

- None at this stage.

## Changelog

- 0.1.0: Initial FR1 draft.


