# Task 009 — Compare Checkstyle and Java standard

- Task ID: `01a0569a-1eb8-7092-93b0-1875a407451a`
- Scope: Explain and integrate Checkstyle alongside the SE-EDU standard.

## Prompts and interactions

- Asked whether Checkstyle adds value when the project already uses the
  `seedu-java-coding-standard`. The response explained that Checkstyle gives
  automated enforcement for mechanical rules while the coding standard remains
  the broader authoritative guidance.
- Asked to set up Checkstyle from the SE-EDU tutorial and AddressBook
  configuration. Gradle integration, `checkstyle.xml`, and `suppressions.xml`
  were added; existing violations were fixed and UI verification recorded.
- Asked to run Checkstyle and fix violations. A fresh Java 25 scan passed with
  no current violations, so no additional fixes were necessary.
- Asked to commit the changes. Commit `12bd481 Add Checkstyle and find support`
  was created with the setup, formatting fixes, find support, tests, and UI
  documentation.

## Outcome and verification

`checkstyleMain` and `checkstyleTest` passed under Java 25, and the documented
UI cases passed. The main artifacts were `build.gradle`, the Checkstyle
configuration files, affected source/tests, and `test/ui-test-plan.md`.
