package homehub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import homehub.storage.Storage;

/** Tests command execution and responses shared by the CLI and JavaFX interfaces. */
class HomeHubTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_taskLifecycleCommands_updatesStateAndReturnsConfirmations() {
        HomeHub homeHub = homeHubAt("data/homehub.txt");

        assertEquals(String.join(System.lineSeparator(),
                "Got it. I've added this task:",
                "  [T][ ] wash dishes",
                "Now you have 1 tasks in the list."), homeHub.getResponse("todo wash dishes"));
        assertEquals(String.join(System.lineSeparator(),
                "Here are the household tasks in your HomeHub:",
                "1.[T][ ] wash dishes"), homeHub.getResponse("list"));
        assertEquals(String.join(System.lineSeparator(),
                "Nice! I've marked this household task as done:",
                "  [T][X] wash dishes"), homeHub.getResponse("mark 1"));
        assertEquals(String.join(System.lineSeparator(),
                "I've marked this household task as not done:",
                "  [T][ ] wash dishes"), homeHub.getResponse("unmark 1"));
        assertEquals(String.join(System.lineSeparator(),
                "Noted. I've removed this task:",
                "  [T][ ] wash dishes",
                "Now you have 0 tasks in the list."), homeHub.getResponse("delete 1"));
        assertEquals("Here are the household tasks in your HomeHub:", homeHub.getResponse("list"));
    }

    @Test
    void getResponse_deadlineAndEventCommands_addTypedTasks() {
        HomeHub homeHub = homeHubAt("data/homehub.txt");

        homeHub.getResponse("deadline pay bill /by 2026-09-01");
        homeHub.getResponse("event meeting /from 2026-09-02 14:00 /to 2026-09-02 16:00");

        assertEquals(String.join(System.lineSeparator(),
                "Here are the household tasks in your HomeHub:",
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
                "Here are the matching tasks in your list:",
                "1.[T][ ] wash dishes"), homeHub.getResponse("find DISH"));
    }

    @Test
    void getResponse_invalidCommands_returnErrorsWithoutChangingState() {
        HomeHub homeHub = homeHubAt("data/homehub.txt");

        assertTrue(homeHub.getResponse("unknown command").startsWith("Oops! I don't recognise that command."));
        assertTrue(homeHub.getResponse("find").contains("Please provide a keyword after find."));
        assertTrue(homeHub.getResponse("mark 1").contains("That task number does not exist."));
        assertEquals("Here are the household tasks in your HomeHub:", homeHub.getResponse("list"));
    }

    @Test
    void getResponse_byeCommand_requestsExit() {
        HomeHub homeHub = homeHubAt("data/homehub.txt");

        assertEquals("Bye. Hope to see you again soon!", homeHub.getResponse("bye"));
        assertTrue(homeHub.isExitRequested());
    }

    @Test
    void getResponse_persistsTasksForAReopenedHomeHub() {
        Storage storage = storageAt("data/homehub.txt");
        HomeHub firstSession = new HomeHub(storage);
        firstSession.getResponse("todo clean room");

        HomeHub reopenedSession = new HomeHub(storage);

        assertEquals(String.join(System.lineSeparator(),
                "Here are the household tasks in your HomeHub:",
                "1.[T][ ] clean room"), reopenedSession.getResponse("list"));
    }

    private HomeHub homeHubAt(String relativePath) {
        return new HomeHub(storageAt(relativePath));
    }

    private Storage storageAt(String relativePath) {
        return new Storage(temporaryDirectory.resolve(relativePath).toString());
    }
}
