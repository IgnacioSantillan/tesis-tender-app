---
id: QA-003
title: Implementation Review Gate
category: Quality Assurance
version: 0.1.0
status: Draft
language: English
project: TenderApp
thesis_title: Diseño estructural de documentación de ingeniería para el desarrollo asistido por inteligencia artificial, aplicado a un caso de uso: TenderApp
created: 2026-07-05
updated: 2026-07-05
---

# QA-003 — Implementation Review Gate

## What is this document?

This document defines the review gate that every AI-assisted implementation task must pass before being considered complete.

## Why is it used?

The project uses AI assistance, but quality responsibility remains human. A review gate prevents uncontrolled code generation and preserves traceability.

## Required Review Checklist

Before approval, verify:

- The task maps to an approved sprint.
- Codex listed the files it read.
- The implementation follows AGENTS.md and CONTEXT documents.
- The task is small and reviewable.
- The Android project builds or build failure is explained.
- No unrelated files were rewritten.
- No unapproved libraries were added.
- UI code does not contain business logic.
- ViewModel exposes state through StateFlow where applicable.
- Repository pattern is respected for data access.
- Documentation is updated if architecture changed.

## Approval States

- Pending Review
- Approved
- Changes Requested
- Rejected

## AI Guidance

Codex must report against this checklist after each implementation task.

## Changelog

- 0.1.0 — Initial review gate.
