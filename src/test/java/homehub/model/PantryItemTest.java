package homehub.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import homehub.exception.HomeHubException;

/** Tests quantity, expiry, display, and validation behavior for pantry entries. */
class PantryItemTest {
    @Test
    void pantryItem_quantityOperations_updateStockSafely() throws Exception {
        PantryItem item = new PantryItem("rice", 2, "kg", "2026-09-30");

        item.restock(3);
        item.consume(2);

        assertEquals(3, item.getQuantity());
        assertThrows(HomeHubException.class, () -> item.consume(4));
        assertThrows(HomeHubException.class, () -> item.restock(0));
    }

    @Test
    void pantryItem_displayAndStorage_includeInventoryDetails() throws Exception {
        PantryItem item = new PantryItem("rice", 2, "kg", "2026-09-30");

        assertEquals("[P] rice — 2 kg (expires: Sep 30 2026)", item.toDisplayString());
        assertEquals("P | rice | 2 | kg | 2026-09-30 | general | pantry | 0", item.toStorageString());
        assertTrue(item.expiresOnOrBefore(ExpiryDate.parse("2026-10-01")));
    }

    @Test
    void pantryItem_metadataAndThresholds_reportStockHealth() throws Exception {
        PantryItem item = new PantryItem("flour", 1, "bag", "2026-12-01", "baking", "cabinet", 2);

        assertEquals("baking", item.getCategory());
        assertEquals("cabinet", item.getLocation());
        assertEquals(2, item.getMinimumQuantity());
        assertTrue(item.isLowStock());
        item.moveTo("freezer");
        assertEquals("freezer", item.getLocation());
    }

    @Test
    void pantryItem_invalidConstruction_rejectsInvalidQuantityAndDate() {
        assertThrows(HomeHubException.class, () -> new PantryItem("rice", 0, "kg", "2026-09-30"));
        assertThrows(HomeHubException.class, () -> new PantryItem("rice", 1, "kg", "2026-02-30"));
        assertThrows(HomeHubException.class, () -> new PantryItem("", 1, "kg", "2026-09-30"));
    }
}
