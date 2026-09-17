package homehub.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests parsing of pantry inventory commands. */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parse_supportedCommands_returnsCommandAndArguments() {
        assertEquals(new ParsedCommand(CommandType.BYE, ""), parser.parse("bye"));
        assertEquals(new ParsedCommand(CommandType.ADD, "rice /qty 2 /unit kg /expires 2026-09-30"),
                parser.parse("add rice /qty 2 /unit kg /expires 2026-09-30"));
        assertEquals(new ParsedCommand(CommandType.SEARCH, "milk"), parser.parse("search milk"));
        assertEquals(new ParsedCommand(CommandType.RESTOCK, "2 3"), parser.parse("restock 2 3"));
        assertEquals(new ParsedCommand(CommandType.CONSUME, "1 1"), parser.parse("consume 1 1"));
        assertEquals(new ParsedCommand(CommandType.EXPIRING, "2026-10-01"), parser.parse("expiring 2026-10-01"));
        assertEquals(new ParsedCommand(CommandType.LOW_STOCK, ""), parser.parse("lowstock"));
        assertEquals(new ParsedCommand(CommandType.SUMMARY, ""), parser.parse("summary"));
        assertEquals(new ParsedCommand(CommandType.MOVE, "1 freezer"), parser.parse("move 1 freezer"));
        assertEquals(new ParsedCommand(CommandType.DELETE, "2"), parser.parse("delete 2"));
        assertEquals(new ParsedCommand(CommandType.HELP, ""), parser.parse("help"));
    }

    @Test
    void parse_whitespaceAndUnknownInput_returnsNormalizedResult() {
        assertEquals(new ParsedCommand(CommandType.ADD, "rice /qty 2 /unit kg /expires 2026-09-30"),
                parser.parse("  add   rice /qty 2 /unit kg /expires 2026-09-30  "));
        assertEquals(new ParsedCommand(CommandType.UNKNOWN, ""), parser.parse("mark 1"));
        assertEquals(new ParsedCommand(CommandType.UNKNOWN, ""), parser.parse(null));
        assertEquals(CommandType.ADD, CommandType.fromInput("add rice /qty 1 /unit bag /expires 2026-09-30"));
    }
}
