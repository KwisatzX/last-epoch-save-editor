package io.github.kwisatzx.lastepoch.gui.controllers;

import io.github.kwisatzx.lastepoch.gui.models.GuiItemTabModel;
import io.github.kwisatzx.lastepoch.gui.views.GuiItemTabView;
import io.github.kwisatzx.lastepoch.gui.views.elements.AffixDisplayer;
import io.github.kwisatzx.lastepoch.gui.views.elements.SelectionWrapper;
import io.github.kwisatzx.lastepoch.itemdata.Affix;
import io.github.kwisatzx.lastepoch.itemdata.AffixTier;
import io.github.kwisatzx.lastepoch.itemdata.item.AbstractItem;
import io.github.kwisatzx.lastepoch.itemdata.item.Item;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

public abstract class GuiItemTabController extends GuiTabController {
    private SelectionWrapper selection;
    private GuiItemTabView view;
    private GuiItemTabModel model;

    public void initialize(GuiItemTabView view, GuiItemTabModel model, Pane rootPane) {
        super.initialize(model);
        this.view = view;
        this.model = model;
        selection = getSelection();
        installCommonEventHandlers(rootPane);
    }

//    @Override
//    protected void fillDataFields() {
//        view.fillDataFields();
//    }

    public void setItem() {
        model.setItem();
    }

    public Item getUiItemSettingsFromView() {
        return view.getUiItemSettings();
    }

    public void renewStashItems() {
        TreeController.getInstance().renewStashItems();
    }

    public void addCustomItem(Item item) {
        TreeController.getInstance().addCustomItem(item);
    }

    private void initCommonTextFieldChangeEvents() {
        EventHandler<KeyEvent> keyTypedEvent = event -> {
            view.ensureCorrectNumbersInFields();
            model.setItem();
        };

        view.getTextFields().get("affixVisualField").setOnKeyTyped(keyTypedEvent);
        view.getTextFields().get("implicit1Field").setOnKeyTyped(keyTypedEvent);
        view.getTextFields().get("implicit2Field").setOnKeyTyped(keyTypedEvent);
        view.getTextFields().get("implicit3Field").setOnKeyTyped(keyTypedEvent);
        view.getTextFields().get("instabilityField").setOnKeyTyped(keyTypedEvent);
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

    private void initAffixChangeEvents() {
        for (ComboBox<AffixDisplayer> box : view.getComboBoxes().values()) {
            box.setOnAction(event -> affixComboBoxChangeEvent(box));

            int id = Integer.parseInt(box.getId().charAt(5) + "");
            view.getChoiceBoxes().get("affix" + id + "TierChoiceBox").setOnAction(event -> {
                if (isEventsLocked()) return;
                selection.ifItemPresent(item -> {
                    if (item.getAffixList().size() >= id) {
                        item.getAffixList().get(id - 1).tier =
                                AffixTier.valueOf(view.getChoiceBoxes().get("affix" + id + "TierChoiceBox").getValue());
                    }
                });
            });

            view.getTextFields().get("affix" + id + "ValueField").setOnKeyTyped(event -> {
                if (isEventsLocked()) return;
                selection.ifItemPresent(item -> {
                    if (item.getAffixList().size() >= id) {
                        item.getAffixList().get(id - 1).value =
                                Item.percentOf255ToValue(
                                        view.getTextFields().get("affix" + id + "ValueField").getText());
                    }
                });
            });
        }
    }

    public void affixComboBoxChangeEvent(ComboBox<AffixDisplayer> box) {
        if (isEventsLocked()) return;
        int id = Integer.parseInt(box.getId().charAt(5) + "");
        selection.ifItemPresent(item -> {
            Affix affix;
            if ((affix = AffixDisplayer.getAffixFromDisplayName(box.getEditor().getText())) == null) {
                item.setAffix(null, id);
            } else {
                AbstractItem.AffixData affixData = new AbstractItem.AffixData(
                        AffixTier.valueOf(view.getChoiceBoxes().get("affix" + id + "TierChoiceBox").getValue()),
                        affix,
                        Item.percentOf255ToValue(view.getTextFields().get("affix" + id + "ValueField").getText())
                );
                item.setAffix(affixData, id);
            }

            view.getTextFields().get("affixField").setText(item.getAffixNumber() + "");
            view.getTextFields().get("affixVisualField").setText(item.getAffixNumber() + "");

            setCharaEquipment();
            refreshTreeView();
            correctAffixBoxDisplay(box, id, item);
        });
    }

    private void correctAffixBoxDisplay(ComboBox<AffixDisplayer> boxUsedToAddAffix, int id, Item item) {
        int affixSize = item.getAffixList().size();
        if (id > affixSize && affixSize != 0) {
            lockEvents();
            boxUsedToAddAffix.getEditor().clear();
            ComboBox<AffixDisplayer> realBoxOfAddedAffix = view.getComboBoxes().get("affix" + affixSize + "ComboBox");
            realBoxOfAddedAffix.getSelectionModel()
                    .select(new AffixDisplayer(item.getAffixList().get(affixSize - 1).type));
            unlockEvents();
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

    public void maxImplicits() {
        view.getTextFields().get("implicit1Field").setText("100");
        view.getTextFields().get("implicit2Field").setText("100");
        view.getTextFields().get("implicit3Field").setText("100");
        model.setItem();
    }
}
