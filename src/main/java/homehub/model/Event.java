package homehub.model;

import java.time.LocalDateTime;

import homehub.exception.HomeHubException;

/** Represents a task with a starting and ending date or time. */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;
    private final boolean fromHasTime;
    private final boolean toHasTime;

    /**
     * Creates an event task with the supplied start and end dates.
     *
     * @param description task description.
     * @param from event start date or time in HomeHub's accepted format.
     * @param to event end date or time in HomeHub's accepted format.
     * @throws HomeHubException if either date or time is invalid.
     */
    public Event(String description, String from, String to) throws HomeHubException {
        super(description);
        LocalDateTime parsedFrom = DateTimeParser.parse(from);
        LocalDateTime parsedTo = DateTimeParser.parse(to);
        if (!parsedTo.isAfter(parsedFrom)) {
            throw new HomeHubException("An event must end after it starts.");
        }
        this.from = parsedFrom;
        this.to = parsedTo;
        this.fromHasTime = DateTimeParser.hasTime(from);
        this.toHasTime = DateTimeParser.hasTime(to);
    }

    @Override
    public String getTypeIcon() {
        return TaskType.EVENT.getIcon();
    }

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
