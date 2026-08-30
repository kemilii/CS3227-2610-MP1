package homehub.model;

import homehub.exception.HomeHubException;

import java.time.LocalDateTime;

/** Represents a task with a starting and ending date or time. */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;
    private final boolean fromHasTime;
    private final boolean toHasTime;

    public Event(String description, String from, String to) throws HomeHubException {
        super(description);
        this.from = DateTimeParser.parse(from);
        this.to = DateTimeParser.parse(to);
        this.fromHasTime = DateTimeParser.hasTime(from);
        this.toHasTime = DateTimeParser.hasTime(to);
    }

    @Override
    public String getTypeIcon() { return TaskType.EVENT.getIcon(); }

    @Override
    public String getDateDescription() {
        return " (from: " + DateTimeParser.display(from, fromHasTime) + " to: "
                + DateTimeParser.display(to, toHasTime) + ")";
    }

    @Override
    public String toStorageString() {
        return super.toStorageString() + " | " + DateTimeParser.storage(from, fromHasTime)
                + " | " + DateTimeParser.storage(to, toHasTime);
    }
}
