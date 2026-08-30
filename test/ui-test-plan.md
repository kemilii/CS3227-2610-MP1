# HomeHub UI test plan

## Scope and execution information

This plan tests HomeHub through its command-line interface. Each test case is an
end-to-end process test and must start with the stated task-file precondition.

- Required Java version: JDK 25. Verify both `java --version` and `javac --version`; a run using another JDK is not a valid pass.
- Compile command: `javac --release 25 -d <compiled-classes> src/main/java/*.java`
- Launch command: `java -Dfile.encoding=UTF-8 -Duser.language=en -Duser.country=SG -cp <compiled-classes> HomeHub`
- Input: provide every line shown in the test case, including a final newline after the last command.
- Output comparison: for cases with an `Expected output` block, compare stdout exactly after normalising only CRLF to LF. Preserve all other whitespace, blank lines, and the final newline. Assertion-based cases must still retain the complete actual stdout and check every stated result.
- Error output: stderr must be empty unless a test case explicitly says otherwise.
- Process result: the process must exit with status 0 and must not exceed the test harness timeout.
- Execution order: run test cases top to bottom and stop at the first failure.
- Isolation: UI-001 through UI-009 each run in a fresh temporary working directory with no `data/homehub.txt`. UI-010 deliberately reuses one temporary directory across two launches.
- Locale: use the JVM properties above so month names such as `Sept` are deterministic.

`<compiled-classes>` is a placeholder, not a literal shell argument. The
harness must create a temporary classes directory and a separate temporary
working directory for each isolated case.

## Test cases

### UI-001: Exit immediately

Aim: Verify that HomeHub displays its welcome screen and exits with the expected goodbye message.

Inputs:

```text
bye
```

Expected output:

```text
____________________________________________________________
Welcome to HomeHub!
Manage your household tasks here.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### UI-002: Add and list a task

Aim: Verify that a todo task is added and displayed by the list command.

Inputs:

```text
todo wash dishes
list
bye
```

Expected output:

```text
____________________________________________________________
Welcome to HomeHub!
Manage your household tasks here.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] wash dishes
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the household tasks in your HomeHub:
1.[T][ ] wash dishes
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### UI-003: Interleave invalid and valid task commands

Aim: Verify that invalid todo, deadline, event, unknown-command, and task-number inputs do not modify the internal task list, while valid commands continue to add and complete the correct tasks.

Inputs:

```text
todo
todo mop floor
deadline pay bill
deadline pay bill /by 2026-09-01
event meeting /from 2026-09-02
event meeting /from 2026-09-02 14:00 /to 2026-09-02 16:00
blah
list
mark 9
list
mark 1
list
bye
```

Expected output:

```text
____________________________________________________________
Welcome to HomeHub!
Manage your household tasks here.
____________________________________________________________
____________________________________________________________
Oops! A todo description cannot be empty.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] mop floor
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Oops! Use: deadline <description> /by <date or time>.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] pay bill (by: Sept 01 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Oops! Use: event <description> /from <start> /to <end>.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] meeting (from: Sept 02 2026 14:00 to: Sept 02 2026 16:00)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Oops! I don't recognise that command. Try todo, deadline, event, list, mark, unmark, or delete.
____________________________________________________________
____________________________________________________________
Here are the household tasks in your HomeHub:
1.[T][ ] mop floor
2.[D][ ] pay bill (by: Sept 01 2026)
3.[E][ ] meeting (from: Sept 02 2026 14:00 to: Sept 02 2026 16:00)
____________________________________________________________
____________________________________________________________
Oops! That task number does not exist.
____________________________________________________________
____________________________________________________________
Here are the household tasks in your HomeHub:
1.[T][ ] mop floor
2.[D][ ] pay bill (by: Sept 01 2026)
3.[E][ ] meeting (from: Sept 02 2026 14:00 to: Sept 02 2026 16:00)
____________________________________________________________
____________________________________________________________
Nice! I've marked this household task as done:
  [T][X] mop floor
____________________________________________________________
____________________________________________________________
Here are the household tasks in your HomeHub:
1.[T][X] mop floor
2.[D][ ] pay bill (by: Sept 01 2026)
3.[E][ ] meeting (from: Sept 02 2026 14:00 to: Sept 02 2026 16:00)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### UI-004: Delete tasks and preserve remaining state

Aim: Verify that deleting a valid task removes only that task, renumbers the remaining tasks, and that an invalid deletion does not change the list.

Inputs:

```text
todo first task
todo second task
delete 1
list
delete 5
list
bye
```

Expected output:

```text
____________________________________________________________
Welcome to HomeHub!
Manage your household tasks here.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] first task
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] second task
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [T][ ] first task
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the household tasks in your HomeHub:
1.[T][ ] second task
____________________________________________________________
____________________________________________________________
Oops! That task number does not exist.
____________________________________________________________
____________________________________________________________
Here are the household tasks in your HomeHub:
1.[T][ ] second task
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### UI-005: Reject invalid dates without corrupting task state

Aim: Verify that invalid calendar dates, invalid date text, and invalid task numbers are rejected while valid tasks remain correctly ordered and can still be marked or deleted.

Inputs:

```text
todo alpha
deadline due /by 2026-02-30
list
deadline due /by 2026-02-28
event outing /from nope /to 2026-03-02
event outing /from 2026-03-01 /to 2026-03-02
mark 3
list
delete 2
delete 4
list
bye
```

Expected output:

```text
____________________________________________________________
Welcome to HomeHub!
Manage your household tasks here.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] alpha
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Oops! Dates must use yyyy-MM-dd or yyyy-MM-dd HH:mm format.
____________________________________________________________
____________________________________________________________
Here are the household tasks in your HomeHub:
1.[T][ ] alpha
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] due (by: Feb 28 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Oops! Dates must use yyyy-MM-dd or yyyy-MM-dd HH:mm format.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] outing (from: Mar 01 2026 to: Mar 02 2026)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this household task as done:
  [E][X] outing (from: Mar 01 2026 to: Mar 02 2026)
____________________________________________________________
____________________________________________________________
Here are the household tasks in your HomeHub:
1.[T][ ] alpha
2.[D][ ] due (by: Feb 28 2026)
3.[E][X] outing (from: Mar 01 2026 to: Mar 02 2026)
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [D][ ] due (by: Feb 28 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Oops! That task number does not exist.
____________________________________________________________
____________________________________________________________
Here are the household tasks in your HomeHub:
1.[T][ ] alpha
2.[E][X] outing (from: Mar 01 2026 to: Mar 02 2026)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### UI-006: Mark and unmark tasks

Aim: Verify that `mark` changes a task to done, `unmark` changes it back to
pending, and both operations preserve the task list.

Inputs:

```text
todo wash dishes
mark 1
unmark 1
list
bye
```

Expected results:

- The task is displayed as `[T][X] wash dishes` after `mark 1`.
- The task is displayed as `[T][ ] wash dishes` after `unmark 1` and in the final list.
- The task count remains 1 throughout.
- The exact success messages and separators are checked against the application output.

### UI-007: Validate task-number arguments

Aim: Verify that missing, non-numeric, zero, negative, and out-of-range task
numbers are rejected without changing task state.

Inputs:

```text
todo one task
mark
mark nope
mark 0
mark -1
delete
delete nope
delete 0
delete -1
delete 9
list
bye
```

Expected results:

- Each invalid command produces one user-facing error and does not add, remove, or modify a task.
- The final list contains exactly one pending task: `1.[T][ ] one task`.
- Missing arguments, non-numeric arguments, and nonexistent task numbers are checked as distinct error categories.

### UI-008: Handle whitespace and empty input

Aim: Verify that command detection and command handling agree on whitespace
normalisation.

Inputs:

In the input notation below, `␠` represents one literal space and the first
line is an empty line. The harness must translate the notation before running
the case.

```text

␠␠todo spaced task␠␠
␠␠list␠␠
␠␠bye␠␠
```

Expected results:

- The blank line is handled without crashing or changing task state.
- Leading whitespace does not alter command parsing; the stored description is exactly `spaced task`.
- Trailing whitespace is ignored.
- The final list contains exactly one task.

### UI-009: Validate command syntax and date/time boundaries

Aim: Verify empty fields, duplicate markers, malformed date/time values, and
the documented date format are handled consistently.

Inputs:

```text
deadline /by 2026-10-15
deadline review /by
deadline review /by 2028-02-29
deadline invalid-time /by 2026-03-01 25:00
deadline invalid-format /by 2026-03-01T14:00
event outing /from 2026-10-15 /to
event outing /to 2026-10-16 /from 2026-10-15
event outing /from 2026-10-15 /from 2026-10-16 /to 2026-10-17
list
bye
```

Expected results:

- Empty descriptions, empty date/time fields, duplicate markers, and reversed markers are rejected without changing task state.
- `2028-02-29` is accepted as a valid leap-day date.
- `2026-03-01 25:00` is rejected as an invalid date/time.
- For this plan, the documented format is authoritative: an ISO `T` separator is rejected. If the application is intended to accept it instead, update the format contract and add the corresponding expected display output before running this case.
- The final list contains only inputs explicitly accepted by the chosen format contract.

### UI-010: Persist and reload tasks

Aim: Verify that saved tasks, completion state, deletion, and date/time fields
survive a new HomeHub process.

Precondition: use one clean temporary working directory for both launches.

First launch inputs:

```text
todo persisted
deadline review /by 2026-10-15
event meeting /from 2026-10-15 14:00 /to 2026-10-15 16:00
mark 1
delete 2
bye
```

Second launch inputs:

```text
list
bye
```

Expected results:

- The second launch lists exactly two tasks: the completed todo and the pending event, in their original relative order.
- The deleted deadline is absent.
- The saved file contains equivalent records, with no stale deleted task:

```text
T | 1 | persisted
E | 0 | meeting | 2026-10-15 14:00 | 2026-10-15 16:00
```

- Both launches exit with status 0 and produce empty stderr.

## Coverage matrix

| Behavior | Covered by |
| --- | --- |
| Startup, immediate exit, separators | UI-001 |
| Add and list todo | UI-002 |
| Invalid creation syntax and unknown command | UI-003, UI-009 |
| Valid deadline and event creation | UI-003, UI-005 |
| Invalid calendar date and invalid date text | UI-005, UI-009 |
| Mark task | UI-003, UI-005, UI-006 |
| Unmark task | UI-006 |
| Delete and renumber tasks | UI-004, UI-005 |
| Missing, malformed, and out-of-range indices | UI-007 |
| Empty and whitespace-padded input | UI-008 |
| Persistence and reload | UI-010 |

## Test-case pass criteria

For every case, record the exit status, stderr result, timeout result, and the
stdout comparison result. For cases with an exact-output block, include the
complete actual stdout in the execution report or save it as an attached test
artifact. For assertion-based cases, report every failed assertion and the
first differing output line.

## Test execution record

Keep the test specification above stable and record each run separately using
the following template. Do not record a run as PASS if it used a different JDK,
locale, working-directory setup, or input contract.

### Session: YYYY-MM-DD (short description)

- Commit or worktree revision:
- OS and architecture:
- Java vendor and `java --version`:
- `javac --version`:
- Locale JVM properties:
- Compile command:
- Launch command:
- Working-directory setup and persistence-file precondition:
- Timeout:
- Output comparison policy:
- Overall result:

For each executed case, record:

1. Test-case ID and input.
2. Exit status and timeout result.
3. Whether stderr was empty.
4. Exact-output or assertion result.
5. The complete console input/output record, or a link to the captured artifact.
6. If failed, the first differing line and a unified diff.

## Historical execution records (superseded)

The records below are retained for traceability only. They were run under Java
17 and include inputs such as `Friday` and `2pm`, which are not part of the
current documented date format. They must not be used as evidence for the
current plan; future runs belong in the template above.

### Session: 2026-08-30 (interleaved edge cases)

- Java requested: 25 (`sdk use java 25.0.3.fx-zulu` was unavailable; Java 17.0.14 was used)
- Compile command: `javac -d <temporary-directory> src/main/java/*.java`
- Launch command for each case: `java -cp <temporary-directory> HomeHub`
- Each case ran from a clean temporary working directory; stdout and stderr were captured separately and stdout was compared exactly with its expected-output block.
- UI-001, UI-002, UI-003, UI-004, and UI-005: PASS; all exited with status 0 and produced no stderr.
- UI-005 input: `todo alpha`, `deadline due /by 2026-02-30`, `list`, `deadline due /by 2026-02-28`, `event outing /from nope /to 2026-03-02`, `event outing /from 2026-03-01 /to 2026-03-02`, `mark 3`, `list`, `delete 2`, `delete 4`, `list`, `bye`.
- UI-005 actual output: identical to the complete `Expected output` block above; invalid dates and indices left the internal task state unchanged.

### Session: 2026-08-30 (dates and times)

- Java requested: 25 (`sdk use java 25.0.3.fx-zulu` was unavailable; Java 17.0.14 was used)
- Compile command: `javac -d <temporary-directory> src/main/java/*.java`
- Launch command: `java -cp <temporary-directory> HomeHub`
- UI-001 through UI-004: PASS; exact stdout matched the corresponding expected output, stderr was empty, and all processes exited with status 0.
- Additional date/time input: `deadline report /by 2019-10-15`, `event meeting /from 2019-10-15 14:00 /to 2019-10-15 16:00`, `list`, `bye`: PASS.
- Reload verification: started HomeHub again with `list`, `bye`; both parsed tasks were restored with formatted dates/times.

### Session: 2026-08-30

- Java requested: 25 (`sdk use java 25.0.3.fx-zulu` was unavailable; Java 17.0.14 was used)
- Compile command: `javac -d <temporary-directory> src/main/java/*.java`
- Launch command: `java -cp <temporary-directory> HomeHub`
- Result: all documented cases passed exact stdout comparison; no failure occurred.

#### UI-001

Console input:

```text
bye
```

Console output:

```text
____________________________________________________________
Welcome to HomeHub!
Manage your household tasks here.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

Status: PASS

### Session: 2026-08-30 (persistence write happy path)

- Java requested: 25 (`sdk use java 25.0.3.fx-zulu` was unavailable; Java 17.0.14 was used)
- Compile command: `javac -d <temporary-directory> src/main/java/*.java`
- Launch command: `java -cp <temporary-directory> HomeHub`
- Console input: `todo clean kitchen`, `deadline pay bill /by Friday`, `event inspection /from 2pm /to 4pm`, `mark 1`, `delete 2`, `bye`
- Result: PASS; the application exited successfully with no stderr output.
- Saved file verification: `data/homehub.txt` contained:

```text
T | 1 | clean kitchen
E | 0 | inspection | 2pm | 4pm
```

### Session: 2026-08-30 (test-ui rerun)

- Java requested: 25 (`sdk` was unavailable; Java 17.0.14 was used)
- Compile command: `javac -d <temporary-directory> src/main/java/*.java`
- Launch command: `java -cp <temporary-directory> HomeHub`
- Actual stdout was captured without normalization and compared byte-for-byte with each test case's expected output.
- No stderr was produced; all processes exited with status 0.

#### UI-001 through UI-004

The documented inputs were executed in order. The complete actual console outputs were identical to the corresponding `Expected output` blocks above for UI-001, UI-002, UI-003, and UI-004.

Status: PASS

### Session: 2026-08-30 (enum integration)

- Java requested: 25 (`sdk` was unavailable; Java 17.0.14 was used)
- Compile command: `javac -d <temporary-directory> src/main/java/*.java`
- Launch command: `java -cp <temporary-directory> HomeHub`
- Result: UI-001 through UI-004 passed exact stdout comparison; no stderr was produced and all processes exited successfully.

#### UI-004

Console input:

```text
todo first task
todo second task
delete 1
list
delete 5
list
bye
```

Console output:

```text
____________________________________________________________
Welcome to HomeHub!
Manage your household tasks here.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] first task
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] second task
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [T][ ] first task
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the household tasks in your HomeHub:
1.[T][ ] second task
____________________________________________________________
____________________________________________________________
Oops! That task number does not exist.
____________________________________________________________
____________________________________________________________
Here are the household tasks in your HomeHub:
1.[T][ ] second task
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

Status: PASS

### Session: 2026-08-30 (edge cases)

- Java requested: 25 (`sdk use java 25.0.3.fx-zulu` was unavailable; Java 17.0.14 was used)
- Compile command: `javac -d <temporary-directory> src/main/java/*.java`
- Launch command: `java -cp <temporary-directory> HomeHub`
- Result: UI-001, UI-002, and UI-003 passed exact stdout comparison; no stderr was produced and all processes exited successfully.

#### UI-001

Console input:

```text
bye
```

Console output: See the identical exact output recorded under the initial UI-001 session above.

Status: PASS

#### UI-002

Console input:

```text
todo wash dishes
list
bye
```

Console output: See the identical exact output recorded under the initial UI-002 session above.

Status: PASS

#### UI-003

Console input:

```text
todo
todo mop floor
deadline pay bill
deadline pay bill /by Friday
event meeting /from 2pm
event meeting /from 2pm /to 4pm
blah
list
mark 9
list
mark 1
list
bye
```

Console output:

```text
____________________________________________________________
Welcome to HomeHub!
Manage your household tasks here.
____________________________________________________________
____________________________________________________________
Oops! A todo description cannot be empty.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] mop floor
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Oops! Use: deadline <description> /by <date or time>.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] pay bill (by: Friday)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Oops! Use: event <description> /from <start> /to <end>.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] meeting (from: 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Oops! I don't recognise that command. Try todo, deadline, event, list, mark, or delete.
____________________________________________________________
____________________________________________________________
Here are the household tasks in your HomeHub:
1.[T][ ] mop floor
2.[D][ ] pay bill (by: Friday)
3.[E][ ] meeting (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Oops! That task number does not exist.
____________________________________________________________
____________________________________________________________
Here are the household tasks in your HomeHub:
1.[T][ ] mop floor
2.[D][ ] pay bill (by: Friday)
3.[E][ ] meeting (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Nice! I've marked this household task as done:
  [T][X] mop floor
____________________________________________________________
____________________________________________________________
Here are the household tasks in your HomeHub:
1.[T][X] mop floor
2.[D][ ] pay bill (by: Friday)
3.[E][ ] meeting (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

Status: PASS

#### UI-002

Console input:

```text
todo wash dishes
list
bye
```

Console output:

```text
____________________________________________________________
Welcome to HomeHub!
Manage your household tasks here.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] wash dishes
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the household tasks in your HomeHub:
1.[T][ ] wash dishes
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

Status: PASS
