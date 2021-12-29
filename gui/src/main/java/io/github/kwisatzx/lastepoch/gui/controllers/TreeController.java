package io.github.kwisatzx.lastepoch.gui.controllers;

import io.github.kwisatzx.lastepoch.fileoperations.CharacterOperations;
import io.github.kwisatzx.lastepoch.gui.models.TreeModel;
import io.github.kwisatzx.lastepoch.gui.views.MyTreeView;
import io.github.kwisatzx.lastepoch.gui.views.elements.SelectionWrapper;
import io.github.kwisatzx.lastepoch.gui.views.elements.SelectionWrapperImpl;
import io.github.kwisatzx.lastepoch.itemdata.Selectable;
import io.github.kwisatzx.lastepoch.itemdata.item.Item;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class TreeController {
    private static TreeController instance;
    private final SelectionWrapper selection;
    private final TreeModel treeModel;
    private final MyTreeView myTreeView;
    private final RootController rootController;

    public TreeController(TreeView<Selectable> treeView) {
        instance = this;
        selection = new SelectionWrapperImpl();
        treeModel = new TreeModel();
        rootController = RootController.getInstance();
        myTreeView = new MyTreeView(treeView);
        myTreeView.setStashListRoot(treeModel.createStashListRoot());
        myTreeView.setCharacterListRoot(treeModel.createCharacterListRoot());
        myTreeView.setRootByTab();
        setUpEvents(treeView);
    }

    private void setUpEvents(TreeView<Selectable> treeView) {
        treeView.setOnMouseClicked(selectionChangedEvent -> {
            setNewSelection();
            if (selection.isEmpty()) return;
            if (customItemListOrCustomItemSelected()) rootController.switchToTab(RootController.GuiTabs.EDITOR_TAB);
            else if (charaOpOrBlessingSelected()) rootController.switchToTab(RootController.GuiTabs.CHARACTER_TAB);
            else rootController.switchToSelectedTab();
        });
    }

    private boolean customItemListOrCustomItemSelected() {
        return myTreeView.getSelectedTreeItem().toString().equals("Custom Items") ||
                (selection.isItem() && treeModel.getCustomItems().contains(selection.getItem().get()));
    }

    private boolean charaOpOrBlessingSelected() {
        return selection.isCharacterOp() ||
                (selection.isItem() && selection.getItem().get().getItemType().getDataId() == 34);
    }

    public void addNewCharacter(CharacterOperations charaOp) {
        TreeItem<Selectable> charaItem = treeModel.createNewCharacterTreeItem(charaOp);
        myTreeView.addNewCharacterToTree(charaItem);
        rootController.switchToTab(RootController.GuiTabs.CHARACTER_TAB);
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

    public SelectionWrapper getSelection() {
        return selection;
    }

    private void setNewSelection() {
        selection.setSelection(myTreeView.getSelectedTreeItem().getValue());
    }

    public void setRootByTab() {
        myTreeView.setRootByTab();
    }
}
