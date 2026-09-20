---
id: NFR-002
title: Offline Resilience
category: Non-Functional Requirement
version: 0.1.0
status: Draft
language: English
author: Juan Santillán
created: 2026-07-05
updated: 2026-07-05
tags:
  - nfr
  - quality
  - fr1
related:
  - ADR-001
  - ADR-003
---

# NFR-002 — Offline Resilience

## Purpose

Define a quality attribute for TenderApp: Offline Resilience.

## Educational Layer

### What is this artifact?

A non-functional requirement defines a quality property or constraint such as usability, security, performance, or maintainability.

### Why is it used?

Quality attributes influence architecture and testing as much as functional requirements do.

## TenderApp Adaptation

The app shall remain usable with cached data when network connectivity is unavailable.

## Engineering Notes

Rationale: Laundry decisions often occur at home where forecasts can be cached.

## AI Guidance

AI agents must treat this document as a constraint when generating implementation or design artifacts.

## Traceability

Linked to architecture decisions and future TEST artifacts.

## References

- ISO/IEC 25010 — Software product quality model.
- ISO/IEC/IEEE 29148 — Requirements engineering.

## Open Questions

- None at this stage.

## Changelog

- 0.1.0: Initial FR1 draft.
