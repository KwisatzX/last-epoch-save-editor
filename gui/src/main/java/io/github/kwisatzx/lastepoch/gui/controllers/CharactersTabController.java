package io.github.kwisatzx.lastepoch.gui.controllers;

import io.github.kwisatzx.lastepoch.fileoperations.CharacterOperations;
import io.github.kwisatzx.lastepoch.fileoperations.FileHandler;
import io.github.kwisatzx.lastepoch.gui.models.CharactersTabModel;
import io.github.kwisatzx.lastepoch.gui.models.datatransfer.CharacterDTO;
import io.github.kwisatzx.lastepoch.gui.views.CharactersTabView;
import io.github.kwisatzx.lastepoch.gui.views.elements.AffixDisplayer;
import io.github.kwisatzx.lastepoch.gui.views.elements.SelectionWrapper;
import io.github.kwisatzx.lastepoch.itemdata.*;
import io.github.kwisatzx.lastepoch.itemdata.item.AbstractItem;
import io.github.kwisatzx.lastepoch.itemdata.item.Item;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

import java.util.*;

public class CharactersTabController extends GuiTabController {
    private static CharactersTabController instance;
    private final SelectionWrapper selection;
    private final CharactersTabModel model;
    private final CharactersTabView view;

    public CharactersTabController(Pane charactersTabPane) {
        instance = this;
        model = new CharactersTabModel(this);
        view = new CharactersTabView(this, charactersTabPane);
        initialize(model);
        selection = getSelection();

        installEventHandlers(charactersTabPane);
        installBlessingsComboBoxEvents();
        installChoiceBoxEvents();
    }

    public static CharactersTabController getInstance() {
        return instance;
    }

    //TODO: remove middle-man and have View get data from Model?
    public CharacterDTO getCharacterData() {
        return model.getCharacterData();
    }

    public int getAffiliationForClassName(String chrClassName) {
        return model.getAffiliationForClassName(chrClassName);
    }

    public OptionalInt getBlessingIdFromName(String name) {
        return model.getBlessingIdFromName(name);
    }

    public List<String> getClassMasteryStringListFromClass(String chrClassName) {
        return model.getClassMasteryStringListFromClass(chrClassName);
    }

    public List<String> getChrClassesStringList() {
        return model.getChrClassesStringList();
    }

    public List<String> getAffixTierStringList() {
        return model.getAffixTierStringList();
    }

    public List<String> getSkillListForMasteryName(String classMasteryName) {
        return model.getSkillListForMasteryName(classMasteryName);
    }

    public List<String> getAllSkillsNameAndMasteryList() {
        return model.getAllSkillsNameAndMasteryList();
    }

    public String getSkillDisplayNameFromId(String treeId) {
        return model.getSkillDisplayNameFromId(treeId);
    }

    public String getValueForBlessingTier(int blessingTier) {
        return model.getValueForBlessingTier(blessingTier);
    }

    private void installEventHandlers(Node parentNode) {
        final EventHandler<ActionEvent> buttonHandler = event -> {
            if (event.getTarget() != null && event.getTarget() instanceof Button button) {
                switch (button.getId()) {
                    case "copyCharacterButton" -> copyCharacter();
                    case "completeQuestsButton" -> model.completeQuests();
                    case "unlockWaypointsButton" -> model.unlockWaypoints();
                    case "unlockTimelinesButton" -> model.unlockTimelines();
                    case "maxMasteryLevelsButton" -> model.maxMasteryLevels();
                    case "maxPointsAllMasteriesButton" -> model.maxMasteryNodes();
                    case "maxPointsAllPassivesButton" -> model.maxPassiveNodes();
                    case "copySkillsToToolbar" -> view.copySkillsToToolbar();
                    case "editSelectedItemButton" -> moveToItemEditor();
                    case "replaceAllSlotsButton" -> replaceEqWithNewItems();
                    case "addAffixButton" -> addAffixToAllEq();
                    case "removeAllFracturesButton" -> model.removeAllEqFractures();
                    case "maximizeImplicitsButton" -> model.maximizeEqImplicits();
                    case "maximizeAffixesButton" -> model.maximizeEqAffixRanges();
                    case "maximizeAffixTiersButton" -> model.maximizeEqAffixTiers();
                    case "addNewBlessingButton" -> addNewBlessingToEq();
                    case "setAllStabilityButton" -> setAllStability();
                    case "setAllCorruptionButton" -> setAllCorruption();
                }
            }
        };

        final EventHandler<ActionEvent> checkBoxHandler = event -> {
            if (event.getTarget() != null && event.getTarget() instanceof CheckBox checkBox) {
                switch (checkBox.getId()) {
                    case "hardcoreBox" -> model.toggleCharacterHardcore();
                    case "masochistBox" -> model.toggleCharacterMasochist();
                    case "soloBox" -> model.toggleCharacterSolo();
                    case "restrictMasteryBox" -> view.fillSkillChoiceBoxes();
                    case "hideWeakBlessingsCheckBox", "hideDropRateBlessingsCheckBox" -> view.fillBlessingsComboBox(
                            model.getBlessingsList());
                }
            }
        };

        view.getTextFields().get("nameField").setOnKeyTyped(event -> {
            getCharaOp().ifPresent(charaOp -> charaOp.setProperty(
                    "characterName", "\"" + view.getTextFields().get("nameField").getText() + "\""));
            refreshTreeView();
            EditorTabController.getInstance().initCharactersChoiceBox();
        });

        view.getTextFields().get("levelField").setOnKeyTyped(event -> getCharaOp().ifPresent(
                charaOp -> charaOp.setProperty("level", view.getTextFields().get("levelField").getText())));

        parentNode.addEventHandler(ActionEvent.ANY, buttonHandler);
        parentNode.addEventHandler(ActionEvent.ANY, checkBoxHandler);
    }

    private void installBlessingsComboBoxEvents() {
        ComboBox<String> blessingsComboBox = view.getBlessingsComboBox();

        final EventHandler<KeyEvent> preventClosingOnSpace = event -> {
            if (event.getCode() == KeyCode.SPACE) event.consume();
        };

        final EventHandler<KeyEvent> searchForBlessingsMatchingText = keyEvent -> {
            KeyCode kc = keyEvent.getCode();
            if (kc.isLetterKey() || kc.isDigitKey() || kc == KeyCode.BACK_SPACE) {
                List<String> filteredBlessings = new ArrayList<>();
                ItemAttribute blessings = ItemAttributeList.getById(34);
                String[] tierValues = blessings.getTierValues();
                for (int i = 0; i < blessings.getNumberOfTiers(); i++) {
                    if (tierValues[i].contains(blessingsComboBox.getEditor().getText().toUpperCase()))
                        filteredBlessings.add(tierValues[i]);
                }
                keyEvent.consume();
                view.fillBlessingsComboBox(filteredBlessings);
            }
        };

        final EventHandler<ActionEvent> onBlessingSelectionChanged = event -> {
            if (selection.isItem() && selection.getItem().get().getItemType().getDataId() == 34) {
                removeBlessingIfNameFieldEmpty();
                changeBlessingIdOnValidNameChange();
            }
        };

        blessingsComboBox.getEditor().setOnKeyReleased(searchForBlessingsMatchingText);

        ComboBoxListViewSkin<String> eventSkin = new ComboBoxListViewSkin<>(blessingsComboBox);
        eventSkin.getPopupContent().addEventFilter(KeyEvent.ANY, preventClosingOnSpace);
        blessingsComboBox.setSkin(eventSkin); //TODO move up?

        blessingsComboBox.setOnAction(onBlessingSelectionChanged);

        view.fillBlessingsComboBox(model.getBlessingsList());
    }

    private void removeBlessingIfNameFieldEmpty() {
        ComboBox<String> blessingsComboBox = view.getBlessingsComboBox();
        Item item = selection.getItem().get();

        if (blessingsComboBox.getValue() == null || blessingsComboBox.getValue().equals("")) {
            getCharaOp().ifPresent(charaOp -> {
                charaOp.getCharacter().getEquipment().removeIf(eqItem -> eqItem.equals(item) &&
                        eqItem.getItemStashInfo().id == item.getItemStashInfo().id);
            });
            setCharaEquipment();
            reloadTreeView();
        }
    }

    private void changeBlessingIdOnValidNameChange() {
        getBlessingIdFromName(view.getBlessingsComboBox().getValue()).ifPresent(blessingId -> {
            selection.getItem().get().setItemTier(blessingId);
            setCharaEquipment();
            refreshTreeView();
        });
    }

    private void installChoiceBoxEvents() {
        view.getChoiceBoxes().get("choiceBoxMastery").setOnAction(event -> {
            if (view.getCheckBoxes().get("restrictMasteryBox").isSelected()) view.fillSkillChoiceBoxes();
            setChrMastery();
        });

        view.getChoiceBoxes().get("choiceBoxClass").setOnAction(event -> {
            view.changeWhiteClassEquipment();
            view.fillMasteryChoiceBox();
            if (view.getCheckBoxes().get("restrictMasteryBox").isSelected()) view.fillSkillChoiceBoxes();
            setChrClass();
        });

        for (int i = 1; i <= 5; i++) {
            view.getChoiceBoxes().get("masteryChoice" + i).setOnAction(event -> setSkillTrees());
        }

        List<String> toolbarSkillChoiceBoxSuffixes = List.of("Q", "W", "E", "R", "RMB");
        for (String suffix : toolbarSkillChoiceBoxSuffixes) {
            view.getChoiceBoxes().get("toolbarChoice" + suffix).setOnAction(event -> setAbilityBar());
        }
    }

    @Override
    protected void fillDataFields() {
        if (selection.isCharacterOp()) setLastSelectedCharaOp(selection.getCharacterOp().get());
        else {
            Item item = selection.getItem().get();
            setLastSelectedCharaOp(Item.getItemOwner(item));
        }
        view.fillDataFields();
    }

    private ChrClass getChoiceBoxClass() {
        return ChrClass.valueOf(view.getChoiceBoxes().get("choiceBoxClass").getValue());
    }

    private ChrClass.ClassMastery getChoiceBoxMastery() {
        return ChrClass.ClassMastery.valueOf(view.getChoiceBoxes().get("choiceBoxMastery").getValue());
    }

    private void setChrClass() {
        getCharaOp().ifPresent(charaOp -> charaOp.setProperty("characterClass", getChoiceBoxClass().getId() + ""));
    }

    private void setChrMastery() {
        getCharaOp().ifPresent(charaOp -> charaOp.setProperty("chosenMastery", getChoiceBoxMastery().getId() + ""));
    }

    private void setSkillTrees() { //TODO THIS IS A DISASTER! FIX!!!
        if (getCharaOp().isEmpty() || isEventsLocked()) return;
        CharacterOperations charaOp = getCharaOp().get();
        List<CharacterOperations.Character.SkillTree> masteredSkills = charaOp.getCharacter().getMasteredSkills();

        for (int i = 0; i < 5; i++) {
            String displayName = view.getChoiceBoxes().get("masteryChoice" + (i + 1)).getValue();
            String treeId = ChrSkills.getIdAndMasteryFromSkillName(displayName.split(" \\(")[0]).getKey();
            if (treeId.equals("na28") || treeId.equals("ba1")) continue;

            //skill slots are 0 - 4
            if (i >= masteredSkills.size()) {
                int slotNumber = 4;
                for (int j = 0; j < 5; j++) {
                    for (CharacterOperations.Character.SkillTree skillTree : masteredSkills) {
                        if (j == skillTree.slotNumber) {
                            break;
                        } else {
                            slotNumber = Math.min(slotNumber, j);
                        }
                    }
                }
                masteredSkills.add(new CharacterOperations.Character.SkillTree(
                        treeId, slotNumber, CharacterOperations.Character.SkillTree.MAX_XP, new int[]{}, new int[]{}));
            } else masteredSkills.get(i).treeId = treeId;
        }

        charaOp.setSkillTreesInFileString();
    }

    private void setAbilityBar() {
        if (getCharaOp().isEmpty() || isEventsLocked()) return;
        CharacterOperations charaOp = getCharaOp().get();

        List<String> abilityBarChoiceBoxSuffixes = List.of("Q", "W", "E", "R", "RMB");
        String[] abilityBar = charaOp.getCharacter().getAbilityBar();
        for (int i = 0; i < 5; i++) {
            String displayName = view.getChoiceBoxes().get(
                    "toolbarChoice" + abilityBarChoiceBoxSuffixes.get(i)).getValue();
            String treeId = ChrSkills.getIdAndMasteryFromSkillName(displayName.split(" \\(")[0]).getKey();
            abilityBar[i] = treeId;
        }
        charaOp.setAbilityBarInFileString(abilityBar); //TODO Temp fix, replace with list or smth
    }

    private void copyCharacter() {
        if (getCharaOp().isEmpty()) return;

        CharacterOperations newCharaOp = getCharaOp().get().copyCharacter();
        if (newCharaOp == null) {
            setBottomRightText("Error: Failed to copy character. See log file.");
            return;
        }

        String oldCharacterName = newCharaOp.getProperty("characterName", String.class);
        newCharaOp.setProperty("characterName", "\"" + oldCharacterName + "Copy\"");
        newCharaOp.setProperty("slot", getFirstAvailableCharacterSlot() + "");
        newCharaOp.saveToFile();

        TreeController.getInstance().addNewCharacter(newCharaOp);
        EditorTabController.getInstance().initCharactersChoiceBox();
        setBottomRightText("Character copied and saved to file.");
    }

    private int getFirstAvailableCharacterSlot() {
        List<Integer> slots = FileHandler.getCharacterFileList().stream()
                .mapToInt(charaOp -> charaOp.getProperty("slot", int.class))
                .boxed()
                .toList();
        for (int i = 1; ; i++) {
            if (!slots.contains(i)) {
                return i;
            }
        }
    }

    private void moveToItemEditor() {
        if (selection.isItem()) switchToTab(RootController.GuiTabs.EDITOR_TAB);
    }

    private void replaceEqWithNewItems() {
        if (getCharaOp().isEmpty()) return;
        CharacterOperations charaOp = getCharaOp().get();
        List<Item> equipment = charaOp.getCharacter().getEquipment();

        List<Integer> eqContainerIds = List.of(2, 3, 6, 7, 8, 9, 10, 11, 12);
        for (Integer id : eqContainerIds) {
            int itemTier = getEqTierFromContainerId(id);
            Item item = new AbstractItem(Item.ContainerIds.getItemTypeFromContainerId(id),
                                         itemTier,
                                         0, new ArrayList<>());
            item.getItemStashInfo().id = id;
            item.getItemStashInfo().charaEquipment = true;

            boolean itemReplaced = false;
            for (Item eqItem : equipment) {
                if (eqItem.getItemStashInfo().id == id) {
                    equipment.set(equipment.indexOf(eqItem), item);
                    itemReplaced = true;
                    break;
                }
            }
            if (!itemReplaced) equipment.add(item);
        }

        setBottomRightText("Equipment items replaced.");
        setCharaEquipment();
        reloadTreeView();
    }

    private int getEqTierFromContainerId(int containerId) {
        return switch (containerId) {
            case 2 -> getTierFromChoiceBoxValue("helmetChoiceBox", 0);
            case 3 -> getTierFromChoiceBoxValue("chestplateChoiceBox", 1);
            case 6 -> getTierFromChoiceBoxValue("glovesChoiceBox", 4);
            case 7 -> getTierFromChoiceBoxValue("beltChoiceBox", 2);
            case 8 -> getTierFromChoiceBoxValue("bootsChoiceBox", 3);
            case 9 -> getTierFromChoiceBoxValue("leftRingChoiceBox", 21);
            case 10 -> getTierFromChoiceBoxValue("rightRingChoiceBox", 21);
            case 11 -> getTierFromChoiceBoxValue("amuletChoiceBox", 20);
            case 12 -> getTierFromChoiceBoxValue("relicChoiceBox", 22);
            default -> -1;
        };
    }

    private int getTierFromChoiceBoxValue(String choiceBoxId, int attributeListId) {
        String[] item = view.getChoiceBoxes().get(choiceBoxId).getValue().split(" \\[");
        String itemTierName = item[0].trim();
        return ItemAttributeList.getById(attributeListId).getTierIdFromName(itemTierName);
    }

    private void addAffixToAllEq() {
        if (getCharaOp().isEmpty()) return;
        CharacterOperations charaOp = getCharaOp().get();
        List<Item> eq = charaOp.getCharacter().getEquipment();

        AbstractItem.AffixData affixData = new AbstractItem.AffixData(
                AffixTier.valueOf(view.getChoiceBoxes().get("affixTierChoiceBox").getValue()),
                AffixDisplayer.getAffixFromDisplayName(view.getComboBoxes().get("affixComboBox").getEditor().getText()),
                255);

        List<Integer> equipmentContainerIds = new ArrayList<>(Arrays.asList(2, 3, 6, 7, 8, 9, 10, 11, 12));
        if (view.includeInventorySelected()) equipmentContainerIds.add(1);
        if (view.includeWeaponsSelected()) {
            equipmentContainerIds.add(4);
            equipmentContainerIds.add(5);
        }

        for (Item item : eq) {
            if (equipmentContainerIds.contains(item.getItemStashInfo().id)) {
                item.setAffix(affixData, Integer.parseInt(view.getChoiceBoxes().get("affixSlotChoiceBox").getValue()));
            }
        }
        setCharaEquipment();
        setBottomRightText("Affix added to all equipment.");
        reloadTreeView();
    }

    private void addNewBlessingToEq() {
        getCharaOp().ifPresent(charaOp -> {
            OptionalInt blessingId = ItemAttributeList.getById(34).getTierIdFromValue(
                    view.getBlessingsComboBox().getValue());
            if (blessingId.isEmpty()) return;
            int itemTier = blessingId.getAsInt();
            int containerId = Integer.parseInt(view.getChoiceBoxes().get("blessingSlotChoiceBox").getValue());
            if (containerId <= 7) containerId += 32;
            else containerId += 35;

            AbstractItem blessing = new AbstractItem(ItemAttributeList.getById(34), itemTier, 0, new ArrayList<>());
            blessing.getItemStashInfo().id = containerId;
            blessing.getItemStashInfo().charaEquipment = true;
            charaOp.getCharacter().addOrReplaceEquipmentItem(blessing);
            setCharaEquipment();
            reloadTreeView();
        });
    }

    private void setStabilityOrCorruption(String name) { //TODO rewrite
        if (getCharaOp().isEmpty()) return;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Set " + name + " on all timelines");
        dialog.setHeaderText(null);
        dialog.setContentText("Set " + name + ": ");
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            int value;
            try {
                value = Integer.parseInt(result.get());
            } catch (NumberFormatException e) {
                value = 0;
            }
            if (name.equals("stability")) getCharaOp().get().setAllStability(value);
            else getCharaOp().get().setAllCorruption(value);
        }
    }

    private void setAllStability() {
        setStabilityOrCorruption("stability");
    }

    private void setAllCorruption() {
        setStabilityOrCorruption("corruption");
    }

}
