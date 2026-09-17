package homehub.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import homehub.exception.HomeHubException;
import homehub.model.PantryItem;
import homehub.model.PantryList;

/** Tests pantry persistence and safe handling of malformed inventory records. */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void saveAndLoad_inventoryEntries_preservesDetailsAndQuantity() throws Exception {
        Storage storage = storageAt("nested/pantry.txt");
        PantryList pantry = new PantryList();
        pantry.add(new PantryItem("rice", 2, "kg", "2026-09-30"));
        pantry.add(new PantryItem("milk", 1, "carton", "2026-10-15"));

        storage.save(pantry);
        ArrayList<PantryItem> loaded = storage.load();

        assertEquals(2, loaded.size());
        assertEquals("rice", loaded.get(0).getName());
        assertEquals(2, loaded.get(0).getQuantity());
        assertTrue(Files.exists(temporaryDirectory.resolve("nested/pantry.txt")));
    }

    @Test
    void save_replacesPreviousFileAndCleansTemporaryFiles() throws Exception {
        Storage storage = storageAt("atomic/pantry.txt");
        PantryList firstPantry = new PantryList();
        firstPantry.add(new PantryItem("rice", 2, "kg", "2026-09-30"));
        storage.save(firstPantry);

        PantryList replacementPantry = new PantryList();
        replacementPantry.add(new PantryItem("milk", 1, "carton", "2026-10-15"));
        storage.save(replacementPantry);

        ArrayList<PantryItem> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("milk", loaded.get(0).getName());
        try (Stream<Path> files = Files.list(temporaryDirectory.resolve("atomic"))) {
            assertEquals(List.of("pantry.txt"), files.map(path -> path.getFileName().toString()).toList());
        }
    }

    @Test
    void save_shortFileName_stillPersistsInventory() throws Exception {
        Storage storage = storageAt("a");
        PantryList pantry = new PantryList();
        pantry.add(new PantryItem("rice", 1, "kg", "2026-09-30"));

        storage.save(pantry);

        assertEquals(1, storage.load().size());
    }

    @Test
    void load_missingOrMalformedFile_returnsOnlyValidPantryEntries() throws Exception {
        Path file = temporaryDirectory.resolve("pantry.txt");
        Files.write(file, List.of(
                "T | 0 | old task",
                "P | rice | 2 | kg | 2026-09-30",
                "P | broken | nope | kg | 2026-09-30",
                "not a pantry record"));

        ArrayList<PantryItem> loaded = storageAt("pantry.txt").load();

        assertEquals(1, loaded.size());
        assertEquals("rice", loaded.get(0).getName());
        assertFalse(storageAt("missing.txt").load().size() > 0);
    }

    @Test
    void load_malformedRecords_skipsInvalidEntriesAndLoadsLegacyRecords() throws Exception {
        Path file = temporaryDirectory.resolve("mixed.txt");
        Files.write(file, List.of(
                "P | legacy | 1 | bag | 2026-12-01",
                "P | current | 3 | kg | 2026-09-30 | grains | pantry | 1",
                "P | zero | 0 | kg | 2026-09-30 | general | pantry | 0",
                "P | date | 1 | kg | 2026-02-30 | general | pantry | 0",
                "P | threshold | 1 | kg | 2026-09-30 | general | pantry | -1",
                "P | extra | 1 | kg | 2026-09-30 | general | pantry | 0 | field",
                "P | bad\tname | 1 | kg | 2026-09-30 | general | pantry | 0",
                "T | 0 | old task",
                "not a pantry record"));

        ArrayList<PantryItem> loaded = storageAt("mixed.txt").load();

        assertEquals(2, loaded.size());
        assertEquals("legacy", loaded.get(0).getName());
        assertEquals(PantryItem.DEFAULT_CATEGORY, loaded.get(0).getCategory());
        assertEquals(PantryItem.DEFAULT_LOCATION, loaded.get(0).getLocation());
        assertEquals("current", loaded.get(1).getName());
        assertEquals(1, loaded.get(1).getMinimumQuantity());
    }

    @Test
    void load_duplicateRecords_throwsInsteadOfMergingQuantities() throws Exception {
        Path file = temporaryDirectory.resolve("duplicates.txt");
        Files.write(file, List.of(
                "P | rice | 2 | kg | 2026-09-30 | general | pantry | 0",
                "P | RICE | 5 | KG | 2026-09-30 | grains | cabinet | 1"));

        assertThrows(HomeHubException.class, () -> storageAt("duplicates.txt").load());
    }

    @Test
    void save_unsafeMetadata_rejectsPipeDelimitedText() throws Exception {
        Storage storage = storageAt("unsafe.txt");
        PantryList pantry = new PantryList();
        pantry.add(new PantryItem("rice", 1, "kg", "2026-09-30", "dry|goods", "pantry", 0));

        assertThrows(HomeHubException.class, () -> storage.save(pantry));
    }

    private Storage storageAt(String fileName) {
        return new Storage(temporaryDirectory.resolve(fileName).toString());
    }
}
