package io.github.kwisatzx.lastepoch.gui.controllers;

import io.github.kwisatzx.lastepoch.fileoperations.FileHandler;
import io.github.kwisatzx.lastepoch.itemdata.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public abstract class GuiItemTab extends GuiTab {

    protected void itemTabInit(Pane rootPane) {
        tabInit(rootPane);
        installCommonEventHandlers(rootPane);
        initChoiceBoxes();
    }

    protected Optional<Item> getSelectedItem() {
        if (selectedItem != null && selectedItem.getValue() != null)
            return Optional.ofNullable(selectedItem.getValue().getItemObj());
        else return Optional.empty();
    }

    private ChrClass getChoiceBoxClass() {
        return ChrClass.fromString(choiceBoxes.get("classChoiceBox").getValue());
    }

    private int getAffiliationFromClassChoiceBox() {
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
        return -1;
    }

    private void setItem() {
        if (eventsLockedForReading) return;
        getSelectedItem().ifPresent(item -> {
            ensureNumbersInFields();
            item.setItemType(getItemTypeFromChoiceBox());
            item.setItemTier(getChoiceBoxItemTierId());
            item.setImplicitValue1(Item.percentOf255ToValue(textFields.get("implicit1Field").getText()));
            item.setImplicitValue2(Item.percentOf255ToValue(textFields.get("implicit2Field").getText()));
            item.setImplicitValue3(Item.percentOf255ToValue(textFields.get("implicit3Field").getText()));
            item.setAffixNumberVisual(Integer.parseInt(textFields.get("affixVisualField").getText()));
            item.setInstability(Integer.parseInt(textFields.get("instabilityField").getText()));
            setEquipment();
            reloadTreeView();
        });
    }

    protected AbstractItem getItemCopy(Item original) {
        List<AbstractItem.AffixData> newAffixList = new ArrayList<>();
        for (int i = 0; i < original.getAffixList().size(); i++) {
            AbstractItem.AffixData toCopy = original.getAffixList().get(i);
            newAffixList.add(new AbstractItem.AffixData(toCopy.tier, toCopy.type, toCopy.value));
        }

        return new AbstractItem(original.getItemType(),
                                original.getItemTier(),
                                original.getAffixNumberVisual(),
                                original.getImplicitValue1(),
                                original.getImplicitValue2(),
                                original.getImplicitValue3(),
                                original.getInstability(),
                                original.getAffixNumber(),
                                newAffixList);
    }

    @Override
    protected void fillDataFields() {
        if (getSelectedItem().isPresent() && getSelectedItem().get().getItemType().getDataId() != 34) { //blessings
            choiceBoxes.get("itemTypeChoiceBox").setDisable(false); //QoL
            fillCommonDataFields();
        }
    }

    private void fillCommonDataFields() {
        eventsLockedForReading = true;
        Item item = selectedItem.getValue().getItemObj();
        choiceBoxes.get("itemTypeChoiceBox").getSelectionModel()
                .select(item.getItemType().getName());
        choiceBoxes.get("itemTierChoiceBox").getSelectionModel()
                .select(item.getItemTier());
        intToTextField("affixVisualField", item.getAffixNumberVisual());
        intToTextField("implicit1Field", Item.valueToPercentOf255(item.getImplicitValue1()));
        intToTextField("implicit2Field", Item.valueToPercentOf255(item.getImplicitValue2()));
        intToTextField("implicit3Field", Item.valueToPercentOf255(item.getImplicitValue3()));
        intToTextField("instabilityField", Math.min(item.getInstability(), 100));
        int numOfAffixes = item.getAffixNumber();
        intToTextField("affixField", numOfAffixes);
        loadAffixesFromItem(item);
        eventsLockedForReading = false;
    }

    private void loadAffixesFromItem(Item item) {
        if (!eventsLockedForReading) eventsLockedForReading = true;
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

    private void initChoiceBoxes() {
        EventHandler<ActionEvent> choiceBoxSelectEvent = event -> {
            if (eventsLockedForReading && !((Node) event.getSource()).getId().equals("classChoiceBox")) {
                getSelectedItem().ifPresent(item -> {
                    if (item.equals(Item.UNKNOWN_ITEM)) return;
                    int selectedItemAffiliation = item.getItemType().getItemAffiliation()[item.getItemTier()];
                    choiceBoxes.get("classChoiceBox").getSelectionModel()
                            .select(Affiliation.asString(selectedItemAffiliation));
                });
            }
            int affiliation = getAffiliationFromClassChoiceBox();
            ItemAttribute selection = getItemTypeFromChoiceBox();

            List<String> itemTiers = new ArrayList<>();
            for (int i = 0; i < selection.getNumberOfTiers(); i++) {
                int aff = selection.getItemAffiliation()[i];
                if ((affiliation & aff) == aff || aff == Affiliation.ALL) {
                    itemTiers.add(selection.getTierNames()[i] + " [" + selection.getTierValues()[i] + "] (" +
                                          Affiliation.asString(aff) + ")");
                }
            }

            //TODO fillCommonDataFields relies on this event firing to fill tier box with items and select
            //the correct one. Refactor: separate the methods and apply eventsLockedForReading block
            ChoiceBox<String> itemTierBox = choiceBoxes.get("itemTierChoiceBox");
            itemTierBox.getItems().setAll(itemTiers);
            getSelectedItem().ifPresentOrElse(item -> {
                int itemTier = item.getItemTier();
                String itemName = item.getItemType().getTierNames()[itemTier] + " ["
                        + item.getItemType().getTierValues()[itemTier] + "] ("
                        + Affiliation.asString(item.getItemType().getItemAffiliation()[itemTier]) + ")";
                itemTierBox.getSelectionModel().select(itemName);
            }, () -> itemTierBox.getSelectionModel().selectFirst());

            setItem();
        };

        initClassChoiceBox(event -> {
            boolean previousState = eventsLockedForReading;
            eventsLockedForReading = true;
            choiceBoxSelectEvent.handle(event);
            eventsLockedForReading = previousState;
        });
        initItemTypeChoiceBox(choiceBoxSelectEvent);
        choiceBoxes.get("itemTierChoiceBox").setOnAction(event -> setItem());
        initPreviewValuesChoiceBox();
        initAffixTierChoiceBoxes();
        initStashXYChoiceBoxes();
        initStashTabNamesChoiceBox();
    }

    private void initClassChoiceBox(EventHandler<ActionEvent> choiceBoxSelectEvent) {
        ChoiceBox<String> choiceBox = choiceBoxes.get("classChoiceBox");
        choiceBox.getItems().addAll(Affiliation.getStringList());
        choiceBox.getSelectionModel().select("Any");
        choiceBox.setOnAction(choiceBoxSelectEvent);
    }

    private void initItemTypeChoiceBox(EventHandler<ActionEvent> choiceBoxSelectEvent) {
        ChoiceBox<String> choiceBox = choiceBoxes.get("itemTypeChoiceBox");
        choiceBox.getItems().addAll(ItemAttributeList.getListAsStrings());
        choiceBox.getItems().remove("Blessings");
        choiceBox.setOnAction(choiceBoxSelectEvent);
        choiceBox.setDisable(true);
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

    private void initCommonTextFieldChangeEvents() {
        textFields.get("affixVisualField").setOnKeyTyped(event -> setItem());
        textFields.get("implicit1Field").setOnKeyTyped(event -> setItem());
        textFields.get("implicit2Field").setOnKeyTyped(event -> setItem());
        textFields.get("implicit3Field").setOnKeyTyped(event -> setItem());
        textFields.get("instabilityField").setOnKeyTyped(event -> setItem());
    }

    private void ensureNumbersInFields() {
        for (TextField field : textFields.values()) {
            if (field.getId().equals("stashNameField")) continue;
            try {
                Integer.parseInt(field.getText());
            } catch (NumberFormatException e) {
                field.setText("0");
            }
        }
    }

    private void installCommonEventHandlers(Node parentNode) {
        final EventHandler<ActionEvent> buttonHandler = event -> {
            if (event.getTarget() != null && event.getTarget() instanceof Button button) {
                switch (button.getId()) {
                    case "affixInfoButton" -> affixInfo();
                    case "implicit1InfoButton" -> implicit1Info();
                    case "implicit2InfoButton" -> implicit2Info();
                    case "implicit3InfoButton" -> implicit3Info();
                    case "affixInfoButton2" -> affixInfo2();
                    case "restrictAffixesInfoButton" -> restrictAffixesInfo();
                    case "affixesInfoButton" -> affixesInfo();
                    case "maxImplicitsButton" -> maxImplicits();
                }
            }
            if (event.getTarget() != null && event.getTarget() instanceof CheckBox) {
                event.consume();
                toggleRestrictAffixes();
            }
        };
        parentNode.addEventHandler(ActionEvent.ANY, buttonHandler);

        initAffixChangeEvents();
        initCommonTextFieldChangeEvents();
    }

    protected void initAffixChangeEvents() {
        for (ComboBox<AffixDisplayer> box : comboBoxes.values()) {
            box.setOnAction(event -> affixComboBoxChangeEvent(box));

            int id = Integer.parseInt(box.getId().charAt(5) + "");
            choiceBoxes.get("affix" + id + "TierChoiceBox").setOnAction(event -> {
                if (eventsLockedForReading) return;
                getSelectedItem().ifPresent(item -> {
                    if (item.getAffixList().size() >= id) {
                        item.getAffixList().get(id - 1).tier =
                                AffixTier.valueOf(choiceBoxes.get("affix" + id + "TierChoiceBox").getValue());
                    }
                });
            });

            textFields.get("affix" + id + "ValueField").setOnKeyTyped(event -> {
                if (eventsLockedForReading) return;
                getSelectedItem().ifPresent(item -> {
                    if (item.getAffixList().size() >= id) {
                        item.getAffixList().get(id - 1).value =
                                Item.percentOf255ToValue(textFields.get("affix" + id + "ValueField").getText());
                    }
                });
            });
        }
    }

    protected void affixComboBoxChangeEvent(ComboBox<AffixDisplayer> box) {
        if (eventsLockedForReading) return;
        int id = Integer.parseInt(box.getId().charAt(5) + "");
        getSelectedItem().ifPresent(item -> {
            Affix affix;
            if ((affix = AffixDisplayer.getAffixFromDisplayName(box.getEditor().getText())) == null) {
                item.setAffix(null, id);
            } else {
                AbstractItem.AffixData affixData = new AbstractItem.AffixData(
                        AffixTier.valueOf(choiceBoxes.get("affix" + id + "TierChoiceBox").getValue()),
                        affix,
                        Item.percentOf255ToValue(textFields.get("affix" + id + "ValueField").getText())
                );
                item.setAffix(affixData, id);
            }

            textFields.get("affixField").setText(item.getAffixNumber() + "");
            textFields.get("affixVisualField").setText(item.getAffixNumber() + "");

            setEquipment();
            refreshTreeView();
            correctAffixBoxDisplay(box, id, item);
        });
    }

    private void correctAffixBoxDisplay(ComboBox<AffixDisplayer> boxUsedToAddAffix, int id, Item item) {
        int affixSize = item.getAffixList().size();
        if (id > affixSize && affixSize != 0) {
            eventsLockedForReading = true;
            boxUsedToAddAffix.getEditor().clear();
            ComboBox<AffixDisplayer> realBoxOfAddedAffix = comboBoxes.get("affix" + affixSize + "ComboBox");
            realBoxOfAddedAffix.getSelectionModel()
                    .select(new AffixDisplayer(item.getAffixList().get(affixSize - 1).type));
            eventsLockedForReading = false;
        }
    }

    private void affixInfo() {

    }

    private void implicit1Info() {

    }

    private void implicit2Info() {

    }

    private void implicit3Info() {

    }

    private void affixInfo2() {

    }

    private void restrictAffixesInfo() {

    }

    private void toggleRestrictAffixes() {
    }

    private void affixesInfo() {
        String alertText = "Test to display";
        Alert alert = new Alert(Alert.AlertType.INFORMATION, alertText, ButtonType.OK);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    protected void maxImplicits() {
        textFields.get("implicit1Field").setText("100");
        textFields.get("implicit2Field").setText("100");
        textFields.get("implicit3Field").setText("100");
        setItem();
    }
}
