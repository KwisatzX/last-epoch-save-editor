package io.github.kwisatzx.lastepoch.gui;

import io.github.kwisatzx.lastepoch.fileoperations.CharacterOperations;
import io.github.kwisatzx.lastepoch.fileoperations.FileHandler;
import io.github.kwisatzx.lastepoch.fileoperations.Selectable;
import io.github.kwisatzx.lastepoch.gui.tabcontrollers.GuiCharactersTab;
import io.github.kwisatzx.lastepoch.gui.tabcontrollers.TreeViewHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;

import java.io.IOException;
import java.util.Locale;

public class MainController {
    private static MainController INSTANCE;
    private TreeViewHandler treeViewHandlerReference;
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

    public static MainController getInstance() {
        return INSTANCE;
    }

    @FXML
    private void initialize() throws IOException {
        INSTANCE = this;
        new GuiCharactersTab(charactersTabAnchorPane);
        if (FileHandler.getCharacterFileList().isEmpty())
            leftBottomLabel.setText("Error: Failed to load or locate character files. Load data manually.");
        treeViewHandlerReference = new TreeViewHandler(treeView, tabPane);
        stashTabAnchorPane.getChildren().addAll(Launcher.loadFXML("crafting_pane_stash"));
        editorTabAnchorPane.getChildren().addAll(Launcher.loadFXML("crafting_pane_editor"));
    }

    public void setBottomRightText(String text) {
        if (text.toLowerCase(Locale.ROOT).contains("error")) rightBottomLabel.setTextFill(Paint.valueOf("#ff3333"));
        else rightBottomLabel.setTextFill(Paint.valueOf("#000000"));
        rightBottomLabel.setText(text);
    }

    public void setBottomLeftText(String text) {
        leftBottomLabel.setText(text);
    }

    public TreeViewHandler getTreeViewHandlerReference() {
        return treeViewHandlerReference;
    }

    public TabPane getTabPane() {
        return tabPane;
    }

    public void loadSaveData(ActionEvent actionEvent) {
    }

    public void loadBackup(ActionEvent actionEvent) {
    }

    public void openSaveFolder(ActionEvent actionEvent) {
        setBottomRightText("Error: test");
    }

    public void expandFiles(ActionEvent actionEvent) {
        for (CharacterOperations chara : FileHandler.getCharacterFileList()) {
            chara.expandFile();
        }
        FileHandler.getStashFile().expandFile();
        setBottomLeftText("Files expanded and saved.");
    }

    public void saveCharacters(ActionEvent actionEvent) {
        boolean success = true;
        for (CharacterOperations chara : FileHandler.getCharacterFileList()) {
            if (chara.isModified()) {
                if (!chara.saveToFile()) success = false;
            }
        }
        if (success) setBottomLeftText("Character changes saved to file!");
        else setBottomRightText("Error: could not save character changes to file. See log.");
    }

    public void saveStash(ActionEvent actionEvent) {
        if (FileHandler.getStashFile().isModified()) FileHandler.getStashFile().saveToFile();
        setBottomLeftText("Stash changes saved to file!");
    }
}

