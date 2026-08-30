package homehub.model;

import java.util.ArrayList;

/** Owns the in-memory collection of household tasks. */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /** Creates a task list containing the supplied tasks. */
    public TaskList(ArrayList<Task> tasks) {
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
        tasks.add(task);
    }

    /**
     * Inserts a task at the zero-based index.
     *
     * @param index zero-based insertion index.
     * @param task task to add.
     */
    public void add(int index, Task task) {
        tasks.add(index, task);
    }

    /**
     * Removes and returns the task at the zero-based index.
     *
     * @param index zero-based task index.
     * @return the removed task.
     */
    public Task remove(int index) {
        return tasks.remove(index);
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
     * Returns a copy of the tasks for persistence without exposing the mutable list.
     *
     * @return a copy of the tasks.
     */
    public ArrayList<Task> asArrayList() {
        return new ArrayList<>(tasks);
    }
}
