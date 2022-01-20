package io.github.kwisatzx.lastepoch.gui.controllers;

import ch.qos.logback.classic.Logger;
import io.github.kwisatzx.lastepoch.fileoperations.CharacterOperations;
import io.github.kwisatzx.lastepoch.gui.models.GuiTabModel;
import io.github.kwisatzx.lastepoch.gui.views.elements.SelectionWrapper;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public abstract class GuiTabController {
    private SelectionWrapper selection;
    private GuiTabModel model;
    private boolean eventsLocked = false;

    public GuiTabController() {
        selection = TreeController.getInstance().getSelection();
    }

    public void initialize(GuiTabModel model) {
        this.model = model;
    }

    public Logger getLogger(Class<?> callingClass) {
        return (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(callingClass);
    }

    public void switchToTab(RootController.GuiTabs tab) {
        RootController.getInstance().switchToTab(tab);
    }

    public Optional<CharacterOperations> getCharaOp() {
        return model.getCharaOp();
    }

    public void setLastSelectedCharaOp(CharacterOperations lastSelectedCharaOp) {
        model.setLastSelectedCharaOp(lastSelectedCharaOp);
    }

    public void setCharaEquipment() {
        model.setCharaEquipment();
    }

    abstract protected void fillDataFields();

    public void refreshTreeView() {
        TreeController.getInstance().refreshTree();
    }

    public void reloadTreeView() {
        TreeController.getInstance().renewCharacterList();
    }

    public void setBottomRightText(String text) {
        RootController.getInstance().setBottomRightText(text);
    }

    public void setBottomLeftText(String text) {
        RootController.getInstance().setBottomLeftText(text);
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
