package io.github.kwisatzx.lastepoch.itemdata;

abstract class Attribute {
    private final int dataId;
    private final String name;
    private final int numberOfTiers;
    private final String[] tierValues;
    private final int[] levelRequirement;

    Attribute(int dataId,
              String name,
              int numberOfTiers,
              String[] tierValues,
              int[] levelRequirement) {
        this.dataId = dataId;
        this.name = name;
        this.numberOfTiers = numberOfTiers;
        this.tierValues = tierValues;
        this.levelRequirement = levelRequirement;
    }

    Attribute(int dataId, String name, int numberOfTiers, String[] tierValues) {
        this(dataId, name, numberOfTiers, tierValues, new int[]{0});
    }

    public int getDataId() {
        return dataId;
    }

    public String getName() {
        return name;
    }

    public int getNumberOfTiers() {
        return numberOfTiers;
    }

    public String[] getTierValues() {
        return tierValues;
    }

    public int[] getLevelRequirement() {
        return levelRequirement;
    }
}
