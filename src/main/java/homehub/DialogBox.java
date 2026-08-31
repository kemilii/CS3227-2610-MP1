package homehub;

import java.io.IOException;
import java.util.Collections;

import homehub.command.CommandType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/** Displays a HomeHub conversation message alongside its avatar. */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box containing a message and avatar.
     *
     * @param message message to display.
     * @param image avatar to display beside the message.
     */
    private DialogBox(String message, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box interface.", exception);
        }

        assert dialog != null : "DialogBox.fxml must inject the dialog label";
        assert displayPicture != null : "DialogBox.fxml must inject the avatar image view";
        dialog.setText(message);
        displayPicture.setImage(image);
    }

    /** Flips this dialog box so that its image is displayed on the left. */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Applies a response style based on the command that generated it.
     *
     * @param commandType command type associated with the response.
     */
    private void changeDialogStyle(CommandType commandType) {
        assert commandType != null : "A response style requires a command type";
        switch (commandType) {
            case TODO, DEADLINE, EVENT:
                dialog.getStyleClass().add("add-label");
                break;
            case MARK, UNMARK:
                dialog.getStyleClass().add("marked-label");
                break;
            case DELETE:
                dialog.getStyleClass().add("delete-label");
                break;
            default:
                break;
        }
    }

    /**
     * Creates a dialog box for a user's message.
     *
     * @param message user's message.
     * @param image user's avatar.
     * @return a right-aligned user dialog box.
     */
    public static DialogBox getUserDialog(String message, Image image) {
        return new DialogBox(message, image);
    }

    /**
     * Creates a dialog box for HomeHub's response.
     *
     * @param message HomeHub's response.
     * @param image HomeHub's avatar.
     * @return a left-aligned HomeHub dialog box.
     */
    public static DialogBox getHomeHubDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates a styled dialog box for HomeHub's response.
     *
     * @param message HomeHub's response.
     * @param image HomeHub's avatar.
     * @param commandType command type associated with the response.
     * @return a left-aligned, command-styled HomeHub dialog box.
     */
    public static DialogBox getHomeHubDialog(String message, Image image, CommandType commandType) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.flip();
        dialogBox.changeDialogStyle(commandType);
        return dialogBox;
    }
}
