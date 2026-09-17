package homehub.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import homehub.exception.HomeHubException;
import homehub.model.PantryList;
import homehub.storage.Storage;
import homehub.ui.Ui;

/** Tests pantry-entry parsing, validation, duplicate checks, and persistence. */
class PantryCommandsTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void addItem_validEntry_addsAndPersistsInventory() throws Exception {
        PantryList pantry = new PantryList();
        PantryCommands commands = new PantryCommands(storageAt("pantry.txt"), new Ui());

        commands.addItem(pantry, "rice /qty 2 /unit kg /expires 2026-09-30");

        assertEquals(1, pantry.size());
        assertEquals(2, pantry.get(0).getQuantity());
        assertEquals(1, storageAt("pantry.txt").load().size());
    }

    @Test
    void addItem_optionalMetadata_persistsStockManagementDetails() throws Exception {
        PantryList pantry = new PantryList();
        PantryCommands commands = new PantryCommands(storageAt("metadata.txt"), new Ui());

        commands.addItem(pantry, "flour /qty 1 /unit bag /expires 2026-12-01 "
                + "/category baking /location cabinet /min 2");

        assertEquals("baking", pantry.get(0).getCategory());
        assertEquals("cabinet", pantry.get(0).getLocation());
        assertEquals(2, pantry.get(0).getMinimumQuantity());
    }

    @Test
    void addItem_invalidSyntaxDateAndDuplicate_rejectWithoutChangingPantry() throws Exception {
        PantryList pantry = new PantryList();
        PantryCommands commands = new PantryCommands(storageAt("pantry.txt"), new Ui());

        assertThrows(HomeHubException.class, () -> commands.addItem(pantry, "rice /qty 2 /unit kg"));
        assertThrows(HomeHubException.class, () ->
                commands.addItem(pantry, "rice /qty 2 /unit kg /expires 2026-02-30"));
        commands.addItem(pantry, "rice /qty 2 /unit kg /expires 2026-09-30");
        assertThrows(HomeHubException.class, () ->
                commands.addItem(pantry, "RICE /qty 5 /unit KG /expires 2026-09-30"));
        assertEquals(1, pantry.size());
    }

    private Storage storageAt(String fileName) {
        return new Storage(temporaryDirectory.resolve(fileName).toString());
    }
}
