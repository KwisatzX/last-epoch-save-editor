package io.github.kwisatzx.lastepoch.gui.models;

import io.github.kwisatzx.lastepoch.fileoperations.CharacterOperations;
import io.github.kwisatzx.lastepoch.fileoperations.FileHandler;
import io.github.kwisatzx.lastepoch.fileoperations.GlobalDataOperations;
import io.github.kwisatzx.lastepoch.gui.controllers.EditorTabController;
import io.github.kwisatzx.lastepoch.gui.views.elements.SelectionWrapper;
import io.github.kwisatzx.lastepoch.itemdata.AffixTier;
import io.github.kwisatzx.lastepoch.itemdata.ItemAttributeList;
import io.github.kwisatzx.lastepoch.itemdata.item.AbstractItem;
import io.github.kwisatzx.lastepoch.itemdata.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EditorTabModel extends GuiItemTabModel {
    private final EditorTabController controller;
    private final SelectionWrapper selection;
    private final Map<String, CharacterOperations> charactersMap;


    public EditorTabModel(EditorTabController controller) {
        super(controller);
        this.controller = controller;
        selection = controller.getSelection();
        charactersMap = createCharacterMap();
    }

    private Map<String, CharacterOperations> createCharacterMap() {
        return FileHandler.getCharacterFileList().stream()
                .collect(Collectors.<CharacterOperations, String, CharacterOperations>toMap(
                        charaOP -> charaOP.getCharacter().getName(), Function.identity()));
    }

    public Map<String, CharacterOperations> getCharactersMap() {
        return Collections.unmodifiableMap(charactersMap);
    }

    public void addItemToStash() {
        if (!selection.isItem()) return;
        Item item = getItemCopy(selection.getItem().get());
        GlobalDataOperations stashOp = FileHandler.getStashFile();
        AbstractItem.ItemStashInfo stashInfo = item.getItemStashInfo();

        Map<String, String> uiStashSettings = controller.getUiSettingsFromView();
        stashInfo.x = Integer.parseInt(uiStashSettings.get("x"));
        stashInfo.y = Integer.parseInt(uiStashSettings.get("y"));
        stashInfo.id = stashOp.getStashTabByName(uiStashSettings.get("stashTabName")).getTabID();
        stashInfo.charaEquipment = false;

        stashOp.getStashItems().add(item);
        stashOp.setStashItemsInFileString();
        controller.renewStashItems();
        controller.setBottomRightText("Item added to stash.");
    }

    public void addItemToCharacter(boolean replaceSlotRadioIsSelected) {
        if (!selection.isItem()) return;
        Map<String, String> uiSettings = controller.getUiSettingsFromView();
        Item item = getItemCopy(selection.getItem().get());
        CharacterOperations charaOp = charactersMap.get(uiSettings.get("character"));
        item.getItemStashInfo().charaEquipment = true;

        if (replaceSlotRadioIsSelected) {
            item.getItemStashInfo().id = Item.ContainerIds.getContainerIdFromItemType(item.getItemType());
            charaOp.getCharacter().addOrReplaceEquipmentItem(item);
        } else {
            item.getItemStashInfo().x = Integer.parseInt(uiSettings.get("x"));
            item.getItemStashInfo().y = Integer.parseInt(uiSettings.get("y"));
            item.getItemStashInfo().id = 1;
            charaOp.getCharacter().addItem(item);
        }
        setCharaEquipment();
        controller.reloadTreeView();
        controller.setBottomRightText("Item added to character.");
    }

    public void createNewItem() {
        Item item = new AbstractItem(ItemAttributeList.getById(0), 0, 0, new ArrayList<>());
        selection.setSelection(item);
        controller.fillDataFields();
        controller.addCustomItem(item);
        controller.reloadTreeView();
    }

    public void copyItem() {
        selection.ifItemPresent(original -> {
            Item copy = getItemCopy(original);
            selection.setSelection(copy);
            controller.fillDataFields();
            controller.addCustomItem(copy);
            controller.reloadTreeView();
        });
    }

    public void deleteItem() { //TODO: fix (delete items from stash and custom)
        selection.ifItemPresent(item -> {
            if (item.getItemStashInfo().charaEquipment) {
                Item.getItemOwner(item).getCharacter().getEquipment().remove(item);
                setCharaEquipment();
            } else {
                //TODO: if customItem OR itemStash=0,0,0 delete customItem()
                //else: delete item from stash?
            }
            controller.reloadTreeView();
        });
    }

    public void maxItemAffixValues() {
        selection.ifItemPresent(item -> {
            for (AbstractItem.AffixData affixData : item.getAffixList()) {
                affixData.value = 255;
            }
            controller.setCharaEquipment();
            controller.fillDataFields();
        });
    }

    public void maxItemAffixTiers() {
        selection.ifItemPresent(item -> {
            for (AbstractItem.AffixData affixData : item.getAffixList()) {
                affixData.tier = AffixTier.maxTier(affixData.type);
            }
            controller.setCharaEquipment();
            controller.fillDataFields();
        });
    }
}
