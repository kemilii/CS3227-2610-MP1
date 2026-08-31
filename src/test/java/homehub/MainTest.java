package homehub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import javafx.application.Application;
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
    void avatarResources_areAvailableOnClasspath() {
        assertNotNull(Main.class.getResourceAsStream("/images/homeowner.png"));
        assertNotNull(Main.class.getResourceAsStream("/images/homehub.png"));
    }
}
