package io.github.kwisatzx.lastepoch.gui.views;

import io.github.kwisatzx.lastepoch.gui.controllers.CharactersTabController;
import io.github.kwisatzx.lastepoch.gui.models.datatransfer.CharacterDTO;
import io.github.kwisatzx.lastepoch.gui.views.elements.AffixDisplayer;
import io.github.kwisatzx.lastepoch.gui.views.elements.SelectionWrapper;
import io.github.kwisatzx.lastepoch.itemdata.Affiliation;
import io.github.kwisatzx.lastepoch.itemdata.ItemAttribute;
import io.github.kwisatzx.lastepoch.itemdata.ItemAttributeList;
import io.github.kwisatzx.lastepoch.itemdata.item.Item;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CharactersTabView extends GuiTabView {
    final private CharactersTabController controller;
    private final SelectionWrapper selection;
    private final HashMap<String, CheckBox> checkBoxes;
    private ComboBox<String> blessingsComboBox;
    private final HashMap<String, TextField> textFields;
    private final HashMap<String, ChoiceBox<String>> choiceBoxes;
    private final HashMap<String, ComboBox<AffixDisplayer>> comboBoxes;
    private ToggleButton includeWeapons;
    private ToggleButton includeInventory;

    public CharactersTabView(CharactersTabController controller, Pane rootPane) {
        super(rootPane);
        this.controller = controller;
        selection = controller.getSelection();
        checkBoxes = new HashMap<>();
        textFields = getTextFields();
        choiceBoxes = getChoiceBoxes();
        comboBoxes = getComboBoxes();
        groupFieldsFromParentNode(rootPane);
        initChoiceBoxes();
    }

    private int getAffiliationFromChoiceBoxClass() {
        return controller.getAffiliationForClassName(choiceBoxes.get("choiceBoxClass").getValue());
    }

    private void groupFieldsFromParentNode(Pane parentNode) {
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

    public void fillBlessingsComboBox(List<String> blessingsList) {
        blessingsComboBox.getItems().setAll(filterBlessingsList(blessingsList));
    }

    public List<String> filterBlessingsList(List<String> toFilter) {
        return toFilter.stream()
                .filter(str -> {
                    if (checkBoxes.get("hideWeakBlessingsCheckBox").isSelected()) {
                        if (controller.getBlessingIdFromName(str).getAsInt() % 2 == 0) return false;
                    }
                    if (checkBoxes.get("hideDropRateBlessingsCheckBox").isSelected()) return !str.contains("DROP RATE");
                    return true;
                }).toList();
    }

    public void copySkillsToToolbar() {
        //TODO add get'ListName' to view and replace occurrences
        List<String> abilityBarChoiceBoxSuffixes = List.of("Q", "W", "E", "R", "RMB");
        for (int i = 1; i <= 5; i++) {
            choiceBoxes.get("toolbarChoice" + abilityBarChoiceBoxSuffixes.get(i - 1)).getSelectionModel()
                    .select(choiceBoxes.get("masteryChoice" + i).getValue());
        }
    }

    private void initChoiceBoxes() {
        initClassChoiceBox();
        initTierAndSlotChoiceBoxes();
        initWhiteEqChoiceBoxes();
        fillMasteryChoiceBox();
        fillSkillChoiceBoxes();
    }

    private void initClassChoiceBox() {
        choiceBoxes.get("choiceBoxClass").getItems().addAll(controller.getChrClassesStringList());
        choiceBoxes.get("choiceBoxClass").getSelectionModel().selectFirst();
    }

    public void fillMasteryChoiceBox() {
        ChoiceBox<String> masteryBox = choiceBoxes.get("choiceBoxMastery");
        String choiceBoxClass = choiceBoxes.get("choiceBoxClass").getValue();
        masteryBox.getItems().setAll(controller.getClassMasteryStringListFromClass(choiceBoxClass));
        masteryBox.getSelectionModel().selectFirst();
    }

    private void initTierAndSlotChoiceBoxes() {
        choiceBoxes.get("affixTierChoiceBox").getItems().addAll(controller.getAffixTierStringList());
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

    public void changeWhiteClassEquipment() {
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

    public void fillSkillChoiceBoxes() { //TODO add cache
        List<String> skillsList;
        boolean filterToMastery = checkBoxes.get("restrictMasteryBox").isSelected();

        if (filterToMastery) {
            String classMasteryName = choiceBoxes.get("choiceBoxMastery").getValue();
            skillsList = controller.getSkillListForMasteryName(classMasteryName);
        } else skillsList = controller.getAllSkillsNameAndMasteryList();

        for (int i = 1; i <= 5; i++) {
            ChoiceBox<String> skillMasteryChoiceBox = choiceBoxes.get("masteryChoice" + i);
            String previouslySelectedSkill = skillMasteryChoiceBox.getValue();
            skillMasteryChoiceBox.getItems().setAll(skillsList);

            if (previouslySelectedSkill == null || previouslySelectedSkill.equals("")) {
                skillMasteryChoiceBox.getSelectionModel().select("Nothing (ALL)");
            } else {
                if (!skillsList.contains(previouslySelectedSkill))
                    skillMasteryChoiceBox.getItems().add(previouslySelectedSkill);
                skillMasteryChoiceBox.getSelectionModel().select(previouslySelectedSkill);
            }
        }

        List<String> toolbarSkillChoiceBoxSuffixes = List.of("Q", "W", "E", "R", "RMB");
        for (String suffix : toolbarSkillChoiceBoxSuffixes) {
            ChoiceBox<String> toolbarChoiceBox = choiceBoxes.get("toolbarChoice" + suffix);
            String previouslySelectedSkill = toolbarChoiceBox.getValue();
            toolbarChoiceBox.getItems().setAll(skillsList);
            toolbarChoiceBox.getItems().add("Basic Attack (ALL)");

            if (previouslySelectedSkill == null || previouslySelectedSkill.equals("")) {
                toolbarChoiceBox.getSelectionModel().select("Nothing (ALL)");
            } else {
                if (!skillsList.contains(previouslySelectedSkill))
                    toolbarChoiceBox.getItems().add(previouslySelectedSkill);
                toolbarChoiceBox.getSelectionModel().select(previouslySelectedSkill);
            }
        }
    }

    public void fillDataFields() {
        controller.lockEvents();
        //TODO prevent exception with custom item list
        if (selection.isItem()) {
            Item item = selection.getItem().get();
            if (item.getItemType().getDataId() == 34)
                blessingsComboBox.getSelectionModel().select(controller.getValueForBlessingTier(item.getItemTier()));
        }

        CharacterDTO characterData = controller.getCharacterData();
        if (characterData == null) return;
        textFields.get("nameField").setText(characterData.name());
        textFields.get("levelField").setText(characterData.level() + "");
        choiceBoxes.get("choiceBoxClass").getSelectionModel().select(characterData.chrClass());
        choiceBoxes.get("choiceBoxMastery").getSelectionModel().select(characterData.classMastery());
        checkBoxes.get("hardcoreBox").setSelected(characterData.isHardcore());
        checkBoxes.get("masochistBox").setSelected(characterData.isMasochist());
        checkBoxes.get("soloBox").setSelected(characterData.isSolo());

        for (int i = 1; i <= 5; i++) {
            choiceBoxes.get("masteryChoice" + i).getSelectionModel().select("Nothing (ALL)");
        }

        List<String> masteredSkillNames = characterData.masteredSkillsDisplayNames();
        for (int i = 1; i <= masteredSkillNames.size(); i++) {
            choiceBoxes.get("masteryChoice" + i).getSelectionModel().select(masteredSkillNames.get(i - 1));
        }

        List<String> toolbarSkillNames = characterData.toolbarSkillsDisplayNames();
        List<String> abilityBarChoiceBoxSuffixes = List.of("Q", "W", "E", "R", "RMB");
        for (int i = 0; i < 5; i++) {
            choiceBoxes.get("toolbarChoice" + abilityBarChoiceBoxSuffixes.get(i))
                    .getSelectionModel().select(toolbarSkillNames.get(i));
        }

        controller.unlockEvents();
    }

    public ComboBox<String> getBlessingsComboBox() {
        return blessingsComboBox;
    }

    public HashMap<String, CheckBox> getCheckBoxes() {
        return checkBoxes;
    }

    public boolean includeWeaponsSelected() {
        return includeWeapons.isSelected();
    }

    public boolean includeInventorySelected() {
        return includeInventory.isSelected();
    }
}
