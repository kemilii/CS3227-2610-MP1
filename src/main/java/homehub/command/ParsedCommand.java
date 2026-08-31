package homehub.command;

/** Represents a parsed command and its normalized argument text. */
public record ParsedCommand(CommandType type, String arguments) {
    /** Ensures every parsed command contains the data required by command handlers. */
    public ParsedCommand {
        assert type != null : "A parsed command must have a command type";
        assert arguments != null : "A parsed command must have normalized arguments";
    }
}
