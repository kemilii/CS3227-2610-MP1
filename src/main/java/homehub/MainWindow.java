package homehub;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/** Controls the HomeHub conversation view defined in FXML. */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private HomeHub homeHub;
    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/homeowner.png"));
    private final Image homeHubImage = new Image(this.getClass().getResourceAsStream("/images/homehub.png"));

    /** Binds the conversation scroll position to the height of its contents. */
    @FXML
    public void initialize() {
        assert scrollPane != null : "MainWindow.fxml must inject the scroll pane";
        assert dialogContainer != null : "MainWindow.fxml must inject the dialog container";
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the HomeHub instance used to generate responses.
     *
     * @param homeHub HomeHub response generator.
     */
    public void setHomeHub(HomeHub homeHub) {
        this.homeHub = homeHub;
    }

    /**
     * Adds the user's message and HomeHub's response to the conversation.
     * Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        assert homeHub != null : "A HomeHub response generator must be injected before input is handled";
        assert userInput != null : "MainWindow.fxml must inject the user input field";
        String input = userInput.getText();
        String response = homeHub.getResponse(input);
        String commandType = homeHub.getCommandType();
        assert commandType != null : "Every HomeHub response must have a command type for styling";
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getHomeHubDialog(response, homeHubImage, commandType));
        userInput.clear();
        if (homeHub.isExitRequested()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }
}
