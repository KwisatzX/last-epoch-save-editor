package io.github.kwisatzx.lastepoch.gui.controllers;

import io.github.kwisatzx.lastepoch.fileoperations.FileHandler;
import io.github.kwisatzx.lastepoch.fileoperations.GlobalDataOperations;
import io.github.kwisatzx.lastepoch.fileoperations.Selectable;
import io.github.kwisatzx.lastepoch.itemdata.AbstractItem;
import io.github.kwisatzx.lastepoch.itemdata.AffixTier;
import io.github.kwisatzx.lastepoch.itemdata.Item;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;

public class StashTabController extends GuiItemTab {
    private static StashTabController INSTANCE;
    private GlobalDataOperations stashOp;
    @FXML
    private AnchorPane stashAnchorPane;

    @FXML
    private void initialize() {
        INSTANCE = this;
        stashOp = FileHandler.getStashFile();
        itemTabInit(stashAnchorPane);
        installEventHandlers(stashAnchorPane);
        fillGlobalStashDataFields();
    }

    public static StashTabController getInstance() {
        return INSTANCE;
    }

    @Override
    protected void fillDataFields() {
        super.fillDataFields();
        fillItemStashDataFields();
        fillGlobalStashDataFields();
    }

    private void fillGlobalStashDataFields() {
        textFields.get("goldField").setText(stashOp.getGold() + "");
        textFields.get("stashTabsField").setText(stashOp.getStashTabCount() + "");
        textFields.get("glyphsField").setText(stashOp.getGlyphCount() + "");
        textFields.get("runesField").setText(stashOp.getRuneCount() + "");
        textFields.get("affixShardsField").setText(stashOp.getAffixShardCount() + "");
    }

    private void fillItemStashDataFields() {
        getSelectedItem().ifPresent(item -> {
            String tabName = stashOp.getStashTabNameFromId(item.getItemStashInfo().id);
            choiceBoxes.get("stashTabNamesChoiceBox").getSelectionModel().select(tabName);
            choiceBoxes.get("stashXChoiceBox").getSelectionModel().select(item.getItemStashInfo().x);
            choiceBoxes.get("stashYChoiceBox").getSelectionModel().select(item.getItemStashInfo().y);
        });
    }

    private void setStashItems() {
        stashOp.setStashItemsInFileString();
    }

    @Override
    public void reloadTreeView() {
        TreeViewController.getInstance().renewStashItems();
    }

    private void installEventHandlers(Node parentNode) {
        final EventHandler<ActionEvent> buttonHandler = event -> {
            if (event.getTarget() != null && event.getTarget() instanceof Button button) {
                switch (button.getId()) {
                    case "copyItemToEditorButton" -> copyItemToEditor();
                    case "getGlyphsRunesButton" -> addGlyphsAndRunes();
                    case "affixShardsUseableButton" -> addAffixShardsUseable();
                    case "affixShardsAllButton" -> addAffixShardsAll();
                    case "arenaKeysButton" -> addArenaKeys();
                    case "unfractureItemsButton" -> unfractureItems();
                    case "maximizeItemValuesButton" -> maximizeItemValues();
                    case "maximizeItemAffixTiersButton" -> maximizeItemAffixTiers();
                    case "deleteAllButton" -> deleteAll();
                    case "addTenStashTabsButton" -> addTenStashTabs();
                }
            }
        };
        parentNode.addEventHandler(ActionEvent.ANY, buttonHandler);

        installChangeEvents();
    }

    private void installChangeEvents() {
        textFields.get("goldField").setOnKeyTyped(
                event -> stashOp.setProperty("gold", textFields.get("goldField").getText()));

        choiceBoxes.get("stashTabNamesChoiceBox").setOnAction(event -> {
            getSelectedItem().ifPresent(item -> {
                item.getItemStashInfo().id =
                        stashOp.getStashIdFromName(choiceBoxes.get("stashTabNamesChoiceBox").getValue());
                reloadTreeView();
            });
        });

        choiceBoxes.get("stashXChoiceBox").setOnAction(event -> {
            getSelectedItem().ifPresent(item -> item.getItemStashInfo().x =
                    Integer.parseInt(choiceBoxes.get("stashXChoiceBox").getValue()));
        });

        choiceBoxes.get("stashYChoiceBox").setOnAction(event -> {
            getSelectedItem().ifPresent(item -> item.getItemStashInfo().y =
                    Integer.parseInt(choiceBoxes.get("stashYChoiceBox").getValue()));
        });
    }

    private void copyItemToEditor() {
        getSelectedItem().ifPresent(original -> {
            Item copy = getItemCopy(original);
            selectedItem = new TreeItem<>(copy);
            TreeViewController.getInstance().addCustomItem(copy);
            EditorTabController.getInstance().receiveSelection(selectedItem);
            RootController.getInstance().getTabPane().getSelectionModel().select(2);
            super.reloadTreeView();
        });
    }

    private void addGlyphsAndRunes() {
        stashOp.addGlyphsAndRunes();
        fillGlobalStashDataFields();
    }

    private void addAffixShardsUseable() {
        if (!FileHandler.getStashFile().addUseableAffixShards()) {
            RootController.getInstance().setBottomRightText("Error: Failed to open array data file. See log.");
        }
    }

    private void addAffixShardsAll() {
        stashOp.addAllAffixShards();
        fillGlobalStashDataFields();
    }

    private void addArenaKeys() {
        int tabId = addStashTab();
        GlobalDataOperations.StashTab stashTab = stashOp.getStashTabs().get(tabId);
        stashTab.setDisplayName("Arena Keys");
        for (int x = 0; x < 12; x++) {
            for (int y = 0; y < 17; y++) {
//                stashOp.getStashItems().add()
                //TODO handle special items, rework Item class
            }
        }
    }

    private void unfractureItems() {
        for (Item item : stashOp.getStashItems()) {
            item.setInstability(0);
        }
        setStashItems();
        fillDataFields();
    }

    private void maximizeItemValues() {
        for (Item item : stashOp.getStashItems()) {
            item.setImplicitValue1(255);
            item.setImplicitValue2(255);
            item.setImplicitValue3(255);
            for (AbstractItem.AffixData affixData : item.getAffixList()) {
                affixData.value = 255;
            }
        }
        setStashItems();
        fillDataFields();
    }

    private void maximizeItemAffixTiers() {
        for (Item item : stashOp.getStashItems()) {
            for (AbstractItem.AffixData affixData : item.getAffixList()) {
                affixData.tier = AffixTier.maxTier(affixData.type);
            }
        }
        setStashItems();
        fillDataFields();
    }

    private void deleteAll() {
        //TODO confirmation dialog?
        stashOp.getStashItems().clear();
        setStashItems();
        reloadTreeView();
    }

    private void addTenStashTabs() {
        for (int i = 0; i < 10; i++) {
            addStashTab();
        }
        stashOp.setStashTabsInFileString();
        reloadTreeView();
    }

    private int addStashTab() {
        int categoryId = 0;
        if (getSelectedItem().isEmpty()) {
            Selectable selectable = selectedItem.getValue();
            if (selectable instanceof GlobalDataOperations.StashTabCategory category) {
                categoryId = category.getCategoryID();
            }
            if (selectable instanceof GlobalDataOperations.StashTab stashTab) {
                categoryId = stashTab.getCategoryID();
            }
        } else categoryId = Item.getItemStash(getSelectedItem().get()).getCategoryID();

        int tabId = stashOp.getStashTabCount();
        int finalCategoryId = categoryId;
        int displayOrder = (int) stashOp.getStashTabs().stream()
                .filter(stashTab -> stashTab.getCategoryID() == finalCategoryId)
                .count();

        stashOp.getStashTabs().add(
                new GlobalDataOperations.StashTab(finalCategoryId, tabId + "", displayOrder, tabId));
        return tabId;
    }
}