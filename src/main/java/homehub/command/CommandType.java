package homehub.command;

/** Represents a command understood by HomeHub. */
public enum CommandType {
    BYE("bye"), LIST("list"), MARK("mark"), UNMARK("unmark"), DELETE("delete"),
    TODO("todo"), DEADLINE("deadline"), EVENT("event"), FIND("find"), HELP("help"), UNKNOWN("");

    private final String keyword;

    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /** Returns the command keyword used by the parser. */
    String getKeyword() {
        return keyword;
    }

    /** Identifies a command from the first word of user input. */
    public static CommandType fromInput(String input) {
        ParsedCommand parsedCommand = new Parser().parse(input);
        assert parsedCommand != null : "The parser must always return a parsed command";
        assert parsedCommand.type() != null : "A parsed command must always have a command type";
        return parsedCommand.type();
    }
}
