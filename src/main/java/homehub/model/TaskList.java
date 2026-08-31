package homehub.model;

import java.util.ArrayList;
import java.util.Locale;

/** Owns the in-memory collection of household tasks. */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /** Creates a task list containing the supplied tasks. */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "A task list must be initialized from a collection";
        for (Task task : tasks) {
            assert task != null : "A task list must not contain null tasks";
        }
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the task at the zero-based index.
     *
     * @param index zero-based task index.
     * @return the task at the index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add.
     */
    public void add(Task task) {
        assert task != null : "A task list must not contain null tasks";
        tasks.add(task);
    }

    /**
     * Inserts a task at the zero-based index.
     *
     * @param index zero-based insertion index.
     * @param task task to add.
     */
    public void add(int index, Task task) {
        assert task != null : "A task list must not contain null tasks";
        tasks.add(index, task);
    }

    /**
     * Removes and returns the task at the zero-based index.
     *
     * @param index zero-based task index.
     * @return the removed task.
     */
    public Task remove(int index) {
        Task removedTask = tasks.remove(index);
        assert removedTask != null : "A task list must not contain null tasks";
        return removedTask;
    }

    /**
     * Returns the number of tasks.
     *
     * @return the number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the tasks whose descriptions contain the supplied keyword.
     * Matching is case-insensitive and preserves the order of the original list.
     *
     * @param keyword text to search for in task descriptions.
     * @return a new task list containing the matching tasks.
     */
    public TaskList findMatchingTasks(String keyword) {
        String normalizedKeyword = keyword.trim().toLowerCase(Locale.ROOT);
        ArrayList<Task> matchingTasks = new ArrayList<>();
        if (normalizedKeyword.isEmpty()) {
            return new TaskList(matchingTasks);
        }
        for (Task task : tasks) {
            assert task != null : "A task list must not contain null tasks";
            if (task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword)) {
                matchingTasks.add(task);
            }
        }
        return new TaskList(matchingTasks);
    }

    /**
     * Returns a copy of the tasks for persistence without exposing the mutable list.
     *
     * @return a copy of the tasks.
     */
    public ArrayList<Task> asArrayList() {
        return new ArrayList<>(tasks);
    }
}
