# HomeHub Developer Guide

HomeHub is a Java 25 pantry inventory manager with a JavaFX desktop interface
and a command-line interface. Moss is the assistant persona shown to users.
The product is deliberately centered on stock quantities and expiry dates, not
on household work or completion tracking.

## Requirements

| Requirement | Product behavior |
| --- | --- |
| Record pantry stock | Add a named entry with quantity, unit, expiry date, category, location, and optional minimum stock. |
| Keep quantities accurate | Restock or consume a positive quantity by item number. |
| Find useful information | Search names, filter by expiry cutoff, identify low stock, and view a summary. |
| Organize storage | Move entries between named storage locations. |
| Remove obsolete records | Delete entries by their displayed number. |
| Preserve inventory | Save after every successful mutation and reload at startup. |
| Protect data | Reject invalid input, duplicate entries, unsafe text, and over-consumption. |

## Architecture

```text
JavaFX entry point                         Command-line entry point
Launcher -> Main -> MainWindow             HomeHub.main -> Ui
                         |                                  |
                         +----------> HomeHub <-------------+
                                      |
                 +--------------------+--------------------+
                 |                    |                    |
              Parser          PantryCommands          Ui/ResponseUi
                 |                    |                    |
           ParsedCommand       PantryList + PantryItem   output
                                      |
                                   Storage
                                      |
                               data/pantry.txt
```

| Component | Location | Responsibility |
| --- | --- | --- |
| Coordinator | [`HomeHub.java`](../src/main/java/homehub/HomeHub.java) | Loads inventory, dispatches commands, performs quantity/removal mutations, and provides GUI responses. |
| Parser | [`Parser.java`](../src/main/java/homehub/command/Parser.java) | Extracts a lowercase command keyword and normalized argument text. |
| Command representation | [`CommandType.java`](../src/main/java/homehub/command/CommandType.java), [`ParsedCommand.java`](../src/main/java/homehub/command/ParsedCommand.java) | Defines supported command keywords and parsed arguments. |
| Add command | [`PantryCommands.java`](../src/main/java/homehub/command/PantryCommands.java) | Parses marker-delimited add fields, validates them, rejects duplicates, saves, and rolls back failed additions. |
| Domain model | [`PantryItem.java`](../src/main/java/homehub/model/PantryItem.java), [`ExpiryDate.java`](../src/main/java/homehub/model/ExpiryDate.java) | Represents stock, metadata, quantity changes, low-stock thresholds, expiry dates, and display/storage formatting. |
| Collection | [`PantryList.java`](../src/main/java/homehub/model/PantryList.java) | Owns ordering, indexed access, duplicate checks, name search, and expiry filtering. |
| Persistence | [`Storage.java`](../src/main/java/homehub/storage/Storage.java) | Reads and writes the local pantry file and skips malformed records. |
| Presentation | [`Ui.java`](../src/main/java/homehub/ui/Ui.java) | Prints welcome text, confirmations, inventory views, and errors. |
| JavaFX view | `Main.java`, `MainWindow.java`, `DialogBox.java`, and the FXML/CSS resources | Provides the graphical conversation shell and command-specific response styling. |

## Command processing

`Parser` recognizes only exact lowercase keywords. `HomeHub` dispatches read-only
queries directly, delegates add-field parsing to `PantryCommands`, and owns
mutations that target an item number. A successful mutation is reported only
after `Storage.save()` succeeds. Add, quantity changes, and deletion restore
the in-memory state when persistence fails.

The add syntax is intentionally explicit:

```text
add <name> /qty <number> /unit <unit> /expires <yyyy-MM-dd> [/category <category>] [/location <location>] [/min <quantity>]
```

The marker order is fixed so malformed or ambiguous input is rejected. Names,
units, categories, and locations cannot be empty, contain control characters,
or contain the pipe delimiter. Quantities must be positive, minimum stock can
be zero, and `consume` cannot reduce stock below zero. Expiry dates use strict
`ResolverStyle.STRICT` parsing, so impossible dates such as 2026-02-30 are
rejected.

## Persistence format

The default path is `data/pantry.txt`, relative to the process working
directory. Each entry occupies one pipe-delimited record:

```text
P | <name> | <quantity> | <unit> | <expiry date> | <category> | <location> | <minimum quantity>
```

`P` identifies a pantry entry. Dates are stored as `yyyy-MM-dd`. The simple
format intentionally forbids the pipe delimiter in user text. Missing files
represent an empty pantry; legacy five-field pantry records are loaded with
defaults (`general`, `pantry`, and minimum `0`) and rewritten in the new format
on the next save. Records from the former task-oriented format or malformed
pantry records are ignored. Duplicate pantry entries cause a load or save error
rather than silently merging quantities.

## User interface

The JavaFX application is launched through [`Launcher.java`](../src/main/java/homehub/Launcher.java).
`MainWindow` forwards each submitted message to `HomeHub.getResponse()` and
adds the captured response to the conversation. `DialogBox` styles add, stock,
search, expiry, low-stock, summary, move, deletion, and error responses. The CLI remains available via
`HomeHub.main()` and is the basis of the end-to-end UI test plan.

## Build and test

JDK 25 is required. Useful commands are:

```bash
./gradlew run
./gradlew test
./gradlew check
./gradlew shadowJar
```

The unit tests focus on parser behavior, inventory invariants, command parsing,
quantity changes, persistence round trips, malformed input, and coordinator
behavior. The exact CLI scenarios are maintained in
[`test/ui-test-plan.md`](../test/ui-test-plan.md).

When adding a command, update `CommandType`, `HomeHub`, `Ui`, `DialogBox`, the
unit tests, the user guide, and the relevant UI test-plan case together.
