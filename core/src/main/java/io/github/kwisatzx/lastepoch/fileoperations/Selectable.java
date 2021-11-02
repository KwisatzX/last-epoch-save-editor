package io.github.kwisatzx.lastepoch.fileoperations;

import io.github.kwisatzx.lastepoch.itemdata.AbstractItem;
import io.github.kwisatzx.lastepoch.itemdata.Item;

public interface Selectable {
    default CharacterOperations getCharaOp() {
        if (this instanceof CharacterOperations) return (CharacterOperations) this;
        else return null;
    }

    default Item getItemObj() {
        if (this instanceof AbstractItem) return (Item) this;
        else return null;
    }

//    default GlobalDataOperations.StashTabCategory getCategory() {
//        if (this instanceof GlobalDataOperations.StashTabCategory) return (GlobalDataOperations.StashTabCategory) this;
//        else return null;
//    }
//
//    default GlobalDataOperations.StashTab getStashTab() {
//        if (this instanceof GlobalDataOperations.StashTab) return (GlobalDataOperations.StashTab) this;
//        else return null;
//    }
}

