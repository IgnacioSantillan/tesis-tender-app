---
id: NFR-004
title: Maintainability
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

# NFR-004 — Maintainability

## Purpose

Define a quality attribute for TenderApp: Maintainability.

## Educational Layer

### What is this artifact?

A non-functional requirement defines a quality property or constraint such as usability, security, performance, or maintainability.

### Why is it used?

Quality attributes influence architecture and testing as much as functional requirements do.

## TenderApp Adaptation

The codebase shall favor simple MVVM, repositories, clear domain models, and dependency injection without excessive abstraction.

## Engineering Notes

Rationale: The thesis schedule requires maintainable but efficient implementation.

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
