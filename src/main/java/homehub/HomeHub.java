package homehub;

import homehub.command.CommandType;
import homehub.command.ParsedCommand;
import homehub.command.Parser;
import homehub.command.TaskCommands;
import homehub.exception.HomeHubException;
import homehub.model.Task;
import homehub.model.TaskList;
import homehub.model.TaskStatus;
import homehub.storage.Storage;
import homehub.ui.Ui;

/**
 * A simple command-line household task manager that accepts commands until the
 * user says bye.
 */
public class HomeHub {
    /**
     * Runs the HomeHub command-line application.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Parser parser = new Parser();
        Storage storage = new Storage("data/homehub.txt");
        TaskCommands taskCommands = new TaskCommands(storage, ui);
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
                } else if (commandType == CommandType.FIND) {
                    if (parsedCommand.arguments().isEmpty()) {
                        throw new HomeHubException("Please provide a keyword after find.");
                    }
                    ui.showMatchingTasks(tasks.findMatchingTasks(parsedCommand.arguments()));
                } else if (commandType == CommandType.MARK) {
                    markTask(tasks, parsedCommand.arguments(), true, storage);
                } else if (commandType == CommandType.UNMARK) {
                    markTask(tasks, parsedCommand.arguments(), false, storage);
                } else if (commandType == CommandType.DELETE) {
                    deleteTask(tasks, parsedCommand.arguments(), storage);
                } else if (commandType == CommandType.TODO) {
                    taskCommands.addTodo(tasks, parsedCommand.arguments());
                } else if (commandType == CommandType.DEADLINE) {
                    taskCommands.addDeadline(tasks, parsedCommand.arguments());
                } else if (commandType == CommandType.EVENT) {
                    taskCommands.addEvent(tasks, parsedCommand.arguments());
                } else {
                    throw new HomeHubException("I don't recognise that command. Try todo, deadline, event, list, "
                            + "find, mark, unmark, or delete.");
                }
            } catch (HomeHubException exception) {
                ui.showError(exception.getMessage());
            } catch (RuntimeException exception) {
                ui.showError("HomeHub could not process that input.");
            }
            ui.showSeparator();
        }
    }

    private static void markTask(TaskList tasks, String arguments,
            boolean markAsDone, Storage storage)
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
            TaskStatus previousStatus = task.getStatus();
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
                task.setStatus(previousStatus);
                throw exception;
            }
            System.out.println("  " + task.toDisplayString());
        } catch (NumberFormatException exception) {
            throw new HomeHubException("The task number must be a whole number.");
        }
    }

    private static void deleteTask(TaskList tasks, String arguments,
            Storage storage) throws HomeHubException {
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

}
