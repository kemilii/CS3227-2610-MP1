/** Executes commands that create and persist household tasks. */
public class TaskCommands {
    private final Storage storage;
    private final Ui ui;

    /** Creates a task-command handler with the required collaborators. */
    public TaskCommands(Storage storage, Ui ui) {
        this.storage = storage;
        this.ui = ui;
    }

    /** Adds a todo task. */
    public void addTodo(TaskList tasks, String description) throws HomeHubException {
        if (description.isEmpty()) throw new HomeHubException("A todo description cannot be empty.");
        validateText(description, "A todo description");
        addTask(tasks, new Todo(description));
    }

    /** Adds a deadline task. */
    public void addDeadline(TaskList tasks, String content) throws HomeHubException {
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

    /** Adds an event task. */
    public void addEvent(TaskList tasks, String content) throws HomeHubException {
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

    private void addTask(TaskList tasks, Task task) throws HomeHubException {
        tasks.add(task);
        try {
            storage.save(tasks);
        } catch (HomeHubException exception) {
            tasks.remove(tasks.size() - 1);
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
