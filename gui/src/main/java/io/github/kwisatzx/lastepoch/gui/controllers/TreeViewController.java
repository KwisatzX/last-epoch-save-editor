package io.github.kwisatzx.lastepoch.gui.controllers;

import io.github.kwisatzx.lastepoch.fileoperations.Selectable;
import io.github.kwisatzx.lastepoch.gui.models.TreeViewModel;
import io.github.kwisatzx.lastepoch.gui.views.TreeViewView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;

public class TreeViewController {
    private static TreeViewController instance;
    private final TreeViewModel treeViewModel;
    private final TreeViewView treeViewView;

    public TreeViewController(TreeView<Selectable> treeView, TabPane tabPane) {
        instance = this;
        treeViewModel = new TreeViewModel();
        treeViewView = new TreeViewView(treeView, tabPane);
        treeViewView.setStashListRoot(treeViewModel.createStashListRoot());
        treeViewView.setCharacterListRoot(treeViewModel.createCharacterListRoot());
        treeViewView.setRootByTab();
    }

    public static TreeViewController getInstance() {
        return instance;
    }

    public void renewStashItems() {
        treeViewView.renewStashItems(treeViewModel.createStashListRoot());
    }

    public void renewCharacterList() {
        treeViewView.renewCharacterList(treeViewModel.createCharacterListRoot());
    }

    public void refreshTree() {
        treeViewView.refresh();
    }
}
