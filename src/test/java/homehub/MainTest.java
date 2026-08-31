package homehub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import homehub.command.CommandType;
import homehub.storage.Storage;
import javafx.application.Application;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

/** Tests the structure of the JavaFX application entry points. */
class MainTest {
    @TempDir
    Path temporaryDirectory;

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
    void homeHubResponse_listsCurrentTasks() {
        HomeHub homeHub = new HomeHub(new Storage(temporaryDirectory.resolve("homehub.txt").toString()));

        assertEquals("Moss's household board:", homeHub.getResponse("list"));
    }

    @Test
    void homeHubResponse_recordsCommandTypeForStyling() {
        HomeHub homeHub = new HomeHub(new Storage(temporaryDirectory.resolve("styling-homehub.txt").toString()));

        homeHub.getResponse("todo wash dishes");

        assertEquals(CommandType.TODO, homeHub.getCommandType());
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

    @Test
    void mainWindowLayout_supportsResponsivePolishedConversation() throws IOException {
        String fxml;
        try (InputStream inputStream = Main.class.getResourceAsStream("/view/MainWindow.fxml")) {
            assertNotNull(inputStream);
            fxml = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }

        assertTrue(fxml.contains("minWidth=\"320.0\""));
        assertTrue(fxml.contains("minHeight=\"420.0\""));
        assertTrue(fxml.contains("HBox.hgrow=\"ALWAYS\""));
        assertTrue(fxml.contains("styleClass=\"welcome-card\""));
        assertTrue(fxml.contains("Moss · your calm household concierge"));
        assertTrue(fxml.contains("Ask Moss:"));
    }

    @Test
    void mossPersona_hasDistinctIdentityAndErrorVoice() {
        assertEquals("Moss", Moss.NAME);
        assertEquals("your calm household concierge", Moss.ROLE);
        assertEquals("Moss says: ", Moss.ERROR_PREFIX);
    }
}
