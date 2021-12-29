package io.github.kwisatzx.lastepoch.gui.controllers;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kwisatzx.lastepoch.fileoperations.CharacterOperations;
import io.github.kwisatzx.lastepoch.fileoperations.FileHandler;
import io.github.kwisatzx.lastepoch.gui.views.elements.SelectionWrapper;
import io.github.kwisatzx.lastepoch.itemdata.*;
import io.github.kwisatzx.lastepoch.itemdata.item.AbstractItem;
import io.github.kwisatzx.lastepoch.itemdata.item.Item;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class CharactersTabController extends GuiTab {
    private static CharactersTabController INSTANCE;
    private final HashMap<String, CheckBox> checkBoxes;
    private ComboBox<String> blessingsComboBox;
    private ToggleButton includeWeapons;
    private ToggleButton includeInventory;

    private final SelectionWrapper selection;
    private final HashMap<String, TextField> textFields;
    private final HashMap<String, ChoiceBox<String>> choiceBoxes;
    private final HashMap<String, ComboBox<AffixDisplayer>> comboBoxes;

    public CharactersTabController(Pane charactersTabPane) {
        INSTANCE = this;
        checkBoxes = new HashMap<>();
        tabInit(charactersTabPane);
        selection = super.getSelection();
        textFields = super.getTextFields();
        choiceBoxes = super.getChoiceBoxes();
        comboBoxes = super.getComboBoxes();
        assignChildrenFromParentNode(charactersTabPane);
        installEventHandlers(charactersTabPane);
        initBlessingsComboBox();
        initChoiceBoxes();
    }

    //TODO: Extract init into CharactersTabModel
    //TODO 2. replace manual I/O from fileString with json mapping and copied object structure

    //--- UTILITY ---//
    //---------------//

    public static CharactersTabController getInstance() {
        return INSTANCE;
    }

    private ChrClass.ClassMastery getChoiceBoxMastery() {
        return ChrClass.ClassMastery.fromString(choiceBoxes.get("choiceBoxMastery").getValue());
    }

    private ChrClass getChoiceBoxClass() {
        return ChrClass.fromString(choiceBoxes.get("choiceBoxClass").getValue());
    }

    private int getAffiliationFromChoiceBoxClass() {
        return Affiliation.asInt(choiceBoxes.get("choiceBoxClass").getValue());
    }

    private List<String> filterBlessingsList(List<String> toFilter) {
        return toFilter.stream()
                .filter(str -> {
                    if (checkBoxes.get("hideWeakBlessingsCheckBox").isSelected()) {
                        if (ItemAttributeList.getById(34).getTierIdFromValue(str).getAsInt() % 2 == 0) return false;
                    }
                    if (checkBoxes.get("hideDropRateBlessingsCheckBox").isSelected()) {
                        return !str.contains("DROP RATE");
                    }
                    return true;
                }).toList();
    }

    //--- INITIALIZATION ---//
    //----------------------//

    private void assignChildrenFromParentNode(Pane parentNode) {
        for (Node node : parentNode.getChildren()) {
            if (node.getId() != null) {
                if (node instanceof CheckBox checkBox) {
                    checkBoxes.put(checkBox.getId(), checkBox);
                } else if (node.getId().equals("blessingComboBox"))
                    blessingsComboBox = (ComboBox<String>) node;
                else if (node instanceof ToggleButton button) {
                    if (button.getId().equals("includeWeaponsToggleButton")) includeWeapons = button;
                    else includeInventory = button;
                }
            }
        }
    }

    private void installEventHandlers(Node parentNode) {
        final EventHandler<ActionEvent> buttonHandler = event -> {
            if (event.getTarget() != null && event.getTarget() instanceof Button button) {
                switch (button.getId()) {
                    case "copyCharacterButton" -> copyCharacter();
                    case "completeQuestsButton" -> completeQuests();
                    case "unlockWaypointsButton" -> unlockWaypoints();
                    case "unlockTimelinesButton" -> unlockTimelines();
                    case "maxMasteryLevelsButton" -> maxMasteryLevels();
                    case "maxPointsAllMasteriesButton" -> maxMasteryNodes();
                    case "maxPointsAllPassivesButton" -> maxPassiveNodes();
                    case "copySkillsToToolbar" -> copySkillsToToolbar();
                    case "editSelectedItemButton" -> moveToItemEditor();
                    case "replaceAllSlotsButton" -> replaceEqWithNewItems();
                    case "addAffixButton" -> addAffixToAllEq();
                    case "removeAllFracturesButton" -> removeAllEqFractures();
                    case "maximizeImplicitsButton" -> maximizeEqImplicits();
                    case "maximizeAffixesButton" -> maximizeEqAffixRanges();
                    case "maximizeAffixTiersButton" -> maximizeEqAffixTiers();
                    case "addNewBlessingButton" -> addNewBlessingToEq();
                    case "setAllStabilityButton" -> setAllStability();
                    case "setAllCorruptionButton" -> setAllCorruption();
                }
            }
        };

        final EventHandler<ActionEvent> checkBoxHandler = event -> {
            if (event.getTarget() != null && event.getTarget() instanceof CheckBox checkBox) {
                selection.ifCharacterPresent(charaOp -> {
                    switch (checkBox.getId()) {
                        case "hardcoreBox" -> {
                            if (charaOp.getCharacter().isHardcore()) charaOp.setProperty("hardcore", "false");
                            else charaOp.setProperty("hardcore", "true");
                        }
                        case "masochistBox" -> {
                            if (charaOp.getCharacter().isMasochist()) charaOp.setProperty("masochist", "false");
                            else charaOp.setProperty("masochist", "true");
                        }
                        case "soloBox" -> {
                            if (charaOp.getCharacter().isSolo()) charaOp.setProperty("soloChallenge", "false");
                            else charaOp.setProperty("soloChallenge", "true");
                        }
                    }
                });
                switch (checkBox.getId()) {
                    case "restrictMasteryBox" -> initSkillChoiceBoxes();
                    case "hideWeakBlessingsCheckBox", "hideDropRateBlessingsCheckBox" -> fillBlessingsComboBox();
                }
            }
        };

        textFields.get("nameField").setOnKeyTyped(event -> {
            getCharaOp().ifPresent(charaOp -> charaOp.setProperty(
                    "characterName", "\"" + textFields.get("nameField").getText() + "\""));
            refreshTreeView();
            EditorTabController.getInstance().initCharactersChoiceBox();
        });

        textFields.get("levelField").setOnKeyTyped(event -> getCharaOp().ifPresent(
                charaOp -> charaOp.setProperty("level", textFields.get("levelField").getText())));

        parentNode.addEventHandler(ActionEvent.ANY, buttonHandler);
        parentNode.addEventHandler(ActionEvent.ANY, checkBoxHandler);
    }

    private void initBlessingsComboBox() {
        EventHandler<KeyEvent> preventClosingOnSpace = event -> {
            if (event.getCode() == KeyCode.SPACE) event.consume();
        };

        blessingsComboBox.getEditor().setOnKeyReleased(keyEvent -> {
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
                blessingsComboBox.getItems().setAll(filterBlessingsList(filteredBlessings));
            }
        });

        ComboBoxListViewSkin<String> eventSkin = new ComboBoxListViewSkin<>(blessingsComboBox);
        eventSkin.getPopupContent().addEventFilter(KeyEvent.ANY, preventClosingOnSpace);
        blessingsComboBox.setSkin(eventSkin);

        blessingsComboBox.setOnAction(event -> {
            selection.ifItemPresent(item -> {
                if (item.getItemType().getDataId() == 34) {
                    if (blessingsComboBox.getValue() == null || blessingsComboBox.getValue().equals("")) {
                        getCharaOp().ifPresent(charaOp -> {
                            charaOp.getCharacter().getEquipment().removeIf(eqItem -> eqItem.equals(item) &&
                                    eqItem.getItemStashInfo().id == item.getItemStashInfo().id);
                        });
                        setEquipment();
                        reloadTreeView();
                    }
                    ItemAttributeList.getById(34).getTierIdFromValue(blessingsComboBox.getValue()).ifPresent(
                            blessingId -> {
                                item.setItemTier(blessingId);
                                setEquipment();
                                refreshTreeView();
                            });
                }
            });
        });
        fillBlessingsComboBox();
    }

    private void fillBlessingsComboBox() {
        blessingsComboBox.getItems().setAll(filterBlessingsList(
                Arrays.stream(ItemAttributeList.getById(34).getTierValues()).toList()));
    }

    private void initChoiceBoxes() {
        initClassChoiceBox();
        initTierAndSlotChoiceBoxes();
        initWhiteEqChoiceBoxes();
        initSkillChoiceBoxes();
    }

    private void initClassChoiceBox() {
        choiceBoxes.get("choiceBoxClass").getItems().addAll(ChrClass.getStringList());
        choiceBoxes.get("choiceBoxClass").getSelectionModel().selectFirst();
        choiceBoxes.get("choiceBoxClass").setOnAction(event -> {
            changeWhiteClassEquipment();
            initMasteryChoiceBox();
            setChrClass();
        });

        initWhiteEqChoiceBoxes();
        initMasteryChoiceBox();
    }

    private void initMasteryChoiceBox() {
        ChoiceBox<String> masteryBox = choiceBoxes.get("choiceBoxMastery");

        masteryBox.getItems().setAll(
                ChrClass.fromString(choiceBoxes.get("choiceBoxClass").getValue()).getMasteryStringList());

        selection.getCharacterOp().ifPresentOrElse(charaOp -> {
            if (charaOp.getCharacter().getChrClass() == getChoiceBoxClass()) {
                masteryBox.getSelectionModel()
                        .select(charaOp.getCharacter().getMastery().name());
            } else masteryBox.getSelectionModel().selectFirst();
        }, () -> masteryBox.getSelectionModel().selectFirst());

        masteryBox.setOnAction(event -> {
            if (checkBoxes.get("restrictMasteryBox").isSelected()) initSkillChoiceBoxes();
            setChrMastery();
        });


        if (checkBoxes.get("restrictMasteryBox").isSelected()) initSkillChoiceBoxes();
    }

    private void initTierAndSlotChoiceBoxes() {
        choiceBoxes.get("affixTierChoiceBox").getItems().addAll(AffixTier.getList().stream()
                                                                        .map(AffixTier::name)
                                                                        .toList());
        choiceBoxes.get("affixTierChoiceBox").getSelectionModel().select("TIER7");

        choiceBoxes.get("affixSlotChoiceBox").getItems().addAll("1", "2", "3", "4");
        choiceBoxes.get("affixSlotChoiceBox").getSelectionModel().selectFirst();

        choiceBoxes.get("blessingSlotChoiceBox").getItems().addAll("1", "2", "3", "4", "5",
                                                                   "6", "7", "8", "9", "10");
        choiceBoxes.get("blessingSlotChoiceBox").getSelectionModel().selectFirst();
    }

    private void initWhiteEqChoiceBoxes() {
        int affiliation = getAffiliationFromChoiceBoxClass();
        fillEqChoiceBox("helmetChoiceBox", "Helmets", affiliation);
        fillEqChoiceBox("chestplateChoiceBox", "Body Armours", affiliation);
        fillEqChoiceBox("beltChoiceBox", "Belts", affiliation);
        fillEqChoiceBox("glovesChoiceBox", "Gloves", affiliation);
        fillEqChoiceBox("bootsChoiceBox", "Boots", affiliation);
        fillEqChoiceBox("relicChoiceBox", "Relics", affiliation);
        fillEqChoiceBox("amuletChoiceBox", "Amulets", affiliation);
        fillEqChoiceBox("leftRingChoiceBox", "Rings", affiliation);
        fillEqChoiceBox("rightRingChoiceBox", "Rings", affiliation);
    }

    private void changeWhiteClassEquipment() {
        int affiliation = getAffiliationFromChoiceBoxClass();
        fillEqChoiceBox("helmetChoiceBox", "Helmets", affiliation);
        fillEqChoiceBox("chestplateChoiceBox", "Body Armours", affiliation);
        fillEqChoiceBox("relicChoiceBox", "Relics", affiliation);
    }

    private void fillEqChoiceBox(String boxId, String itemTypeName, int affiliation) {
        ItemAttribute selection = ItemAttributeList.getByName(itemTypeName);

        List<String> itemTiers = new ArrayList<>();
        for (int i = 0; i < selection.getNumberOfTiers(); i++) {
            int aff = selection.getItemAffiliation()[i];
            if ((affiliation & aff) == aff || aff == Affiliation.ALL) {
                itemTiers.add(selection.getTierNames()[i] + " [" + selection.getTierValues()[i] + "] (" +
                                      Affiliation.asString(aff) + ")");
            }
        }

        ChoiceBox<String> eqBox = choiceBoxes.get(boxId);
        eqBox.getItems().setAll(itemTiers);
        eqBox.getSelectionModel().selectLast();
    }

    private void initSkillChoiceBoxes() {
        fillSkillChoiceBoxes(checkBoxes.get("restrictMasteryBox").isSelected());
    }

    private void fillSkillChoiceBoxes(boolean filterToMastery) {
        List<String> filteredSkills = null;

        if (filterToMastery) {
            filteredSkills = new ArrayList<>();
            String chrMastery = getChoiceBoxMastery().name();
            for (Map.Entry<String, Map.Entry<String, String>> entry : ChrSkills.getMap().entrySet()) {
                String mastery = entry.getValue().getValue();
                if (mastery.equals(chrMastery))
                    filteredSkills.add(entry.getKey() + " (" + mastery + ")");
            }
            filteredSkills.add("Nothing (ALL)");
        }

        for (int i = 1; i <= 5; i++) {
            ChoiceBox<String> box = choiceBoxes.get("masteryChoice" + i);
            String selection = box.getValue();
            if (filterToMastery) {
                box.getItems().setAll(filteredSkills);
                if (!selection.equals("Nothing (ALL)")) box.getItems().add(selection);
            } else {
                box.getItems().setAll(ChrSkills.getNameAndMasteryList());
                box.getSelectionModel().select(selection);
            }

            if (box.getValue() == null || box.getValue().equals("")) {
                box.getSelectionModel().select("Nothing (ALL)");
            }

            box.setOnAction(event -> setSkillTrees());
        }

        List<String> toolbarSkillChoiceBoxSuffixes = List.of("Q", "W", "E", "R", "RMB");
        for (String suffix : toolbarSkillChoiceBoxSuffixes) {
            ChoiceBox<String> box = choiceBoxes.get("toolbarChoice" + suffix);
            String selection = box.getValue();
            if (filterToMastery) {
                filteredSkills.add("Basic Attack (ALL)");
                box.getItems().setAll(filteredSkills);
                if (!selection.equals("Nothing (ALL)")) box.getItems().add(selection);
            } else {
                box.getItems().setAll(ChrSkills.getNameAndMasteryList());
                box.getSelectionModel().select(selection);
            }

            if (box.getSelectionModel().isEmpty()) {
                box.getSelectionModel().select("Nothing (ALL)");
            }

            box.setOnAction(event -> setAbilityBar());
        }
    }

    //--- INPUT/OUTPUT ---//
    //--------------------//
    //TODO: refactor to contain all event handlers here or in a separate class

    protected void fillDataFields() {
        CharacterOperations charaOp;
        Item item;
        //TODO prevent exception with custom item list

        if (selection.isCharacterOp()) charaOp = selection.getCharacterOp().get();
        else {
            item = selection.getItem().get();
            charaOp = Item.getItemOwner(item);

            if (item.getItemType().getDataId() == 34) {
                blessingsComboBox.getSelectionModel().select(
                        ItemAttributeList.getById(34).getTierValues()[item.getItemTier()]);
            }
        }
        lastSelectedChr = charaOp;

        textFields.get("nameField").setText(charaOp.getCharacter().getName());
        textFields.get("levelField").setText(charaOp.getCharacter().getLevel() + "");

        choiceBoxes.get("choiceBoxClass").getSelectionModel()
                .select(charaOp.getCharacter().getChrClass().name());

        choiceBoxes.get("choiceBoxMastery").getSelectionModel()
                .select(charaOp.getCharacter().getMastery().name());

        checkBoxes.get("hardcoreBox").setSelected(charaOp.getCharacter().isHardcore());
        checkBoxes.get("masochistBox").setSelected(charaOp.getCharacter().isMasochist());
        checkBoxes.get("soloBox").setSelected(charaOp.getCharacter().isSolo());

        eventsLockedForReading = true;
        for (int i = 1; i <= 5; i++) {
            choiceBoxes.get("masteryChoice" + i).getSelectionModel().select("Nothing (ALL)");
        }

        List<CharacterOperations.Character.SkillTree> masteredSkills = charaOp.getCharacter().getMasteredSkills();
        for (int i = 1; i <= masteredSkills.size(); i++) {
            choiceBoxes.get("masteryChoice" + i).getSelectionModel()
                    .select(ChrSkills.getDisplayNameFromIdString(masteredSkills.get(i - 1).treeId));
        }

        List<String> abilityBarChoiceBoxSuffixes = List.of("Q", "W", "E", "R", "RMB");
        String[] abilityBar = charaOp.getCharacter().getAbilityBar();
        for (int i = 0; i < 5; i++) {
            choiceBoxes.get("toolbarChoice" + abilityBarChoiceBoxSuffixes.get(i)).getSelectionModel()
                    .select(ChrSkills.getDisplayNameFromIdString(abilityBar[i]));
        }
        eventsLockedForReading = false;
    }

    private void setChrClass() {
        getCharaOp().ifPresent(charaOp -> charaOp.setProperty("characterClass", getChoiceBoxClass().getId() + ""));
    }

    private void setChrMastery() {
        getCharaOp().ifPresent(charaOp -> charaOp.setProperty("chosenMastery", getChoiceBoxMastery().getId() + ""));
    }

    private void setSkillTrees() {
        if (getCharaOp().isEmpty() || eventsLockedForReading) return;
        CharacterOperations charaOp = getCharaOp().get();
        List<CharacterOperations.Character.SkillTree> masteredSkills = charaOp.getCharacter().getMasteredSkills();

        for (int i = 0; i < 5; i++) {
            String displayName = choiceBoxes.get("masteryChoice" + (i + 1)).getValue();
            String treeId = ChrSkills.get(displayName.split(" \\(")[0]).getKey();
            if (treeId.equals("na28") || treeId.equals("ba1")) continue;

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
        if (getCharaOp().isEmpty() || eventsLockedForReading) return;
        CharacterOperations charaOp = getCharaOp().get();

        List<String> abilityBarChoiceBoxSuffixes = List.of("Q", "W", "E", "R", "RMB");
        String[] abilityBar = charaOp.getCharacter().getAbilityBar();
        for (int i = 0; i < 5; i++) {
            String displayName = choiceBoxes.get("toolbarChoice" + abilityBarChoiceBoxSuffixes.get(i)).getValue();
            String treeId = ChrSkills.get(displayName.split(" \\(")[0]).getKey();
            abilityBar[i] = treeId;
        }
        charaOp.setAbilityBarInFileString();
    }

    private void copyCharacter() {
        if (getCharaOp().isEmpty()) return;

        CharacterOperations newCharaOp = getCharaOp().get().copyCharacter();
        if (newCharaOp == null) {
            RootController.getInstance().setBottomRightText("Error: Failed to copy character. See log file.");
            return;
        }

        int newSlot;
        List<Integer> slots = FileHandler.getCharacterFileList().stream()
                .mapToInt(charaOp -> charaOp.getProperty("slot", int.class))
                .boxed()
                .toList();
        for (int i = 1; ; i++) {
            if (!slots.contains(i)) {
                newSlot = i;
                break;
            }
        }
        newCharaOp.setProperty("slot", newSlot + "");

        String oldCharacterName = newCharaOp.getProperty("characterName", String.class);
        String newCharacterName = "\"" + oldCharacterName + "Copy\"";
        newCharaOp.setProperty("characterName", newCharacterName);
        newCharaOp.saveToFile();

        TreeController.getInstance().addNewCharacter(newCharaOp);
        EditorTabController.getInstance().initCharactersChoiceBox();
        RootController.getInstance().setBottomRightText("Character copied and saved to file.");
    }

    private void unlockTimelines() {
        getCharaOp().ifPresent(charaOp -> {
            if (charaOp.setTimelinesUnlocked()) RootController.getInstance().setBottomRightText("Timelines unlocked.");
            else RootController.getInstance().setBottomRightText("Error: Failed to open array data file. See log.");
        });
    }

    private void unlockWaypoints() {
        getCharaOp().ifPresent(charaOp -> {
            if (charaOp.setWaypointsUnlocked()) RootController.getInstance().setBottomRightText("Waypoints unlocked.");
            else RootController.getInstance().setBottomRightText("Error: Failed to open array data file. See log.");
        });
    }

    private void completeQuests() {
        getCharaOp().ifPresent(charaOp -> {
            if (charaOp.setQuestsCompleted()) RootController.getInstance().setBottomRightText("Quests completed.");
            else RootController.getInstance().setBottomRightText("Error: Failed to open array data file. See log.");
        });
    }

    private void maxMasteryLevels() {
        getCharaOp().ifPresent(charaOp -> {
            charaOp.getCharacter().maxMasteryLevels();
            RootController.getInstance().setBottomRightText("All selected masteries leveled to 20.");
        });
    }

    //TODO Dialog with more options
    private void maxMasteryNodes() {
        getCharaOp().ifPresent(charaOp -> {
            charaOp.getCharacter().maxMasteryNodes();
            RootController.getInstance().setBottomRightText(
                    "Picked nodes on all mastery trees have been given max points.");
        });
    }

    //TODO Dialog with more options
    private void maxPassiveNodes() {
        getCharaOp().ifPresent(charaOp -> {
            charaOp.getCharacter().maxPassiveNodes();
            RootController.getInstance().setBottomRightText(
                    "Picked nodes on the passive trees have been given max points.");
        });
    }

    private void copySkillsToToolbar() {
        List<String> abilityBarChoiceBoxSuffixes = List.of("Q", "W", "E", "R", "RMB");
        for (int i = 1; i <= 5; i++) {
            choiceBoxes.get("toolbarChoice" + abilityBarChoiceBoxSuffixes.get(i - 1)).getSelectionModel()
                    .select(choiceBoxes.get("masteryChoice" + i).getValue());
        }
    }

    private void moveToItemEditor() {
        if (selection.isItem()) RootController.getInstance().switchToTab(RootController.GuiTabs.EDITOR_TAB);
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

        RootController.getInstance().setBottomRightText("Equipment items replaced.");
        setEquipment();
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
        String[] item = choiceBoxes.get(choiceBoxId).getValue().split(" \\[");
        String itemTierName = item[0].trim();
        return ItemAttributeList.getById(attributeListId).getTierIdFromName(itemTierName);
    }

    private void addAffixToAllEq() {
        if (getCharaOp().isEmpty()) return;
        CharacterOperations charaOp = getCharaOp().get();
        List<Item> eq = charaOp.getCharacter().getEquipment();

        AbstractItem.AffixData affixData = new AbstractItem.AffixData(
                AffixTier.valueOf(choiceBoxes.get("affixTierChoiceBox").getValue()),
                AffixDisplayer.getAffixFromDisplayName(comboBoxes.get("affixComboBox").getEditor().getText()),
                255);

        List<Integer> equipmentContainerIds = new ArrayList<>(Arrays.asList(2, 3, 6, 7, 8, 9, 10, 11, 12));
        if (includeInventory.isSelected()) equipmentContainerIds.add(1);
        if (includeWeapons.isSelected()) {
            equipmentContainerIds.add(4);
            equipmentContainerIds.add(5);
        }

        for (Item item : eq) {
            if (equipmentContainerIds.contains(item.getItemStashInfo().id)) {
                item.setAffix(affixData, Integer.parseInt(choiceBoxes.get("affixSlotChoiceBox").getValue()));
            }
        }
        setEquipment();
        RootController.getInstance().setBottomRightText("Affix added to all equipment.");
        reloadTreeView();
    }

    private void removeAllEqFractures() {
        getCharaOp().ifPresent(charaOp -> {
            for (Item item : charaOp.getCharacter().getEquipment()) {
                item.setInstability(0);
            }

            setEquipment();
            RootController.getInstance().setBottomRightText("All equipment instability (and fractures) removed.");
        });
    }

    //TODO move all implementations to charaOp class
    private void maximizeEqImplicits() {
        getCharaOp().ifPresent(charaOp -> {
            for (Item item : charaOp.getCharacter().getEquipment()) {
                item.setImplicitValue1(255);
                item.setImplicitValue2(255);
                item.setImplicitValue3(255);
            }

            setEquipment();
            RootController.getInstance().setBottomRightText("All equipment implicits maximized.");
        });
    }

    private void maximizeEqAffixRanges() {
        getCharaOp().ifPresent(charaOp -> {
            for (Item item : charaOp.getCharacter().getEquipment()) {
                for (AbstractItem.AffixData affixData : item.getAffixList()) {
                    affixData.value = 255;
                }
            }

            setEquipment();
            RootController.getInstance().setBottomRightText("All equipment affix ranges maximized.");
        });
    }

    private void maximizeEqAffixTiers() {
        getCharaOp().ifPresent(charaOp -> {
            for (Item item : charaOp.getCharacter().getEquipment()) {
                for (AbstractItem.AffixData affixData : item.getAffixList()) {
                    affixData.tier = AffixTier.TIER7;
                }
            }

            setEquipment();
            RootController.getInstance().setBottomRightText("All equipment affix tiers maximized.");
        });
    }

    private void addNewBlessingToEq() {
        getCharaOp().ifPresent(charaOp -> {
            OptionalInt blessingId = ItemAttributeList.getById(34).getTierIdFromValue(blessingsComboBox.getValue());
            if (blessingId.isEmpty()) return;
            int itemTier = blessingId.getAsInt();
            int containerId = Integer.parseInt(choiceBoxes.get("blessingSlotChoiceBox").getValue());
            if (containerId <= 7) containerId += 32;
            else containerId += 35;

            AbstractItem blessing = new AbstractItem(ItemAttributeList.getById(34), itemTier, 0, new ArrayList<>());
            blessing.getItemStashInfo().id = containerId;
            blessing.getItemStashInfo().charaEquipment = true;
            charaOp.getCharacter().replaceEquipmentItem(blessing);
            setEquipment();
            reloadTreeView();
        });
    }

    private void setStabilityOrCorruption(String name) {
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

    private static class ChrSkills {
        private static TreeMap<String, Map.Entry<String, String>> skills;

        static {
            readFromFile();
        }

        static Map.Entry<String, String> get(String name) {
            return skills.get(name);
        }

        static List<String> getNameAndMasteryList() {
            return skills.keySet().stream()
                    .map(key -> key + " (" + skills.get(key).getValue() + ")")
                    .toList();
        }

        static Map<String, Map.Entry<String, String>> getMap() {
            return skills;
        }

        static String getDisplayNameFromIdString(String id) {
            for (Map.Entry<String, Map.Entry<String, String>> entry : skills.entrySet()) {
                if (entry.getValue().getKey().equals(id))
                    return entry.getKey() + " (" + entry.getValue().getValue() + ")";
            }
            return "";
        }

        private static void readFromFile() {
//            Path file = Paths.get("item data/Skills.json");
            InputStream fileStream = CharactersTabController.class.getResourceAsStream("/item data/Skills.json");
            if (fileStream != null) {
                ObjectMapper objectMapper = new ObjectMapper()
                        .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
                try {
                    skills = objectMapper.readValue(fileStream, new TypeReference<>() {});
                } catch (IOException e) {e.printStackTrace();}
            } else System.err.println("JSON list not found for character skills!"); //TODO: Logger
        }
    }
}
