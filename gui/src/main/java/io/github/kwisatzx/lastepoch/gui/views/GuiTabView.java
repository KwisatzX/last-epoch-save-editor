package io.github.kwisatzx.lastepoch.gui.views;

import io.github.kwisatzx.lastepoch.gui.views.elements.AffixDisplayer;
import io.github.kwisatzx.lastepoch.itemdata.AffixList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

import java.util.HashMap;

public abstract class GuiTabView {
    private final HashMap<String, TextField> textFields;
    private final HashMap<String, ChoiceBox<String>> choiceBoxes;
    private final HashMap<String, ComboBox<AffixDisplayer>> comboBoxes;

    public GuiTabView(Pane rootPane) {
        textFields = new HashMap<>();
        choiceBoxes = new HashMap<>();
        comboBoxes = new HashMap<>();
        groupFieldsFromParentNode(rootPane);
        initAffixComboBoxes();
    }

    protected void intToTextField(String textFieldName, int num) {
        textFields.get(textFieldName).setText(String.valueOf(num));
    }

    private void groupFieldsFromParentNode(Pane parentNode) {
        for (Node node : parentNode.getChildren()) {
            if (node.getId() != null) {
                if (node instanceof TextField) {
                    textFields.put(node.getId(), (TextField) node);
                } else if (node instanceof ChoiceBox) {
                    choiceBoxes.put(node.getId(), (ChoiceBox<String>) node);
                } else if (node instanceof ComboBox) {
                    if (node.getId().contains("affix"))
                        comboBoxes.put(node.getId(), (ComboBox<AffixDisplayer>) node);
                }
            }
        }
    }

    public abstract void fillDataFields();

    private void initAffixComboBoxes() {
        EventHandler<KeyEvent> preventClosingOnSpace = event -> {
            if (event.getCode() == KeyCode.SPACE) event.consume();
        };

        for (ComboBox<AffixDisplayer> comboBox : comboBoxes.values()) {
            comboBox.setVisibleRowCount(15);
            comboBox.getItems().addAll(AffixDisplayer.getList());

            comboBox.getEditor().setOnKeyReleased(keyEvent -> {
                KeyCode kc = keyEvent.getCode();
                if (kc.isLetterKey() || kc.isDigitKey() || kc == KeyCode.BACK_SPACE) {
                    comboBox.getItems().setAll(AffixDisplayer.getList(
                            AffixList.getListFromPartialName(comboBox.getEditor().getText())));
                    keyEvent.consume();
                }
            });

            ComboBoxListViewSkin<AffixDisplayer> eventSkin = new ComboBoxListViewSkin<>(comboBox);
            eventSkin.getPopupContent().addEventFilter(KeyEvent.ANY, preventClosingOnSpace);
            comboBox.setSkin(eventSkin);
        }
    }

    public HashMap<String, TextField> getTextFields() {
        return textFields;
    }

    public HashMap<String, ChoiceBox<String>> getChoiceBoxes() {
        return choiceBoxes;
    }

    public HashMap<String, ComboBox<AffixDisplayer>> getComboBoxes() {
        return comboBoxes;
    }

}
