package homehub.exception;
/**
 * Represents an error caused by invalid input to HomeHub.
 */
public class HomeHubException extends Exception {
    /**
     * Creates an exception with the supplied user-facing message.
     *
     * @param message explanation of how the input was invalid
     */
    public HomeHubException(String message) {
        super(message);
    }
}
