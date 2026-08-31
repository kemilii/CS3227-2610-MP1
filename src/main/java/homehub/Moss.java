package homehub;

/** Defines the name and tone markers for HomeHub's household concierge. */
public final class Moss {
    /** The assistant's display name. */
    public static final String NAME = "Moss";

    /** The assistant's short role description. */
    public static final String ROLE = "your calm household concierge";

    /** Prefix used when the assistant explains that it rejected an input. */
    public static final String ERROR_PREFIX = NAME + " says: ";

    private Moss() {
        // Utility class.
    }
}
