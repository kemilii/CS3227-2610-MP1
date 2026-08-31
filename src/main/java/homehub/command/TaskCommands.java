package homehub.command;

import homehub.exception.HomeHubException;
import homehub.model.Deadline;
import homehub.model.Event;
import homehub.model.Task;
import homehub.model.TaskList;
import homehub.model.Todo;
import homehub.storage.Storage;
import homehub.ui.Ui;

/** Executes commands that create and persist household tasks. */
public class TaskCommands {
    private static final String DEADLINE_MARKER = " /by ";
    private static final String EVENT_FROM_MARKER = " /from ";
    private static final String EVENT_TO_MARKER = " /to ";

    private final Storage storage;
    private final Ui ui;

    /**
     * Creates a task-command handler with the required collaborators.
     *
     * @param storage persistence service for tasks.
     * @param ui user-interface service for confirmations.
     */
    public TaskCommands(Storage storage, Ui ui) {
        assert storage != null : "Task commands require initialized storage";
        assert ui != null : "Task commands require initialized user interface";
        this.storage = storage;
        this.ui = ui;
    }

    /**
     * Adds a todo task.
     *
     * @param tasks task list to update.
     * @param description task description.
     * @throws HomeHubException if the description is invalid or persistence fails.
     */
    public void addTodo(TaskList tasks, String description) throws HomeHubException {
        if (description.isEmpty()) {
            throw new HomeHubException("A todo description cannot be empty.");
        }
        validateText(description, "A todo description");
        addTask(tasks, new Todo(description));
    }

    /**
     * Adds a deadline task.
     *
     * @param tasks task list to update.
     * @param content description and deadline separated by {@code /by}.
     * @throws HomeHubException if the command content is invalid or persistence fails.
     */
    public void addDeadline(TaskList tasks, String content) throws HomeHubException {
        int byMarker = content.indexOf(DEADLINE_MARKER);
        boolean hasDuplicateByMarker = byMarker >= 0
                && content.indexOf(DEADLINE_MARKER, byMarker + DEADLINE_MARKER.length()) >= 0;
        boolean hasMissingDeadline = byMarker < 0
                || content.substring(byMarker + DEADLINE_MARKER.length()).trim().isEmpty();
        if (byMarker <= 0 || hasDuplicateByMarker || hasMissingDeadline) {
            throw new HomeHubException("Use: deadline <description> /by <date or time>.");
        }
        String description = content.substring(0, byMarker).trim();
        String by = content.substring(byMarker + DEADLINE_MARKER.length()).trim();
        validateText(description, "A deadline description");
        validateText(by, "A deadline date or time");
        addTask(tasks, new Deadline(description, by));
    }

    /**
     * Adds an event task.
     *
     * @param tasks task list to update.
     * @param content description and event dates separated by {@code /from} and {@code /to}.
     * @throws HomeHubException if the command content is invalid or persistence fails.
     */
    public void addEvent(TaskList tasks, String content) throws HomeHubException {
        int fromMarker = content.indexOf(EVENT_FROM_MARKER);
        int toMarker = content.indexOf(EVENT_TO_MARKER);
        boolean hasDuplicateFromMarker = fromMarker >= 0
                && content.indexOf(EVENT_FROM_MARKER, fromMarker + EVENT_FROM_MARKER.length()) >= 0;
        boolean hasDuplicateToMarker = toMarker >= 0
                && content.indexOf(EVENT_TO_MARKER, toMarker + EVENT_TO_MARKER.length()) >= 0;
        boolean hasMissingEndTime = toMarker < 0
                || content.substring(toMarker + EVENT_TO_MARKER.length()).trim().isEmpty();
        if (fromMarker <= 0 || hasDuplicateFromMarker || toMarker <= fromMarker
                || hasDuplicateToMarker || hasMissingEndTime) {
            throw new HomeHubException("Use: event <description> /from <start> /to <end>.");
        }
        String description = content.substring(0, fromMarker).trim();
        String from = content.substring(fromMarker + EVENT_FROM_MARKER.length(), toMarker).trim();
        String to = content.substring(toMarker + EVENT_TO_MARKER.length()).trim();
        validateText(description, "An event description");
        validateText(from, "An event start date or time");
        validateText(to, "An event end date or time");
        addTask(tasks, new Event(description, from, to));
    }

    private void addTask(TaskList tasks, Task task) throws HomeHubException {
        assert tasks != null : "Adding a task requires an initialized task list";
        assert task != null : "Adding a task requires a task instance";
        int taskCountBefore = tasks.size();
        tasks.add(task);
        assert tasks.size() == taskCountBefore + 1 : "Adding a task must increase the list by one";
        assert tasks.get(taskCountBefore) == task : "The newly added task must be appended to the list";
        try {
            storage.save(tasks);
        } catch (HomeHubException exception) {
            tasks.remove(tasks.size() - 1);
            assert tasks.size() == taskCountBefore : "A failed save must roll back the added task";
            throw exception;
        }
        ui.showAddedTask(task, tasks.size());
    }

    private void validateText(String value, String fieldName) throws HomeHubException {
        if (value.indexOf('|') >= 0) {
            throw new HomeHubException(fieldName + " cannot contain the '|' character.");
        }
    }
}
