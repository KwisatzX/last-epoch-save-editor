package io.github.kwisatzx.lastepoch.gui.views;

import io.github.kwisatzx.lastepoch.fileoperations.Selectable;
import javafx.collections.ObservableList;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyTreeView {
    private final TreeView<Selectable> treeView;
    private TreeItem<Selectable> characterListRoot;
    private TreeItem<Selectable> stashListRoot;
    private final TreeItem<Selectable> customItems;
    private final TabPane tabPane;

    public MyTreeView(TreeView<Selectable> treeView, TabPane tabPane) {
        this.treeView = treeView;
        treeView.setShowRoot(false);
        this.tabPane = tabPane;
        this.customItems = new TreeItem<>(new Selectable() {
            @Override
            public String toString() {
                return "Custom Items";
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

    public void addCustomItemToTree(TreeItem<Selectable> treeItem) {
        customItems.getChildren().add(treeItem);
        customItems.setExpanded(true);
    }

    public void addNewCharacterToTree(TreeItem<Selectable> charaItem) {
        characterListRoot.getChildren().add(charaItem);
        refresh();
    }

    public void setCharacterListRoot(TreeItem<Selectable> root) {
        characterListRoot = root;
    }

    public void setStashListRoot(TreeItem<Selectable> root) {
        stashListRoot = root;
    }
}
