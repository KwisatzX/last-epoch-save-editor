package io.github.kwisatzx.lastepoch.gui.controllers;

import io.github.kwisatzx.lastepoch.fileoperations.CharacterOperations;
import io.github.kwisatzx.lastepoch.fileoperations.Selectable;
import io.github.kwisatzx.lastepoch.gui.models.TreeModel;
import io.github.kwisatzx.lastepoch.gui.views.MyTreeView;
import io.github.kwisatzx.lastepoch.itemdata.Item;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class TreeController {
    private static TreeController instance;
    private final TreeModel treeModel;
    private final MyTreeView myTreeView;

    public TreeController(TreeView<Selectable> treeView, TabPane tabPane) {
        instance = this;
        treeModel = new TreeModel();
        myTreeView = new MyTreeView(treeView, tabPane);
        myTreeView.setStashListRoot(treeModel.createStashListRoot());
        myTreeView.setCharacterListRoot(treeModel.createCharacterListRoot());
        myTreeView.setRootByTab();
        setUpEvents(treeView, tabPane);
    }

    private void setUpEvents(TreeView<Selectable> treeView, TabPane tabPane) {
        treeView.setOnMouseClicked(event -> {
            TreeItem<Selectable> selection = treeView.getSelectionModel().getSelectedItem();
            String guiTabName = tabPane.getSelectionModel().getSelectedItem().getId();

            if (selection == null) return;
            if (selection.toString().equals("Custom Items") ||
                    (selection.getValue().getItemObj() != null &&
                            treeModel.getCustomItems().contains(selection.getValue().getItemObj()))) {
                if (!guiTabName.equals("tabEditor")) tabPane.getSelectionModel().select(2);
                EditorTabController.getInstance().receiveSelection(selection);
                return;
            } //TODO Refactor Selectable/whatever into doing it's own receiveSelection and tab switching, and setModified
            //or rather remove setModified and just compare fileStrings during saving

            treeModel.setModifiedStatus(selection);

            if (selection.getValue().getCharaOp() != null ||
                    (selection.getValue().getItemObj() != null &&
                            selection.getValue().getItemObj().getItemType().getDataId() == 34)) {
                if (guiTabName.equals("tabEditor")) tabPane.getSelectionModel().selectFirst();
                CharactersTabController.getInstance().receiveSelection(selection);
            } else {
                switch (guiTabName) {
                    case "tabCharacters" -> CharactersTabController.getInstance().receiveSelection(selection);
                    case "tabStash" -> StashTabController.getInstance().receiveSelection(selection);
                    case "tabEditor" -> EditorTabController.getInstance().receiveSelection(selection);
                    //TODO Uniques tab
                }
            }
        });

        for (javafx.scene.control.Tab tab : tabPane.getTabs()) {
            tab.setOnSelectionChanged(changeEvent -> myTreeView.setRootByTab());
        }

        tabPane.getTabs().get(2).setOnSelectionChanged(event -> {
            if (!treeView.getSelectionModel().isEmpty()) {
                EditorTabController.getInstance().receiveSelection(treeView.getSelectionModel().getSelectedItem());
            }
        });
    }

    public void addNewCharacter(CharacterOperations charaOp) {
        TreeItem<Selectable> charaItem = treeModel.createNewCharacterTreeItem(charaOp);
        myTreeView.addNewCharacterToTree(charaItem);
        CharactersTabController.getInstance().receiveSelection(charaItem);
    }

    public void addCustomItem(Item item) {
        treeModel.addCustomItem(item);
        myTreeView.addCustomItemToTree(new TreeItem<>(item));
    }

    public static TreeController getInstance() {
        return instance;
    }

    public void renewStashItems() {
        myTreeView.renewStashItems(treeModel.createStashListRoot());
    }

    public void renewCharacterList() {
        myTreeView.renewCharacterList(treeModel.createCharacterListRoot());
    }

    public void refreshTree() {
        myTreeView.refresh();
    }
}
