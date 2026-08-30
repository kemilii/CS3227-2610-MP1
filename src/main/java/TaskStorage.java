import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/** Reads and writes HomeHub tasks to the local data file. */
public class TaskStorage {
    private static final Path FILE_PATH = Path.of("data", "homehub.txt");

    /**
     * Saves the current tasks, replacing the previous file contents.
     *
     * @param tasks tasks to save
     * @throws HomeHubException if the file cannot be written
     */
    public static void save(ArrayList<Task> tasks) throws HomeHubException {
        ArrayList<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(task.toStorageString());
        }
        try {
            Files.createDirectories(FILE_PATH.getParent());
            Files.write(FILE_PATH, lines);
        } catch (IOException | SecurityException exception) {
            throw new HomeHubException("I couldn't save your tasks to disk.");
        }
    }

    /**
     * Loads tasks from the local data file. A missing file represents an empty
     * task list, which is the normal state on the first launch.
     *
     * @return tasks reconstructed from the save file
     * @throws HomeHubException if the existing file cannot be read
     */
    public static ArrayList<Task> load() throws HomeHubException {
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            if (!Files.exists(FILE_PATH)) {
                return tasks;
            }
            for (String line : Files.readAllLines(FILE_PATH)) {
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

    private static Task parseTask(String line) throws HomeHubException {
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
