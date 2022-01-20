package io.github.kwisatzx.lastepoch.fileoperations.models;

import java.util.Arrays;

//add Lombok
public class SkillTreeJson {
    public static final int MAX_XP = 5700000;
    private String treeID;
    private int slotNumber;
    private int xp;
    private int version;
    private int[] nodeIDs;
    private int[] nodePoints;
    private int unspentPoints;
    private int[] nodesTaken;
    private float abilityXP;

    public static SkillTreeJson toObject(String jsonString) {
        return ObjectMapperCache.readValue(jsonString, SkillTreeJson.class);
    }

    public String toJson() {
        return ObjectMapperCache.writeValueAsString(this);
    }

    private SkillTreeJson() {
    }

    public SkillTreeJson(String treeId, int slotNumber, int xp,
                         int[] nodeIds, int[] nodePoints) {
        this.treeID = treeId;
        this.slotNumber = slotNumber;
        this.xp = xp;
        this.nodeIDs = nodeIds;
        this.nodePoints = nodePoints;
        unspentPoints = 25;
        abilityXP = 1.0f;
        nodesTaken = new int[]{};
    }

    public String getTreeID() {
        return treeID;
    }

    public void setTreeID(String treeID) {
        this.treeID = treeID;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(int slotNumber) {
        this.slotNumber = slotNumber;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int[] getNodeIDs() {
        return nodeIDs;
    }

    public void setNodeIDs(int[] nodeIDs) {
        this.nodeIDs = nodeIDs;
    }

    public int[] getNodePoints() {
        return nodePoints;
    }

    public void setNodePoints(int[] nodePoints) {
        this.nodePoints = nodePoints;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SkillTreeJson that = (SkillTreeJson) o;

        if (slotNumber != that.slotNumber) return false;
        if (xp != that.xp) return false;
        if (version != that.version) return false;
        if (unspentPoints != that.unspentPoints) return false;
        if (Float.compare(that.abilityXP, abilityXP) != 0) return false;
        if (!treeID.equals(that.treeID)) return false;
        if (!Arrays.equals(nodeIDs, that.nodeIDs)) return false;
        if (!Arrays.equals(nodePoints, that.nodePoints)) return false;
        return Arrays.equals(nodesTaken, that.nodesTaken);
    }

    @Override
    public int hashCode() {
        int result = treeID.hashCode();
        result = 31 * result + slotNumber;
        result = 31 * result + Arrays.hashCode(nodeIDs);
        result = 31 * result + Arrays.hashCode(nodePoints);
        return result;
    }
}
