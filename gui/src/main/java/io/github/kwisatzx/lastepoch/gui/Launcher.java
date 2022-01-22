package io.github.kwisatzx.lastepoch.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

public class Launcher extends Application {
    public final String VERSION = "0.9.2";

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(loadFXML("main_window"));
        stage.setScene(scene);
        stage.setTitle("Last Epoch Single Player Data Editor by KwisatzX v" + VERSION);
        stage.show();
    }

    public static Parent loadFXML(String fxml) throws IOException {
        InputStream fxmlStream = Launcher.class.getResourceAsStream("/fxml/" + fxml + ".fxml");
        return new FXMLLoader().load(fxmlStream);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
