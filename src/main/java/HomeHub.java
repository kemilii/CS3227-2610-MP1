import java.util.Scanner;

/**
 * A simple command-line household task manager that accepts commands until the
 * user says bye.
 */
public class HomeHub {
    private static final int MAX_TASKS = 100;

    public static void main(String[] args) {
        String separator = "____________________________________________________________";
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        System.out.println(separator);
        System.out.println("Welcome to HomeHub!");
        System.out.println("Manage your household tasks here.");
        System.out.println(separator);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(separator);

            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(separator);
                break;
            } else if (command.equals("list")) {
                System.out.println("Here are the household tasks in your HomeHub:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + ".[" + tasks[i].getStatusIcon() + "] "
                            + tasks[i].getDescription());
                }
                System.out.println(separator);
            } else if (command.startsWith("mark ")) {
                String taskNumberText = command.substring("mark ".length()).trim();
                try {
                    int taskNumber = Integer.parseInt(taskNumberText);
                    if (taskNumber >= 1 && taskNumber <= taskCount) {
                        int taskIndex = taskNumber - 1;
                        tasks[taskIndex].markAsDone();
                        System.out.println("Nice! I've marked this household task as done:");
                        System.out.println("  [X] " + tasks[taskIndex].getDescription());
                    } else {
                        System.out.println("That task number does not exist.");
                    }
                } catch (NumberFormatException exception) {
                    System.out.println("Please specify a valid task number.");
                }
                System.out.println(separator);
            } else if (command.startsWith("unmark ")) {
                String taskNumberText = command.substring("unmark ".length()).trim();
                try {
                    int taskNumber = Integer.parseInt(taskNumberText);
                    if (taskNumber >= 1 && taskNumber <= taskCount) {
                        int taskIndex = taskNumber - 1;
                        tasks[taskIndex].markAsNotDone();
                        System.out.println("OK, I've marked this task as not done yet:");
                        System.out.println("  [ ] " + tasks[taskIndex].getDescription());
                    } else {
                        System.out.println("That task number does not exist.");
                    }
                } catch (NumberFormatException exception) {
                    System.out.println("Please specify a valid task number.");
                }
                System.out.println(separator);
            } else if (taskCount < MAX_TASKS) {
                tasks[taskCount] = new Task(command);
                taskCount++;
                System.out.println("added: " + command);
                System.out.println(separator);
            }
        }
    }
}
