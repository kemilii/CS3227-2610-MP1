package homehub.command;

/** Interprets raw user input as a supported HomeHub command. */
public class Parser {
    /**
     * Parses the command keyword and the text following it.
     *
     * @param input raw command-line input.
     * @return the recognized command and its trimmed arguments.
     */
    public ParsedCommand parse(String input) {
        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            return new ParsedCommand(CommandType.UNKNOWN, "");
        }
        String keyword = trimmedInput.split("\\s+", 2)[0];
        for (CommandType command : CommandType.values()) {
            if (command != CommandType.UNKNOWN
                    && command.getKeyword().equals(keyword)) {
                return new ParsedCommand(command, trimmedInput.substring(keyword.length()).trim());
            }
        }
        return new ParsedCommand(CommandType.UNKNOWN, "");
    }
}
