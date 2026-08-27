import java.util.Scanner;

/**
 * A simple command-line chatbot that echoes commands until the user says bye.
 */
public class Caki {
    private static final int MAX_TASKS = 100;

    public static void main(String[] args) {
        String separator = "____________________________________________________________";
        String[] tasks = new String[MAX_TASKS];
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
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + ". " + tasks[i]);
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
