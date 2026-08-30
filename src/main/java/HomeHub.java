/**
 * A simple command-line household task manager that accepts commands until the
 * user says bye.
 */
public class HomeHub {
    public static void main(String[] args) {
        Ui ui = new Ui();
        Parser parser = new Parser();
        Storage storage = new Storage("data/homehub.txt");
        TaskList tasks;
        try {
            tasks = new TaskList(storage.load());
        } catch (HomeHubException exception) {
            tasks = new TaskList();
            ui.showError(exception.getMessage());
        }

        ui.showWelcome();

        String command;
        while ((command = ui.readCommand()) != null) {
            ui.showSeparator();

            try {
                ParsedCommand parsedCommand = parser.parse(command);
                CommandType commandType = parsedCommand.type();
                if (commandType == CommandType.BYE) {
                    ui.showGoodbye();
                    break;
                } else if (commandType == CommandType.LIST) {
                    ui.showTaskList(tasks);
                } else if (commandType == CommandType.MARK) {
                    markTask(tasks, parsedCommand.arguments(), true, storage);
                } else if (commandType == CommandType.UNMARK) {
                    markTask(tasks, parsedCommand.arguments(), false, storage);
                } else if (commandType == CommandType.DELETE) {
                    deleteTask(tasks, parsedCommand.arguments(), storage);
                } else if (commandType == CommandType.TODO) {
                    addTodo(tasks, parsedCommand.arguments(), storage);
                } else if (commandType == CommandType.DEADLINE) {
                    addDeadline(tasks, parsedCommand.arguments(), storage);
                } else if (commandType == CommandType.EVENT) {
                    addEvent(tasks, parsedCommand.arguments(), storage);
                } else {
                    throw new HomeHubException("I don't recognise that command. Try todo, deadline, event, list, mark, unmark, or delete.");
                }
            } catch (HomeHubException exception) {
                ui.showError(exception.getMessage());
            } catch (RuntimeException exception) {
                ui.showError("HomeHub could not process that input.");
            }
            ui.showSeparator();
        }
    }

    private static void markTask(TaskList tasks, String arguments, boolean markAsDone,
            Storage storage)
            throws HomeHubException {
        String action = markAsDone ? "mark" : "unmark";
        String taskNumberText = arguments;
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
                storage.save(tasks);
            } catch (HomeHubException exception) {
                task.status = previousStatus;
                throw exception;
            }
            System.out.println("  " + task.toDisplayString());
        } catch (NumberFormatException exception) {
            throw new HomeHubException("The task number must be a whole number.");
        }
    }

    private static void deleteTask(TaskList tasks, String arguments, Storage storage)
            throws HomeHubException {
        String taskNumberText = arguments;
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
                storage.save(tasks);
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

    private static void addTodo(TaskList tasks, String description, Storage storage)
            throws HomeHubException {
        if (description.isEmpty()) {
            throw new HomeHubException("A todo description cannot be empty.");
        }
        validateText(description, "A todo description");
        addTask(tasks, new Todo(description), storage);
    }

    private static void addDeadline(TaskList tasks, String content, Storage storage)
            throws HomeHubException {
        int byMarker = content.indexOf(" /by ");
        if (byMarker <= 0 || content.indexOf(" /by ", byMarker + 1) >= 0
                || content.substring(byMarker + 5).trim().isEmpty()) {
            throw new HomeHubException("Use: deadline <description> /by <date or time>.");
        }
        String description = content.substring(0, byMarker).trim();
        String by = content.substring(byMarker + 5).trim();
        validateText(description, "A deadline description");
        validateText(by, "A deadline date or time");
        addTask(tasks, new Deadline(description, by), storage);
    }

    private static void addEvent(TaskList tasks, String content, Storage storage)
            throws HomeHubException {
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
        addTask(tasks, new Event(description, from, to), storage);
    }

    private static void addTask(TaskList tasks, Task task, Storage storage)
            throws HomeHubException {
        tasks.add(task);
        try {
            storage.save(tasks);
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
