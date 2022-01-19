package io.github.kwisatzx.lastepoch.itemdata;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ChrSkills {
    private static TreeMap<String, Map.Entry<String, String>> skills;
    private static final Logger logger = LoggerFactory.getLogger(ChrSkills.class);

    private ChrSkills() {
    }

    static {
        readFromFile();
    }

    public static Map.Entry<String, String> getIdAndMasteryFromSkillName(String name) {
        return skills.get(name);
    }

    public static String getSkillId(Map.Entry<String, Map.Entry<String, String>> fullEntry) {
        return fullEntry.getValue().getKey();
    }

    public static ChrClass.ClassMastery getSkillMastery(Map.Entry<String, Map.Entry<String, String>> fullEntry) {
        return ChrClass.ClassMastery.valueOf(fullEntry.getValue().getValue());
    }

    public static String getSkillName(Map.Entry<String, Map.Entry<String, String>> fullEntry) {
        return fullEntry.getKey();
    }

    public static String getSkillDisplayName(Map.Entry<String, Map.Entry<String, String>> fullEntry) {
        return getSkillName(fullEntry) + " (" + getSkillMastery(fullEntry) + ")";
    }

    public static List<String> getNameAndMasteryList() {
        return skills.keySet().stream()
                .map(key -> key + " (" + skills.get(key).getValue() + ")")
                .toList();
    }

    public static List<String> getSkillListForMastery(ChrClass.ClassMastery chrMastery) {
        List<String> filteredSkills = new ArrayList<>();
        ChrClass.ClassMastery baseClassMastery = ChrClass.getMasteryGroupFromMastery(chrMastery).get(0);

        for (Map.Entry<String, Map.Entry<String, String>> entry : skills.entrySet()) {
            if (getSkillMastery(entry).equals(chrMastery) || getSkillMastery(entry).equals(baseClassMastery))
                filteredSkills.add(getSkillDisplayName(entry));
        }
        filteredSkills.add("Nothing (ALL)");
        return filteredSkills;
    }

    public static String getSkillDisplayNameFromId(String id) {
        for (Map.Entry<String, Map.Entry<String, String>> entry : skills.entrySet()) {
            if (getSkillId(entry).equals(id)) return getSkillDisplayName(entry);
        }
        logger.error("Cannot find skill for given id: " + id);
        throw new IllegalArgumentException("Cannot find skill for given id: " + id);
    }

    private static void readFromFile() {
        InputStream fileStream = ChrSkills.class.getResourceAsStream("/item data/Skills.json");
        if (fileStream != null) {
            ObjectMapper objectMapper = new ObjectMapper()
                    .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            try {
                skills = objectMapper.readValue(fileStream, new TypeReference<>() {});
            } catch (IOException e) {e.printStackTrace();}
        } else {
            logger.error("Fatal error: JSON list not found for character skills!");
            throw new RuntimeException("JSON list not found for character skills!");
        }
    }
}
