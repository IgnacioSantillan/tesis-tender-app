---
id: QA-003
title: Implementation Review Gate
category: Quality Assurance
version: 1.0.0
status: Curated
language: English
project: TenderApp
---

# QA-003 - Implementation Review Gate

## Purpose

This review gate determines whether a TenderApp capability can move from implemented to validated and documented.

## Review checklist

Before closing a capability, verify:

- The capability belongs to one of the five approved increments.
- The scope and acceptance criteria are identifiable.
- The implementation respects the Android, backend and data boundaries.
- The Android project or backend test slice builds when the environment is configured.
- Automated tests, smoke tests or manual checks are recorded according to the risk.
- No secrets, personal data or local-only files are included in the evidence.
- The interface, API contract, persistence and domain state remain coherent.
- Known limitations and pending observations are stated explicitly.
- The evidence can be linked to the implementation and the corresponding increment.

## Evidence states

- Pending: scope identified but not implemented.
- Implemented: code or configuration exists.
- Validated: a relevant check was executed and recorded.
- Documented: evidence and limitations are available for review.
- Closed: the capability is complete within the declared MVP scope.

## Human review

The final acceptance of a capability remains a human responsibility. Automated checks support the decision but do not replace inspection of scope, evidence or limitations.


