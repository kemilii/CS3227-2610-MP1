/** Represents a command understood by HomeHub. */
public enum CommandType {
    BYE("bye"), LIST("list"), MARK("mark"), UNMARK("unmark"), DELETE("delete"),
    TODO("todo"), DEADLINE("deadline"), EVENT("event"), UNKNOWN("");

    private final String keyword;

    CommandType(String keyword) { this.keyword = keyword; }

    /** Returns the command keyword used by the parser. */
    String getKeyword() { return keyword; }

    /** Identifies a command from the first word of user input. */
    public static CommandType fromInput(String input) { return new Parser().parse(input); }
}
