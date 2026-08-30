package homehub.model;

import homehub.exception.HomeHubException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Tests task state transitions and date-aware display/storage formatting. */
class TaskTest {
    @Test
    void todo_statusTransitions_updateDisplayAndStorageMarkers() {
        Task todo = new Todo("wash dishes");

        assertEquals("[T][ ] wash dishes", todo.toDisplayString());
        assertEquals("T | 0 | wash dishes", todo.toStorageString());

        todo.markAsDone();
        assertEquals(TaskStatus.DONE, todo.getStatus());
        assertEquals("[T][X] wash dishes", todo.toDisplayString());
        assertEquals("T | 1 | wash dishes", todo.toStorageString());

        todo.markAsNotDone();
        assertEquals(TaskStatus.PENDING, todo.getStatus());
    }

    @Test
    void deadline_dateOnlyAndDateTime_formatForDisplayAndStorage() throws Exception {
        Task dateOnly = new Deadline("submit report", "2026-09-01");
        Task dateTime = new Deadline("submit report", "2026-09-01 09:30");

        assertEquals("[D][ ] submit report (by: Sept 01 2026)", dateOnly.toDisplayString());
        assertEquals("D | 0 | submit report | 2026-09-01", dateOnly.toStorageString());
        assertEquals("[D][ ] submit report (by: Sept 01 2026 09:30)", dateTime.toDisplayString());
        assertEquals("D | 0 | submit report | 2026-09-01 09:30", dateTime.toStorageString());
    }

    @Test
    void event_dateOnlyAndDateTime_formatForDisplayAndStorage() throws Exception {
        Task dateOnly = new Event("team meeting", "2026-09-02", "2026-09-03");
        Task dateTime = new Event("team meeting", "2026-09-02 14:00", "2026-09-02 16:00");

        assertEquals("[E][ ] team meeting (from: Sept 02 2026 to: Sept 03 2026)",
                dateOnly.toDisplayString());
        assertEquals("E | 0 | team meeting | 2026-09-02 | 2026-09-03", dateOnly.toStorageString());
        assertEquals("[E][ ] team meeting (from: Sept 02 2026 14:00 to: Sept 02 2026 16:00)",
                dateTime.toDisplayString());
        assertEquals("E | 0 | team meeting | 2026-09-02 14:00 | 2026-09-02 16:00",
                dateTime.toStorageString());
    }

    @Test
    void deadlineOrEvent_invalidDate_throwsHomeHubException() {
        assertThrows(HomeHubException.class, () -> new Deadline("submit report", "2026-02-30"));
        assertThrows(HomeHubException.class,
                () -> new Event("team meeting", "2026-09-02 25:00", "2026-09-02 16:00"));
    }
}
