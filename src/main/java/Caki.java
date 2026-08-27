import java.util.Scanner;

/**
 * A simple command-line chatbot that echoes commands until the user says bye.
 */
public class Caki {
    private static final int MAX_TASKS = 100;

    public static void main(String[] args) {
        String separator = "____________________________________________________________";
        String[] tasks = new String[MAX_TASKS];
        boolean[] completed = new boolean[MAX_TASKS];
        int taskCount = 0;

        System.out.println(separator);
        System.out.println("   ____      _    _      ");
        System.out.println("  / ___|__ _| | _(_)     ");
        System.out.println(" | |   / _` | |/ / |     ");
        System.out.println(" | |__| (_| |   <| |     ");
        System.out.println("  \\____\\__,_|_|\\_\\_|     ");
        System.out.println("Hello! I'm Caki.");
        System.out.println("What can I do for you?");
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
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    String status = completed[i] ? "X" : " ";
                    System.out.println((i + 1) + ".[" + status + "] " + tasks[i]);
                }
                System.out.println(separator);
            } else if (command.startsWith("mark ")) {
                String taskNumberText = command.substring("mark ".length()).trim();
                try {
                    int taskNumber = Integer.parseInt(taskNumberText);
                    if (taskNumber >= 1 && taskNumber <= taskCount) {
                        int taskIndex = taskNumber - 1;
                        completed[taskIndex] = true;
                        System.out.println("Nice! I've marked this task as done:");
                        System.out.println("  [X] " + tasks[taskIndex]);
                    } else {
                        System.out.println("That task number does not exist.");
                    }
                } catch (NumberFormatException exception) {
                    System.out.println("Please specify a valid task number.");
                }
                System.out.println(separator);
            } else if (taskCount < MAX_TASKS) {
                tasks[taskCount] = command;
                taskCount++;
                System.out.println("added: " + command);
                System.out.println(separator);
            }
        }
    }
}
