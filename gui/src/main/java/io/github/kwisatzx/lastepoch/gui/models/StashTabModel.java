package io.github.kwisatzx.lastepoch.gui.models;

import io.github.kwisatzx.lastepoch.fileoperations.FileHandler;
import io.github.kwisatzx.lastepoch.fileoperations.GlobalDataOperations;
import io.github.kwisatzx.lastepoch.gui.controllers.RootController;
import io.github.kwisatzx.lastepoch.gui.controllers.StashTabController;
import io.github.kwisatzx.lastepoch.gui.views.elements.SelectionWrapper;
import io.github.kwisatzx.lastepoch.itemdata.AffixTier;
import io.github.kwisatzx.lastepoch.itemdata.item.AbstractItem;
import io.github.kwisatzx.lastepoch.itemdata.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class StashTabModel extends GuiItemTabModel {
    private final StashTabController controller;
    private final SelectionWrapper selection;
    private final GlobalDataOperations stashOp;

    public StashTabModel(StashTabController controller) {
        super(controller);
        this.controller = controller;
        selection = controller.getSelection();
        stashOp = FileHandler.getStashFile();
    }

    public void setStashItems() {
        stashOp.setStashItemsInFileString();
    }

    public int getStashTabIdFromName(String name) {
        return stashOp.getStashIdFromName(name);
    }

    public String getStashTabNameFromId(int id) {
        return stashOp.getStashTabNameFromId(id);
    }

    public void setStashProperty(String propertyName, String value) {
        stashOp.setProperty(propertyName, value);
    }

    //TODO Replace this and others with DTO classes, also return Strings instead of Integers cuz it's for view
    public Map<String, Integer> getStashDataNumbers() {
        HashMap<String, Integer> data = new HashMap<>();
        data.put("gold", stashOp.getGold());
        data.put("stashTabs", stashOp.getStashTabCount());
        data.put("glyphs", stashOp.getGlyphCount());
        data.put("runes", stashOp.getRuneCount());
        data.put("affixShards", stashOp.getAffixShardCount());
        return data;
    }

    public void copyItemToEditor() {
        selection.ifItemPresent(original -> {
            Item copy = getItemCopy(original);
            selection.setSelection(copy);
            controller.addCustomItem(copy);
            controller.switchToTab(RootController.GuiTabs.EDITOR_TAB);
            controller.renewCharacterList();
        });
    }

    public void addGlyphsAndRunes() {
        stashOp.addGlyphsAndRunes();
        controller.fillDataFields();
    }

    public void addAffixShardsUseable() {
        if (!stashOp.addUseableAffixShards()) {
            controller.setBottomRightText("Error: Failed to open array data file. See log.");
        }
    }

    public void addAffixShardsAll() {
        stashOp.addAllAffixShards();
        controller.fillDataFields();
    }

    public void addArenaKeys() {
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

    public void unfractureItems() {
        for (Item item : stashOp.getStashItems()) {
            item.setInstability(0);
        }
        setStashItems();
        controller.fillDataFields();
    }

    public void maximizeItemValues() {
        for (Item item : stashOp.getStashItems()) {
            item.setImplicitValue1(255);
            item.setImplicitValue2(255);
            item.setImplicitValue3(255);
            for (AbstractItem.AffixData affixData : item.getAffixList()) {
                affixData.value = 255;
            }
        }
        setStashItems();
        controller.fillDataFields();
    }

    public void maximizeItemAffixTiers() {
        for (Item item : stashOp.getStashItems()) {
            for (AbstractItem.AffixData affixData : item.getAffixList()) {
                affixData.tier = AffixTier.maxTier(affixData.type);
            }
        }
        setStashItems();
        controller.fillDataFields();
    }

    public void deleteAll() {
        //TODO confirmation dialog?
        stashOp.getStashItems().clear();
        setStashItems();
        controller.reloadTreeView();
    }

    public void addTenStashTabs() {
        for (int i = 0; i < 10; i++) {
            addStashTab();
        }
        stashOp.setStashTabsInFileString();
        controller.reloadTreeView();
    }

    private int addStashTab() {
        AtomicInteger categoryId = new AtomicInteger();
        selection.getStashTab().ifPresentOrElse(
                stashTab -> categoryId.set(stashTab.getCategoryID()),
                () -> categoryId.set(Item.getItemStash(
                        selection.getItem().get()).getCategoryID())); //TODO: what if custom item is selected?

        int tabId = stashOp.getStashTabCount();
        int finalCategoryId = categoryId.get();
        int displayOrder = (int) stashOp.getStashTabs().stream()
                .filter(stashTab -> stashTab.getCategoryID() == finalCategoryId)
                .count();

        stashOp.getStashTabs().add(
                new GlobalDataOperations.StashTab(finalCategoryId, tabId + "", displayOrder, tabId));
        return tabId;
    }
}
