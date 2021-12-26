package io.github.kwisatzx.lastepoch.gui.controllers;

import io.github.kwisatzx.lastepoch.fileoperations.FileHandler;
import io.github.kwisatzx.lastepoch.gui.Launcher;
import io.github.kwisatzx.lastepoch.gui.models.RootModel;
import io.github.kwisatzx.lastepoch.gui.views.RootView;
import io.github.kwisatzx.lastepoch.itemdata.Selectable;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
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
        new TreeController(treeView);
        new CharactersTabController(charactersTabAnchorPane);
        if (FileHandler.getCharacterFileList().isEmpty()) {
            leftBottomLabel.setText("Error: Failed to load or locate character files. Load data manually.");
        }
        stashTabAnchorPane.getChildren().addAll(Launcher.loadFXML("crafting_pane_stash"));
        editorTabAnchorPane.getChildren().addAll(Launcher.loadFXML("crafting_pane_editor"));
        initGuiTabsEnum();
        setUpEvents();
    }

    private void setUpEvents() {
        for (javafx.scene.control.Tab tab : tabPane.getTabs()) {
            tab.setOnSelectionChanged(changeEvent -> TreeController.getInstance().setRootByTab());
        }

        GuiTabs.EDITOR_TAB.getTab().setOnSelectionChanged(event -> {
            if (!treeView.getSelectionModel().isEmpty()) {
                EditorTabController.getInstance().fillDataFields();
            }
        });
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

    public void switchToTab(GuiTabs tab) {
        tab.getController().fillDataFields();
        if (getSelectedTab() != tab.getTab()) tabPane.getSelectionModel().select(tab.getTab());
    }

    public void switchToSelectedTab() {
        for (GuiTabs guiTab : GuiTabs.values()) {
            if (guiTab.getTab() == getSelectedTab()) {
                switchToTab(guiTab);
            }
        }
    }

    public Tab getSelectedTab() {
        return tabPane.getSelectionModel().getSelectedItem();
    }

    private void initGuiTabsEnum() {
        ObservableList<Tab> tabs = tabPane.getTabs();

        GuiTabs.CHARACTER_TAB.setTab(tabs.get(0));
        GuiTabs.CHARACTER_TAB.setController(CharactersTabController.getInstance());
        GuiTabs.STASH_TAB.setTab(tabs.get(1));
        GuiTabs.STASH_TAB.setController(StashTabController.getInstance());
        GuiTabs.EDITOR_TAB.setTab(tabs.get(2));
        GuiTabs.EDITOR_TAB.setController(EditorTabController.getInstance());
        GuiTabs.UNIQUES_TAB.setTab(tabs.get(3));
//        GuiTabs.UNIQUES_TAB.setController();
    }

    public enum GuiTabs {
        CHARACTER_TAB(),
        STASH_TAB,
        EDITOR_TAB,
        UNIQUES_TAB;

        private Tab tab;
        private GuiTab controller;

        public Tab getTab() {
            return tab;
        }

        public void setTab(Tab tab) {
            this.tab = tab;
        }

        public GuiTab getController() {
            return controller;
        }

        public void setController(GuiTab controller) {
            this.controller = controller;
        }
    }
}

