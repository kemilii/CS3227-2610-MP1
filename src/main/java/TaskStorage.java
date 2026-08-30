import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/** Writes HomeHub tasks to the local data file. */
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
        } catch (IOException exception) {
            throw new HomeHubException("I couldn't save your tasks to disk.");
        }
    }
}
