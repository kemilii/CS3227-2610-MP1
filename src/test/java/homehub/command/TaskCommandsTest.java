package homehub.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import homehub.exception.HomeHubException;
import homehub.model.TaskList;
import homehub.storage.Storage;
import homehub.ui.Ui;

/** Tests validation, creation, and persistence of tasks from user commands. */
class TaskCommandsTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void addTodo_validDescription_addsAndPersistsTask() throws Exception {
        Storage storage = storageAt("data/homehub.txt");
        TaskList tasks = new TaskList();
        TaskCommands commands = new TaskCommands(storage, new Ui());

        commands.addTodo(tasks, "wash dishes");

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] wash dishes", tasks.get(0).toDisplayString());
        assertEquals("T | 0 | wash dishes", Files.readString(temporaryDirectory.resolve("data/homehub.txt")).trim());
    }

    @Test
    void addTodo_emptyDescription_rejectsWithoutChangingTasks() {
        TaskList tasks = new TaskList();
        TaskCommands commands = new TaskCommands(storageAt("data/homehub.txt"), new Ui());

        assertThrows(HomeHubException.class, () -> commands.addTodo(tasks, ""));

        assertEquals(0, tasks.size());
    }

    @Test
    void addTodo_descriptionContainingPipe_rejectsWithoutChangingTasks() {
        TaskList tasks = new TaskList();
        TaskCommands commands = new TaskCommands(storageAt("data/homehub.txt"), new Ui());

        assertThrows(HomeHubException.class, () -> commands.addTodo(tasks, "wash | dry dishes"));

        assertEquals(0, tasks.size());
    }

    @Test
    void addTodo_storageFailure_rollsBackInMemoryTask() {
        TaskList tasks = new TaskList();
        // A directory cannot be written as the storage file, forcing save() to fail.
        TaskCommands commands = new TaskCommands(new Storage(temporaryDirectory.toString()), new Ui());

        assertThrows(HomeHubException.class, () -> commands.addTodo(tasks, "wash dishes"));

        assertEquals(0, tasks.size());
    }

    @Test
    void addDeadline_validDateTime_addsFormattedAndPersistedTask() throws Exception {
        Storage storage = storageAt("data/homehub.txt");
        TaskList tasks = new TaskList();
        TaskCommands commands = new TaskCommands(storage, new Ui());

        commands.addDeadline(tasks, "pay bill /by 2026-09-01 09:30");

        assertEquals("[D][ ] pay bill (by: Sept 01 2026 09:30)", tasks.get(0).toDisplayString());
        assertEquals("D | 0 | pay bill | 2026-09-01 09:30",
                Files.readString(temporaryDirectory.resolve("data/homehub.txt")).trim());
    }

    @Test
    void addDeadline_invalidSyntaxOrDate_rejectsWithoutChangingTasks() {
        TaskList tasks = new TaskList();
        TaskCommands commands = new TaskCommands(storageAt("data/homehub.txt"), new Ui());

        assertThrows(HomeHubException.class, () -> commands.addDeadline(tasks, "pay bill"));
        assertThrows(HomeHubException.class, () -> commands.addDeadline(tasks, "pay bill /by"));
        assertThrows(HomeHubException.class, () -> commands.addDeadline(tasks, "pay bill /by 2026-02-30"));

        assertEquals(0, tasks.size());
    }

    @Test
    void addEvent_validDateTimes_addsFormattedAndPersistedTask() throws Exception {
        Storage storage = storageAt("data/homehub.txt");
        TaskList tasks = new TaskList();
        TaskCommands commands = new TaskCommands(storage, new Ui());

        commands.addEvent(tasks, "meeting /from 2026-09-02 14:00 /to 2026-09-02 16:00");

        assertEquals("[E][ ] meeting (from: Sept 02 2026 14:00 to: Sept 02 2026 16:00)",
                tasks.get(0).toDisplayString());
        assertEquals("E | 0 | meeting | 2026-09-02 14:00 | 2026-09-02 16:00",
                Files.readString(temporaryDirectory.resolve("data/homehub.txt")).trim());
    }

    @Test
    void addEvent_invalidSyntaxOrDate_rejectsWithoutChangingTasks() {
        TaskList tasks = new TaskList();
        TaskCommands commands = new TaskCommands(storageAt("data/homehub.txt"), new Ui());

        assertThrows(HomeHubException.class, () -> commands.addEvent(tasks, "meeting /from 2026-09-02"));
        assertThrows(HomeHubException.class, () ->
                commands.addEvent(tasks, "meeting /from 2026-09-02 /to 2026-02-30"));
        assertThrows(HomeHubException.class, () ->
                commands.addEvent(tasks, "meeting /from 2026-09-02 /from 2026-09-03 /to 2026-09-04"));

        assertEquals(0, tasks.size());
    }

    private Storage storageAt(String relativePath) {
        return new Storage(temporaryDirectory.resolve(relativePath).toString());
    }
}
