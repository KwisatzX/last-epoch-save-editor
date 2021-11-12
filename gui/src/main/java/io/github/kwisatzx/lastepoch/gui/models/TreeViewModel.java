package io.github.kwisatzx.lastepoch.gui.models;

import io.github.kwisatzx.lastepoch.fileoperations.CharacterOperations;
import io.github.kwisatzx.lastepoch.fileoperations.FileHandler;
import io.github.kwisatzx.lastepoch.fileoperations.GlobalDataOperations;
import io.github.kwisatzx.lastepoch.fileoperations.Selectable;
import io.github.kwisatzx.lastepoch.itemdata.Item;
import javafx.scene.control.TreeItem;

public class TreeViewModel {

    public TreeItem<Selectable> createStashListRoot() {
        GlobalDataOperations globalOp = FileHandler.getStashFile();
        TreeItem<Selectable> root = new TreeItem<>(new Selectable() {
            @Override
            public String toString() {
                return "Stash Tabs";
            }
        });
        root.setExpanded(true);

        for (GlobalDataOperations.StashTabCategory stashTabCategory : globalOp.getStashTabCategories()) {
            TreeItem<Selectable> categoryTreeItem = new TreeItem<>(stashTabCategory);
            int categoryID = stashTabCategory.getCategoryID();
            root.getChildren().add(categoryTreeItem);

            for (GlobalDataOperations.StashTab stashTab : globalOp.getStashTabs()) {
                if (stashTab.getCategoryID() == categoryID) {
                    TreeItem<Selectable> stashTabTreeItem = new TreeItem<>(stashTab);
                    int tabID = stashTab.getTabID();
                    categoryTreeItem.getChildren().add(stashTabTreeItem);

                    for (Item stashItem : globalOp.getStashItems()) {
                        if (stashItem.getItemStashInfo().id == tabID) {
                            TreeItem<Selectable> itemTreeItem = new TreeItem<>(stashItem);
                            stashTabTreeItem.getChildren().add(itemTreeItem);
                        }
                    }
                }
            }
        }
        return root;
    }

    public TreeItem<Selectable> createCharacterListRoot() {
        TreeItem<Selectable> root = new TreeItem<>(new Selectable() {
            @Override
            public String toString() {
                return "Characters";
            }
        });
        root.setExpanded(true);

        for (CharacterOperations chara : FileHandler.getCharacterFileList()) {
            TreeItem<Selectable> charaItem = new TreeItem<>(chara);
            charaItem.getChildren().addAll(
                    chara.getCharacter().getEquipment().stream()
                            .map(item -> new TreeItem<Selectable>(item))
                            .toList());

            root.getChildren().add(charaItem);
        }
        return root;
    }
}
