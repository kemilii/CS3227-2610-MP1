# Task 007 — Add SE-EDU Java coding standard

- Task ID: `01a052d6-6de4-74b2-b7bf-9e417983661b`
- Scope: Create and enforce project-specific Java and Git conventions.

## Prompts and interactions

- Asked to create `seedu-java-coding-standard`, update agent files, refactor
  current Java code as needed, and show the diff visually. The skill was
  created from the SE-EDU intermediate Java standard; Java production/test
  code and agent guidance were updated, and a visual diff was generated.
- Asked to create a `seedu-git-standard` skill and mandate it for future
  commits. The skill and `AGENTS.md`/`CLAUDE.md` updates were created and
  validated without committing.
- Asked for a commit message for uncommitted Java changes. The response found
  no uncommitted Java files and proposed a rationale-based message for the
  earlier refactor.
- Asked for one commit per standalone agent-file change. The Git-standard
  change was committed as `2513c52 Mandate SE-EDU Git standard`.

## Outcome

The repository gained reusable Java and Git convention skills, agent rules
mandating them, and a broad formatting/documentation refactor that preserved
behavior.

## Verification and files

JUnit and UI tests passed under Java 25 for the Java refactor. The visual diff
was generated at `_temp/visual-diff.html`. Changes covered `.codex/skills`,
`AGENTS.md`, `CLAUDE.md`, the HomeHub source/test tree, and the UI test plan.
