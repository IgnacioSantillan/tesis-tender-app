---
id: QA-001
title: Definition of Done
category: Quality Standard
version: 0.1.0
status: Curated
language: English
author: Juan Santillán
created: 2026-07-05
updated: 2026-07-05
tags: ['quality', 'dod']
related: ['TEST-000', 'TEST-003']
---

# Definition of Done

## Purpose

Define when a task can be considered complete.

## TenderApp Definition of Done

A task is done only when:

- The requested behavior is implemented.
- The change respects approved architecture.
- Relevant tests are added or updated where feasible.
- No secrets or local-only values are committed.
- UI follows the design system when applicable.
- Documentation impact is reviewed.
- Traceability to backlog/requisites is maintained.
- Human review has been completed.

## Traceability

Consumes TEST-000 to TEST-003, GOV-003 and EDS lifecycle. Used by the implementation and review process.

## References

- Software quality assurance practices.
- ISO/IEC 25010 as quality model reference.
- Code review practices.

## Open Questions

- Define minimum unit test coverage after first implementation spike.

## Changelog

| Version | Date | Change |
|---|---|---|
| 0.1.0 | 2026-07-05 | Initial Definition of Done. |


