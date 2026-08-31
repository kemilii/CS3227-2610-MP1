# Task 013 — Add chatbot user guide and developer guide

- Task ID: `01a0589c-6bcc-7491-981e-992c114c8fe0`
- Scope: User-facing and developer-facing documentation.

## Prompts and interactions

- Asked to update `docs/README.md` as a concise user guide with the chatbot
  name and enough guidance for all important features. The guide was written
  with setup, command tables, examples, date/time rules, task numbering,
  persistence, and a sample workflow.
- Asked to commit it and then add `docs/DeveloperGuide.md` describing the
  current design, software-engineering process, release baseline, and reuse
  acknowledgements. The User Guide was committed first as `7c95488 Document
  HomeHub user guide`; the Developer Guide was then added and verified.
- Asked to “commit it”. Only `docs/DeveloperGuide.md` was staged and committed
  as `3b6a0c2 Add HomeHub developer guide`; a pre-existing
  `data/homehub.txt` modification was deliberately left untouched.

## Outcome and verification

Both guides were completed and committed under the SE-EDU Git convention.
This was documentation work, so no UI tests were needed for the User Guide;
the Developer Guide was checked against the source tree, test suite, history,
and reuse acknowledgements.
