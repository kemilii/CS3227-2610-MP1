# Task 006 — Improve UI test plan

- Task ID: `01a0527f-5899-7713-9429-ca20e6652259`
- Scope: Review, repair, and execute the command-line UI test plan.

## Prompts and interactions

- Asked for all issues found during testing to be listed and solved. Testing
  exposed missing `unmark` help text, leading-whitespace corruption, and date
  parsing that accepted undocumented ISO `T` separators. All were fixed.
- Requested a fresh `$test-ui` run. UI-001–UI-007 passed, then UI-008 failed
  because the command keyword was trimmed but the task description was not.
- Asked to update the code. The help-text issue was fixed, then the whitespace
  issue was fixed and the date parser was tightened.
- Asked whether everything was good. The response accurately reported that a
  fail-fast run was still blocked by the first remaining UI mismatch.
- Asked to complete expected-output blocks for UI-006–UI-010 and run the skill.
  The plan was completed; a Java 25 run stopped at UI-003 because `unmark` was
  still absent from the old implementation at that point.
- Asked how to make Java 25 and the cases available. SDKMAN setup and the
  missing fenced expected-output requirements were explained.
- Requested another `$test-ui` run. Preflight was blocked when SDKMAN/Java 25
  were unavailable and UI-006–UI-010 did not yet satisfy the skill contract.
- Asked to add the cases. The plan gained deterministic environment rules,
  isolated persistence runs, new edge cases, coverage criteria, and clearer
  historical records.
- Asked for a better-model review of the plan. Recommendations covered stale
  records, test isolation, reproducible Java/locale setup, missing command
  coverage, and state-preservation checks.
- The review task was interrupted after the recommendations were delivered.

## Outcome

The plan became reproducible and fail-fast, and the discovered implementation
defects were resolved in subsequent turns. The final recorded run passed
UI-001 through UI-010 under Java 25 with empty stderr and zero exit status.

## Verification and files

The main artifact was `test/ui-test-plan.md`; implementation fixes touched
`HomeHub.java` and `Task.java`. Complete session records were retained in the
plan and `_temp` files.
