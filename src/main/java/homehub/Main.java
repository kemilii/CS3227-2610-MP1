package homehub;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Displays the HomeHub JavaFX user interface using an FXML view. */
public class Main extends Application {
    private final HomeHub homeHub = new HomeHub();

    /**
     * Loads and displays the HomeHub FXML view.
     *
     * @param stage the primary stage provided by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainLayout = fxmlLoader.load();
            stage.setScene(new Scene(mainLayout));
            fxmlLoader.<MainWindow>getController().setHomeHub(homeHub);
            stage.setTitle("HomeHub");
            stage.setResizable(false);
            stage.setMinHeight(600.0);
            stage.setMinWidth(400.0);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the HomeHub interface.", exception);
        }
    }
}
