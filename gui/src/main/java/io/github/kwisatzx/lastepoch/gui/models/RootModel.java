package io.github.kwisatzx.lastepoch.gui.models;

import io.github.kwisatzx.lastepoch.fileoperations.CharacterOperations;
import io.github.kwisatzx.lastepoch.fileoperations.FileHandler;

public class RootModel {
    public void expandFiles() {
        for (CharacterOperations chara : FileHandler.getCharacterFileList()) {
            chara.expandFile();
        }
        FileHandler.getStashFile().expandFile();
    }

    public boolean saveCharacters() {
        boolean success = true;
        for (CharacterOperations chara : FileHandler.getCharacterFileList()) {
            if (chara.isModified()) {
                if (!chara.saveToFile()) success = false;
            }
        }
        return success;
    }

    public void saveStash() {
        if (FileHandler.getStashFile().isModified()) FileHandler.getStashFile().saveToFile();
    }
}
