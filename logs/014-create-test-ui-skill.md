# Task 014 — Create `test-ui` skill

- Task ID: `01a05244-0c35-7291-9ecc-531c8fc748b9`
- Scope: Create a reusable project-specific console UI testing workflow.

## Prompts and interactions

- Asked to create a `test-ui` skill that accepts commands and expected output,
  records cases in `test/ui-test-plan.md`, compares output, records complete
  sessions, and stops at the first failure.
- The project skill was initialized under `.codex/skills/test-ui`, completed
  with exact-output/fail-fast guidance, and validated.
- The UI plan was created with launch instructions and initial cases.
- Asked to update agent files so every code change reviews the UI plan and
  invokes the skill. `AGENTS.md` was updated; `CLAUDE.md` already delegated to
  it, so no duplicate rule was added.
- Explicitly asked to use the skill. UI-001 and UI-002 passed exact comparison;
  Java 17 was used because Java 25 was unavailable at that time, and the full
  session record was added to the plan.

## Outcome and verification

The skill now defines ordered execution, exact stdout comparison, separate
stderr/non-zero-exit handling, fail-fast behavior, and complete transcript
reporting. The main artifacts are `.codex/skills/test-ui/SKILL.md`, its
`agents/openai.yaml`, `test/ui-test-plan.md`, and the `AGENTS.md` workflow rule.
