package io.github.kwisatzx.lastepoch.fileoperations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class FileHandler {
    private static final List<CharacterOperations> characterFileList;
    private static GlobalDataOperations stashFile;
    private static final String defaultSaveLocation;

    static {
        defaultSaveLocation = System.getenv("LOCALAPPDATA") + "Low\\Eleventh Hour Games\\Last Epoch\\Saves";
        characterFileList = new ArrayList<>();
        try {
            loadCharacters(defaultSaveLocation);
            stashFile = new GlobalDataOperations(defaultSaveLocation);
        } catch (IOException e) { e.printStackTrace(); } //TODO: logger
    }

    private static void loadCharacters(String path) throws IOException {
        String characterFilename = "1CHARACTERSLOT_BETA_";
        for (int i = 1; i <= 200; i++) {
            Path characterFilePath = Paths.get(path, characterFilename + i);
            if (!Files.exists(characterFilePath) || !Files.isReadable(characterFilePath)) continue;
            characterFileList.add(new CharacterOperations(characterFilePath));
        }
    }

    public static CharacterOperations addCharacter(String fileName) throws IOException {
        Path characterFilePath = Paths.get(defaultSaveLocation, fileName);
        if (Files.exists(characterFilePath) && Files.isReadable(characterFilePath)) {
            CharacterOperations newCharaOp = new CharacterOperations(characterFilePath);
            characterFileList.add(newCharaOp);
            return newCharaOp;
        }
        return null;
    }

    public static List<CharacterOperations> getCharacterFileList() {
        return Collections.unmodifiableList(characterFileList);
    }

    public static GlobalDataOperations getStashFile() {
        return stashFile;
    }
}
