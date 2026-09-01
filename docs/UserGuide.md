# HomeHub User Guide

HomeHub is a household task manager for chores, deadlines, and events. Moss,
your calm household concierge, keeps the household board tidy and saves your
tasks automatically.

## Getting started

1. Install and select JDK 25.
2. From the project folder, start HomeHub:

   ```bash
   ./gradlew run
   ```

3. Type a command in the message box and press **Send**. In the command-line
   interface, type a command and press **Enter**.

Tasks are saved in `data/homehub.txt` and reloaded the next time HomeHub starts.
Commands and their keywords must be entered in lowercase.

### Run the release JAR

The latest bundled release is available at `release/homehub.jar`. It includes
the JavaFX libraries needed by the application and requires JDK 25.

```bash
java -jar release/homehub.jar
```

To regenerate the release JAR after making changes, run:

```bash
./gradlew shadowJar
cp build/libs/homehub.jar release/homehub.jar
```

## Test the application

With JDK 25 selected, run the automated test suite from the project root:

```bash
./gradlew test
```

To run the test suite together with Checkstyle verification, use:

```bash
./gradlew check
```

## Command reference

Replace text in angle brackets with your own values; do not type the angle
brackets. Task numbers are the one-based numbers shown by `list`.

### Add tasks

| Command | Description | Example usage |
| --- | --- | --- |
| `todo <description>` | Adds a task without a date or time. | `todo wash dishes` |
| `deadline <description> /by <date or time>` | Adds a task with a deadline. | `deadline pay electricity bill /by 2026-09-05` |
| `event <description> /from <start> /to <end>` | Adds an event with a start and end. | `event dinner /from 2026-09-06 19:00 /to 2026-09-06 21:00` |

Dates and times must use one of these formats:

- `yyyy-MM-dd`, for example `2026-09-01`
- `yyyy-MM-dd HH:mm`, for example `2026-09-01 14:30`

An event's end must be later than its start. Descriptions and date values cannot
be empty, and descriptions cannot contain the `|` character.

### View and search tasks

| Command | Description | Example usage |
| --- | --- | --- |
| `list` | Shows every task on the household board. | `list` |
| `find <keyword>` | Finds tasks whose descriptions contain the keyword, ignoring letter case. | `find bill` |

`find` is for viewing only and does not change the board. If no task matches,
Moss responds: `🫧 Moss couldn't find any tasks matching that keyword.`

### Update or remove tasks

| Command | Description | Example usage |
| --- | --- | --- |
| `mark <task number>` | Marks a task as done. | `mark 1` |
| `unmark <task number>` | Changes a completed task back to pending. | `unmark 1` |
| `delete <task number>` | Removes a task from the board. | `delete 2` |

The board is renumbered after a deletion.

### Other commands

| Command | Description | Example usage |
| --- | --- | --- |
| `help` | Shows every supported command and the accepted date/time formats. | `help` |
| `bye` | Closes HomeHub. Changes have already been saved automatically. | `bye` |

## Actual example session

The following transcript was captured by running HomeHub from a clean temporary
data directory. Lines beginning with `>` are commands entered by the user; the
remaining lines are the actual output printed by HomeHub.

```text
____________________________________________________________
🌿 Welcome to HomeHub. Moss is on duty.
Let's keep the household running smoothly. 🏡
____________________________________________________________
> help
____________________________________________________________
____________________________________________________________
📖 Moss's command guide:
todo <description> - add a household task.
deadline <description> /by <date or time> - add a task with a deadline.
event <description> /from <start> /to <end> - add a scheduled event.
list - show every task on the household board.
find <keyword> - find tasks by description.
mark <task number> - mark a task as done.
unmark <task number> - mark a task as pending.
delete <task number> - remove a task from the board.
help - show this command guide.
bye - close HomeHub.
Date/time format: yyyy-MM-dd or yyyy-MM-dd HH:mm.
Examples: 2026-09-01 or 2026-09-01 14:30.
____________________________________________________________
____________________________________________________________
> todo wash dishes
✨ On it. I've added this task:
  [T][ ] wash dishes
That makes 1 tasks on the board. 🎯
____________________________________________________________
____________________________________________________________
> deadline pay electricity bill /by 2026-09-05
✨ On it. I've added this task:
  [D][ ] pay electricity bill (by: Sept 05 2026)
That makes 2 tasks on the board. 🎯
____________________________________________________________
____________________________________________________________
> event dinner /from 2026-09-06 19:00 /to 2026-09-06 21:00
✨ On it. I've added this task:
  [E][ ] dinner (from: Sept 06 2026 19:00 to: Sept 06 2026 21:00)
That makes 3 tasks on the board. 🎯
____________________________________________________________
____________________________________________________________
> list
📋 Moss's household board:
1.[T][ ] wash dishes
2.[D][ ] pay electricity bill (by: Sept 05 2026)
3.[E][ ] dinner (from: Sept 06 2026 19:00 to: Sept 06 2026 21:00)
____________________________________________________________
____________________________________________________________
> find bill
🔎 Moss found these matching tasks:
1.[D][ ] pay electricity bill (by: Sept 05 2026)
____________________________________________________________
____________________________________________________________
> mark 1
Done and dusted. This task is complete: ✅
  [T][X] wash dishes
____________________________________________________________
____________________________________________________________
> unmark 1
Back on the board. This task is pending: 🔄
  [T][ ] wash dishes
____________________________________________________________
____________________________________________________________
> delete 2
Cleared from the board: 🗑️
  [D][ ] pay electricity bill (by: Sept 05 2026)
That leaves 2 tasks to keep tidy. ✨
____________________________________________________________
____________________________________________________________
> list
📋 Moss's household board:
1.[T][ ] wash dishes
2.[E][ ] dinner (from: Sept 06 2026 19:00 to: Sept 06 2026 21:00)
____________________________________________________________
____________________________________________________________
> bye
All tucked away. See you soon! 👋
____________________________________________________________
```

In task displays, `[T]`, `[D]`, and `[E]` identify todo, deadline, and event
tasks. `[ ]` means pending and `[X]` means done.
