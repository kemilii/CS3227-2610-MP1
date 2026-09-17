package homehub.command;

/** Represents a command understood by HomeHub's pantry inventory assistant. */
public enum CommandType {
    BYE("bye"), LIST("list"), ADD("add"), SEARCH("search"), RESTOCK("restock"), CONSUME("consume"),
    EXPIRING("expiring"), LOW_STOCK("lowstock"), SUMMARY("summary"), MOVE("move"), DELETE("delete"),
    HELP("help"), UNKNOWN("");

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
