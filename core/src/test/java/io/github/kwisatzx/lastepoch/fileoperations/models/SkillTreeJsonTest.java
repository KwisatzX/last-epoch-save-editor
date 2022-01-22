package io.github.kwisatzx.lastepoch.fileoperations.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.kwisatzx.lastepoch.fileoperations.ObjectMapperCache;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SkillTreeJsonTest {

    @Test
    void toObject() throws JsonProcessingException {
        String exampleJson = "{\"treeID\":\"shiif\",\"slotNumber\":2,\"xp\":5700000,\"version\":3," +
                "\"nodeIDs\":[27,9,22,24,15,8,32,19,30],\"nodePoints\":[4,3,1,1,3,3,1,1,3]," +
                "\"unspentPoints\":2,\"nodesTaken\":[],\"abilityXP\":1.0}";

        SkillTreeJson result = ObjectMapperCache.getObjectMapper().readValue(exampleJson, SkillTreeJson.class);
        assertEquals("shiif", result.getTreeID());
        assertEquals(2, result.getSlotNumber());
        assertEquals(5700000, result.getXp());
        int[] expectedNodeIds = new int[]{27, 9, 22, 24, 15, 8, 32, 19, 30};
        assertArrayEquals(expectedNodeIds, result.getNodeIDs());
        int[] expectedNodePoints = new int[]{4, 3, 1, 1, 3, 3, 1, 1, 3};
        assertArrayEquals(expectedNodePoints, result.getNodePoints());

        assertEquals(result, SkillTreeJson.toObject(exampleJson));
    }

    @Test
    void toJson() throws JsonProcessingException {
        SkillTreeJson exampleTree = new SkillTreeJson("shiif", 0, 5700000,
                                                      new int[]{1, 3, 5, 8},
                                                      new int[]{1, 2, 3, 1});

        String expectedJson = "{\"treeID\":\"shiif\",\"slotNumber\":0,\"xp\":5700000,\"version\":0," +
                "\"nodeIDs\":[1,3,5,8],\"nodePoints\":[1,2,3,1]," +
                "\"unspentPoints\":25,\"nodesTaken\":[],\"abilityXP\":1.0}";

        String result = ObjectMapperCache.getObjectMapper().writeValueAsString(exampleTree);

        assertEquals(expectedJson, result);
        assertEquals(expectedJson, exampleTree.toJson());
    }
}