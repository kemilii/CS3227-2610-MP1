package homehub.ui;

import java.util.Scanner;

import homehub.model.Task;
import homehub.model.TaskList;

/** Handles HomeHub's interaction with the command-line user. */
public class Ui {
    private static final String SEPARATOR = "____________________________________________________________";
    private final Scanner scanner;

    /** Creates a UI that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Displays the welcome message. */
    public void showWelcome() {
        showSeparator();
        printLine("Welcome to HomeHub!");
        printLine("Manage your household tasks here.");
        showSeparator();
    }

    /** Displays the goodbye message. */
    public void showGoodbye() {
        printLine("Bye. Hope to see you again soon!");
        showSeparator();
    }

    /** Displays a separator before processing a command. */
    public void showSeparator() {
        printLine(SEPARATOR);
    }

    /** Reads the next command, or returns {@code null} at end of input. */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine().trim() : null;
    }

    /** Displays an error message. */
    public void showError(String message) {
        printLine("Oops! " + message);
    }

    /** Displays all tasks. */
    public void showTaskList(TaskList tasks) {
        assert tasks != null : "Displaying tasks requires an initialized task list";
        printLine("Here are the household tasks in your HomeHub:");
        for (int i = 0; i < tasks.size(); i++) {
            printLine((i + 1) + "." + tasks.get(i).toDisplayString());
        }
    }

    /**
     * Displays tasks matching a search keyword.
     *
     * @param matchingTasks tasks whose descriptions matched the search keyword.
     */
    public void showMatchingTasks(TaskList matchingTasks) {
        assert matchingTasks != null : "Displaying matching tasks requires a task list";
        printLine("Here are the matching tasks in your list:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            printLine((i + 1) + "." + matchingTasks.get(i).toDisplayString());
        }
    }

    /** Displays the confirmation for an added task. */
    public void showAddedTask(Task task, int taskCount) {
        assert task != null : "An added-task confirmation requires a task";
        assert taskCount > 0 : "An added-task confirmation requires a non-empty list";
        printLine("Got it. I've added this task:");
        printLine("  " + task.toDisplayString());
        printLine("Now you have " + taskCount + " tasks in the list.");
    }

    /** Displays the confirmation for a marked or unmarked task. */
    public void showMarkedTask(Task task, boolean markedAsDone) {
        assert task != null : "A marked-task confirmation requires a task";
        String message = markedAsDone
                ? "Nice! I've marked this household task as done:"
                : "I've marked this household task as not done:";
        printLine(message);
        printLine("  " + task.toDisplayString());
    }

    /** Displays the confirmation for a deleted task. */
    public void showDeletedTask(Task task, int taskCount) {
        assert task != null : "A deleted-task confirmation requires a task";
        assert taskCount >= 0 : "A deleted-task confirmation requires a non-negative task count";
        printLine("Noted. I've removed this task:");
        printLine("  " + task.toDisplayString());
        printLine("Now you have " + taskCount + " tasks in the list.");
    }

    /** Writes one output line; subclasses can redirect the output destination. */
    protected void printLine(String line) {
        System.out.println(line);
    }
}
