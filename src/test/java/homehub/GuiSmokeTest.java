package homehub;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import homehub.storage.Storage;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

@Tag("gui")
class GuiSmokeTest {
    private static final long UI_TIMEOUT_SECONDS = 10;

    private static Stage stage;
    private static VBox dialogContainer;
    private static TextField userInput;
    private static Button sendButton;

    @TempDir
    Path temporaryDirectory;

    @BeforeAll
    static void startJavaFxToolkit() throws Exception {
        CountDownLatch startupLatch = new CountDownLatch(1);
        Platform.startup(startupLatch::countDown);
        assertTrue(startupLatch.await(UI_TIMEOUT_SECONDS, TimeUnit.SECONDS),
                "JavaFX toolkit did not start within the timeout");
    }

    @AfterAll
    static void stopJavaFxToolkit() throws Exception {
        runOnFxThread(() -> {
            if (stage != null) {
                stage.close();
            }
            Platform.exit();
        });
    }

    @Test
    void guiCommandFlow_addListHelpInvalidInputAndBye_updatesConversation() throws Exception {
        loadMainWindow();

        submit("add rice /qty 2 /unit kg /expires 2099-12-31");
        assertConversationContains("Added to the pantry", "rice — 2 kg");

        submit("list");
        assertConversationContains("Keke's pantry inventory", "rice — 2 kg");

        submit("help");
        assertConversationContains("Keke's pantry command menu", "add", "<name> /qty");

        submit("consume 1 3");
        assertConversationContains("cannot consume more than the available stock");

        submit("bye");
        assertConversationContains("Pantry secured. See you soon!");
        assertTrue(sendButton.isDisabled(), "Send button should be disabled after bye");
        assertTrue(userInput.isDisabled(), "Input field should be disabled after bye");
    }

    private void loadMainWindow() throws Exception {
        runOnFxThread(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
                AnchorPane root = loader.load();
                MainWindow mainWindow = loader.getController();
                mainWindow.setHomeHub(new HomeHub(
                        new Storage(temporaryDirectory.resolve("pantry.txt").toString())));
                stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
                dialogContainer = findNode(root, "dialogContainer", VBox.class);
                userInput = findNode(root, "userInput", TextField.class);
                sendButton = findNode(root, "sendButton", Button.class);
            } catch (IOException exception) {
                throw new IllegalStateException("Unable to load the HomeHub smoke-test window.", exception);
            }
        });
        assertNotNull(dialogContainer);
        assertNotNull(userInput);
        assertNotNull(sendButton);
    }

    private void submit(String command) throws Exception {
        runOnFxThread(() -> {
            userInput.setText(command);
            sendButton.fire();
        });
        assertTrue(userInput.getText().isEmpty(), "The input field should clear after submission");
    }

    private void assertConversationContains(String... expectedFragments) throws Exception {
        AtomicReference<String> conversation = new AtomicReference<>();
        runOnFxThread(() -> conversation.set(readText(dialogContainer)));
        for (String expectedFragment : expectedFragments) {
            String actualConversation = conversation.get();
            String failureMessage = "Conversation did not contain: " + expectedFragment
                    + "\nActual conversation:\n" + actualConversation;
            assertTrue(actualConversation.contains(expectedFragment), failureMessage);
        }
    }

    private static <T extends Node> T findNode(Parent root, String id, Class<T> nodeType) {
        Node node = root.lookup("#" + id);
        assertNotNull(node, "Missing GUI node: " + id);
        assertTrue(nodeType.isInstance(node), "GUI node has unexpected type: " + id);
        return nodeType.cast(node);
    }

    private static String readText(Node node) {
        StringBuilder text = new StringBuilder();
        appendText(node, text);
        return text.toString();
    }

    private static void appendText(Node node, StringBuilder text) {
        if (node instanceof Label label) {
            text.append(label.getText()).append('\n');
            return;
        }
        if (node instanceof Text textNode) {
            text.append(textNode.getText()).append('\n');
            return;
        }
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                appendText(child, text);
            }
        }
    }

    private static void runOnFxThread(ThrowingRunnable action) throws Exception {
        CountDownLatch actionLatch = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Platform.runLater(() -> {
            try {
                action.run();
            } catch (Throwable throwable) {
                failure.set(throwable);
            } finally {
                actionLatch.countDown();
            }
        });
        assertTrue(actionLatch.await(UI_TIMEOUT_SECONDS, TimeUnit.SECONDS),
                "JavaFX action did not complete within the timeout");
        if (failure.get() != null) {
            throw new AssertionError("JavaFX action failed", failure.get());
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
