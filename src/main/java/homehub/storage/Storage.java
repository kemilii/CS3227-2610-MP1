package homehub.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;

import homehub.exception.HomeHubException;
import homehub.model.PantryItem;
import homehub.model.PantryList;

/** Reads and writes HomeHub pantry inventory to a configured local data file. */
public class Storage {
    private static final String PANTRY_TYPE = "P";
    private static final int LEGACY_FIELD_COUNT = 5;
    private static final int FIELD_COUNT = 8;

    private final Path filePath;

    /** Creates storage backed by the supplied file path. */
    public Storage(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("A storage file path is required.");
        }
        try {
            this.filePath = Path.of(filePath);
        } catch (InvalidPathException exception) {
            throw new IllegalArgumentException("The storage file path is invalid.", exception);
        }
    }

    /**
     * Saves the current pantry inventory, replacing the previous file contents.
     *
     * @param pantry pantry entries to save.
     * @throws HomeHubException if the file cannot be written.
     */
    public void save(PantryList pantry) throws HomeHubException {
        assert pantry != null : "Saving requires an initialized pantry list";
        ArrayList<PantryItem> items = pantry.asArrayList();
        ArrayList<String> lines = new ArrayList<>();
        if (containsDuplicateItemDetails(items)) {
            throw new HomeHubException("Pantry entries contain invalid or duplicate details and cannot be saved.");
        }
        for (PantryItem item : items) {
            assert item != null : "A pantry list must not contain null items when saved";
            if (containsInvalidText(item)) {
                throw new HomeHubException("Pantry entries contain invalid or duplicate details and cannot be saved.");
            }
            lines.add(item.toStorageString());
        }
        try {
            Path parent = filePath.toAbsolutePath().normalize().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(filePath, lines);
        } catch (IOException | SecurityException exception) {
            throw new HomeHubException("I couldn't save your pantry inventory to disk.");
        }
    }

    /**
     * Loads pantry entries from the local data file; a missing file means an empty pantry.
     *
     * @return the loaded pantry entries.
     * @throws HomeHubException if the file cannot be read.
     */
    public ArrayList<PantryItem> load() throws HomeHubException {
        ArrayList<PantryItem> items = new ArrayList<>();
        try {
            if (!Files.exists(filePath)) {
                return items;
            }
            for (String line : Files.readAllLines(filePath)) {
                PantryItem item = parseItem(line);
                if (item != null) {
                    if (containsItemWithSameDetails(items, item)) {
                        throw new HomeHubException("Your saved pantry contains duplicate inventory entries.");
                    }
                    items.add(item);
                }
            }
            return items;
        } catch (IOException | SecurityException exception) {
            throw new HomeHubException("I couldn't load your pantry inventory.");
        }
    }

    private PantryItem parseItem(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        String[] fields = line.split("\\s*\\|\\s*", -1);
        if ((fields.length != LEGACY_FIELD_COUNT && fields.length != FIELD_COUNT) || !PANTRY_TYPE.equals(fields[0])
                || fields[1].trim().isEmpty() || fields[2].trim().isEmpty()
                || fields[3].trim().isEmpty() || fields[4].trim().isEmpty()) {
            return null;
        }
        try {
            int quantity = Integer.parseInt(fields[2].trim());
            String category = fields.length == FIELD_COUNT ? fields[5].trim() : PantryItem.DEFAULT_CATEGORY;
            String location = fields.length == FIELD_COUNT ? fields[6].trim() : PantryItem.DEFAULT_LOCATION;
            int minimumQuantity = fields.length == FIELD_COUNT ? Integer.parseInt(fields[7].trim()) : 0;
            PantryItem item = new PantryItem(fields[1].trim(), quantity, fields[3].trim(), fields[4].trim(),
                    category, location, minimumQuantity);
            if (containsControlCharacters(fields[1]) || containsControlCharacters(fields[3])
                    || containsControlCharacters(category) || containsControlCharacters(location)) {
                return null;
            }
            return item;
        } catch (HomeHubException | NumberFormatException exception) {
            return null;
        }
    }

    private boolean containsItemWithSameDetails(ArrayList<PantryItem> items, PantryItem candidate) {
        for (PantryItem item : items) {
            if (item.hasSameDetailsAs(candidate)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsDuplicateItemDetails(ArrayList<PantryItem> items) {
        for (int firstIndex = 0; firstIndex < items.size(); firstIndex++) {
            for (int secondIndex = firstIndex + 1; secondIndex < items.size(); secondIndex++) {
                if (items.get(firstIndex).hasSameDetailsAs(items.get(secondIndex))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean containsInvalidText(PantryItem item) {
        return containsInvalidText(item.getName()) || containsInvalidText(item.getUnit());
    }

    private boolean containsInvalidText(String value) {
        return value == null || value.trim().isEmpty() || containsControlCharacters(value)
                || value.contains("|");
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
