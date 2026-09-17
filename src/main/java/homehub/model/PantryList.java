package homehub.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;

/** Owns the ordered collection of pantry inventory entries. */
public class PantryList {
    private final ArrayList<PantryItem> items;

    /** Creates an empty pantry list. */
    public PantryList() {
        items = new ArrayList<>();
    }

    /** Creates a pantry list containing a defensive copy of the supplied entries. */
    public PantryList(ArrayList<PantryItem> items) {
        assert items != null : "A pantry list requires a collection";
        for (PantryItem item : items) {
            assert item != null : "A pantry list must not contain null items";
        }
        this.items = new ArrayList<>(items);
    }

    /** Returns the pantry item at the zero-based index. */
    public PantryItem get(int index) {
        return items.get(index);
    }

    /** Adds an item to the end of the pantry list. */
    public void add(PantryItem item) {
        assert item != null : "A pantry list must not contain null items";
        items.add(item);
    }

    /** Inserts an item at the zero-based index. */
    public void add(int index, PantryItem item) {
        assert item != null : "A pantry list must not contain null items";
        items.add(index, item);
    }

    /** Removes and returns the item at the zero-based index. */
    public PantryItem remove(int index) {
        PantryItem removedItem = items.remove(index);
        assert removedItem != null : "A pantry list must not contain null items";
        return removedItem;
    }

    /** Returns the number of tracked pantry entries. */
    public int size() {
        return items.size();
    }

    /** Returns whether an equivalent pantry entry is already tracked. */
    public boolean hasItemWithSameDetails(PantryItem candidate) {
        assert candidate != null : "Duplicate checks require an item candidate";
        for (PantryItem item : items) {
            if (item.hasSameDetailsAs(candidate)) {
                return true;
            }
        }
        return false;
    }

    /** Returns pantry entries whose names contain the keyword, ignoring case. */
    public PantryList findMatchingItems(String keyword) {
        ArrayList<PantryItem> matchingItems = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return new PantryList(matchingItems);
        }
        String normalizedKeyword = keyword.trim().toLowerCase(Locale.ROOT);
        for (PantryItem item : items) {
            if (item.getName().toLowerCase(Locale.ROOT).contains(normalizedKeyword)) {
                matchingItems.add(item);
            }
        }
        return new PantryList(matchingItems);
    }

    /** Returns pantry entries expiring on or before the supplied date. */
    public PantryList findExpiringItems(LocalDate date) {
        assert date != null : "Expiry searches require a date";
        ArrayList<PantryItem> expiringItems = new ArrayList<>();
        for (PantryItem item : items) {
            if (item.expiresOnOrBefore(date)) {
                expiringItems.add(item);
            }
        }
        return new PantryList(expiringItems);
    }

    /** Returns pantry entries whose quantities are at or below their thresholds. */
    public PantryList findLowStockItems() {
        ArrayList<PantryItem> lowStockItems = new ArrayList<>();
        for (PantryItem item : items) {
            if (item.isLowStock()) {
                lowStockItems.add(item);
            }
        }
        return new PantryList(lowStockItems);
    }

    /** Returns the number of entries that have expired before the supplied date. */
    public int countExpiredItems(LocalDate date) {
        assert date != null : "Expired-item counts require a date";
        int expiredCount = 0;
        for (PantryItem item : items) {
            if (item.getExpiryDate().isBefore(date)) {
                expiredCount++;
            }
        }
        return expiredCount;
    }

    /** Returns a defensive copy for persistence. */
    public ArrayList<PantryItem> asArrayList() {
        return new ArrayList<>(items);
    }
}
