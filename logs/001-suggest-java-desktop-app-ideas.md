# Task 001 — Suggest Java desktop app ideas

- Task ID: `01a0523a-8798-7f70-902a-14e29c850343`
- Scope: Initial HomeHub concept and the main Duke Level 3 implementation sequence.

## Prompts and interactions

1. Asked for Java desktop project ideas satisfying the CS3227 Duke Level 3 requirements. Several options were suggested, including HomeHub.
2. Chose HomeHub and asked for key features. The response proposed household chores, maintenance, bills, events, completion tracking, deadlines, deletion, search, local persistence, validation, and summaries.
3. Asked to rename the existing app to fit HomeHub without adding features. The app was renamed from Caki to HomeHub and its user-facing wording was changed while existing commands were preserved.
4. Requested ToDos, Deadlines, and Events. These were implemented as task types with `todo`, `deadline ... /by ...`, and `event ... /from ... /to ...` commands.
5. Requested inheritance and polymorphic storage. The response confirmed that `Todo`, `Deadline`, and `Event` extend `Task` and can be stored in a `Task[]`.
6. Requested exception-based error handling. `HomeHubException` and command-specific validation were added for malformed input, invalid indices, unknown commands, and full lists.
7. Requested automatic persistence, initially as the happy-path write-only implementation. `TaskStorage` was added and mutations rewrote `data/homehub.txt`; a visual diff and UI record were generated.
8. Requested startup loading. Saved task records were reconstructed into the correct subclasses, completion state was restored, missing files produced an empty list, and malformed records were ignored.
9. Requested more interleaved positive and negative UI cases. UI-003 was added to check that rejected commands did not corrupt internal state.
10. Requested broader edge-case handling. Strict date parsing and additional validation were added after testing exposed silent date normalization.
11. Requested date/time support using `LocalDate`/`LocalDateTime`. Deadline and event values were converted from strings to `LocalDateTime`, canonical persistence was added, and formatted display output was updated.
12. Added requirements for relative, OS-independent paths and missing data directories/files. Storage handling was updated accordingly.
13. Requested Java Collections support through `delete`. The fixed array was replaced with `ArrayList<Task>`, deletion was implemented, and numbering/state behavior was tested.
14. Requested another UI test pass and edge-case coverage. The test plan was expanded and later used to identify missing `unmark` help text, leading-whitespace handling, and overly permissive date parsing; those defects were fixed.
15. The final task history recorded the cumulative HomeHub development trajectory from project selection through validation and persistence.

## Outcome

The thread established the HomeHub product direction and delivered the early
CLI application: typed tasks, inheritance, exceptions, collections, date/time
handling, persistence, deletion, and progressively stronger UI tests.

## Verification and files

Verification repeatedly used compilation, smoke tests, JUnit where available,
and the project UI plan. Earlier runs sometimes used Java 17 because Java 25
was unavailable; later runs used Java 25. Key files included the original
`HomeHub.java`, `Task.java`, `Todo.java`, `Deadline.java`, `Event.java`,
`TaskStorage.java`/`Storage.java`, `TaskList.java`, `HomeHubException.java`,
and `test/ui-test-plan.md`.
