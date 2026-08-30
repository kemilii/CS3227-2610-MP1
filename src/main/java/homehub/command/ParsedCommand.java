package homehub.command;

/** Represents a parsed command and its normalized argument text. */
public record ParsedCommand(CommandType type, String arguments) { }
