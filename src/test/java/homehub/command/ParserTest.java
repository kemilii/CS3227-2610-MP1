package homehub.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** Tests parsing of HomeHub command input into normalized parsed commands. */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parse_supportedCommands_returnsCorrectParsedCommands() {
        assertAll(
                () -> assertEquals(new ParsedCommand(CommandType.BYE, ""), parser.parse("bye")),
                () -> assertEquals(new ParsedCommand(CommandType.LIST, ""), parser.parse("list")),
                () -> assertEquals(new ParsedCommand(CommandType.MARK, "2"), parser.parse("mark 2")),
                () -> assertEquals(new ParsedCommand(CommandType.UNMARK, "2"), parser.parse("unmark 2")),
                () -> assertEquals(new ParsedCommand(CommandType.DELETE, "2"), parser.parse("delete 2")),
                () -> assertEquals(new ParsedCommand(CommandType.TODO, "wash dishes"),
                        parser.parse("todo wash dishes")),
                () -> assertEquals(new ParsedCommand(CommandType.DEADLINE, "pay bill /by 2026-09-01"),
                        parser.parse("deadline pay bill /by 2026-09-01")),
                () -> assertEquals(new ParsedCommand(CommandType.EVENT,
                                "meeting /from 2026-09-02 /to 2026-09-03"),
                        parser.parse("event meeting /from 2026-09-02 /to 2026-09-03"))
        );
    }

    @Test
    void parse_commandWithWhitespace_returnsTrimmedArguments() {
        ParsedCommand parsed = parser.parse("  todo   wash   dishes  ");

        assertEquals(CommandType.TODO, parsed.type());
        assertEquals("wash   dishes", parsed.arguments());
    }

    @Test
    void parse_commandWithTabsAndNewlines_returnsNormalizedArguments() {
        ParsedCommand parsed = parser.parse("\tdeadline\tpay bill /by 2026-09-01\n");

        assertEquals(new ParsedCommand(CommandType.DEADLINE, "pay bill /by 2026-09-01"), parsed);
    }

    @Test
    void parse_commandWithoutArguments_returnsEmptyArguments() {
        assertAll(
                () -> assertEquals("", parser.parse("bye").arguments()),
                () -> assertEquals("", parser.parse(" list ").arguments())
        );
    }

    @Test
    void parse_blankInput_returnsUnknownCommandWithEmptyArguments() {
        assertAll(
                () -> assertEquals(new ParsedCommand(CommandType.UNKNOWN, ""), parser.parse("")),
                () -> assertEquals(new ParsedCommand(CommandType.UNKNOWN, ""), parser.parse("   ")),
                () -> assertEquals(new ParsedCommand(CommandType.UNKNOWN, ""), parser.parse("\t\n"))
        );
    }

    @Test
    void parse_unrecognizedCommand_returnsUnknownCommandWithEmptyArguments() {
        assertAll(
                () -> assertEquals(new ParsedCommand(CommandType.UNKNOWN, ""), parser.parse("complete task")),
                () -> assertEquals(new ParsedCommand(CommandType.UNKNOWN, ""), parser.parse("TODO task"))
        );
    }
}
