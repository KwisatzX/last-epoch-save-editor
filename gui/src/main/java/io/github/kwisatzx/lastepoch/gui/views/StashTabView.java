package io.github.kwisatzx.lastepoch.gui.views;

import io.github.kwisatzx.lastepoch.gui.controllers.StashTabController;
import io.github.kwisatzx.lastepoch.gui.views.elements.SelectionWrapper;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.Map;

public class StashTabView extends GuiItemTabView {
    private final StashTabController controller;
    private final SelectionWrapper selection;
    private final HashMap<String, TextField> textFields;
    private final HashMap<String, ChoiceBox<String>> choiceBoxes;

    public StashTabView(StashTabController controller, Pane rootPane) {
        super(controller, rootPane);
        this.controller = controller;
        selection = controller.getSelection();
        textFields = getTextFields();
        choiceBoxes = getChoiceBoxes();
    }

    public void fillDataFields() {
        fillGlobalStashDataFields();
        fillItemStashDataFields();
    }

    private void fillGlobalStashDataFields() {
        Map<String, Integer> stashData = controller.getStashDataNumbersFromModel();
        textFields.get("goldField").setText(stashData.get("gold") + "");
        textFields.get("stashTabsField").setText(stashData.get("stashTabs") + "");
        textFields.get("glyphsField").setText(stashData.get("glyphs") + "");
        textFields.get("runesField").setText(stashData.get("runes") + "");
        textFields.get("affixShardsField").setText(stashData.get("affixShards") + "");
    }

    private void fillItemStashDataFields() {
        selection.ifItemPresent(item -> {
            String tabName = controller.getStashTabNameFromId(item.getItemStashInfo().id);
            choiceBoxes.get("stashTabNamesChoiceBox").getSelectionModel().select(tabName);
            choiceBoxes.get("stashXChoiceBox").getSelectionModel().select(item.getItemStashInfo().x);
            choiceBoxes.get("stashYChoiceBox").getSelectionModel().select(item.getItemStashInfo().y);
        });
    }
}
