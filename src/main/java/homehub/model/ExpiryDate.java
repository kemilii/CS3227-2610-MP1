package homehub.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import homehub.exception.HomeHubException;

/** Parses and formats the calendar dates used by pantry inventory entries. */
public final class ExpiryDate {
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);

    private ExpiryDate() {
        // Utility class.
    }

    /**
     * Parses a strict ISO calendar date.
     *
     * @param value date in {@code yyyy-MM-dd} format.
     * @return the parsed date.
     * @throws HomeHubException if the value is not a valid calendar date.
     */
    public static LocalDate parse(String value) throws HomeHubException {
        if (value == null || value.trim().isEmpty()) {
            throw new HomeHubException("Expiry dates must use yyyy-MM-dd format.");
        }
        try {
            return LocalDate.parse(value.trim(), INPUT_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new HomeHubException("Expiry dates must use yyyy-MM-dd format.");
        }
    }

    /** Returns a friendly date for display in the pantry view. */
    public static String display(LocalDate date) {
        assert date != null : "A pantry item must have an expiry date";
        return date.format(DISPLAY_FORMAT);
    }

    /** Returns the stable date representation used by the storage file. */
    public static String storage(LocalDate date) {
        assert date != null : "A pantry item must have an expiry date";
        return date.format(INPUT_FORMAT);
    }
}
