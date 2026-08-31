# Task 012 — Add Java assertions

- Task ID: `01a056f5-9474-7fd3-b5a2-c226b25b8fb0`
- Scope: Document and enforce meaningful Java invariants.

## Prompts and interactions

- Asked to review the linked CS2103 code-quality guidance and improve the code,
  using one rationale-based commit per independent change. Four quality
  improvements were completed: centralized UI rendering, named command
  markers, named storage values, and enum-based GUI response styling.
- Asked to commit code changes. The working tree was already clean and the
  existing quality commits were reported; no empty commit was created.
- Asked to add Java `assert` statements and justify each case. Assertions were
  added for parser results, initialized collaborators/FXML fields, task and
  list invariants, mutation/persistence/rollback behavior, and valid UI inputs.
  Focused null-rejection tests were added and Gradle test assertions were
  enabled.
- Asked to “commit it”. The assertion work was already included in commit
  `3a5870b Implement HomeHub GUI commands`; the working tree was clean.

## Outcome and verification

Assertions include explanatory messages and cover the main domain and UI
invariants. `./gradlew test checkstyleMain checkstyleTest` passed; 41 JUnit
tests and UI-001–UI-011 passed under Java 25, including assertion-enabled UI
runs. The UI plan and complete `_temp` session records were updated.
