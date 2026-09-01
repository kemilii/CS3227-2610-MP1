# HomeHub Developer Guide

HomeHub is a Java 25 household task manager with a JavaFX desktop interface and a command-line interface. Moss is the assistant persona shown to users. This document explains the current design, implementation choices, engineering process, and extension points.

## Release baseline

The repository does not contain a version tag. This guide describes the current
product implementation on `master`. The checked-in release artifact at
`release/homehub.jar` is generated with Gradle's `shadowJar` task and includes
the JavaFX libraries required by the application. The release includes todo,
deadline, event, list, find, mark, unmark, delete, help, and bye commands;
automatic local persistence; strict date validation; duplicate detection; and
the JavaFX conversation interface. User setup instructions and command examples
are maintained in [`UserGuide.md`](UserGuide.md).

## Product requirements

HomeHub is designed around a small set of user needs:

| Requirement | Product behavior |
| --- | --- |
| Record household work | Add todo, deadline, and event tasks. |
| Review work | List all tasks or search descriptions by a case-insensitive keyword. |
| Track progress | Mark tasks done or return them to pending. |
| Remove obsolete work | Delete tasks by their displayed one-based number. |
| Preserve work | Save after every successful mutation and reload tasks at startup. |
| Guide and protect users | Provide `help`, reject malformed input, and explain errors without corrupting task state. |

The product deliberately remains a local, single-user task manager. It does not provide accounts, cloud synchronisation, reminders, sorting, or time-zone handling.

## Architecture

The application is split into a presentation layer, an application layer, a domain layer, and a persistence layer. Both interfaces use the same command-processing and domain logic.

```text
JavaFX entry point                         Command-line entry point
Launcher -> Main -> MainWindow             HomeHub.main -> Ui
                         |                                  |
                         +----------> HomeHub <-------------+
                                      |
                 +--------------------+--------------------+
                 |                    |                    |
              Parser             TaskCommands          Ui/ResponseUi
                 |                    |                    |
           ParsedCommand        TaskList + Task       user-facing output
                                      |
                                   Storage
                                      |
                              data/homehub.txt
```

### Component responsibilities

| Component | Location | Responsibility |
| --- | --- | --- |
| Application coordinator | [`HomeHub.java`](../src/main/java/homehub/HomeHub.java) | Loads tasks, parses and dispatches commands, manages task mutations, and exposes GUI responses. |
| Parser | [`Parser.java`](../src/main/java/homehub/command/Parser.java) | Trims input, identifies the first-word command keyword, and returns a `ParsedCommand`. |
| Command representation | [`CommandType.java`](../src/main/java/homehub/command/CommandType.java), [`ParsedCommand.java`](../src/main/java/homehub/command/ParsedCommand.java) | Defines supported command keywords and their normalized arguments. |
| Creation commands | [`TaskCommands.java`](../src/main/java/homehub/command/TaskCommands.java) | Parses todo, deadline, and event arguments, validates them, prevents duplicates, saves changes, and reports confirmations. |
| Task model | [`Task.java`](../src/main/java/homehub/model/Task.java), [`Todo.java`](../src/main/java/homehub/model/Todo.java), [`Deadline.java`](../src/main/java/homehub/model/Deadline.java), [`Event.java`](../src/main/java/homehub/model/Event.java) | Represents task descriptions, completion status, typed task details, display formatting, and storage formatting. |
| Task collection | [`TaskList.java`](../src/main/java/homehub/model/TaskList.java) | Owns task ordering, indexed access, duplicate checks, and keyword search without exposing its mutable list. |
| Persistence | [`Storage.java`](../src/main/java/homehub/storage/Storage.java) | Reads and writes the local task file and filters malformed records. |
| CLI presentation | [`Ui.java`](../src/main/java/homehub/ui/Ui.java) | Reads commands from standard input and prints welcome messages, confirmations, errors, and task lists. |
| JavaFX presentation | [`Main.java`](../src/main/java/homehub/Main.java), [`MainWindow.java`](../src/main/java/homehub/MainWindow.java), [`DialogBox.java`](../src/main/java/homehub/DialogBox.java) | Builds the conversation window, forwards user input to `HomeHub`, and styles responses. |
| JavaFX resources | [`MainWindow.fxml`](../src/main/resources/view/MainWindow.fxml), [`DialogBox.fxml`](../src/main/resources/view/DialogBox.fxml), [`main.css`](../src/main/resources/css/main.css), [`dialog-box.css`](../src/main/resources/css/dialog-box.css) | Define the window layout, reusable message bubble, and visual styling. |

This separation keeps domain behavior independent of JavaFX. The CLI and GUI can therefore exercise the same command behavior and persistence rules.

## Main flows

### Processing a command

1. `MainWindow.handleUserInput()` sends the text field contents to `HomeHub.getResponse()`. The CLI entry point sends each line to the same private command executor with a concrete `Ui`.
2. `Parser.parse()` trims the input and splits it into a command keyword and the remaining argument text. Keywords are matched exactly against `CommandType`, so users enter commands in lowercase.
3. `HomeHub.executeCommand()` dispatches the parsed command. It handles list, find, help, bye, mark, unmark, and delete directly; creation commands are delegated to `TaskCommands`.
4. The selected operation validates its arguments and either updates `TaskList` or throws `HomeHubException` with a user-facing explanation.
5. The GUI receives the captured response from `ResponseUi`; the CLI prints through `Ui`. Successful GUI responses are styled according to the command type, while errors receive the error style.

### Adding a task safely

`TaskCommands` extracts marker-delimited fields (`/by`, `/from`, and `/to`) before constructing a typed task. `Deadline` and `Event` use the strict date parser in `Task.java`. An event is accepted only when its end is later than its start.

After validation, the command handler:

1. Checks `TaskList.hasTaskWithSameDetails()` to reject a duplicate of the same task type and details.
2. Appends the task to the in-memory list.
3. Calls `Storage.save()`.
4. Removes the appended task again if saving fails.
5. Displays a confirmation only after persistence succeeds.

This ordering prevents the in-memory state and the file from silently disagreeing after a failed write.

### Marking and deleting a task

Task numbers are one-based for users and zero-based inside `TaskList`. `HomeHub` parses and range-checks the number before changing state. Marking saves the new status and restores the previous status if saving fails. Deletion removes the selected task, saves the shorter list, and reinserts the task at its original index if saving fails. Both operations report success only after the save completes.

## Domain model

`Task` contains the shared description and mutable `TaskStatus` (`PENDING` or `DONE`). `Todo`, `Deadline`, and `Event` use inheritance for type-specific data and formatting:

- `Todo` has only a description.
- `Deadline` stores a parsed `LocalDateTime` and whether the input included a time.
- `Event` stores parsed start and end `LocalDateTime` values and their time-presence flags.

Each subtype overrides `getTypeIcon()`, `getDateDescription()`, and, where necessary, `toStorageString()`. This lets `Ui` display all task types through the `Task` interface without type checks.

Date input accepts `yyyy-MM-dd` and `yyyy-MM-dd HH:mm`. Date-only values are represented internally at midnight but retain their original date-only form for display and storage. `ResolverStyle.STRICT` rejects impossible calendar dates and invalid times.

`TaskList.findMatchingTasks()` lowercases both the keyword and each description using `Locale.ROOT`, then performs a substring search. It returns a new `TaskList`, preserving the original order and leaving the board unchanged.

## Persistence design

The default path is `data/homehub.txt`, relative to the process working directory. Each task occupies one pipe-delimited record:

```text
T | <status> | <description>
D | <status> | <description> | <date or date-time>
E | <status> | <description> | <start> | <end>
```

`T`, `D`, and `E` identify todo, deadline, and event tasks. Status `0` means pending and status `1` means done. Dates are stored in the same two machine-readable formats accepted by the command interface.

`Storage.save()` creates missing parent directories and replaces the file with the current list. It rejects duplicate task details, empty descriptions, control characters, and the `|` delimiter. `Storage.load()` treats a missing file as an empty list, ignores malformed records, and raises a `HomeHubException` for invalid dates or duplicate task details. The application catches startup load failures, starts with an empty list, and reports the problem through the active UI.

The pipe delimiter is intentionally forbidden in descriptions because the storage format is deliberately simple and has no escaping layer. If richer text or arbitrary user content becomes a requirement, the storage format should be replaced or given a proper escaping/serialization strategy rather than weakening this validation.

## Error handling and invariants

User-correctable input problems use `HomeHubException` and are shown with Moss's error prefix. Assertions document programmer-facing invariants such as non-null collaborators, valid task-list contents, and rollback results; Gradle enables assertions for tests.

Important invariants are:

- A task always has a description and a valid completion status.
- A `TaskList` never stores a null task.
- Duplicate task details are not allowed, regardless of completion status.
- An event's end is strictly after its start.
- A mutation is reported as successful only after `Storage.save()` succeeds.
- A failed add, mark/unmark, or delete restores the previous in-memory state.

## User-interface design

The JavaFX application is launched through [`Launcher.java`](../src/main/java/homehub/Launcher.java), which starts [`Main.java`](../src/main/java/homehub/Main.java). `Main` loads `MainWindow.fxml`, injects the shared `HomeHub` instance into `MainWindow`, and displays the stage.

`MainWindow` owns the input field, Send button, scroll pane, and conversation container. Each submitted input creates a user dialog and a Moss response dialog. `DialogBox` is a reusable FXML-backed control; it flips the message layout for Moss, hides the avatar for user messages, and applies command-specific or error-specific CSS classes. After `bye`, the controller disables further input.

The command-line interface remains available through `HomeHub.main()`. It uses `Ui` to read one trimmed line at a time, prints separators around responses, and stops when `isExitRequested()` becomes true. Keeping this entry point makes command behavior easy to test without requiring a graphical environment.

## Build and development workflow

### Tooling

- JDK 25 is required for compilation and execution.
- Gradle manages the application, JavaFX 17.0.7 platform dependencies, JUnit Jupiter 5.14.4, and Checkstyle 11.0.0.
- The Gradle application entry point is `homehub.Launcher`, which starts the JavaFX interface.

Useful commands from the repository root are:

```bash
./gradlew run       # launch the JavaFX application
./gradlew test      # run JUnit tests
./gradlew check     # run tests and Checkstyle
./gradlew shadowJar # build the executable fat JAR
```

On macOS, select the project-required Java distribution before running build tasks:

```bash
sdk use java 25.0.3.fx-zulu
```

### Engineering process

Development proceeded incrementally from the SE-EDU Java/JavaFX starter project:

1. Establish the JavaFX application shell and FXML-based conversation layout.
2. Introduce the task domain model and support todo, deadline, and event creation.
3. Extract parsing, UI, storage, and task-list responsibilities into focused packages.
4. Add completion tracking, unmarking, deletion, keyword search, and command help.
5. Add strict date parsing, duplicate detection, safe text validation, and rollback on persistence failures.
6. Add regression tests and expand the end-to-end UI test plan for each user-visible behavior.
7. Apply Checkstyle, the project Java coding standard, and the project Git conventions.
8. Update [`UserGuide.md`](UserGuide.md) and this Developer Guide to match the
   current implementation.

Changes are kept in focused commits with imperative, capitalized subjects. Behavior changes should update the relevant JUnit tests and, when console behavior changes, the corresponding cases and expected output in [`test/ui-test-plan.md`](../test/ui-test-plan.md).

## Testing strategy

The test suite emphasizes high-value behavior rather than trivial accessors:

| Test area | Test classes or artifacts | Coverage |
| --- | --- | --- |
| Command parsing | [`ParserTest.java`](../src/test/java/homehub/command/ParserTest.java) | Supported keywords, whitespace, blank input, unknown commands, and argument extraction. |
| Command orchestration | [`HomeHubTest.java`](../src/test/java/homehub/HomeHubTest.java) | Lifecycle operations, search, help, persistence, invalid input, duplicate tasks, and exit state. |
| Task creation | [`TaskCommandsTest.java`](../src/test/java/homehub/command/TaskCommandsTest.java) | Valid and invalid task syntax, dates, duplicates, unsafe text, and save-failure rollback. |
| Domain behavior | [`TaskTest.java`](../src/test/java/homehub/model/TaskTest.java), [`TaskListTest.java`](../src/test/java/homehub/model/TaskListTest.java) | Status transitions, display/storage formatting, date constraints, searching, ordering, and duplicate semantics. |
| Persistence | [`StorageTest.java`](../src/test/java/homehub/storage/StorageTest.java) | Mixed task round trips, missing files, malformed records, invalid dates, duplicate records, and invalid configuration. |
| GUI wiring/resources | [`MainTest.java`](../src/test/java/homehub/MainTest.java) | JavaFX class relationships, resource availability, FXML layout properties, and response styling metadata. |
| End-to-end CLI | [`test/ui-test-plan.md`](../test/ui-test-plan.md) | Thirteen documented scenarios, including persistence across two launches, with exact stdout comparison. |

The UI test plan uses fresh temporary working directories for isolated cases and one shared directory for the two persistence launches. It requires Java 25, fixed locale properties, empty stderr, a zero exit status, and exact output comparison. The latest recorded session reports all fourteen launches passing (thirteen scenarios, with UI-010 using two launches).

## Extending HomeHub

### Adding a command

1. Add the keyword to [`CommandType.java`](../src/main/java/homehub/command/CommandType.java).
2. Add a dispatch branch in `HomeHub.executeCommand()`.
3. Implement parsing and validation in the appropriate command class rather than placing all logic in `HomeHub`.
4. Add help text in `Ui.showHelp()` and, if necessary, response styling in `DialogBox.changeDialogStyle()`.
5. Add unit tests for parsing and command behavior.
6. Add or update the relevant UI test-plan case and expected output.
7. Update both user and developer documentation.

### Adding a task type

1. Create a `Task` subtype with type-specific fields and validation.
2. Override display and storage formatting methods.
3. Add its storage type marker and record shape to `Storage`.
4. Extend creation-command parsing and duplicate semantics as needed.
5. Add model, command, storage, and end-to-end regression tests.
6. Document the syntax, display behavior, and persistence representation.

### Changing persistence

Treat the file format as a compatibility boundary. Update both serialization and parsing together, add round-trip tests, and decide how existing files will be migrated before releasing the change. Avoid changing field delimiters or date formats without considering existing user data.

## Acknowledgements

HomeHub began from the SE-EDU Duke starter project. The repository history shows that the base branch, Gradle setup, JavaFX tutorial scaffold, FXML layout progression, and starter documentation were imported or adapted from that educational material. The HomeHub-specific task model, command set, persistence behavior, validation, Moss persona, GUI styling, and regression tests were developed in this repository.

The following sources and dependencies were reused or consulted:

- [SE-EDU Duke](https://github.com/se-edu/duke) — initial chatbot project structure and educational starting point.
- [SE-EDU JavaFX tutorial series](https://se-education.org/guides/tutorials/javaFx.html) — JavaFX application, FXML, controller, and conversation UI patterns reflected in the starter scaffold.
- [SE-EDU text UI testing tutorial](https://se-education.org/guides/tutorials/textUiTesting.html) — input redirection and expected-output comparison principles used by the CLI test plan.
- [SE-EDU Java coding conventions](https://se-education.org/guides/conventions/java.html) and [Git conventions](https://se-education.org/guides/conventions/git.html) — standards used for production code, tests, commits, and maintenance workflow.
- [SE-EDU Markdown conventions](https://se-education.org/guides/conventions/markdown.html) and [GitHub Markdown syntax](https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax) — formatting guidance used for the project documentation.
- [OpenJFX](https://openjfx.io/) — JavaFX runtime and controls used by the desktop interface through Gradle dependencies.
- [JUnit 5](https://junit.org/junit5/) — testing framework used by the automated unit and integration-style tests through Gradle dependencies.
- [Gradle](https://gradle.org/) — build and dependency-management tool, including its checked-in wrapper.

No external source is intended to receive credit for the HomeHub-specific behavior beyond the reused starter structure and the explicitly listed educational patterns, standards, and libraries above.
