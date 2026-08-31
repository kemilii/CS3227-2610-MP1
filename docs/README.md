# HomeHub User Guide

HomeHub is a household task manager. Moss, your calm household concierge, helps you keep track of chores, deadlines, and events.

## Getting started

1. Make sure JDK 25 is installed.
2. From the project folder, start HomeHub with:

   ```bash
   ./gradlew run
   ```

3. Type a command in the message box and press **Send**. Type `help` at any time to display the command list.

HomeHub saves your tasks automatically as you make changes and reloads them the next time you start the application. The local data file is `data/homehub.txt`.

## Commands

Commands are written in lowercase. Replace text in angle brackets with your own values; do not type the angle brackets.

### Add tasks

| Command | What it does | Example |
| --- | --- | --- |
| `todo <description>` | Adds a task without a date or time. | `todo wash dishes` |
| `deadline <description> /by <date or time>` | Adds a task with a deadline. | `deadline submit report /by 2026-09-01` |
| `event <description> /from <start> /to <end>` | Adds an event with a start and end. | `event team meeting /from 2026-09-02 14:00 /to 2026-09-02 16:00` |

Dates and times must use one of these formats:

- `yyyy-MM-dd`, for example `2026-09-01`
- `yyyy-MM-dd HH:mm`, for example `2026-09-01 14:30`

An event's end must be later than its start. Task descriptions and date values cannot be empty, and descriptions cannot contain the `|` character.

### View and search tasks

| Command | What it does | Example |
| --- | --- | --- |
| `list` | Shows every task on the household board. | `list` |
| `find <keyword>` | Finds tasks whose descriptions contain the keyword, ignoring letter case. | `find report` |

Tasks are numbered from `1` in the order they appear. Search results are for viewing only and do not change the board.

### Update or remove tasks

| Command | What it does | Example |
| --- | --- | --- |
| `mark <task number>` | Marks a task as done. | `mark 1` |
| `unmark <task number>` | Changes a completed task back to pending. | `unmark 1` |
| `delete <task number>` | Removes a task from the board. | `delete 2` |

Use the number shown by `list` when marking, unmarking, or deleting a task. The board is renumbered after a deletion.

### Other commands

| Command | What it does |
| --- | --- |
| `help` | Shows all commands and accepted date/time formats. |
| `bye` | Closes HomeHub. Your changes have already been saved automatically. |

## Example session

```text
todo buy groceries
deadline pay electricity bill /by 2026-09-05
event dinner /from 2026-09-06 19:00 /to 2026-09-06 21:00
list
mark 1
find bill
bye
```

Moss confirms successful changes and explains how to correct invalid commands.
