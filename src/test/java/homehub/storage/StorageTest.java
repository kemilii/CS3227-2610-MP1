package homehub.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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
    void load_missingOrMalformedFile_returnsOnlyValidPantryEntries() throws Exception {
        Path file = temporaryDirectory.resolve("pantry.txt");
        Files.write(file, java.util.List.of(
                "T | 0 | old task",
                "P | rice | 2 | kg | 2026-09-30",
                "P | broken | nope | kg | 2026-09-30",
                "not a pantry record"));

        ArrayList<PantryItem> loaded = storageAt("pantry.txt").load();

        assertEquals(1, loaded.size());
        assertEquals("rice", loaded.get(0).getName());
        assertFalse(storageAt("missing.txt").load().size() > 0);
    }

    private Storage storageAt(String fileName) {
        return new Storage(temporaryDirectory.resolve(fileName).toString());
    }
}
