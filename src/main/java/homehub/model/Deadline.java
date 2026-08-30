package homehub.model;

import homehub.exception.HomeHubException;

import java.time.LocalDateTime;

/** Represents a task that must be completed before a specified date or time. */
public class Deadline extends Task {
    private final LocalDateTime by;
    private final boolean hasTime;

    /**
     * Creates a deadline task with the supplied description and due date.
     *
     * @param description task description.
     * @param by due date or time in HomeHub's accepted format.
     * @throws HomeHubException if the due date or time is invalid.
     */
    public Deadline(String description, String by) throws HomeHubException {
        super(description);
        this.by = DateTimeParser.parse(by);
        this.hasTime = DateTimeParser.hasTime(by);
    }

    @Override
    public String getTypeIcon() {
        return TaskType.DEADLINE.getIcon();
    }

    @Override
    public String getDateDescription() {
        return " (by: " + DateTimeParser.display(by, hasTime) + ")";
    }

    @Override
    public String toStorageString() {
        return super.toStorageString() + " | " + DateTimeParser.storage(by, hasTime);
    }
}
