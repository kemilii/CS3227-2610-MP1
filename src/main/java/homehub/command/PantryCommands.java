package homehub.command;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import homehub.exception.HomeHubException;
import homehub.model.PantryItem;
import homehub.model.PantryList;
import homehub.storage.Storage;
import homehub.ui.Ui;

/** Parses, validates, and persists commands that add pantry inventory. */
public class PantryCommands {
    private static final Pattern FIELD_MARKER = Pattern.compile(
            "[ \\t]+/(qty|unit|expires|category|location|min)[ \\t]+");
    private static final String[] REQUIRED_FIELDS = {"qty", "unit", "expires"};
    private static final String[] OPTIONAL_FIELDS = {"category", "location", "min"};

    private final Storage storage;
    private final Ui ui;

    /** Creates a pantry-command handler with its persistence and presentation collaborators. */
    public PantryCommands(Storage storage, Ui ui) {
        assert storage != null : "Pantry commands require initialized storage";
        assert ui != null : "Pantry commands require initialized user interface";
        this.storage = storage;
        this.ui = ui;
    }

    /**
     * Adds an inventory entry and optional category, location, and low-stock metadata.
     *
     * @param items pantry list to update.
     * @param content command content after {@code add}.
     * @throws HomeHubException if the syntax, quantity, date, or persistence operation is invalid.
     */
    public void addItem(PantryList items, String content) throws HomeHubException {
        assert items != null : "Adding an item requires an initialized pantry list";
        if (content == null) {
            throw usageError();
        }
        ArrayList<String> values = extractFields(content);
        PantryItem item;
        try {
            int quantity = Integer.parseInt(values.get(0));
            int minimumQuantity = Integer.parseInt(values.get(6));
            String category = values.get(4);
            String location = values.get(5);
            item = new PantryItem(values.get(3), quantity, values.get(1), values.get(2), category, location,
                    minimumQuantity);
        } catch (NumberFormatException exception) {
            throw new HomeHubException("Quantity must be a positive whole number.");
        }
        if (items.hasItemWithSameDetails(item)) {
            throw new HomeHubException("That pantry entry is already being tracked.");
        }
        int originalSize = items.size();
        items.add(item);
        try {
            storage.save(items);
        } catch (HomeHubException exception) {
            items.remove(originalSize);
            throw exception;
        }
        ui.showAddedItem(item, items.size());
    }

    private ArrayList<String> extractFields(String content) throws HomeHubException {
        Matcher matcher = FIELD_MARKER.matcher(content);
        ArrayList<String> fieldNames = new ArrayList<>();
        ArrayList<Integer> starts = new ArrayList<>();
        ArrayList<Integer> ends = new ArrayList<>();
        while (matcher.find()) {
            fieldNames.add(matcher.group(1));
            starts.add(matcher.start());
            ends.add(matcher.end());
        }
        if (fieldNames.size() < REQUIRED_FIELDS.length || starts.isEmpty() || starts.get(0) == 0) {
            throw usageError();
        }
        for (int index = 0; index < REQUIRED_FIELDS.length; index++) {
            if (!REQUIRED_FIELDS[index].equals(fieldNames.get(index))) {
                throw usageError();
            }
        }
        int nextOptionalIndex = 0;
        for (int index = REQUIRED_FIELDS.length; index < fieldNames.size(); index++) {
            while (nextOptionalIndex < OPTIONAL_FIELDS.length
                    && !OPTIONAL_FIELDS[nextOptionalIndex].equals(fieldNames.get(index))) {
                nextOptionalIndex++;
            }
            if (nextOptionalIndex == OPTIONAL_FIELDS.length) {
                throw usageError();
            }
            nextOptionalIndex++;
        }
        ArrayList<String> fieldValues = new ArrayList<>();
        for (int index = 0; index < fieldNames.size(); index++) {
            int valueEnd = index + 1 < starts.size() ? starts.get(index + 1) : content.length();
            fieldValues.add(content.substring(ends.get(index), valueEnd).trim());
        }
        String itemName = content.substring(0, starts.get(0)).trim();
        ArrayList<String> values = new ArrayList<>();
        values.add(fieldValues.get(0));
        values.add(fieldValues.get(1));
        values.add(fieldValues.get(2));
        values.add(itemName);
        values.add(PantryItem.DEFAULT_CATEGORY);
        values.add(PantryItem.DEFAULT_LOCATION);
        values.add("0");
        for (int index = REQUIRED_FIELDS.length; index < fieldNames.size(); index++) {
            String fieldName = fieldNames.get(index);
            String value = fieldValues.get(index);
            switch (fieldName) {
                case "category":
                    values.set(4, value);
                    break;
                case "location":
                    values.set(5, value);
                    break;
                case "min":
                    values.set(6, value);
                    break;
                default:
                    throw usageError();
            }
        }
        for (String value : values) {
            if (value.isEmpty() || value.contains("|") || containsControlCharacters(value)) {
                throw usageError();
            }
        }
        return values;
    }

    private HomeHubException usageError() {
        return new HomeHubException("Use: add <name> /qty <number> /unit <unit> /expires <yyyy-MM-dd> "
                + "[/category <category>] [/location <location>] [/min <quantity>].");
    }

    private boolean containsControlCharacters(String value) {
        for (int index = 0; index < value.length(); index++) {
            if (Character.isISOControl(value.charAt(index))) {
                return true;
            }
        }
        return false;
    }
}
