---
name: test-ui
description: Run the project's command-line UI test cases from test/ui-test-plan.md, compare actual output with expected output, and stop at the first failure.
---

# Test UI

Use this skill when validating the interactive console behavior of this Java project.

## Test plan contract

Read `test/ui-test-plan.md` before running tests. It must contain the launch command, an ordered list of test cases, and for every case an aim, newline-separated console inputs, and complete expected output in fenced `text` blocks headed `Inputs` and `Expected output`.

## Execution

1. Ensure Java 25 is selected (`sdk use java 25.0.3.fx-zulu` on macOS), then compile into a temporary directory if the plan does not provide a build command.
2. Run cases in documented order. Feed each case's inputs to the launch command and capture stdout and stderr separately without adding shell text or normalizing output.
3. Compare stdout exactly with expected output. Whitespace, blank lines, missing lines, and extra lines are significant. A non-zero exit status is also a failure.
4. Stop immediately after the first failure; do not run later cases. Report the first failing case's actual and expected output, plus stderr and exit status when relevant.
5. Update the plan's `Test session record` with the date, each executed command, console input, actual console output, and pass/fail status. For a failure, include both actual and expected output. Do not overwrite the test cases.
6. Show the complete console input/output record in the final response. If a case failed, clearly identify it and report the actual-versus-expected difference.

Use temporary directories for compiled classes and captures. Preserve existing repository files except for the requested test session record.
