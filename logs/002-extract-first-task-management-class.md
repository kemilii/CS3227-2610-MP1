# Task 002 — Extract first task management class

- Task ID: `01a052a0-086d-7bf0-ad79-f45cc1266c76`
- Scope: Incremental separation of HomeHub responsibilities.

## Prompts and interactions

- Asked to gradually extract UI, storage, parser, and task-list responsibilities
  into classes, testing and committing each natural increment and generating a
  visual diff.
- Six iterative confirmations of “go ahead” continued the refactor.
- Iteration 1 extracted console interaction into `Ui`.
- Iteration 2 extracted persistence into instance-based `Storage`.
- Iteration 3 extracted collection ownership into `TaskList`.
- Iteration 4 extracted command keyword interpretation into `Parser`.
- Iteration 5 made `Parser` return both command type and normalized arguments.
- The final request, “extract it”, moved todo/deadline/event creation and
  validation into `TaskCommands`.

## Outcome

The target separation was completed incrementally. `HomeHub` became primarily
an application coordinator, while UI, storage, parsing, task-list ownership,
and task-creation commands gained dedicated classes. Each increment preserved
behavior and was committed with a rationale; visual diffs were generated in
`_temp/visual-diff.html`.

## Verification and files

Java 25 compilation and focused UI regressions passed after each increment,
with the UI test plan updated for the new boundaries. Important files were
`Ui.java`, `Storage.java`, `Parser.java`, `TaskList.java`, `TaskCommands.java`,
`HomeHub.java`, and `test/ui-test-plan.md`.
