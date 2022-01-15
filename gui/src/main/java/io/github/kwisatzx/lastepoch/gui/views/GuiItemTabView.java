package io.github.kwisatzx.lastepoch.gui.views;

import io.github.kwisatzx.lastepoch.fileoperations.FileHandler;
import io.github.kwisatzx.lastepoch.gui.controllers.GuiItemTabController;
import io.github.kwisatzx.lastepoch.gui.views.elements.AffixDisplayer;
import io.github.kwisatzx.lastepoch.gui.views.elements.SelectionWrapper;
import io.github.kwisatzx.lastepoch.itemdata.*;
import io.github.kwisatzx.lastepoch.itemdata.item.AbstractItem;
import io.github.kwisatzx.lastepoch.itemdata.item.Item;
import io.github.kwisatzx.lastepoch.itemdata.item.ItemUtils;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public abstract class GuiItemTabView extends GuiTabView {
    private final GuiItemTabController controller;
    private final SelectionWrapper selection;
    private final HashMap<String, TextField> textFields;
    private final HashMap<String, ChoiceBox<String>> choiceBoxes;
    private final HashMap<String, ComboBox<AffixDisplayer>> comboBoxes;

    public GuiItemTabView(GuiItemTabController controller, Pane rootPane) {
        super(rootPane);
        this.controller = controller;
        selection = controller.getSelection();
        textFields = getTextFields();
        choiceBoxes = getChoiceBoxes();
        comboBoxes = getComboBoxes();
        initChoiceBoxes();
    }

    private int getAffiliationFromChoiceBox() {
        return Affiliation.asInt(choiceBoxes.get("classChoiceBox").getValue());
    }

    protected ItemAttribute getItemTypeFromChoiceBox() {
        return ItemAttributeList.getByName(choiceBoxes.get("itemTypeChoiceBox").getValue());
    }

    protected int getChoiceBoxItemTierId() {
        String tierName = choiceBoxes.get("itemTierChoiceBox").getValue().split(" \\[")[0];
        for (ItemAttribute itemType : ItemAttributeList.getList()) {
            int id = itemType.getTierIdFromName(tierName);
            if (id != -1) return id;
        }
        controller.getLogger(getClass()).error("Cannot find tier id from itemTierChoiceBox.");
        throw new RuntimeException("Cannot find tier id from itemTierChoiceBox.");
    }

    private ChrClass getChoiceBoxClass() {
        return ChrClass.fromString(choiceBoxes.get("classChoiceBox").getValue());
    }

    public Item getUiItemSettings() {
        ensureCorrectNumbersInFields();
        return new AbstractItem(getItemTypeFromChoiceBox(),
                                getChoiceBoxItemTierId(),
                                Integer.parseInt(textFields.get("affixVisualField").getText()),
                                Item.percentOf255ToValue(textFields.get("implicit1Field").getText()),
                                Item.percentOf255ToValue(textFields.get("implicit2Field").getText()),
                                Item.percentOf255ToValue(textFields.get("implicit3Field").getText()),
                                Integer.parseInt(textFields.get("instabilityField").getText()),
                                0, null);
    }

    private void setClassChoiceBoxForItem(Item item) {
        int selectedItemAffiliation = item.getItemType().getItemAffiliation()[item.getItemTier()];
        choiceBoxes.get("classChoiceBox").getSelectionModel().select(Affiliation.asString(selectedItemAffiliation));
    }

    protected List<String> getItemTiersForAffiliation(ItemAttribute selectedItemType, int affiliation) {
        List<String> itemTiers = new ArrayList<>();
        for (int i = 0; i < selectedItemType.getNumberOfTiers(); i++) {
            int itemAff = selectedItemType.getItemAffiliation()[i];
            if ((affiliation & itemAff) == itemAff || itemAff == Affiliation.ALL) {
                itemTiers.add(ItemUtils.getItemTierNameString(selectedItemType, i));
            }
        }
        return itemTiers;
    }

    private void fillItemTierChoiceBox() {
        List<String> itemTiers = getItemTiersForAffiliation(getItemTypeFromChoiceBox(),
                                                            getAffiliationFromChoiceBox());
        choiceBoxes.get("itemTierChoiceBox").getItems().setAll(itemTiers);
    }

    @Override
    public void fillDataFields() {
        if (selection.isItem() && selection.getItem().get().getItemType().getDataId() != 34) { //blessings
            choiceBoxes.get("itemTypeChoiceBox").setDisable(false); //QoL
            fillCommonDataFields();
        }
    }

    private void fillCommonDataFields() {
        controller.lockEvents();
        Item item = selection.getItem().get();

        choiceBoxes.get("itemTypeChoiceBox").getSelectionModel().select(item.getItemType().getName());
        setClassChoiceBoxForItem(item);

        fillItemTierChoiceBox();
        String itemName = ItemUtils.getItemTierNameString(item.getItemType(), item.getItemTier());
        choiceBoxes.get("itemTierChoiceBox").getSelectionModel().select(itemName);

        intToTextField("affixVisualField", item.getAffixNumberVisual());
        intToTextField("implicit1Field", Item.valueToPercentOf255(item.getImplicitValue1()));
        intToTextField("implicit2Field", Item.valueToPercentOf255(item.getImplicitValue2()));
        intToTextField("implicit3Field", Item.valueToPercentOf255(item.getImplicitValue3()));
        intToTextField("instabilityField", Math.min(item.getInstability(), 100));
        intToTextField("affixField", item.getAffixNumber());
        loadAffixesFromItem(item);
        controller.unlockEvents();
    }

    private void initChoiceBoxes() {
        initClassChoiceBox();
        initItemTypeChoiceBox();
        initItemTierChoiceBox();
        initPreviewValuesChoiceBox();
        initAffixTierChoiceBoxes();
        initStashXYChoiceBoxes();
        initStashTabNamesChoiceBox();
    }

    private void loadAffixesFromItem(Item item) {
        List<AbstractItem.AffixData> affixList = item.getAffixList();
        for (int i = 1; i <= 4; i++) {
            ComboBox<AffixDisplayer> affixBox = comboBoxes.get("affix" + i + "ComboBox");
            ChoiceBox<String> affixTierBox = choiceBoxes.get("affix" + i + "TierChoiceBox");

            if (i > affixList.size()) {
                affixBox.getSelectionModel().clearSelection();
                affixBox.getEditor().clear();

                affixTierBox.getItems().setAll(AffixTier.getList().stream()
                                                       .map(AffixTier::name)
                                                       .toList());
                affixTierBox.getSelectionModel().select("TIER7");

                intToTextField("affix" + i + "ValueField", Item.valueToPercentOf255(255));
                continue;
            }

            affixBox.getSelectionModel().select(new AffixDisplayer(affixList.get(i - 1).type));
            intToTextField("affix" + i + "ValueField", Item.valueToPercentOf255(affixList.get(i - 1).value));

            if (affixList.get(i - 1).type.getNumberOfTiers() == 1) {
                affixTierBox.getItems().setAll("TIER1");
            } else {
                if (!affixTierBox.getItems().contains("TIER2")) {
                    affixTierBox.getItems().setAll(AffixTier.getList().stream()
                                                           .map(AffixTier::name)
                                                           .toList());
                }
            }
            affixTierBox.getSelectionModel().select(affixList.get(i - 1).tier.toString());
        }
    }

    //TODO: Move all events to Controller?
    private void initClassChoiceBox() {
        ChoiceBox<String> choiceBox = choiceBoxes.get("classChoiceBox");
        choiceBox.getItems().addAll(Affiliation.getStringList());
        choiceBox.getSelectionModel().select("Any");
        choiceBox.setOnAction(event -> {
            if (!controller.isEventsLocked()) {
                fillItemTierChoiceBox();
                controller.setItem();
            }
        });
    }

    private void initItemTypeChoiceBox() {
        ChoiceBox<String> choiceBox = choiceBoxes.get("itemTypeChoiceBox");
        choiceBox.getItems().addAll(ItemAttributeList.getListAsStrings());
        choiceBox.getItems().remove("Blessings");
        choiceBox.setOnAction(event -> {
            if (controller.isEventsLocked()) return;
            selection.ifItemPresent(this::setClassChoiceBoxForItem);
            fillItemTierChoiceBox();
            choiceBoxes.get("itemTierChoiceBox").getSelectionModel().selectFirst();
            controller.setItem();
        });
        choiceBox.setDisable(true);
    }

    private void initItemTierChoiceBox() {
        choiceBoxes.get("itemTierChoiceBox").setOnAction(event -> {
            if (!controller.isEventsLocked()) controller.setItem();
        });
    }

    private void initPreviewValuesChoiceBox() {
        ChoiceBox<String> choiceBox = choiceBoxes.get("affixPreviewValuesChoiceBox");
        choiceBox.getItems().addAll("For Tier 7", "For Tier 1", "No values");
        choiceBox.getSelectionModel().select("For Tier 7");
        choiceBox.setOnAction(event -> {
            AffixDisplayer.setPreviewChoice(choiceBox.getValue());

            for (ComboBox<AffixDisplayer> comboBox : comboBoxes.values()) {
                comboBox.getItems().setAll(AffixDisplayer.getList());
                if (!comboBox.getEditor().getText().equals("")) {
                    comboBox.getSelectionModel().select(comboBox.getValue().getAffix().getAffixListId());
                }
            }
        });
    }

    private void initAffixTierChoiceBoxes() {
        choiceBoxes.values().stream()
                .filter(box -> box.getId().contains("TierChoiceBox") &&
                        !box.getId().equals("itemTierChoiceBox"))
                .forEach(box -> {
                    box.getItems().addAll(AffixTier.getList().stream()
                                                  .map(AffixTier::name)
                                                  .toList());
                    box.getSelectionModel().select("TIER7");
                });
    }

    private void initStashXYChoiceBoxes() {
        choiceBoxes.get("stashXChoiceBox").getItems().addAll(IntStream.rangeClosed(0, 11)
                                                                     .mapToObj(String::valueOf)
                                                                     .toList());
        choiceBoxes.get("stashXChoiceBox").getSelectionModel().select(0);

        choiceBoxes.get("stashYChoiceBox").getItems().addAll(IntStream.rangeClosed(0, 16)
                                                                     .mapToObj(String::valueOf)
                                                                     .toList());
        choiceBoxes.get("stashYChoiceBox").getSelectionModel().select(0);
    }

    private void initStashTabNamesChoiceBox() {
        choiceBoxes.get("stashTabNamesChoiceBox").getItems().addAll(
                FileHandler.getStashFile().getStashTabs().stream()
                        .map(tab -> tab.getDisplayName())
                        .toList());
        choiceBoxes.get("stashTabNamesChoiceBox").getSelectionModel().selectFirst();
    }

    public void ensureCorrectNumbersInFields() {
        for (TextField field : textFields.values()) {
            if (field.getId().equals("stashNameField")) continue;
            try {
                Integer.parseInt(field.getText());
            } catch (NumberFormatException e) {
                field.setText("0");
            }
        }
    }
}
