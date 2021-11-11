package io.github.kwisatzx.lastepoch.gui.controllers;

import io.github.kwisatzx.lastepoch.fileoperations.FileHandler;
import io.github.kwisatzx.lastepoch.fileoperations.Selectable;
import io.github.kwisatzx.lastepoch.gui.Launcher;
import io.github.kwisatzx.lastepoch.gui.models.RootModel;
import io.github.kwisatzx.lastepoch.gui.views.RootView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class RootController {
    private static RootController instance;
    private RootView rootView;
    private RootModel rootModel;
    @FXML
    private TabPane tabPane;
    @FXML
    private AnchorPane charactersTabAnchorPane;
    @FXML
    private AnchorPane stashTabAnchorPane;
    @FXML
    private AnchorPane editorTabAnchorPane;
    @FXML
    private AnchorPane uniquesTabAnchorPane;
    @FXML
    private TreeView<Selectable> treeView;
    @FXML
    private Label leftBottomLabel;
    @FXML
    private Label rightBottomLabel;

    public static RootController getInstance() {
        return instance;
    }

    @FXML
    private void initialize() throws IOException {
        instance = this;
        rootView = new RootView(leftBottomLabel, rightBottomLabel);
        rootModel = new RootModel();
        new CharactersTabController(charactersTabAnchorPane);
        if (FileHandler.getCharacterFileList().isEmpty())
            leftBottomLabel.setText("Error: Failed to load or locate character files. Load data manually.");
        new TreeViewController(treeView, tabPane);
        stashTabAnchorPane.getChildren().addAll(Launcher.loadFXML("crafting_pane_stash"));
        editorTabAnchorPane.getChildren().addAll(Launcher.loadFXML("crafting_pane_editor"));
    }

    public void setBottomRightText(String text) {
        rootView.setBottomRightText(text);
    }

    public void setBottomLeftText(String text) {
        rootView.setBottomLeftText(text);
    }

    public TabPane getTabPane() {
        return tabPane;
    }

    public void loadSaveData(ActionEvent actionEvent) {
    }

    public void loadBackup(ActionEvent actionEvent) {
    }

    public void openSaveFolder(ActionEvent actionEvent) {
    }

    public void expandFiles(ActionEvent actionEvent) {
        rootModel.expandFiles();
        setBottomLeftText("Files expanded and saved.");
    }

    public void saveCharacters(ActionEvent actionEvent) {
        boolean success = rootModel.saveCharacters();
        if (success) setBottomLeftText("Character changes saved to file!");
        else setBottomLeftText("Error: could not save character changes to file. See log.");
    }

    public void saveStash(ActionEvent actionEvent) {
        rootModel.saveStash();
        setBottomLeftText("Stash changes saved to file!");
    }
}

