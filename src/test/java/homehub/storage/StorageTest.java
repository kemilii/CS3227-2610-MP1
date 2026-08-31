package homehub.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import homehub.exception.HomeHubException;
import homehub.model.Deadline;
import homehub.model.Event;
import homehub.model.Task;
import homehub.model.TaskList;
import homehub.model.Todo;

/** Tests persistence and recovery of the supported task representations. */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void saveAndLoad_mixedTasksAndStatuses_preservesTaskData() throws Exception {
        Storage storage = storageAt("nested/data/homehub.txt");
        TaskList tasks = new TaskList();
        Task todo = new Todo("wash dishes");
        Task deadline = new Deadline("pay bill", "2026-09-01");
        Task event = new Event("meeting", "2026-09-02 14:00", "2026-09-02 16:00");
        todo.markAsDone();
        event.markAsDone();
        tasks.add(todo);
        tasks.add(deadline);
        tasks.add(event);

        storage.save(tasks);
        ArrayList<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertEquals("[T][X] wash dishes", loaded.get(0).toDisplayString());
        assertEquals("[D][ ] pay bill (by: Sept 01 2026)", loaded.get(1).toDisplayString());
        assertEquals("[E][X] meeting (from: Sept 02 2026 14:00 to: Sept 02 2026 16:00)",
                loaded.get(2).toDisplayString());
        assertTrue(Files.exists(temporaryDirectory.resolve("nested/data/homehub.txt")));
    }

    @Test
    void load_missingFile_returnsEmptyTaskCollection() throws Exception {
        ArrayList<Task> loaded = storageAt("data/homehub.txt").load();

        assertTrue(loaded.isEmpty());
    }

    @Test
    void load_malformedRecords_ignoresInvalidRecordsAndLoadsValidOnes() throws Exception {
        Path file = temporaryDirectory.resolve("data/homehub.txt");
        Files.createDirectories(file.getParent());
        Files.writeString(file, String.join("\n",
                "",
                "T | 0 | valid todo",
                "D | 1 | valid deadline | 2026-02-28",
                "E | 0 | valid event | 2026-03-01 | 2026-03-02",
                "X | 0 | unknown type",
                "T | 2 | invalid status",
                "T | 0 |",
                "D | 0 | missing date",
                "E | 0 | missing end | 2026-03-01 |",
                "T | 0 | too many fields | extra"));

        ArrayList<Task> loaded = storageAt("data/homehub.txt").load();

        assertEquals(3, loaded.size());
        assertEquals("valid todo", loaded.get(0).getDescription());
        assertEquals("valid deadline", loaded.get(1).getDescription());
        assertEquals("valid event", loaded.get(2).getDescription());
    }

    @Test
    void load_recordWithInvalidDate_throwsHomeHubException() throws Exception {
        Path file = temporaryDirectory.resolve("data/homehub.txt");
        Files.createDirectories(file.getParent());
        Files.writeString(file, "D | 0 | invalid deadline | 2026-02-30");

        assertThrows(HomeHubException.class, () -> storageAt("data/homehub.txt").load());
    }

    private Storage storageAt(String relativePath) {
        return new Storage(temporaryDirectory.resolve(relativePath).toString());
    }
}
