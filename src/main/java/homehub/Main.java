package homehub;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/** Displays a minimal JavaFX application containing a Hello World label. */
public class Main extends Application {
    /**
     * Creates and displays the application's primary scene.
     *
     * @param stage the primary stage provided by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        Label helloWorld = new Label("Hello World!");
        Scene scene = new Scene(helloWorld);
        stage.setScene(scene);
        stage.show();
    }
}
