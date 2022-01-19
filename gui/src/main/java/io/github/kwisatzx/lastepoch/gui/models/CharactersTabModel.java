package io.github.kwisatzx.lastepoch.gui.models;

import io.github.kwisatzx.lastepoch.fileoperations.CharacterOperations;
import io.github.kwisatzx.lastepoch.gui.controllers.CharactersTabController;
import io.github.kwisatzx.lastepoch.gui.models.datatransfer.CharacterDTO;
import io.github.kwisatzx.lastepoch.gui.views.elements.SelectionWrapper;
import io.github.kwisatzx.lastepoch.itemdata.*;
import io.github.kwisatzx.lastepoch.itemdata.item.AbstractItem;
import io.github.kwisatzx.lastepoch.itemdata.item.Item;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class CharactersTabModel extends GuiTabModel {
    private final CharactersTabController controller;
    private final SelectionWrapper selection;

    public CharactersTabModel(CharactersTabController controller) {
        super(controller);
        this.controller = controller;
        selection = controller.getSelection();
    }

    public CharacterDTO getCharacterData() {
        Optional<CharacterOperations> charaOpOpt = getCharaOp();
        if (charaOpOpt.isPresent()) {
            CharacterOperations.Character character = charaOpOpt.get().getCharacter();
            List<String> masteredSkillNames = character.getMasteredSkills().stream()
                    .map(skillTree -> ChrSkills.getSkillDisplayNameFromId(skillTree.treeId))
                    .toList();
            List<String> toolbarSkillNames = Arrays.stream(character.getAbilityBar())
                    .map(ChrSkills::getSkillDisplayNameFromId)
                    .toList();

            return new CharacterDTO(character.getName(),
                                    character.getLevel(),
                                    character.getChrClass().name(),
                                    character.getMastery().name(),
                                    character.isHardcore(),
                                    character.isMasochist(),
                                    character.isSolo(),
                                    masteredSkillNames,
                                    toolbarSkillNames);
        } else return null;
    }

    public void toggleCharacterHardcore() {
        getCharaOp().ifPresent(charaOp -> {
            charaOp.setProperty("hardcore", charaOp.getCharacter().isHardcore() ? "false" : "true");
        });
    }

    public void toggleCharacterMasochist() {
        getCharaOp().ifPresent(charaOp -> {
            charaOp.setProperty("masochist", charaOp.getCharacter().isMasochist() ? "false" : "true");
        });
    }

    public void toggleCharacterSolo() {
        getCharaOp().ifPresent(charaOp -> {
            charaOp.setProperty("soloChallenge", charaOp.getCharacter().isSolo() ? "false" : "true");
        });
    }

    public void unlockTimelines() {
        getCharaOp().ifPresent(charaOp -> {
            if (charaOp.setTimelinesUnlocked()) controller.setBottomRightText("Timelines unlocked.");
            else controller.setBottomRightText("Error: Failed to open array data file. See log.");
        });
    }

    public void unlockWaypoints() {
        getCharaOp().ifPresent(charaOp -> {
            if (charaOp.setWaypointsUnlocked()) controller.setBottomRightText("Waypoints unlocked.");
            else controller.setBottomRightText("Error: Failed to open array data file. See log.");
        });
    }

    public void completeQuests() {
        getCharaOp().ifPresent(charaOp -> {
            if (charaOp.setQuestsCompleted()) controller.setBottomRightText("Quests completed.");
            else controller.setBottomRightText("Error: Failed to open array data file. See log.");
        });
    }

    public void maxMasteryLevels() {
        getCharaOp().ifPresent(charaOp -> {
            charaOp.getCharacter().maxMasteryLevels();
            controller.setBottomRightText("All selected masteries leveled to 20.");
        });
    }

    //TODO Dialog with more options
    public void maxMasteryNodes() {
        getCharaOp().ifPresent(charaOp -> {
            charaOp.getCharacter().maxMasteryNodes();
            controller.setBottomRightText("Picked nodes on all mastery trees have been given max points.");
        });
    }

    //TODO Dialog with more options
    public void maxPassiveNodes() {
        getCharaOp().ifPresent(charaOp -> {
            charaOp.getCharacter().maxPassiveNodes();
            controller.setBottomRightText("Picked nodes on the passive trees have been given max points.");
        });
    }

    public void removeAllEqFractures() {
        getCharaOp().ifPresent(charaOp -> {
            for (Item item : charaOp.getCharacter().getEquipment()) {
                item.setInstability(0);
            }

            setCharaEquipment();
            controller.setBottomRightText("All equipment instability (and fractures) removed.");
        });
    }

    //TODO move all implementations to charaOp class?
    public void maximizeEqImplicits() {
        getCharaOp().ifPresent(charaOp -> {
            for (Item item : charaOp.getCharacter().getEquipment()) {
                item.setImplicitValue1(255);
                item.setImplicitValue2(255);
                item.setImplicitValue3(255);
            }

            setCharaEquipment();
            controller.setBottomRightText("All equipment implicits maximized.");
        });
    }

    public void maximizeEqAffixRanges() {
        getCharaOp().ifPresent(charaOp -> {
            for (Item item : charaOp.getCharacter().getEquipment()) {
                for (AbstractItem.AffixData affixData : item.getAffixList()) {
                    affixData.value = 255;
                }
            }

            setCharaEquipment();
            controller.setBottomRightText("All equipment affix ranges maximized.");
        });
    }

    public void maximizeEqAffixTiers() {
        getCharaOp().ifPresent(charaOp -> {
            for (Item item : charaOp.getCharacter().getEquipment()) {
                for (AbstractItem.AffixData affixData : item.getAffixList()) {
                    affixData.tier = AffixTier.TIER7;
                }
            }

            setCharaEquipment();
            controller.setBottomRightText("All equipment affix tiers maximized.");
        });
    }

    public int getAffiliationForClassName(String chrClassName) {
        return Affiliation.asInt(chrClassName);
    }

    public List<String> getClassMasteryStringListFromClass(String chrClassName) {
        return ChrClass.valueOf(chrClassName).getMasteryStringList();
    }

    public List<String> getSkillListForMasteryName(String classMasteryName) {
        return ChrSkills.getSkillListForMastery(ChrClass.ClassMastery.valueOf(classMasteryName));
    }

    public List<String> getAllSkillsNameAndMasteryList() {
        return ChrSkills.getNameAndMasteryList();
    }

    public List<String> getChrClassesStringList() {
        return ChrClass.getChrClassesStringList();
    }

    public List<String> getAffixTierStringList() {
        return AffixTier.getStringList();
    }

    public String getSkillDisplayNameFromId(String treeId) {
        return ChrSkills.getSkillDisplayNameFromId(treeId);
    }

    public String getValueForBlessingTier(int blessingTier) {
        return ItemAttributeList.getById(34).getTierValues()[blessingTier];
    }

    public List<String> getBlessingsList() {
        return Arrays.stream(ItemAttributeList.getById(34).getTierValues()).toList();
    }

    public OptionalInt getBlessingIdFromName(String name) {
        return ItemAttributeList.getById(34).getTierIdFromValue(name);
    }
}

