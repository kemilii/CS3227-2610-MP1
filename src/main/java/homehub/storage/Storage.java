package homehub.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import homehub.exception.HomeHubException;
import homehub.model.Deadline;
import homehub.model.Event;
import homehub.model.Task;
import homehub.model.TaskList;
import homehub.model.Todo;

/** Reads and writes HomeHub tasks to a configured local data file. */
public class Storage {
    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";
    private static final String DONE_STATUS = "1";
    private static final String PENDING_STATUS = "0";
    private static final int MINIMUM_FIELD_COUNT = 3;
    private static final int TODO_FIELD_COUNT = 3;
    private static final int DEADLINE_FIELD_COUNT = 4;
    private static final int EVENT_FIELD_COUNT = 5;

    private final Path filePath;

    /** Creates storage backed by the supplied file path. */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Saves the current tasks, replacing the previous file contents.
     *
     * @param taskList tasks to save.
     * @throws HomeHubException if the file cannot be written.
     */
    public void save(TaskList taskList) throws HomeHubException {
        assert taskList != null : "Saving requires an initialized task list";
        ArrayList<String> lines = new ArrayList<>();
        for (Task task : taskList.asArrayList()) {
            assert task != null : "A task list must not contain null tasks when saved";
            lines.add(task.toStorageString());
        }
        try {
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, lines);
        } catch (IOException | SecurityException exception) {
            throw new HomeHubException("I couldn't save your tasks to disk.");
        }
    }

    /**
     * Loads tasks from the local data file; a missing file means no tasks.
     *
     * @return the loaded tasks.
     * @throws HomeHubException if the file cannot be read or contains an invalid date.
     */
    public ArrayList<Task> load() throws HomeHubException {
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            if (!Files.exists(filePath)) {
                return tasks;
            }
            for (String line : Files.readAllLines(filePath)) {
                Task task = parseTask(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
            return tasks;
        } catch (IOException | SecurityException exception) {
            throw new HomeHubException("I couldn't load your saved tasks.");
        }
    }

    private Task parseTask(String line) throws HomeHubException {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        String[] fields = line.split("\\s*\\|\\s*", -1);
        if (fields.length < MINIMUM_FIELD_COUNT || fields[1].isEmpty()
                || fields[2].isEmpty()) {
            return null;
        }
        Task task = createTask(fields);
        if (task == null) {
            return null;
        }
        assert task != null : "A recognized storage record must create a task";
        if (fields[1].equals(DONE_STATUS)) {
            task.markAsDone();
        } else if (!fields[1].equals(PENDING_STATUS)) {
            return null;
        }
        return task;
    }

    private Task createTask(String[] fields) throws HomeHubException {
        switch (fields[0]) {
            case TODO_TYPE:
                return fields.length == TODO_FIELD_COUNT ? new Todo(fields[2]) : null;
            case DEADLINE_TYPE:
                return fields.length == DEADLINE_FIELD_COUNT && !fields[3].isEmpty()
                        ? new Deadline(fields[2], fields[3]) : null;
            case EVENT_TYPE:
                return fields.length == EVENT_FIELD_COUNT && !fields[3].isEmpty() && !fields[4].isEmpty()
                        ? new Event(fields[2], fields[3], fields[4]) : null;
            default:
                return null;
        }
    }
}
