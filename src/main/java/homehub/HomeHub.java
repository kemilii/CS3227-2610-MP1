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

/** A household task manager that supports both command-line and graphical interfaces. */
public class HomeHub {
    private static final String DEFAULT_STORAGE_PATH = "data/homehub.txt";

    private final Parser parser;
    private final Storage storage;
    private TaskList tasks;
    private CommandType commandType;
    private boolean exitRequested;
    private String startupError;

    /** Creates a HomeHub instance backed by the default task file. */
    public HomeHub() {
        this(new Storage(DEFAULT_STORAGE_PATH), null);
    }

    /**
     * Creates a HomeHub instance backed by the supplied storage service.
     *
     * @param storage persistence service for household tasks.
     */
    public HomeHub(Storage storage) {
        this(storage, null);
    }

    /**
     * Creates a HomeHub instance and reports storage errors through the supplied UI.
     *
     * @param storage persistence service for household tasks.
     * @param startupUi UI used to report an error while loading saved tasks.
     */
    public HomeHub(Storage storage, Ui startupUi) {
        assert storage != null : "HomeHub requires initialized storage";
        this.parser = new Parser();
        this.storage = storage;
        this.exitRequested = false;
        try {
            this.tasks = new TaskList(storage.load());
        } catch (HomeHubException exception) {
            this.tasks = new TaskList();
            if (startupUi == null) {
                this.startupError = exception.getMessage();
            } else {
                startupUi.showError(exception.getMessage());
            }
        }
    }

    /**
     * Processes a command entered in the graphical interface.
     *
     * @param input user's message.
     * @return HomeHub's user-facing response.
     */
    public String getResponse(String input) {
        ResponseUi responseUi = new ResponseUi();
        if (startupError != null) {
            responseUi.showError(startupError);
            startupError = null;
        }
        if (input == null) {
            commandType = CommandType.UNKNOWN;
            responseUi.showError(Moss.NAME + " does not recognise that command yet. Try todo, deadline, event, list, "
                    + "find, mark, "
                    + "unmark, or delete.");
            return responseUi.getResponse();
        }
        try {
            executeCommand(input, responseUi);
        } catch (HomeHubException exception) {
            responseUi.showError(exception.getMessage());
        } catch (RuntimeException exception) {
            responseUi.showError(Moss.NAME + " could not process that input.");
        }
        return responseUi.getResponse();
    }

    /** Returns the command type from the most recent graphical-interface input. */
    public CommandType getCommandType() {
        return commandType;
    }

    /** Returns whether the most recent command requested that HomeHub exit. */
    public boolean isExitRequested() {
        return exitRequested;
    }

    /**
     * Runs the HomeHub command-line application.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        HomeHub homeHub = new HomeHub(new Storage(DEFAULT_STORAGE_PATH), ui);

        ui.showWelcome();

        String command;
        while ((command = ui.readCommand()) != null) {
            ui.showSeparator();

            try {
                homeHub.executeCommand(command, ui);
                if (homeHub.isExitRequested()) {
                    break;
                }
            } catch (HomeHubException exception) {
                ui.showError(exception.getMessage());
            } catch (RuntimeException exception) {
                ui.showError(Moss.NAME + " could not process that input.");
            }
            ui.showSeparator();
        }
    }

    private void executeCommand(String input, Ui ui) throws HomeHubException {
        assert input != null : "A command input must not be null";
        assert ui != null : "Command execution requires initialized user interface";
        ParsedCommand parsedCommand = parser.parse(input);
        assert parsedCommand != null : "The parser must return a command for every input";
        assert parsedCommand.type() != null : "A parsed command must have a command type";
        assert parsedCommand.arguments() != null : "A parsed command must have normalized arguments";
        commandType = parsedCommand.type();
        TaskCommands taskCommands = new TaskCommands(storage, ui);

        switch (parsedCommand.type()) {
            case BYE:
                exitRequested = true;
                ui.showGoodbye();
                break;
            case LIST:
                ui.showTaskList(tasks);
                break;
            case HELP:
                ui.showHelp();
                break;
            case FIND:
                if (parsedCommand.arguments().isEmpty()) {
                    throw new HomeHubException("Please provide a keyword after find.");
                }
                ui.showMatchingTasks(tasks.findMatchingTasks(parsedCommand.arguments()));
                break;
            case MARK:
                markTask(parsedCommand.arguments(), true, ui);
                break;
            case UNMARK:
                markTask(parsedCommand.arguments(), false, ui);
                break;
            case DELETE:
                deleteTask(parsedCommand.arguments(), ui);
                break;
            case TODO:
                taskCommands.addTodo(tasks, parsedCommand.arguments());
                break;
            case DEADLINE:
                taskCommands.addDeadline(tasks, parsedCommand.arguments());
                break;
            case EVENT:
                taskCommands.addEvent(tasks, parsedCommand.arguments());
                break;
            default:
                throw new HomeHubException(Moss.NAME + " does not recognise that command yet. Try todo, deadline, "
                        + "event, list, "
                        + "find, mark, unmark, or delete.");
        }
    }

    private void markTask(String arguments, boolean markAsDone, Ui ui) throws HomeHubException {
        assert tasks != null : "Task operations require an initialized task list";
        assert arguments != null : "Parsed task commands must provide argument text";
        assert storage != null : "Task operations require initialized storage";
        assert ui != null : "Task operations require initialized user interface";
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
            assert task != null : "A task list must not contain null tasks";
            TaskStatus previousStatus = task.getStatus();
            if (markAsDone) {
                task.markAsDone();
            } else {
                task.markAsNotDone();
            }
            assert task.getStatus() == (markAsDone ? TaskStatus.DONE : TaskStatus.PENDING)
                    : "Marking a task must update its status to the requested state";
            try {
                storage.save(tasks);
            } catch (HomeHubException exception) {
                task.setStatus(previousStatus);
                throw exception;
            }
            assert tasks.get(taskNumber - 1) == task : "Saving a task must not replace it in memory";
            ui.showMarkedTask(task, markAsDone);
        } catch (NumberFormatException exception) {
            throw new HomeHubException("The task number must be a whole number.");
        }
    }

    private void deleteTask(String arguments, Ui ui) throws HomeHubException {
        assert tasks != null : "Task operations require an initialized task list";
        assert arguments != null : "Parsed task commands must provide argument text";
        assert storage != null : "Task operations require initialized storage";
        assert ui != null : "Task operations require initialized user interface";
        String taskNumberText = arguments;
        if (taskNumberText.isEmpty()) {
            throw new HomeHubException("Please provide a task number after delete.");
        }
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new HomeHubException("That task number does not exist.");
            }
            int taskCountBefore = tasks.size();
            Task removedTask = tasks.remove(taskNumber - 1);
            assert removedTask != null : "A task list must not contain null tasks";
            assert tasks.size() == taskCountBefore - 1 : "Deleting a task must reduce the list by one";
            try {
                storage.save(tasks);
            } catch (HomeHubException exception) {
                tasks.add(taskNumber - 1, removedTask);
                assert tasks.size() == taskCountBefore : "A failed deletion must restore the original list size";
                assert tasks.get(taskNumber - 1) == removedTask
                        : "A failed deletion must restore the removed task at its original index";
                throw exception;
            }
            ui.showDeletedTask(removedTask, tasks.size());
        } catch (NumberFormatException exception) {
            throw new HomeHubException("The task number must be a whole number.");
        }
    }

    /** Captures command output for display in a GUI response bubble. */
    private static final class ResponseUi extends Ui {
        private final StringBuilder response = new StringBuilder();

        @Override
        public void showGoodbye() {
            printLine("All tucked away. See you soon!");
        }

        @Override
        protected void printLine(String line) {
            if (response.length() > 0) {
                response.append(System.lineSeparator());
            }
            response.append(line);
        }

        /** Returns the captured response without a trailing line separator. */
        String getResponse() {
            return response.toString();
        }
    }
}
