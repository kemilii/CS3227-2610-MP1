package homehub.model;

/** Represents a task without an attached date or time. */
public class Todo extends Task {
    /**
     * Creates a todo task with the supplied description.
     *
     * @param description task description.
     */
    public Todo(String description) {
        super(description);
    }
}
