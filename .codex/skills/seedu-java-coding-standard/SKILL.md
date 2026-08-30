---
name: seedu-java-coding-standard
description: "Apply the SE-EDU basic and intermediate Java coding standard when writing, reviewing, or refactoring Java code in this project."
---

# Seedu Java Coding Standard

Apply these rules to all Java production and test code in this repository. Use the
SE-EDU [Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
as the authoritative source, and use the Google Java Style Guide for topics not
covered there.

## Required conventions

- Keep package names lowercase; use PascalCase nouns for classes and enums,
  camelCase for variables and verb-based methods, and SCREAMING_SNAKE_CASE for
  constants. Keep names in English, avoid uppercase acronyms within names, and
  use boolean names that read as predicates (`is`, `has`, `can`, or `should`).
- Use plural names for collections, short names only for genuinely local scratch
  variables, and the three-part underscore form for test methods when useful:
  `featureUnderTest_testScenario_expectedBehavior`.
- Use four spaces, K&R braces, spaces around operators/keywords/commas, and
  blank lines between logical units. Keep lines at or below 120 characters,
  prefer below 110, and indent wrapped lines by eight additional spaces.
- Put every class in a package, keep imports explicit and consistently ordered,
  and attach array brackets to the type (`String[]`).
- Initialize variables at declaration where practical and keep them in the
  smallest possible scope. Do not expose mutable class fields publicly; use
  methods to preserve encapsulation. Always brace loop and conditional bodies,
  including single-statement bodies, and make intentional switch fallthrough
  explicit with `// Fallthrough`.
- Write English comments using American spelling. Add descriptive Javadocs to
  public classes and public methods, except getters/setters, exact overrides,
  and test code. Start method summaries with an action such as `Returns` or
  `Adds`, and include useful parameter, return, and exception details.

When changing code, inspect the surrounding files and preserve existing behavior
unless the request explicitly requires a behavior change. Update high-value
JUnit tests for behavior changes, and review the UI test plan when commands or
console output change.
