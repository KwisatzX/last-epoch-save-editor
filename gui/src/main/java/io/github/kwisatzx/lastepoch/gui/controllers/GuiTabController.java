package io.github.kwisatzx.lastepoch.gui.controllers;

import ch.qos.logback.classic.Logger;
import io.github.kwisatzx.lastepoch.fileoperations.CharacterOperations;
import io.github.kwisatzx.lastepoch.gui.models.GuiTabModel;
import io.github.kwisatzx.lastepoch.gui.views.elements.SelectionWrapper;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public abstract class GuiTabController {
    private final SelectionWrapper selection;
    private final GuiTabModel model;
    private boolean eventsLocked = false;

    public GuiTabController(GuiTabModel model) {
        selection = TreeController.getInstance().getSelection();
        this.model = model;
    }

    public Logger getLogger(Class<?> callingClass) {
        return (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(callingClass);
    }

    protected Optional<CharacterOperations> getCharaOp() {
        return model.getCharaOp();
    }

    protected void setLastSelectedCharaOp(CharacterOperations lastSelectedCharaOp) {
        model.setLastSelectedCharaOp(lastSelectedCharaOp);
    }

    public void setEquipment() {
        model.setEquipment();
    }

    abstract protected void fillDataFields();

    public void refreshTreeView() {
        TreeController.getInstance().refreshTree();
    }

    public void reloadTreeView() {
        TreeController.getInstance().renewCharacterList();
    }

    public void lockEvents() {
        eventsLocked = true;
    }

    public void unlockEvents() {
        eventsLocked = false;
    }

    public boolean isEventsLocked() {
        return eventsLocked;
    }

    public SelectionWrapper getSelection() {
        return selection;
    }
}
