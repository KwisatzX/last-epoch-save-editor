package io.github.kwisatzx.lastepoch.fileoperations;

import io.github.kwisatzx.lastepoch.fileoperations.models.SkillTreeJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FileStringOperationsTest {

    @Test
    void getObjectArray() {
        SkillTreeJson[] result = FileHandler.getCharacterFileList().get(0)
                .getObjectArray("savedSkillTrees", SkillTreeJson[].class);

        if (result.length > 0) {
            Assertions.assertNotNull(result[0]);
        }
    }
}