package homehub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import javafx.application.Application;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

/** Tests the structure of the JavaFX application entry points. */
class MainTest {
    @Test
    void main_extendsApplication_isJavaFxApplication() {
        assertEquals(Application.class, Main.class.getSuperclass());
    }

    @Test
    void dialogBox_extendsHBox_isReusableConversationControl() {
        assertEquals(HBox.class, DialogBox.class.getSuperclass());
    }

    @Test
    void mainWindow_extendsAnchorPane_isFxmlController() {
        assertEquals(AnchorPane.class, MainWindow.class.getSuperclass());
    }

    @Test
    void homeHubResponse_echoesUserMessage() {
        assertEquals("HomeHub heard: list", new HomeHub().getResponse("list"));
    }

    @Test
    void homeHubResponse_recordsCommandTypeForStyling() {
        HomeHub homeHub = new HomeHub();

        homeHub.getResponse("todo wash dishes");

        assertEquals("TODO", homeHub.getCommandType());
    }

    @Test
    void avatarResources_areAvailableOnClasspath() {
        assertNotNull(Main.class.getResourceAsStream("/images/homeowner.png"));
        assertNotNull(Main.class.getResourceAsStream("/images/homehub.png"));
    }

    @Test
    void fxmlResources_areAvailableOnClasspath() {
        assertNotNull(Main.class.getResourceAsStream("/view/MainWindow.fxml"));
        assertNotNull(Main.class.getResourceAsStream("/view/DialogBox.fxml"));
    }

    @Test
    void stylesheetResources_areAvailableOnClasspath() {
        assertNotNull(Main.class.getResourceAsStream("/css/main.css"));
        assertNotNull(Main.class.getResourceAsStream("/css/dialog-box.css"));
    }
}
