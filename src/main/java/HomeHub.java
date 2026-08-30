import java.util.ArrayList;
import java.util.Scanner;

/**
 * A simple command-line household task manager that accepts commands until the
 * user says bye.
 */
public class HomeHub {
    public static void main(String[] args) {
        String separator = "____________________________________________________________";
        ArrayList<Task> tasks;
        try {
            tasks = TaskStorage.load();
        } catch (HomeHubException exception) {
            tasks = new ArrayList<>();
            System.out.println("Oops! " + exception.getMessage());
        }

        System.out.println(separator);
        System.out.println("Welcome to HomeHub!");
        System.out.println("Manage your household tasks here.");
        System.out.println(separator);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(separator);

            try {
                CommandType commandType = CommandType.fromInput(command);
                if (commandType == CommandType.BYE) {
                    System.out.println("Bye. Hope to see you again soon!");
                    System.out.println(separator);
                    break;
                } else if (commandType == CommandType.LIST) {
                    printTaskList(tasks);
                } else if (commandType == CommandType.MARK) {
                    markTask(tasks, command, true);
                } else if (commandType == CommandType.UNMARK) {
                    markTask(tasks, command, false);
                } else if (commandType == CommandType.DELETE) {
                    deleteTask(tasks, command);
                } else if (commandType == CommandType.TODO) {
                    addTodo(tasks, command);
                } else if (commandType == CommandType.DEADLINE) {
                    addDeadline(tasks, command);
                } else if (commandType == CommandType.EVENT) {
                    addEvent(tasks, command);
                } else {
                    throw new HomeHubException("I don't recognise that command. Try todo, deadline, event, list, mark, or delete.");
                }
            } catch (HomeHubException exception) {
                System.out.println("Oops! " + exception.getMessage());
            } catch (RuntimeException exception) {
                System.out.println("Oops! HomeHub could not process that input.");
            }
            System.out.println(separator);
        }
    }

    private static void printTaskList(ArrayList<Task> tasks) {
        System.out.println("Here are the household tasks in your HomeHub:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i).toDisplayString());
        }
    }

    private static void markTask(ArrayList<Task> tasks, String command, boolean markAsDone)
            throws HomeHubException {
        String action = markAsDone ? "mark" : "unmark";
        String taskNumberText = command.substring(action.length()).trim();
        if (taskNumberText.isEmpty()) {
            throw new HomeHubException("Please provide a task number after " + action + ".");
        }
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new HomeHubException("That task number does not exist.");
            }
            Task task = tasks.get(taskNumber - 1);
            TaskStatus previousStatus = task.status;
            if (markAsDone) {
                task.markAsDone();
                System.out.println("Nice! I've marked this household task as done:");
            } else {
                task.markAsNotDone();
                System.out.println("I've marked this household task as not done:");
            }
            try {
                TaskStorage.save(tasks);
            } catch (HomeHubException exception) {
                task.status = previousStatus;
                throw exception;
            }
            System.out.println("  " + task.toDisplayString());
        } catch (NumberFormatException exception) {
            throw new HomeHubException("The task number must be a whole number.");
        }
    }

    private static void deleteTask(ArrayList<Task> tasks, String command) throws HomeHubException {
        String taskNumberText = command.substring("delete".length()).trim();
        if (taskNumberText.isEmpty()) {
            throw new HomeHubException("Please provide a task number after delete.");
        }
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new HomeHubException("That task number does not exist.");
            }
            Task removedTask = tasks.remove(taskNumber - 1);
            try {
                TaskStorage.save(tasks);
            } catch (HomeHubException exception) {
                tasks.add(taskNumber - 1, removedTask);
                throw exception;
            }
            System.out.println("Noted. I've removed this task:");
            System.out.println("  " + removedTask.toDisplayString());
            System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        } catch (NumberFormatException exception) {
            throw new HomeHubException("The task number must be a whole number.");
        }
    }

    private static void addTodo(ArrayList<Task> tasks, String command) throws HomeHubException {
        String description = command.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new HomeHubException("A todo description cannot be empty.");
        }
        validateText(description, "A todo description");
        addTask(tasks, new Todo(description));
    }

    private static void addDeadline(ArrayList<Task> tasks, String command) throws HomeHubException {
        String content = command.substring("deadline".length()).trim();
        int byMarker = content.indexOf(" /by ");
        if (byMarker <= 0 || content.indexOf(" /by ", byMarker + 1) >= 0
                || content.substring(byMarker + 5).trim().isEmpty()) {
            throw new HomeHubException("Use: deadline <description> /by <date or time>.");
        }
        String description = content.substring(0, byMarker).trim();
        String by = content.substring(byMarker + 5).trim();
        validateText(description, "A deadline description");
        validateText(by, "A deadline date or time");
        addTask(tasks, new Deadline(description, by));
    }

    private static void addEvent(ArrayList<Task> tasks, String command) throws HomeHubException {
        String content = command.substring("event".length()).trim();
        int fromMarker = content.indexOf(" /from ");
        int toMarker = content.indexOf(" /to ");
        if (fromMarker <= 0 || content.indexOf(" /from ", fromMarker + 1) >= 0
                || toMarker <= fromMarker || content.indexOf(" /to ", toMarker + 1) >= 0
                || content.substring(toMarker + 5).trim().isEmpty()) {
            throw new HomeHubException("Use: event <description> /from <start> /to <end>.");
        }
        String description = content.substring(0, fromMarker).trim();
        String from = content.substring(fromMarker + 7, toMarker).trim();
        String to = content.substring(toMarker + 5).trim();
        validateText(description, "An event description");
        validateText(from, "An event start date or time");
        validateText(to, "An event end date or time");
        addTask(tasks, new Event(description, from, to));
    }

    private static void addTask(ArrayList<Task> tasks, Task task) throws HomeHubException {
        tasks.add(task);
        try {
            TaskStorage.save(tasks);
        } catch (HomeHubException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
        printAddedTask(task, tasks.size());
    }

    private static void validateText(String value, String fieldName) throws HomeHubException {
        if (value.indexOf('|') >= 0) {
            throw new HomeHubException(fieldName + " cannot contain the '|' character.");
        }
    }

    private static void printAddedTask(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task.toDisplayString());
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }
}
