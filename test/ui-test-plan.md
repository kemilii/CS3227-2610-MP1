# HomeHub UI test plan

## Execution information

- Launch command: `java -cp <compiled-classes> HomeHub`
- Compile command: `javac -d <compiled-classes> src/main/java/*.java`
- Java version: 25
- Output comparison: exact stdout comparison; whitespace and blank lines are significant.
- Execution order: run test cases top to bottom and stop at the first failure.

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
Oops! I don't recognise that command. Try todo, deadline, event, list, mark, or delete.
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

## Test session record

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
