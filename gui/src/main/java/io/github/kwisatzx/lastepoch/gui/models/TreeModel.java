package io.github.kwisatzx.lastepoch.gui.models;

import io.github.kwisatzx.lastepoch.fileoperations.CharacterOperations;
import io.github.kwisatzx.lastepoch.fileoperations.FileHandler;
import io.github.kwisatzx.lastepoch.fileoperations.GlobalDataOperations;
import io.github.kwisatzx.lastepoch.itemdata.Item;
import io.github.kwisatzx.lastepoch.itemdata.Selectable;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TreeModel {
    private final List<Item> customItems;

    public TreeModel() {
        customItems = new ArrayList<>();
    }

    public void addCustomItem(Item item) {
        customItems.add(item);
    }

    public List<Item> getCustomItems() {
        return Collections.unmodifiableList(customItems);
    }

    public TreeItem<Selectable> createNewCharacterTreeItem(CharacterOperations charaOp) {
        TreeItem<Selectable> charaItem = new TreeItem<>(charaOp);
        charaItem.setExpanded(true);
        charaItem.getChildren().addAll(
                charaOp.getCharacter().getEquipment().stream()
                        .map(item -> new TreeItem<Selectable>(item))
                        .toList());
        return charaItem;
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
}
