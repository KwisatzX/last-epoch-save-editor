package io.github.kwisatzx.lastepoch.gui.controllers;

import io.github.kwisatzx.lastepoch.fileoperations.CharacterOperations;
import io.github.kwisatzx.lastepoch.gui.models.EditorTabModel;
import io.github.kwisatzx.lastepoch.gui.views.EditorTabView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.util.Map;

public class EditorTabController extends GuiItemTabController {
    private static EditorTabController instance;
    private EditorTabModel model;
    private EditorTabView view;
    @FXML
    private RadioButton replaceSlotRadio;
    @FXML
    private AnchorPane editorAnchorPane;

    public static EditorTabController getInstance() {
        return instance;
    }

    @FXML
    private void initialize() {
        instance = this;
        view = new EditorTabView(this, editorAnchorPane);
        model = new EditorTabModel(this);
        super.initialize(view, model, editorAnchorPane);
        installEventHandlers(editorAnchorPane);
    }

    @Override
    public void fillDataFields() {
        view.fillDataFields();
    }

    private void installEventHandlers(Node parentNode) {
        final EventHandler<ActionEvent> buttonHandler = event -> {
            if (event.getTarget() != null && event.getTarget() instanceof Button button) {
                switch (button.getId()) {
                    case "addItemToStashButton" -> model.addItemToStash();
                    case "addItemToCharacterButton" -> model.addItemToCharacter(replaceSlotRadio.isSelected());
                    case "resetPropertiesButton" -> view.resetProperties();
                    case "resetAffixesButton" -> view.resetAffixes();
                    case "createItemButton" -> model.createNewItem();
                    case "copyItemButton" -> model.copyItem();
                    case "deleteItemButton" -> model.deleteItem();
                    case "maxItemAffixValuesButton" -> model.maxItemAffixValues();
                    case "maxItemAffixTiersButton" -> model.maxItemAffixTiers();
                }
            }
        };

        parentNode.addEventHandler(ActionEvent.ANY, buttonHandler);
    }

    public void initCharactersChoiceBox() {
        view.initCharactersChoiceBox();
    }

    public Map<String, CharacterOperations> getCharactersMap() {
        return model.getCharactersMap();
    }

    public Map<String, String> getUiSettingsFromView() {
        return view.getUiSettings();
    }

    public void bottomLeft(MouseEvent mouseEvent) {
        RootController.getInstance().setBottomLeftText("Kikkeriki!");
    }
}
