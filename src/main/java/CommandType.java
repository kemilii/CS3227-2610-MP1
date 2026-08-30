/** Represents a command understood by HomeHub. */
public enum CommandType {
    BYE("bye"), LIST("list"), MARK("mark"), UNMARK("unmark"), DELETE("delete"),
    TODO("todo"), DEADLINE("deadline"), EVENT("event"), UNKNOWN("");

    private final String keyword;

    CommandType(String keyword) { this.keyword = keyword; }

    /** Identifies a command from the first word of user input. */
    public static CommandType fromInput(String input) {
        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) return UNKNOWN;
        String keyword = trimmedInput.split("\\s+", 2)[0];
        for (CommandType command : values()) {
            if (command.keyword.equals(keyword)) return command;
        }
        return UNKNOWN;
    }
}
