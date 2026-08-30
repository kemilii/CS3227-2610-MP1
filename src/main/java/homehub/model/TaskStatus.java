package homehub.model;

/** Represents the completion state of a household task. */
public enum TaskStatus {
    PENDING(" "), DONE("X");

    private final String icon;

    TaskStatus(String icon) {
        this.icon = icon;
    }

    /** Returns the marker used when displaying this status. */
    public String getIcon() {
        return icon;
    }
}
