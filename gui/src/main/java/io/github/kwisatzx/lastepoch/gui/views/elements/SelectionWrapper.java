package io.github.kwisatzx.lastepoch.gui.views.elements;

import io.github.kwisatzx.lastepoch.fileoperations.CharacterOperations;
import io.github.kwisatzx.lastepoch.fileoperations.GlobalDataOperations;
import io.github.kwisatzx.lastepoch.itemdata.Item;
import io.github.kwisatzx.lastepoch.itemdata.Selectable;

import java.util.Optional;
import java.util.function.Consumer;

public interface SelectionWrapper {

    void setSelection(Selectable selection);

    boolean isItem();

    boolean isCharacterOp();

    boolean isEmpty();

    Optional<Item> getItem();

    Optional<CharacterOperations> getCharacterOp();

    Optional<GlobalDataOperations.StashTabCategory> getStashTab();

    default void ifItemPresent(Consumer<Item> consumer) {
        getItem().ifPresent(consumer);
    }

    default void ifCharacterPresent(Consumer<CharacterOperations> consumer) {
        getCharacterOp().ifPresent(consumer);
    }
}

