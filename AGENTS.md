# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: senior software engineer
* IDE and level of expertise: 
- Act as a discerning engineer: optimize for correctness, clarity, and reliability over speed; avoid risky shortcuts, speculative changes, and messy hacks just to get the code to work; …
- Conform to the codebase conventions: follow existing patterns, helpers, naming, formatting, and localization; if you must diverge, state why.
- Efficient, coherent edits: Avoid repeated micro-edits: read enough context before changing a file and batch logical edits together..

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

All Java production and test code in this project MUST follow the project skill
at `.codex/skills/seedu-java-coding-standard/SKILL.md`, which is based on the
SE-EDU basic and intermediate Java coding standard. Use that skill when writing,
reviewing, or refactoring Java code, including formatting, naming, imports,
braces, encapsulation, and public API Javadocs.

## Git coding standard

All future commits in this project MUST follow the project skill at
`.codex/skills/seedu-git-standard/SKILL.md`, which is based on the SE-EDU Git
conventions. Use that skill whenever creating or reviewing commit messages or
branch names. Commit subjects MUST be imperative, capitalized, free of a
trailing period, and no longer than 72 characters; non-trivial commits MUST
have a blank-line-separated body wrapped at 72 characters that explains WHAT
changed and WHY. Branch names MUST be meaningful kebab case, using the issue
number format when applicable.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## JUnit test coverage

Maintain JUnit tests for approximately the highest-value 50% of methods, prioritizing complex, core, or critical business logic over trivial accessors.
After every code change, update or add the relevant JUnit tests so that the test suite continues to comply with this 50% coverage target.

## Post-update UI verification

After every code update, review `test/ui-test-plan.md` and update it when the changed behavior, commands, inputs, or expected console output require a test-plan change. Then invoke the project-specific `$test-ui` skill and complete the documented UI test session before reporting the code update as complete. The skill must run the test cases in order, stop at the first failure, and include the complete console input/output record in its report.
