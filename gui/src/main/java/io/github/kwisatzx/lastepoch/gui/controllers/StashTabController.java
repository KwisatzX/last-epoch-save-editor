package io.github.kwisatzx.lastepoch.gui.controllers;

import io.github.kwisatzx.lastepoch.gui.models.StashTabModel;
import io.github.kwisatzx.lastepoch.gui.views.StashTabView;
import io.github.kwisatzx.lastepoch.gui.views.elements.SelectionWrapper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.util.Map;

public class StashTabController extends GuiItemTabController {
    private static StashTabController instance;
    private SelectionWrapper selection;
    private StashTabModel model;
    private StashTabView view;
    @FXML
    private AnchorPane stashAnchorPane;

    @FXML
    private void initialize() {
        instance = this;
        model = new StashTabModel(this);
        view = new StashTabView(this, stashAnchorPane);
        initialize(view, model, stashAnchorPane);
        selection = getSelection();
        installEventHandlers(stashAnchorPane);
        fillDataFields();
    }

    public static StashTabController getInstance() {
        return instance;
    }

    @Override
    public void fillDataFields() {
        view.fillDataFields();
    }

    @Override
    public void reloadTreeView() {
        TreeController.getInstance().renewStashItems();
    }

    public void renewCharacterList() {
        super.reloadTreeView();
    }

    public int getStashTabIdFromName(String name) {
        return model.getStashTabIdFromName(name);
    }

    public String getStashTabNameFromId(int id) {
        return model.getStashTabNameFromId(id);
    }

    public Map<String, Integer> getStashDataNumbersFromModel() {
        return model.getStashDataNumbers();
    }

    private void installEventHandlers(Node parentNode) {
        final EventHandler<ActionEvent> buttonHandler = event -> {
            if (event.getTarget() != null && event.getTarget() instanceof Button button) {
                switch (button.getId()) {
                    case "copyItemToEditorButton" -> model.copyItemToEditor();
                    case "getGlyphsRunesButton" -> model.addGlyphsAndRunes();
                    case "affixShardsUseableButton" -> model.addAffixShardsUseable();
                    case "affixShardsAllButton" -> model.addAffixShardsAll();
                    case "arenaKeysButton" -> model.addArenaKeys();
                    case "unfractureItemsButton" -> model.unfractureItems();
                    case "maximizeItemValuesButton" -> model.maximizeItemValues();
                    case "maximizeItemAffixTiersButton" -> model.maximizeItemAffixTiers();
                    case "deleteAllButton" -> model.deleteAll();
                    case "addTenStashTabsButton" -> model.addTenStashTabs();
                }
            }
        };
        parentNode.addEventHandler(ActionEvent.ANY, buttonHandler);

        installChangeEvents();
    }

    private void installChangeEvents() {
        view.getTextFields().get("goldField").setOnKeyTyped(
                event -> model.setStashProperty("gold", view.getTextFields().get("goldField").getText()));

        view.getChoiceBoxes().get("stashTabNamesChoiceBox").setOnAction(event -> {
            selection.ifItemPresent(item -> {
                item.getItemStashInfo().id = getStashTabIdFromName(
                        view.getChoiceBoxes().get("stashTabNamesChoiceBox").getValue());
                reloadTreeView();
            });
        });

        view.getChoiceBoxes().get("stashXChoiceBox").setOnAction(event -> {
            selection.ifItemPresent(item -> item.getItemStashInfo().x =
                    Integer.parseInt(view.getChoiceBoxes().get("stashXChoiceBox").getValue()));
        });

        view.getChoiceBoxes().get("stashYChoiceBox").setOnAction(event -> {
            selection.ifItemPresent(item -> item.getItemStashInfo().y =
                    Integer.parseInt(view.getChoiceBoxes().get("stashYChoiceBox").getValue()));
        });
    }
}