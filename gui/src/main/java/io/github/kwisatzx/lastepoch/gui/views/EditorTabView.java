package io.github.kwisatzx.lastepoch.gui.views;

import io.github.kwisatzx.lastepoch.gui.controllers.EditorTabController;
import io.github.kwisatzx.lastepoch.gui.views.elements.AffixDisplayer;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class EditorTabView extends GuiItemTabView {
    private final EditorTabController controller;
    private final HashMap<String, TextField> textFields;
    private final HashMap<String, ChoiceBox<String>> choiceBoxes;
    private final HashMap<String, ComboBox<AffixDisplayer>> comboBoxes;

    public EditorTabView(EditorTabController controller, Pane rootPane) {
        super(controller, rootPane);
        this.controller = controller;
        textFields = getTextFields();
        choiceBoxes = getChoiceBoxes();
        comboBoxes = getComboBoxes();
        initCharactersChoiceBox();
        initInvXYChoiceBoxes();
    }

    @Override
    public void fillDataFields() {
        super.fillDataFields();
        controller.getCharaOp().ifPresent(charaOp -> choiceBoxes.get("charactersChoiceBox").getSelectionModel()
                .select(charaOp.getCharacter().getName()));
    }

    public Map<String, String> getUiSettings() {
        Map<String, String> nameValuePairs = new HashMap<>();
        nameValuePairs.put("x", choiceBoxes.get("stashXChoiceBox").getValue());
        nameValuePairs.put("y", choiceBoxes.get("stashYChoiceBox").getValue());
        nameValuePairs.put("stashTabName", choiceBoxes.get("stashTabNamesChoiceBox").getValue());
        nameValuePairs.put("character", choiceBoxes.get("charactersChoiceBox").getValue());
        return nameValuePairs;
    }

    public void resetProperties() {
        resetAffixes();
        choiceBoxes.get("itemTierChoiceBox").getSelectionModel().selectLast();
        textFields.get("instabilityField").setText("0");
        controller.maxImplicits();
    }

    public void resetAffixes() {
        for (int i = 4; i >= 1; i--) { //top-down because affixes are stored in a list
            ComboBox<AffixDisplayer> comboBox = comboBoxes.get("affix" + i + "ComboBox");
            comboBox.getSelectionModel().clearSelection();
            comboBox.getEditor().clear();
            choiceBoxes.get("affix" + i + "TierChoiceBox").getSelectionModel().selectLast();
            textFields.get("affix" + i + "ValueField").setText("100");
            textFields.get("affixField").setText(0 + "");
            textFields.get("affixVisualField").setText(0 + "");
            controller.affixComboBoxChangeEvent(comboBox);
        }
    }

    public void initCharactersChoiceBox() {
        ChoiceBox<String> charactersChoiceBox = choiceBoxes.get("charactersChoiceBox");
        charactersChoiceBox.getItems().setAll(controller.getCharactersMap().keySet());
        if (charactersChoiceBox.getSelectionModel().isEmpty())
            charactersChoiceBox.getSelectionModel().selectFirst();
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
}
