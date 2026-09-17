package homehub.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

/** Tests ordered pantry collection, duplicate detection, and inventory queries. */
class PantryListTest {
    @Test
    void pantryList_queriesPreserveOrderAndFilterDetails() throws Exception {
        PantryItem rice = new PantryItem("rice", 2, "kg", "2026-09-30");
        PantryItem milk = new PantryItem("milk", 1, "carton", "2026-10-15");
        PantryList pantry = new PantryList(new ArrayList<PantryItem>());
        pantry.add(rice);
        pantry.add(milk);

        assertEquals(2, pantry.size());
        assertTrue(pantry.hasItemWithSameDetails(new PantryItem("RICE", 5, "KG", "2026-09-30")));
        assertEquals(1, pantry.findMatchingItems("MIL").size());
        assertEquals(milk, pantry.findMatchingItems("MIL").get(0));
        assertEquals(1, pantry.findExpiringItems(ExpiryDate.parse("2026-10-01")).size());
        assertEquals(0, pantry.findLowStockItems().size());
        assertFalse(pantry.findMatchingItems("tea").size() > 0);
    }

    @Test
    void pantryList_lowStockQuery_returnsEntriesAtTheirThreshold() throws Exception {
        PantryList pantry = new PantryList();
        pantry.add(new PantryItem("flour", 1, "bag", "2026-12-01", "baking", "cabinet", 2));
        pantry.add(new PantryItem("rice", 5, "kg", "2026-12-01", "grains", "pantry", 2));

        assertEquals(1, pantry.findLowStockItems().size());
        assertEquals(2, pantry.countExpiredItems(ExpiryDate.parse("2027-01-01")));
    }
}
