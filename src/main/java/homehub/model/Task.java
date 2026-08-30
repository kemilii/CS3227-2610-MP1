package homehub.model;
import homehub.exception.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Represents a household task stored by HomeHub.
 */
public class Task {
    protected String description;
    protected TaskStatus status;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the text describing the task
     */
    public Task(String description) {
        this.description = description;
        this.status = TaskStatus.PENDING;
    }

    /**
     * Returns the marker used to display this task's completion status.
     *
     * @return {@code X} for a completed task, or a space otherwise
     */
    public String getStatusIcon() {
        return status.getIcon();
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        status = TaskStatus.DONE;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        status = TaskStatus.PENDING;
    }

    public TaskStatus getStatus() { return status; }

    public void setStatus(TaskStatus status) { this.status = status; }

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
        return TaskType.TODO.getIcon();
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

    /** Returns this task in the format used by the local save file. */
    public String toStorageString() {
        return getTypeIcon() + " | " + (status == TaskStatus.DONE ? "1" : "0")
                + " | " + description;
    }
}

/** Parses and formats date/time values accepted by HomeHub. */
final class DateTimeParser {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private static final DateTimeFormatter DISPLAY_DATE_TIME = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm");

    private DateTimeParser() { }

    static LocalDateTime parse(String value) throws HomeHubException {
        try {
            String trimmed = value.trim();
            if (trimmed.length() > 10) return LocalDateTime.parse(trimmed, DATE_TIME);
            return java.time.LocalDate.parse(trimmed, DATE).atStartOfDay();
        } catch (DateTimeParseException exception) {
            throw new HomeHubException("Dates must use yyyy-MM-dd or yyyy-MM-dd HH:mm format.");
        }
    }

    static boolean hasTime(String value) { return value.trim().length() > 10; }

    static String display(LocalDateTime value, boolean includeTime) {
        return value.format(includeTime ? DISPLAY_DATE_TIME : DISPLAY_DATE);
    }

    static String storage(LocalDateTime value, boolean includeTime) {
        return value.format(includeTime ? DATE_TIME : DATE);
    }
}

