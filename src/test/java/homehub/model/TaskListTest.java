package homehub.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests searching for tasks by description. */
class TaskListTest {
    @Test
    void findMatchingTasks_caseInsensitiveSubstring_returnsMatchingTasksInOrder() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("clean room"));
        tasks.add(new Deadline("return BOOK", "2026-06-06"));

        TaskList matchingTasks = tasks.findMatchingTasks(" BoOk ");

        assertEquals(2, matchingTasks.size());
        assertEquals("read book", matchingTasks.get(0).getDescription());
        assertEquals("return BOOK", matchingTasks.get(1).getDescription());
        assertEquals(3, tasks.size());
    }

    @Test
    void findMatchingTasks_noMatch_returnsEmptyTaskList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertEquals(0, tasks.findMatchingTasks("movie").size());
    }
}
