package io.github.kwisatzx.lastepoch.gui.controllers;

import io.github.kwisatzx.lastepoch.fileoperations.Selectable;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;

public class TreeViewController {
    private static TreeViewController instance;

    public TreeViewController(TreeView<Selectable> treeView, TabPane tabPane) {
        instance = this;

    }

    public static TreeViewController getInstance() {
        return instance;
    }
}
