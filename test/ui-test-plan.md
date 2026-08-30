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

## Test session record

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
