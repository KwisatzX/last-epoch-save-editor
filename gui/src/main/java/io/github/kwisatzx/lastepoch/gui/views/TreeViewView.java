package io.github.kwisatzx.lastepoch.gui.views;

import io.github.kwisatzx.lastepoch.fileoperations.CharacterOperations;
import io.github.kwisatzx.lastepoch.fileoperations.FileHandler;
import io.github.kwisatzx.lastepoch.fileoperations.Selectable;
import io.github.kwisatzx.lastepoch.gui.controllers.CharactersTabController;
import io.github.kwisatzx.lastepoch.gui.controllers.EditorTabController;
import io.github.kwisatzx.lastepoch.gui.controllers.StashTabController;
import io.github.kwisatzx.lastepoch.itemdata.Item;
import javafx.collections.ObservableList;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TreeViewView {
    private final TreeView<Selectable> treeView;
    private TreeItem<Selectable> characterListRoot;
    private TreeItem<Selectable> stashListRoot;
    private final TreeItem<Selectable> customItems;
    private final TabPane tabPane;

    public TreeViewView(TreeView<Selectable> treeView, TabPane tabPane) {
        this.treeView = treeView;
        treeView.setShowRoot(false);
        this.tabPane = tabPane;
        this.customItems = new TreeItem<>(new Selectable() {
            @Override
            public String toString() {
                return "Custom Items";
            }
        });

        treeView.setOnMouseClicked(event -> {
            TreeItem<Selectable> selection = treeView.getSelectionModel().getSelectedItem();
            String guiTabName = tabPane.getSelectionModel().getSelectedItem().getId();

            if (selection == null) return;
            if (selection.equals(customItems) || customItems.getChildren().contains(selection)) {
                if (!guiTabName.equals("tabEditor")) tabPane.getSelectionModel().select(2);
                EditorTabController.getInstance().receiveSelection(selection);
                return;
            }

            setModifiedStatus(selection);

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
            tab.setOnSelectionChanged(changeEvent -> setRootByTab());
        }

        tabPane.getTabs().get(2).setOnSelectionChanged(event -> {
            if (!treeView.getSelectionModel().isEmpty()) {
                EditorTabController.getInstance().receiveSelection(treeView.getSelectionModel().getSelectedItem());
            }
        });
    }

    public void setRootByTab() {
        String tabId = tabPane.getSelectionModel().getSelectedItem().getId();
        switch (tabId) {
            case "tabCharacters", "tabEditor" -> treeView.setRoot(characterListRoot);
            case "tabStash" -> treeView.setRoot(stashListRoot);
            //TODO Uniques tab
            default -> treeView.setRoot(characterListRoot);
        }
        refresh();
    }

    public void renewCharacterList(TreeItem<Selectable> root) {
        characterListRoot.getChildren().clear();
        characterListRoot.getChildren().addAll(root.getChildren());
        if (!customItems.getChildren().isEmpty()) {
            characterListRoot.getChildren().remove(customItems);
            characterListRoot.getChildren().add(customItems);
        }
    }

    public void renewStashItems(TreeItem<Selectable> root) {
        HashMap<Integer, Boolean> categoryExpandedStatus = new HashMap<>();
        List<HashMap<Integer, Boolean>> tabsExpandedStatus = new ArrayList<>();
        saveTreeItemExpandedStatus(categoryExpandedStatus, tabsExpandedStatus);
        stashListRoot.getChildren().clear();
        stashListRoot.getChildren().addAll(root.getChildren());
        loadTreeItemExpandedStatus(categoryExpandedStatus, tabsExpandedStatus);
    }

    private void saveTreeItemExpandedStatus(HashMap<Integer, Boolean> categoryExpandedStatus,
                                            List<HashMap<Integer, Boolean>> tabsExpandedStatus) {
        for (int i = 0; i < stashListRoot.getChildren().size(); i++) {
            categoryExpandedStatus.put(i, stashListRoot.getChildren().get(i).isExpanded());

            ObservableList<TreeItem<Selectable>> itemsList = stashListRoot.getChildren().get(i).getChildren();
            HashMap<Integer, Boolean> expandedStatus = new HashMap<>();
            for (int j = 0; j < itemsList.size(); j++) {
                expandedStatus.put(j, itemsList.get(j).isExpanded());
            }
            tabsExpandedStatus.add(expandedStatus);
        }
    }

    private void loadTreeItemExpandedStatus(HashMap<Integer, Boolean> categoryExpandedStatus,
                                            List<HashMap<Integer, Boolean>> tabsExpandedStatus) {
        for (int i = 0; i < categoryExpandedStatus.size(); i++) {
            TreeItem<Selectable> category = stashListRoot.getChildren().get(i);
            category.setExpanded(categoryExpandedStatus.get(i));

            for (int j = 0; j < tabsExpandedStatus.get(i).size(); j++) {
                category.getChildren().get(j).setExpanded(tabsExpandedStatus.get(i).get(j));
            }
        }
    }

    public void refresh() {
        treeView.refresh();
    }

    public void addCustomItem(Item item) {
        customItems.getChildren().add(new TreeItem<>(item));
        customItems.setExpanded(true);
    }

    public void addNewCharacter(CharacterOperations charaOp) {
        TreeItem<Selectable> charaItem = new TreeItem<>(charaOp);
        charaItem.setExpanded(true);
        charaItem.getChildren().addAll(
                charaOp.getCharacter().getEquipment().stream()
                        .map(item -> new TreeItem<Selectable>(item))
                        .toList());
        characterListRoot.getChildren().add(charaItem);
        refresh();
        CharactersTabController.getInstance().receiveSelection(charaItem);
    }

    private void setModifiedStatus(TreeItem<Selectable> selection) {
        if (selection.getValue().getCharaOp() != null) {
            selection.getValue().getCharaOp().setModified(true);
        } else if (selection.getValue().getItemObj() != null) {
            CharacterOperations charaOp = Item.getItemOwner(selection.getValue().getItemObj());
            if (charaOp != null) {
                charaOp.setModified(true);
            } else FileHandler.getStashFile().setModified(true);
        }
    }

    public void setCharacterListRoot(TreeItem<Selectable> root) {
        characterListRoot = root;
    }

    public void setStashListRoot(TreeItem<Selectable> root) {
        stashListRoot = root;
    }
}
