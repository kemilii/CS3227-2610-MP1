package homehub.ui;

import java.time.LocalDate;
import java.util.Scanner;

import homehub.Moss;
import homehub.model.ExpiryDate;
import homehub.model.PantryItem;
import homehub.model.PantryList;

/** Handles HomeHub's command-line pantry inventory interaction. */
public class Ui {
    private static final String SEPARATOR = "____________________________________________________________";
    private final Scanner scanner;

    /** Creates a UI that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Displays the welcome message. */
    public void showWelcome() {
        showSeparator();
        printLine("🌿 Welcome to HomeHub. " + Moss.NAME + " is on duty.");
        printLine("HomeHub tracks pantry stock, expiry dates, locations, and low-stock levels.");
        printLine("Commands: add, list, search, restock, consume, expiring, lowstock, summary, move, delete.");
        printLine("Type help anytime for the full command menu. 🏡");
        showSeparator();
    }

    /** Displays the goodbye message. */
    public void showGoodbye() {
        printLine("Pantry secured. See you soon! 👋");
        showSeparator();
    }

    /** Displays a separator before processing a command. */
    public void showSeparator() {
        printLine(SEPARATOR);
    }

    /** Displays the pantry commands and their accepted formats. */
    public void showHelp() {
        printLine("📖 " + Moss.NAME + "'s pantry command menu (available anytime with help):");
        printLine("Inventory:");
        printLine("add <name> /qty <number> /unit <unit> /expires <yyyy-MM-dd> "
                + "[/category <category>] [/location <location>] [/min <quantity>] - add pantry stock.");
        printLine("list - show every tracked pantry entry.");
        printLine("search <keyword> - find pantry entries by name.");
        printLine("Stock updates:");
        printLine("restock <item number> <quantity> - increase available stock.");
        printLine("consume <item number> <quantity> - reduce available stock.");
        printLine("Stock health and storage:");
        printLine("expiring <yyyy-MM-dd> - show entries expiring by a cutoff date.");
        printLine("lowstock - show entries at or below their minimum stock level.");
        printLine("summary - show a stock health summary.");
        printLine("move <item number> <location> - move an entry to another storage location.");
        printLine("delete <item number> - remove an entry from the pantry.");
        printLine("Other:");
        printLine("help - show this command menu.");
        printLine("bye - close HomeHub.");
        printLine("Date format: yyyy-MM-dd. Example: 2026-09-30.");
    }

    /** Reads the next command, or returns {@code null} at end of input. */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine().trim() : null;
    }

    /** Displays an error message. */
    public void showError(String message) {
        printLine(Moss.ERROR_PREFIX + "💬 " + message);
    }

    /** Displays all tracked pantry entries. */
    public void showPantry(PantryList pantry) {
        assert pantry != null : "Displaying the pantry requires an initialized pantry list";
        printLine("🧺 " + Moss.NAME + "'s pantry inventory:");
        for (int index = 0; index < pantry.size(); index++) {
            printLine((index + 1) + "." + pantry.get(index).toDisplayString());
        }
    }

    /** Displays entries whose expiry date is on or before the cutoff. */
    public void showExpiringItems(PantryList items, LocalDate cutoff) {
        assert items != null : "Displaying expiring items requires a pantry list";
        assert cutoff != null : "Displaying expiring items requires a cutoff date";
        if (items.size() == 0) {
            printLine("✅ " + Moss.NAME + " found no pantry entries expiring by " + ExpiryDate.display(cutoff) + ".");
            return;
        }
        printLine("⏳ Pantry entries expiring by " + ExpiryDate.display(cutoff) + ":");
        for (int index = 0; index < items.size(); index++) {
            printLine((index + 1) + "." + items.get(index).toDisplayString());
        }
    }

    /** Displays entries matching a name keyword. */
    public void showMatchingItems(PantryList items) {
        assert items != null : "Displaying matching items requires a pantry list";
        if (items.size() == 0) {
            printLine("🫧 " + Moss.NAME + " couldn't find any pantry entries matching that keyword.");
            return;
        }
        printLine("🔎 " + Moss.NAME + " found these pantry entries:");
        for (int index = 0; index < items.size(); index++) {
            printLine((index + 1) + "." + items.get(index).toDisplayString());
        }
    }

    /** Displays entries at or below their configured minimum quantities. */
    public void showLowStockItems(PantryList items) {
        assert items != null : "Displaying low-stock items requires a pantry list";
        if (items.size() == 0) {
            printLine("✅ " + Moss.NAME + " found no low-stock pantry entries.");
            return;
        }
        printLine("📉 Low-stock pantry entries:");
        for (int index = 0; index < items.size(); index++) {
            printLine((index + 1) + "." + items.get(index).toDisplayString());
        }
    }

    /** Displays counts that summarize pantry stock health. */
    public void showSummary(PantryList pantry) {
        assert pantry != null : "Displaying a summary requires an initialized pantry list";
        printLine("📊 Pantry summary:");
        printLine("Entries tracked: " + pantry.size());
        printLine("Low-stock entries: " + pantry.findLowStockItems().size());
        printLine("Expired entries: " + pantry.countExpiredItems(LocalDate.now()));
    }

    /** Displays confirmation for a newly added inventory entry. */
    public void showAddedItem(PantryItem item, int entryCount) {
        assert item != null : "An add confirmation requires a pantry item";
        assert entryCount > 0 : "An add confirmation requires a non-empty pantry";
        printLine("✨ Added to the pantry:");
        printLine("  " + item.toDisplayString());
        printLine("There are " + entryCount + " pantry entries tracked. 🧺");
    }

    /** Displays confirmation for a stock quantity change. */
    public void showQuantityChanged(PantryItem item, int amount, boolean isRestocking) {
        assert item != null : "A quantity confirmation requires a pantry item";
        String message = isRestocking ? "Restocked the pantry: 📈" : "Used from the pantry: 📉";
        printLine(message);
        printLine("  " + item.toDisplayString());
        printLine("Quantity changed by " + amount + ".");
    }

    /** Displays confirmation that an entry moved to a different location. */
    public void showMovedItem(PantryItem item) {
        assert item != null : "A move confirmation requires a pantry item";
        printLine("Moved within the home: 🚚");
        printLine("  " + item.toDisplayString());
    }

    /** Displays confirmation for a deleted inventory entry. */
    public void showDeletedItem(PantryItem item, int entryCount) {
        assert item != null : "A deletion confirmation requires a pantry item";
        assert entryCount >= 0 : "A deletion confirmation requires a non-negative count";
        printLine("Removed from the pantry: 🗑️");
        printLine("  " + item.toDisplayString());
        printLine("That leaves " + entryCount + " pantry entries tracked. ✨");
    }

    /** Writes one output line; subclasses can redirect the output destination. */
    protected void printLine(String line) {
        System.out.println(line);
    }
}
