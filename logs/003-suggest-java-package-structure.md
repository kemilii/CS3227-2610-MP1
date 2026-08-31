# Task 003 — Suggest Java package structure

- Task ID: `01a052ac-ac2e-7d01-8a76-940063f92d64`
- Scope: Organize source classes into suitable packages beneath `src/main/java`.

## Prompts and interactions

- Asked for a package structure without implementation. A layered layout was
  proposed: `homehub`, `homehub.model`, `homehub.command`, `homehub.storage`,
  `homehub.ui`, and `homehub.exception`.
- Replied “sounds good”; implementation was left pending.
- Asked to “implement it”. The package declarations, imports, visibility, and
  source layout were changed, and task classes plus `ParsedCommand` were
  separated into public classes.

## Outcome

The package organization was implemented beneath the unchanged source root.
The new structure improved separation of domain, command, storage, UI, and
exception responsibilities without changing the intended CLI behavior.

## Verification and files

Compilation succeeded with Java 25. UI cases UI-001 through UI-010 passed with
empty stderr. The package move affected the HomeHub classes, command/model/
storage/ui/exception files, and `test/ui-test-plan.md`.
