package io.github.kwisatzx.lastepoch.gui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class LauncherTest {

    @Test
    void loadFXML() throws IOException {
        List<String> fileNames = List.of("main_window.fxml", "crafting_pane_stash.fxml", "crafting_pane_editor.fxml");
        for (String fileName : fileNames) {
            InputStream fxmlStream = Launcher.class.getResourceAsStream("/fxml/" + fileName);
            assertNotNull(fxmlStream);
//            Object root = new FXMLLoader().load(fxmlStream);
//            assertTrue(root instanceof Parent);
        }
    }
}