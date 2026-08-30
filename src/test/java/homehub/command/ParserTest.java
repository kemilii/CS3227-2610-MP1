package homehub.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** Tests parsing of HomeHub command input into normalized parsed commands. */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parse_supportedCommands_returnsCorrectCommandTypes() {
        assertAll(
                () -> assertEquals(CommandType.BYE, parser.parse("bye").type()),
                () -> assertEquals(CommandType.LIST, parser.parse("list").type()),
                () -> assertEquals(CommandType.MARK, parser.parse("mark 2").type()),
                () -> assertEquals(CommandType.UNMARK, parser.parse("unmark 2").type()),
                () -> assertEquals(CommandType.DELETE, parser.parse("delete 2").type()),
                () -> assertEquals(CommandType.TODO, parser.parse("todo wash dishes").type()),
                () -> assertEquals(CommandType.DEADLINE, parser.parse("deadline pay bill /by 2026-09-01").type()),
                () -> assertEquals(CommandType.EVENT, parser.parse("event meeting /from 2026-09-02 /to 2026-09-03").type())
        );
    }

    @Test
    void parse_commandWithWhitespace_returnsTrimmedArguments() {
        ParsedCommand parsed = parser.parse("  todo   wash   dishes  ");

        assertEquals(CommandType.TODO, parsed.type());
        assertEquals("wash   dishes", parsed.arguments());
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
                () -> assertEquals(new ParsedCommand(CommandType.UNKNOWN, ""), parser.parse("   "))
        );
    }

    @Test
    void parse_unrecognizedCommand_returnsUnknownCommandWithEmptyArguments() {
        assertEquals(new ParsedCommand(CommandType.UNKNOWN, ""), parser.parse("complete task"));
    }
}
