package homehub.model;

import java.time.LocalDate;

import homehub.exception.HomeHubException;

/** Represents one stocked item in the household pantry. */
public class PantryItem {
    public static final String DEFAULT_CATEGORY = "general";
    public static final String DEFAULT_LOCATION = "pantry";
    private static final String TYPE_ICON = "P";

    private final String name;
    private int quantity;
    private final String unit;
    private final LocalDate expiryDate;
    private final String category;
    private String location;
    private final int minimumQuantity;

    /**
     * Creates a pantry item with a positive quantity and a required expiry date.
     *
     * @param name item name.
     * @param quantity number of units currently available.
     * @param unit measurement unit, such as kg or bottles.
     * @param expiryDateText expiry date in {@code yyyy-MM-dd} format.
     * @throws HomeHubException if the quantity or expiry date is invalid.
     */
    public PantryItem(String name, int quantity, String unit, String expiryDateText) throws HomeHubException {
        this(name, quantity, unit, expiryDateText, DEFAULT_CATEGORY, DEFAULT_LOCATION, 0);
    }

    /**
     * Creates a pantry item with stock-management metadata.
     *
     * @param name item name.
     * @param quantity number of units currently available.
     * @param unit measurement unit, such as kg or bottles.
     * @param expiryDateText expiry date in {@code yyyy-MM-dd} format.
     * @param category grouping such as dairy or baking.
     * @param location storage location such as pantry or freezer.
     * @param minimumQuantity quantity at or below which the item is low stock.
     * @throws HomeHubException if any item detail is invalid.
     */
    public PantryItem(String name, int quantity, String unit, String expiryDateText,
            String category, String location, int minimumQuantity) throws HomeHubException {
        if (name == null || name.trim().isEmpty()) {
            throw new HomeHubException("A pantry item needs a name.");
        }
        if (unit == null || unit.trim().isEmpty()) {
            throw new HomeHubException("A pantry item needs a measurement unit.");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new HomeHubException("A pantry item needs a category.");
        }
        if (location == null || location.trim().isEmpty()) {
            throw new HomeHubException("A pantry item needs a storage location.");
        }
        if (quantity <= 0) {
            throw new HomeHubException("Quantity must be a positive whole number.");
        }
        if (minimumQuantity < 0) {
            throw new HomeHubException("Minimum stock must be zero or a positive whole number.");
        }
        this.name = name.trim();
        this.quantity = quantity;
        this.unit = unit.trim();
        this.expiryDate = ExpiryDate.parse(expiryDateText);
        this.category = category.trim();
        this.location = location.trim();
        this.minimumQuantity = minimumQuantity;
    }

    /** Returns the item's name. */
    public String getName() {
        return name;
    }

    /** Returns the quantity currently in the pantry. */
    public int getQuantity() {
        return quantity;
    }

    /** Returns the item's measurement unit. */
    public String getUnit() {
        return unit;
    }

    /** Returns the item's expiry date. */
    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    /** Returns the item's category. */
    public String getCategory() {
        return category;
    }

    /** Returns the item's current storage location. */
    public String getLocation() {
        return location;
    }

    /** Returns the item's low-stock threshold. */
    public int getMinimumQuantity() {
        return minimumQuantity;
    }

    /** Increases the available quantity by a positive amount. */
    public void restock(int amount) throws HomeHubException {
        validateAmount(amount);
        if (amount > Integer.MAX_VALUE - quantity) {
            throw new HomeHubException("The resulting quantity is too large to track.");
        }
        quantity += amount;
    }

    /**
     * Decreases the available quantity without allowing stock to become negative.
     *
     * @param amount quantity to consume.
     * @throws HomeHubException if the amount is invalid or exceeds available stock.
     */
    public void consume(int amount) throws HomeHubException {
        validateAmount(amount);
        if (amount > quantity) {
            throw new HomeHubException("You cannot consume more than the available stock.");
        }
        quantity -= amount;
    }

    /** Restores a previously changed quantity when persistence fails. */
    public void setQuantity(int quantity) {
        assert quantity >= 0 : "A restored pantry quantity cannot be negative";
        this.quantity = quantity;
    }

    /** Moves this item to a new storage location. */
    public void moveTo(String location) throws HomeHubException {
        if (location == null || location.trim().isEmpty() || location.contains("|")) {
            throw new HomeHubException("A storage location cannot be empty or contain |.");
        }
        this.location = location.trim();
    }

    /** Returns whether the current quantity is at or below the low-stock threshold. */
    public boolean isLowStock() {
        return minimumQuantity > 0 && quantity <= minimumQuantity;
    }

    /** Returns whether this item's expiry date is on or before the supplied date. */
    public boolean expiresOnOrBefore(LocalDate date) {
        assert date != null : "Expiry comparisons require a date";
        return !expiryDate.isAfter(date);
    }

    /** Returns a compact pantry representation for the command-line and GUI views. */
    public String toDisplayString() {
        String metadata = "";
        if (!DEFAULT_CATEGORY.equalsIgnoreCase(category)
                || !DEFAULT_LOCATION.equalsIgnoreCase(location) || minimumQuantity > 0) {
            metadata = "; category: " + category + "; location: " + location + "; min: " + minimumQuantity;
        }
        return "[" + TYPE_ICON + "] " + name + " — " + quantity + " " + unit
                + " (expires: " + ExpiryDate.display(expiryDate) + metadata + ")";
    }

    /** Returns the pipe-delimited representation used by the pantry save file. */
    public String toStorageString() {
        return TYPE_ICON + " | " + name + " | " + quantity + " | " + unit + " | "
                + ExpiryDate.storage(expiryDate) + " | " + category + " | " + location + " | " + minimumQuantity;
    }

    /** Returns whether another item has the same name, unit, and expiry date. */
    public boolean hasSameDetailsAs(PantryItem other) {
        if (other == null) {
            return false;
        }
        return name.equalsIgnoreCase(other.name)
                && unit.equalsIgnoreCase(other.unit)
                && expiryDate.equals(other.expiryDate);
    }

    private void validateAmount(int amount) throws HomeHubException {
        if (amount <= 0) {
            throw new HomeHubException("Quantity must be a positive whole number.");
        }
    }
}
