---
id: BR-005
title: Explainable Recommendation
category: Business Rule
version: 0.1.0
status: Draft
language: English
author: Juan Santillán
created: 2026-07-05
updated: 2026-07-05
tags:
  - business-rule
  - fr1
related:
  - REQ-007
  - NFR-001
---

# BR-005 — Explainable Recommendation

## Purpose

Define a rule that constrains TenderApp behavior: Explainable Recommendation.

## Educational Layer

### What is this artifact?

A business rule is a domain-level statement that constrains or guides system behavior independently from a specific UI or database design.

### Why is it used?

Business rules keep domain decisions explicit, testable, and reusable across Android, backend, and documentation.

## TenderApp Adaptation

Every recommendation must include a short reason understandable by a non-technical user.

## Engineering Notes

This rule must be implemented in either prediction logic, repositories, notification scheduling, or UI explanation depending on scope.

## AI Guidance

AI agents must not hardcode conflicting thresholds without a linked algorithm or ADR. Thresholds should be configurable when uncertainty exists.

## Traceability

Related artifacts: REQ-007, NFR-001. Future links: ALG, TEST, UI, API.

## References

- ISO/IEC/IEEE 29148 — Requirements engineering.
- Business rules and domain modeling literature.

## Open Questions

- None at this stage.

## Changelog

- 0.1.0: Initial FR1 draft.
