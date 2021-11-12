package io.github.kwisatzx.lastepoch.gui.controllers;

import io.github.kwisatzx.lastepoch.fileoperations.CharacterOperations;
import io.github.kwisatzx.lastepoch.fileoperations.FileHandler;
import io.github.kwisatzx.lastepoch.fileoperations.GlobalDataOperations;
import io.github.kwisatzx.lastepoch.itemdata.AbstractItem;
import io.github.kwisatzx.lastepoch.itemdata.AffixTier;
import io.github.kwisatzx.lastepoch.itemdata.Item;
import io.github.kwisatzx.lastepoch.itemdata.ItemAttributeList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EditorTabController extends GuiItemTab {
    private static EditorTabController INSTANCE;
    @FXML
    private RadioButton replaceSlotRadio;
    @FXML
    private AnchorPane editorAnchorPane;
    private Map<String, CharacterOperations> characters;

    @FXML
    private void initialize() {
        INSTANCE = this;
        itemTabInit(editorAnchorPane);
        installEventHandlers(editorAnchorPane);
        initCharactersChoiceBox();
        initInvXYChoiceBoxes();
    }

    public static EditorTabController getInstance() {
        return INSTANCE;
    }

    protected void fillDataFields() {
        super.fillDataFields();
        getCharaOp().ifPresent(charaOp -> choiceBoxes.get("charactersChoiceBox").getSelectionModel().select(
                charaOp.getCharacter().getName()));
    }

    private void installEventHandlers(Node parentNode) {
        final EventHandler<ActionEvent> buttonHandler = event -> {
            if (event.getTarget() != null && event.getTarget() instanceof Button button) {
                switch (button.getId()) {
                    case "addItemToStashButton" -> addItemToStash();
                    case "addItemToCharacterButton" -> addItemToCharacter();
                    case "resetPropertiesButton" -> resetProperties();
                    case "resetAffixesButton" -> resetAffixes();
                    case "createItemButton" -> createNewItem();
                    case "copyItemButton" -> copyItem();
                    case "deleteItemButton" -> deleteItem();
                    case "maxItemAffixValuesButton" -> maxItemAffixValues();
                    case "maxItemAffixTiersButton" -> maxItemAffixTiers();
                }
            }
        };

        parentNode.addEventHandler(ActionEvent.ANY, buttonHandler);
    }

    public void initCharactersChoiceBox() {
        characters = FileHandler.getCharacterFileList().stream()
                .collect(Collectors.<CharacterOperations, String, CharacterOperations>toMap(
                        charaOP -> charaOP.getCharacter().getName(), Function.identity()));

        choiceBoxes.get("charactersChoiceBox").getItems().setAll(characters.keySet());
        if (choiceBoxes.get("charactersChoiceBox").getSelectionModel().isEmpty())
            choiceBoxes.get("charactersChoiceBox").getSelectionModel().selectFirst();
    }

    private void initInvXYChoiceBoxes() {
        choiceBoxes.get("invXChoiceBox").getItems().addAll(IntStream.rangeClosed(0, 13)
                                                                   .mapToObj(String::valueOf)
                                                                   .toList());
        choiceBoxes.get("invXChoiceBox").getSelectionModel().select(0);
        choiceBoxes.get("invYChoiceBox").getItems().addAll(IntStream.rangeClosed(0, 7)
                                                                   .mapToObj(String::valueOf)
                                                                   .toList());
        choiceBoxes.get("invYChoiceBox").getSelectionModel().select(0);
    }

    private void addItemToStash() {
        if (getSelectedItem().isEmpty()) return;
        Item item = getItemCopy(getSelectedItem().get());
        GlobalDataOperations stashOp = FileHandler.getStashFile();
        AbstractItem.ItemStashInfo stashInfo = item.getItemStashInfo();

        stashInfo.x = Integer.parseInt(choiceBoxes.get("stashXChoiceBox").getValue());
        stashInfo.y = Integer.parseInt(choiceBoxes.get("stashYChoiceBox").getValue());
        stashInfo.id = stashOp.getStashTabByName(
                choiceBoxes.get("stashTabNamesChoiceBox").getValue()).getTabID();
        stashInfo.charaEquipment = false;

        stashOp.getStashItems().add(item);
        stashOp.setStashItemsInFileString();
        TreeViewController.getInstance().renewStashItems();
        RootController.getInstance().setBottomRightText("Item added to stash.");
    }

    private void addItemToCharacter() {
        if (getSelectedItem().isEmpty()) return;
        AbstractItem item = getItemCopy(getSelectedItem().get());
        CharacterOperations charaOp = characters.get(choiceBoxes.get("charactersChoiceBox").getValue());
        if (item == null || charaOp == null) return;
        item.getItemStashInfo().charaEquipment = true;

        if (replaceSlotRadio.isSelected()) {
            item.getItemStashInfo().id = Item.ContainerIds.getContainerIdFromItemType(item.getItemType());
            charaOp.getCharacter().replaceEquipmentItem(item);
            setEquipment();
            reloadTreeView();
        } else {
            item.getItemStashInfo().x = Integer.parseInt(choiceBoxes.get("invXChoiceBox").getValue());
            item.getItemStashInfo().y = Integer.parseInt(choiceBoxes.get("invYChoiceBox").getValue());
            item.getItemStashInfo().id = 1;
            charaOp.getCharacter().addItem(item);
            setEquipment();
            reloadTreeView();
        }
        RootController.getInstance().setBottomRightText("Item added to character.");
    }

    private void resetProperties() {
        resetAffixes();
        choiceBoxes.get("itemTierChoiceBox").getSelectionModel().selectLast();
        textFields.get("instabilityField").setText("0");
        maxImplicits();
    }

    private void resetAffixes() {
        for (int i = 4; i >= 1; i--) { //top-down because affixes are stored in a list
            ComboBox<AffixDisplayer> box = comboBoxes.get("affix" + i + "ComboBox");
            box.getSelectionModel().clearSelection();
            box.getEditor().clear();
            choiceBoxes.get("affix" + i + "TierChoiceBox").getSelectionModel().selectLast();
            textFields.get("affix" + i + "ValueField").setText("100");
            textFields.get("affixField").setText(0 + "");
            textFields.get("affixVisualField").setText(0 + "");
            affixComboBoxChangeEvent(box);
        }
    }

    private void createNewItem() {
        Item item = new AbstractItem(ItemAttributeList.getById(0), 0, 0, new ArrayList<>());
        selectedItem = new TreeItem<>(item);
        fillDataFields();
        TreeViewController.getInstance().addCustomItem(item);
        reloadTreeView();
    }

    private void copyItem() {
        getSelectedItem().ifPresent(original -> {
            Item copy = getItemCopy(original);
            selectedItem = new TreeItem<>(copy);
            fillDataFields();
            TreeViewController.getInstance().addCustomItem(copy);
            reloadTreeView();
        });
    }

    private void deleteItem() {
        getSelectedItem().ifPresent(item -> {
            if (!item.getItemStashInfo().charaEquipment) return;
            Item.getItemOwner(item).getCharacter().getEquipment().remove(item);
            setEquipment();
            reloadTreeView();
        });
    }

    private void maxItemAffixValues() {
        getSelectedItem().ifPresent(item -> {
            for (AbstractItem.AffixData affixData : item.getAffixList()) {
                affixData.value = 255;
            }
            setEquipment();
            fillDataFields();
        });
    }

    private void maxItemAffixTiers() {
        getSelectedItem().ifPresent(item -> {
            for (AbstractItem.AffixData affixData : item.getAffixList()) {
                affixData.tier = AffixTier.maxTier(affixData.type);
            }
            setEquipment();
            fillDataFields();
        });
    }

    public void bottomLeft(MouseEvent mouseEvent) {
        RootController.getInstance().setBottomLeftText("Kikkeriki!");
    }
}
