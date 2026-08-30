/**
 * Represents a household task stored by HomeHub.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the text describing the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the marker used to display this task's completion status.
     *
     * @return {@code X} for a completed task, or a space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the type marker used when displaying this task.
     *
     * @return the task type marker
     */
    public String getTypeIcon() {
        return "T";
    }

    /**
     * Returns the date/time suffix used when displaying this task.
     *
     * @return an empty string for a todo task
     */
    public String getDateDescription() {
        return "";
    }

    /**
     * Returns this task in the format used by HomeHub.
     *
     * @return the formatted task
     */
    public String toDisplayString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description
                + getDateDescription();
    }
}

/** Represents a task without an attached date or time. */
class Todo extends Task {
    Todo(String description) {
        super(description);
    }
}

/** Represents a task that must be completed before a specified date or time. */
class Deadline extends Task {
    private final String by;

    Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    public String getTypeIcon() {
        return "D";
    }

    @Override
    public String getDateDescription() {
        return " (by: " + by + ")";
    }
}

/** Represents a task with a starting and ending date or time. */
class Event extends Task {
    private final String from;
    private final String to;

    Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String getTypeIcon() {
        return "E";
    }

    @Override
    public String getDateDescription() {
        return " (from: " + from + " to: " + to + ")";
    }
}
