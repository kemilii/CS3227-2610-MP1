package homehub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import homehub.command.CommandType;
import homehub.storage.Storage;

/** Tests command execution and responses shared by the CLI and JavaFX interfaces. */
class HomeHubTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_taskLifecycleCommands_updatesStateAndReturnsConfirmations() {
        HomeHub homeHub = homeHubAt("data/homehub.txt");

        assertEquals(String.join(System.lineSeparator(),
                "On it. I've added this task:",
                "  [T][ ] wash dishes",
                "That makes 1 tasks on the board."), homeHub.getResponse("todo wash dishes"));
        assertEquals(String.join(System.lineSeparator(),
                "Moss's household board:",
                "1.[T][ ] wash dishes"), homeHub.getResponse("list"));
        assertEquals(String.join(System.lineSeparator(),
                "Done and dusted. This task is complete:",
                "  [T][X] wash dishes"), homeHub.getResponse("mark 1"));
        assertEquals(String.join(System.lineSeparator(),
                "Back on the board. This task is pending:",
                "  [T][ ] wash dishes"), homeHub.getResponse("unmark 1"));
        assertEquals(String.join(System.lineSeparator(),
                "Cleared from the board:",
                "  [T][ ] wash dishes",
                "That leaves 0 tasks to keep tidy."), homeHub.getResponse("delete 1"));
        assertEquals("Moss's household board:", homeHub.getResponse("list"));
    }

    @Test
    void getResponse_deadlineAndEventCommands_addTypedTasks() {
        HomeHub homeHub = homeHubAt("data/homehub.txt");

        homeHub.getResponse("deadline pay bill /by 2026-09-01");
        homeHub.getResponse("event meeting /from 2026-09-02 14:00 /to 2026-09-02 16:00");

        assertEquals(String.join(System.lineSeparator(),
                "Moss's household board:",
                "1.[D][ ] pay bill (by: Sept 01 2026)",
                "2.[E][ ] meeting (from: Sept 02 2026 14:00 to: Sept 02 2026 16:00)"),
                homeHub.getResponse("list"));
    }

    @Test
    void getResponse_findCommand_returnsOnlyMatchingTasks() {
        HomeHub homeHub = homeHubAt("data/homehub.txt");
        homeHub.getResponse("todo wash dishes");
        homeHub.getResponse("todo read book");

        assertEquals(String.join(System.lineSeparator(),
                "Moss found these matching tasks:",
                "1.[T][ ] wash dishes"), homeHub.getResponse("find DISH"));
    }

    @Test
    void getResponse_helpCommand_listsCommandsAndDateTimeFormats() {
        HomeHub homeHub = homeHubAt("data/homehub.txt");

        assertEquals(String.join(System.lineSeparator(),
                "Moss's command guide:",
                "todo <description> - add a household task.",
                "deadline <description> /by <date or time> - add a task with a deadline.",
                "event <description> /from <start> /to <end> - add a scheduled event.",
                "list - show every task on the household board.",
                "find <keyword> - find tasks by description.",
                "mark <task number> - mark a task as done.",
                "unmark <task number> - mark a task as pending.",
                "delete <task number> - remove a task from the board.",
                "help - show this command guide.",
                "bye - close HomeHub.",
                "Date/time format: yyyy-MM-dd or yyyy-MM-dd HH:mm.",
                "Examples: 2026-09-01 or 2026-09-01 14:30."), homeHub.getResponse("help"));
        assertEquals(CommandType.HELP, homeHub.getCommandType());
    }

    @Test
    void getResponse_invalidCommands_returnErrorsWithoutChangingState() {
        HomeHub homeHub = homeHubAt("data/homehub.txt");

        assertTrue(homeHub.getResponse("unknown command").startsWith("Moss says: Moss does not recognise "));
        assertTrue(homeHub.getResponse("find").contains("Please provide a keyword after find."));
        assertTrue(homeHub.getResponse("mark 1").contains("That task number does not exist."));
        assertEquals("Moss's household board:", homeHub.getResponse("list"));
    }

    @Test
    void getResponse_extraArgumentsAndNullInput_returnClearErrors() {
        HomeHub homeHub = homeHubAt("data/homehub.txt");

        assertTrue(homeHub.getResponse("list now").contains("The list command does not take arguments."));
        assertTrue(homeHub.getResponse("help now").contains("The help command does not take arguments."));
        assertTrue(homeHub.getResponse("bye now").contains("The bye command does not take arguments."));
        assertTrue(homeHub.getResponse(null).contains("Moss does not recognise that command yet."));
        assertFalse(homeHub.isExitRequested());
    }

    @Test
    void getResponse_duplicateAndUnorderedTasks_rejectWithoutChangingState() {
        HomeHub homeHub = homeHubAt("data/homehub.txt");

        homeHub.getResponse("todo wash dishes");
        assertTrue(homeHub.getResponse("todo wash dishes").contains("already on the board"));
        assertTrue(homeHub.getResponse("event meeting /from 2026-09-02 /to 2026-09-01")
                .contains("must end after it starts"));
        assertTrue(homeHub.getResponse("event meeting /from 2026-09-02 /to 2026-09-02")
                .contains("must end after it starts"));
        assertEquals(String.join(System.lineSeparator(),
                "Moss's household board:",
                "1.[T][ ] wash dishes"), homeHub.getResponse("list"));
    }

    @Test
    void getResponse_byeCommand_requestsExit() {
        HomeHub homeHub = homeHubAt("data/homehub.txt");

        assertEquals("All tucked away. See you soon!", homeHub.getResponse("bye"));
        assertTrue(homeHub.isExitRequested());
    }

    @Test
    void getResponse_persistsTasksForAReopenedHomeHub() {
        Storage storage = storageAt("data/homehub.txt");
        HomeHub firstSession = new HomeHub(storage);
        firstSession.getResponse("todo clean room");

        HomeHub reopenedSession = new HomeHub(storage);

        assertEquals(String.join(System.lineSeparator(),
                "Moss's household board:",
                "1.[T][ ] clean room"), reopenedSession.getResponse("list"));
    }

    private HomeHub homeHubAt(String relativePath) {
        return new HomeHub(storageAt(relativePath));
    }

    private Storage storageAt(String relativePath) {
        return new Storage(temporaryDirectory.resolve(relativePath).toString());
    }
}
