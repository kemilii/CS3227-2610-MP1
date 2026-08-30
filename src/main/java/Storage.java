import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/** Reads and writes HomeHub tasks to a configured local data file. */
public class Storage {
    private final Path filePath;

    /** Creates storage backed by the supplied file path. */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /** Saves the current tasks, replacing the previous file contents. */
    public void save(TaskList taskList) throws HomeHubException {
        ArrayList<String> lines = new ArrayList<>();
        for (Task task : taskList.asArrayList()) {
            lines.add(task.toStorageString());
        }
        try {
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, lines);
        } catch (IOException | SecurityException exception) {
            throw new HomeHubException("I couldn't save your tasks to disk.");
        }
    }

    /** Loads tasks from the local data file; a missing file means no tasks. */
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
        if (fields.length < 3 || fields[1].isEmpty() || fields[2].isEmpty()) {
            return null;
        }
        Task task;
        if (fields[0].equals("T") && fields.length == 3) {
            task = new Todo(fields[2]);
        } else if (fields[0].equals("D") && fields.length == 4 && !fields[3].isEmpty()) {
            task = new Deadline(fields[2], fields[3]);
        } else if (fields[0].equals("E") && fields.length == 5
                && !fields[3].isEmpty() && !fields[4].isEmpty()) {
            task = new Event(fields[2], fields[3], fields[4]);
        } else {
            return null;
        }
        if (fields[1].equals("1")) {
            task.markAsDone();
        } else if (!fields[1].equals("0")) {
            return null;
        }
        return task;
    }
}
