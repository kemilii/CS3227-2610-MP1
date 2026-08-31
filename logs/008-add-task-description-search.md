# Task 008 — Add task description search

- Task ID: `01a052fb-1d29-7200-bdae-445553d9d1f5`
- Scope: Implement the `find` command.

## Prompt and interaction

Asked to find tasks by a keyword in their descriptions, matching the example
output. The feature was implemented end-to-end with case-insensitive
substring search, preserved task order, result numbering, no-match handling,
missing-keyword validation, updated help/parser support, JUnit coverage, and UI
coverage.

## Outcome and verification

`find <keyword>` was added to `HomeHub`, `CommandType`, `TaskList`, and `Ui`.
All Gradle tests and UI-001 through UI-011 passed under Java 25. The complete
console transcript was recorded in `_temp/ui-test-session-2026-08-30-find.txt`
and referenced by `test/ui-test-plan.md`.
