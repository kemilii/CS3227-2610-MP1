package homehub;

import java.time.LocalDate;

import homehub.command.CommandType;
import homehub.command.PantryCommands;
import homehub.command.ParsedCommand;
import homehub.command.Parser;
import homehub.exception.HomeHubException;
import homehub.model.ExpiryDate;
import homehub.model.PantryItem;
import homehub.model.PantryList;
import homehub.storage.Storage;
import homehub.ui.Ui;

/** A household pantry inventory manager with command-line and graphical interfaces. */
public class HomeHub {
    private static final String DEFAULT_STORAGE_PATH = "data/pantry.txt";

    private final Parser parser;
    private final Storage storage;
    private PantryList pantry;
    private CommandType commandType;
    private boolean exitRequested;
    private String startupError;

    /** Creates a HomeHub instance backed by the default pantry file. */
    public HomeHub() {
        this(new Storage(DEFAULT_STORAGE_PATH), null);
    }

    /** Creates a HomeHub instance backed by the supplied storage service. */
    public HomeHub(Storage storage) {
        this(storage, null);
    }

    /** Creates a HomeHub instance and reports loading errors through the supplied UI. */
    public HomeHub(Storage storage, Ui startupUi) {
        assert storage != null : "HomeHub requires initialized storage";
        parser = new Parser();
        this.storage = storage;
        exitRequested = false;
        try {
            pantry = new PantryList(storage.load());
        } catch (HomeHubException exception) {
            pantry = new PantryList();
            if (startupUi == null) {
                startupError = exception.getMessage();
            } else {
                startupUi.showError(exception.getMessage());
            }
        }
    }

    /** Processes a command entered in the graphical interface and returns its response. */
    public String getResponse(String input) {
        ResponseUi responseUi = new ResponseUi();
        if (startupError != null) {
            responseUi.showError(startupError);
            startupError = null;
        }
        if (input == null) {
            commandType = CommandType.UNKNOWN;
            responseUi.showError(unknownCommandMessage());
            return responseUi.getResponse();
        }
        try {
            executeCommand(input, responseUi);
        } catch (HomeHubException exception) {
            responseUi.showError(exception.getMessage());
        } catch (RuntimeException exception) {
            responseUi.showError("HomeHub could not process that input.");
        }
        return responseUi.getResponse();
    }

    /** Returns the command type from the most recent graphical-interface input. */
    public CommandType getCommandType() {
        return commandType;
    }

    /** Returns whether the most recent command requested that HomeHub exit. */
    public boolean isExitRequested() {
        return exitRequested;
    }

    /** Runs the HomeHub command-line application. */
    public static void main(String[] args) {
        Ui ui = new Ui();
        HomeHub homeHub = new HomeHub(new Storage(DEFAULT_STORAGE_PATH), ui);
        ui.showWelcome();

        String command;
        while ((command = ui.readCommand()) != null) {
            ui.showSeparator();
            try {
                homeHub.executeCommand(command, ui);
                if (homeHub.isExitRequested()) {
                    break;
                }
            } catch (HomeHubException exception) {
                ui.showError(exception.getMessage());
            } catch (RuntimeException exception) {
                ui.showError("HomeHub could not process that input.");
            }
            ui.showSeparator();
        }
    }

    private void executeCommand(String input, Ui ui) throws HomeHubException {
        ParsedCommand parsedCommand = parser.parse(input);
        commandType = parsedCommand.type();
        PantryCommands pantryCommands = new PantryCommands(storage, ui);
        switch (parsedCommand.type()) {
            case BYE:
                requireNoArguments(parsedCommand.arguments(), "bye");
                exitRequested = true;
                ui.showGoodbye();
                break;
            case LIST:
                requireNoArguments(parsedCommand.arguments(), "list");
                ui.showPantry(pantry);
                break;
            case ADD:
                pantryCommands.addItem(pantry, parsedCommand.arguments());
                break;
            case SEARCH:
                requireArgument(parsedCommand.arguments(), "search", "a keyword");
                ui.showMatchingItems(pantry.findMatchingItems(parsedCommand.arguments()));
                break;
            case EXPIRING:
                showExpiringItems(parsedCommand.arguments(), ui);
                break;
            case LOW_STOCK:
                requireNoArguments(parsedCommand.arguments(), "lowstock");
                ui.showLowStockItems(pantry.findLowStockItems());
                break;
            case SUMMARY:
                requireNoArguments(parsedCommand.arguments(), "summary");
                ui.showSummary(pantry);
                break;
            case MOVE:
                moveItem(parsedCommand.arguments(), ui);
                break;
            case RESTOCK:
                changeQuantity(parsedCommand.arguments(), true, ui);
                break;
            case CONSUME:
                changeQuantity(parsedCommand.arguments(), false, ui);
                break;
            case DELETE:
                deleteItem(parsedCommand.arguments(), ui);
                break;
            case HELP:
                requireNoArguments(parsedCommand.arguments(), "help");
                ui.showHelp();
                break;
            default:
                throw new HomeHubException(unknownCommandMessage());
        }
    }

    private void showExpiringItems(String arguments, Ui ui) throws HomeHubException {
        requireArgument(arguments, "expiring", "a cutoff date");
        LocalDate cutoff = ExpiryDate.parse(arguments);
        ui.showExpiringItems(pantry.findExpiringItems(cutoff), cutoff);
    }

    private void changeQuantity(String arguments, boolean isRestocking, Ui ui) throws HomeHubException {
        String[] fields = arguments.trim().split("\\s+");
        if (fields.length != 2 || fields[0].isEmpty() || fields[1].isEmpty()) {
            String action = isRestocking ? "restock" : "consume";
            throw new HomeHubException("Use: " + action + " <item number> <positive quantity>.");
        }
        int itemNumber;
        int amount;
        try {
            itemNumber = Integer.parseInt(fields[0]);
            amount = Integer.parseInt(fields[1]);
        } catch (NumberFormatException exception) {
            throw new HomeHubException("Item number and quantity must be whole numbers.");
        }
        PantryItem item = getItem(itemNumber);
        int previousQuantity = item.getQuantity();
        if (isRestocking) {
            item.restock(amount);
        } else {
            item.consume(amount);
        }
        try {
            storage.save(pantry);
        } catch (HomeHubException exception) {
            item.setQuantity(previousQuantity);
            throw exception;
        }
        ui.showQuantityChanged(item, amount, isRestocking);
    }

    private void moveItem(String arguments, Ui ui) throws HomeHubException {
        String[] fields = arguments.trim().split("\\s+");
        if (fields.length != 2 || fields[0].isEmpty() || fields[1].isEmpty()) {
            throw new HomeHubException("Use: move <item number> <location>.");
        }
        int itemNumber;
        try {
            itemNumber = Integer.parseInt(fields[0]);
        } catch (NumberFormatException exception) {
            throw new HomeHubException("The item number must be a whole number.");
        }
        PantryItem item = getItem(itemNumber);
        String previousLocation = item.getLocation();
        item.moveTo(fields[1]);
        try {
            storage.save(pantry);
        } catch (HomeHubException exception) {
            item.moveTo(previousLocation);
            throw exception;
        }
        ui.showMovedItem(item);
    }

    private void deleteItem(String arguments, Ui ui) throws HomeHubException {
        if (arguments.trim().isEmpty()) {
            throw new HomeHubException("Please provide an item number after delete.");
        }
        int itemNumber;
        try {
            itemNumber = Integer.parseInt(arguments.trim());
        } catch (NumberFormatException exception) {
            throw new HomeHubException("The item number must be a whole number.");
        }
        PantryItem removedItem = getItem(itemNumber);
        pantry.remove(itemNumber - 1);
        try {
            storage.save(pantry);
        } catch (HomeHubException exception) {
            pantry.add(itemNumber - 1, removedItem);
            throw exception;
        }
        ui.showDeletedItem(removedItem, pantry.size());
    }

    private PantryItem getItem(int itemNumber) throws HomeHubException {
        if (itemNumber < 1 || itemNumber > pantry.size()) {
            throw new HomeHubException("That pantry item number does not exist.");
        }
        return pantry.get(itemNumber - 1);
    }

    private void requireNoArguments(String arguments, String command) throws HomeHubException {
        if (!arguments.isEmpty()) {
            throw new HomeHubException("The " + command + " command does not take arguments.");
        }
    }

    private void requireArgument(String arguments, String command, String expected) throws HomeHubException {
        if (arguments.trim().isEmpty()) {
            throw new HomeHubException("Please provide " + expected + " after " + command + ".");
        }
    }

    private String unknownCommandMessage() {
        return "That command is not supported yet. Try add, list, search, restock, consume, "
                + "expiring, lowstock, summary, move, delete, or help.";
    }

    /** Captures command output for display in a GUI response bubble. */
    private static final class ResponseUi extends Ui {
        private final StringBuilder response = new StringBuilder();

        @Override
        public void showGoodbye() {
            printLine("Pantry secured. See you soon! 👋");
        }

        @Override
        protected void printLine(String line) {
            if (response.length() > 0) {
                response.append(System.lineSeparator());
            }
            response.append(line);
        }

        /** Returns the captured response without a trailing line separator. */
        String getResponse() {
            return response.toString();
        }
    }
}
