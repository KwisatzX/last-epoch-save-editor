package io.github.kwisatzx.lastepoch.gui.views.elements;

import io.github.kwisatzx.lastepoch.fileoperations.CharacterOperations;
import io.github.kwisatzx.lastepoch.fileoperations.GlobalDataOperations;
import io.github.kwisatzx.lastepoch.itemdata.Item;
import io.github.kwisatzx.lastepoch.itemdata.Selectable;

import java.util.Optional;

public class SelectionWrapperImpl implements SelectionWrapper {
    private Selectable selection;

    @Override
    public void setSelection(Selectable selection) {
        this.selection = selection;
    }

    @Override
    public boolean isItem() {
        return selection instanceof Item;
    }

    @Override
    public boolean isCharacterOp() {
        return selection instanceof CharacterOperations;
    }

    @Override
    public boolean isEmpty() {
        return selection == null;
    }

    @Override
    public Optional<GlobalDataOperations.StashTabCategory> getStashTab() {
        return Optional.ofNullable((GlobalDataOperations.StashTabCategory) selection);
    }

    @Override
    public Optional<Item> getItem() {
        if (isItem()) return Optional.of((Item) selection);
        else return Optional.empty();
    }

    @Override
    public Optional<CharacterOperations> getCharacterOp() {
        if (isCharacterOp()) return Optional.of((CharacterOperations) selection);
        else return Optional.empty();
    }
}
