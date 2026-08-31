package homehub;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import javafx.application.Application;

/** Tests the structure of the JavaFX application entry points. */
class MainTest {
    @Test
    void main_extendsApplication_isJavaFxApplication() {
        assertEquals(Application.class, Main.class.getSuperclass());
    }
}
