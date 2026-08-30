/** Interprets raw user input as a supported HomeHub command. */
public class Parser {
    /** Identifies the command represented by the supplied input. */
    public CommandType parse(String input) {
        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            return CommandType.UNKNOWN;
        }
        String keyword = trimmedInput.split("\\s+", 2)[0];
        for (CommandType command : CommandType.values()) {
            if (command != CommandType.UNKNOWN && command.getKeyword().equals(keyword)) {
                return command;
            }
        }
        return CommandType.UNKNOWN;
    }
}
