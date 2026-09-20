---
id: BR-004
title: Notification Eligibility
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
  - REQ-006
  - REQ-008
---

# BR-004 — Notification Eligibility

## Purpose

Define a rule that constrains TenderApp behavior: Notification Eligibility.

## Educational Layer

### What is this artifact?

A business rule is a domain-level statement that constrains or guides system behavior independently from a specific UI or database design.

### Why is it used?

Business rules keep domain decisions explicit, testable, and reusable across Android, backend, and documentation.

## TenderApp Adaptation

Notifications shall only be scheduled when the user has opted in and when the event is actionable.

## Engineering Notes

This rule must be implemented in either prediction logic, repositories, notification scheduling, or UI explanation depending on scope.

## AI Guidance

AI agents must not hardcode conflicting thresholds without a linked algorithm or ADR. Thresholds should be configurable when uncertainty exists.

## Traceability

Related artifacts: REQ-006, REQ-008. Future links: ALG, TEST, UI, API.

## References

- ISO/IEC/IEEE 29148 — Requirements engineering.
- Business rules and domain modeling literature.

## Open Questions

- None at this stage.

## Changelog

- 0.1.0: Initial FR1 draft.
