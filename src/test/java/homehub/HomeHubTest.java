package homehub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import homehub.command.CommandType;
import homehub.storage.Storage;

/** Tests HomeHub's pantry commands, state transitions, and persistence boundary. */
class HomeHubTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_inventoryLifecycle_updatesQuantitiesAndEntries() {
        HomeHub homeHub = homeHubAt("pantry.txt");

        assertEquals(String.join(System.lineSeparator(),
                "✨ Added to the pantry:",
                "  [P] rice — 2 kg (expires: Sep 30 2026)",
                "There are 1 pantry entries tracked. 🧺"),
                homeHub.getResponse("add rice /qty 2 /unit kg /expires 2026-09-30"));
        assertEquals("🧺 Keke's pantry inventory:", homeHub.getResponse("list").split("\\R")[0]);
        assertTrue(homeHub.getResponse("consume 1 1").contains("[P] rice — 1 kg"));
        assertTrue(homeHub.getResponse("restock 1 3").contains("[P] rice — 4 kg"));
        assertTrue(homeHub.getResponse("delete 1").contains("Removed from the pantry"));
        assertEquals("🧺 Keke's pantry inventory:", homeHub.getResponse("list"));
    }

    @Test
    void getResponse_searchAndExpiryQueries_returnRelevantEntries() {
        HomeHub homeHub = homeHubAt("queries.txt");
        homeHub.getResponse("add rice /qty 2 /unit kg /expires 2026-09-30");
        homeHub.getResponse("add milk /qty 1 /unit carton /expires 2026-10-15");

        assertTrue(homeHub.getResponse("search RICE").contains("rice — 2 kg"));
        assertTrue(homeHub.getResponse("expiring 2026-10-01").contains("rice — 2 kg"));
        assertFalse(homeHub.getResponse("expiring 2026-10-01").contains("milk — 1 carton"));
    }

    @Test
    void getResponse_stockHealthCommands_reportAndMoveMetadata() {
        HomeHub homeHub = homeHubAt("health.txt");
        homeHub.getResponse("add flour /qty 1 /unit bag /expires 2099-12-01 "
                + "/category baking /location cabinet /min 2");

        assertTrue(homeHub.getResponse("lowstock").contains("Low-stock pantry entries"));
        assertTrue(homeHub.getResponse("summary").contains("Low-stock entries: 1"));
        assertTrue(homeHub.getResponse("move 1 freezer").contains("location: freezer"));
        assertTrue(homeHub.getResponse("list").contains("location: freezer"));
    }

    @Test
    void getResponse_invalidCommands_leavePantryUnchanged() {
        HomeHub homeHub = homeHubAt("invalid.txt");

        assertTrue(homeHub.getResponse("mark 1").contains("not supported"));
        assertTrue(homeHub.getResponse("add rice /qty 0 /unit kg /expires 2026-09-30").contains("positive"));
        assertTrue(homeHub.getResponse("consume 1 1").contains("does not exist"));
        assertTrue(homeHub.getResponse("expiring 2026-02-30").contains("yyyy-MM-dd"));
        assertEquals("🧺 Keke's pantry inventory:", homeHub.getResponse("list"));
    }

    @Test
    void getResponse_helpAndBye_setCommandTypeAndExitState() {
        HomeHub homeHub = homeHubAt("lifecycle.txt");

        assertTrue(homeHub.getResponse("help").contains("add <name> /qty"));
        assertEquals(CommandType.HELP, homeHub.getCommandType());
        assertEquals("Pantry secured. See you soon! 👋", homeHub.getResponse("bye"));
        assertTrue(homeHub.isExitRequested());
    }

    @Test
    void reopenedHomeHub_loadsPreviouslySavedInventory() {
        Storage storage = storageAt("reopen.txt");
        HomeHub firstSession = new HomeHub(storage);
        firstSession.getResponse("add oats /qty 1 /unit bag /expires 2026-11-01");

        HomeHub reopenedSession = new HomeHub(storage);

        assertTrue(reopenedSession.getResponse("list").contains("oats — 1 bag"));
    }

    @Test
    void getResponse_storageFailure_rollsBackAddMutation() throws Exception {
        Path blockedParent = temporaryDirectory.resolve("blocked");
        Files.writeString(blockedParent, "not a directory");
        HomeHub homeHub = new HomeHub(new Storage(blockedParent.resolve("pantry.txt").toString()));

        assertTrue(homeHub.getResponse("add rice /qty 2 /unit kg /expires 2026-09-30").contains("couldn't save"));
        assertEquals("🧺 Keke's pantry inventory:", homeHub.getResponse("list"));
    }

    @Test
    void getResponse_storageFailure_rollsBackQuantityMutation() throws Exception {
        Path storageDirectory = temporaryDirectory.resolve("storage");
        Path storageFile = storageDirectory.resolve("pantry.txt");
        Files.createDirectories(storageDirectory);
        Files.writeString(storageFile, "P | rice | 2 | kg | 2026-09-30 | general | pantry | 0\n");
        HomeHub homeHub = new HomeHub(new Storage(storageFile.toString()));

        Files.delete(storageFile);
        Files.delete(storageDirectory);
        Files.writeString(storageDirectory, "not a directory");

        assertTrue(homeHub.getResponse("consume 1 1").contains("couldn't save"));
        assertTrue(homeHub.getResponse("list").contains("rice — 2 kg"));
    }

    private HomeHub homeHubAt(String fileName) {
        return new HomeHub(storageAt(fileName));
    }

    private Storage storageAt(String fileName) {
        return new Storage(temporaryDirectory.resolve(fileName).toString());
    }
}
