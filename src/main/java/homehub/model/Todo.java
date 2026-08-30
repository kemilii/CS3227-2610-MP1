package homehub.model;

import homehub.exception.HomeHubException;

/** Represents a task without an attached date or time. */
public class Todo extends Task {
    public Todo(String description) {
        super(description);
    }
}
