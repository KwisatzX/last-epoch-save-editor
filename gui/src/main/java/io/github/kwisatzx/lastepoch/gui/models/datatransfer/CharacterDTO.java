package io.github.kwisatzx.lastepoch.gui.models.datatransfer;

import java.util.List;

public record CharacterDTO(String name,
                           int level,
                           String chrClass,
                           String classMastery,
                           boolean isHardcore,
                           boolean isMasochist,
                           boolean isSolo,
                           List<String> masteredSkillsDisplayNames,
                           List<String> toolbarSkillsDisplayNames) {
}
