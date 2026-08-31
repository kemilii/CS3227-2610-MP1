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
        String input = userInput.getText();
        String response = homeHub.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getHomeHubDialog(response, homeHubImage, homeHub.getCommandType()));
        userInput.clear();
    }
}
