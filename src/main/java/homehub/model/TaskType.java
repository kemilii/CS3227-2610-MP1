package homehub.model;
/** Represents the supported categories of household tasks. */
public enum TaskType {
    TODO("T"), DEADLINE("D"), EVENT("E");

    private final String icon;

    TaskType(String icon) { this.icon = icon; }

    /** Returns the marker used when displaying this task type. */
    public String getIcon() { return icon; }
}
