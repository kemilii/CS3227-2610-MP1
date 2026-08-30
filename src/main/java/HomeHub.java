import java.util.Scanner;

/**
 * A simple command-line household task manager that accepts commands until the
 * user says bye.
 */
public class HomeHub {
    private static final int MAX_TASKS = 100;

    public static void main(String[] args) {
        String separator = "____________________________________________________________";
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        System.out.println(separator);
        System.out.println("Welcome to HomeHub!");
        System.out.println("Manage your household tasks here.");
        System.out.println(separator);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(separator);

            try {
                if (command.equals("bye")) {
                    System.out.println("Bye. Hope to see you again soon!");
                    System.out.println(separator);
                    break;
                } else if (command.equals("list")) {
                    printTaskList(tasks, taskCount);
                } else if (command.equals("mark") || command.startsWith("mark ")) {
                    markTask(tasks, taskCount, command, true);
                } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                    markTask(tasks, taskCount, command, false);
                } else if (command.equals("todo") || command.startsWith("todo ")) {
                    taskCount = addTodo(tasks, taskCount, command);
                } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                    taskCount = addDeadline(tasks, taskCount, command);
                } else if (command.equals("event") || command.startsWith("event ")) {
                    taskCount = addEvent(tasks, taskCount, command);
                } else {
                    throw new HomeHubException("I don't recognise that command. Try todo, deadline, event, list, or mark.");
                }
            } catch (HomeHubException exception) {
                System.out.println("Oops! " + exception.getMessage());
            }
            System.out.println(separator);
        }
    }

    private static void printTaskList(Task[] tasks, int taskCount) {
        System.out.println("Here are the household tasks in your HomeHub:");
        for (int i = 0; i < taskCount; i++) {
            System.out.println((i + 1) + "." + tasks[i].toDisplayString());
        }
    }

    private static void markTask(Task[] tasks, int taskCount, String command, boolean markAsDone)
            throws HomeHubException {
        String action = markAsDone ? "mark" : "unmark";
        String taskNumberText = command.substring(action.length()).trim();
        if (taskNumberText.isEmpty()) {
            throw new HomeHubException("Please provide a task number after " + action + ".");
        }
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new HomeHubException("That task number does not exist.");
            }
            Task task = tasks[taskNumber - 1];
            if (markAsDone) {
                task.markAsDone();
                System.out.println("Nice! I've marked this household task as done:");
            } else {
                task.markAsNotDone();
                System.out.println("I've marked this household task as not done:");
            }
            System.out.println("  " + task.toDisplayString());
        } catch (NumberFormatException exception) {
            throw new HomeHubException("The task number must be a whole number.");
        }
    }

    private static int addTodo(Task[] tasks, int taskCount, String command) throws HomeHubException {
        String description = command.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new HomeHubException("A todo description cannot be empty.");
        }
        return addTask(tasks, taskCount, new Todo(description));
    }

    private static int addDeadline(Task[] tasks, int taskCount, String command) throws HomeHubException {
        String content = command.substring("deadline".length()).trim();
        int byMarker = content.indexOf(" /by ");
        if (byMarker <= 0 || content.substring(byMarker + 5).trim().isEmpty()) {
            throw new HomeHubException("Use: deadline <description> /by <date or time>.");
        }
        return addTask(tasks, taskCount, new Deadline(content.substring(0, byMarker).trim(),
                content.substring(byMarker + 5).trim()));
    }

    private static int addEvent(Task[] tasks, int taskCount, String command) throws HomeHubException {
        String content = command.substring("event".length()).trim();
        int fromMarker = content.indexOf(" /from ");
        int toMarker = content.indexOf(" /to ");
        if (fromMarker <= 0 || toMarker <= fromMarker || content.substring(toMarker + 5).trim().isEmpty()) {
            throw new HomeHubException("Use: event <description> /from <start> /to <end>.");
        }
        return addTask(tasks, taskCount, new Event(content.substring(0, fromMarker).trim(),
                content.substring(fromMarker + 7, toMarker).trim(),
                content.substring(toMarker + 5).trim()));
    }

    private static int addTask(Task[] tasks, int taskCount, Task task) throws HomeHubException {
        if (taskCount >= MAX_TASKS) {
            throw new HomeHubException("Your task list is full. Delete a task before adding another.");
        }
        tasks[taskCount] = task;
        int newTaskCount = taskCount + 1;
        printAddedTask(task, newTaskCount);
        return newTaskCount;
    }

    private static void printAddedTask(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task.toDisplayString());
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }
}
