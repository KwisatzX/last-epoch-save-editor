package io.github.kwisatzx.lastepoch.gui.models;

import io.github.kwisatzx.lastepoch.fileoperations.CharacterOperations;
import io.github.kwisatzx.lastepoch.gui.controllers.GuiTabController;
import io.github.kwisatzx.lastepoch.gui.views.elements.SelectionWrapper;
import io.github.kwisatzx.lastepoch.itemdata.item.Item;

import java.util.Optional;

public class GuiTabModel {
    private final GuiTabController controller;
    private final SelectionWrapper selection;
    private CharacterOperations lastSelectedCharaOp;

    public GuiTabModel(GuiTabController controller) {
        this.controller = controller;
        selection = controller.getSelection();
    }

    public Optional<CharacterOperations> getCharaOp() {
        if (selection.isCharacterOp()) return selection.getCharacterOp();
        if (lastSelectedCharaOp != null) return Optional.of(lastSelectedCharaOp);
        if (selection.isItem()) return Optional.ofNullable(Item.getItemOwner(selection.getItem().get()));
        else return Optional.empty();
    }

    public void setEquipment() {
        getCharaOp().ifPresent(CharacterOperations::setEquipmentInFileString);
    }

    public void setLastSelectedCharaOp(CharacterOperations lastSelectedCharaOp) {
        this.lastSelectedCharaOp = lastSelectedCharaOp;
    }
}
