package io.github.kwisatzx.lastepoch.itemdata;

public class Affix extends Attribute {
    private final int affixTypeMod;
    private final boolean prefix;

    //for JSON serialization
    private Affix() {
        super(0, null, 0, null);
        this.affixTypeMod = 0;
        this.prefix = false;
    }

    Affix(int dataId,
          String name,
          int numberOfTiers,
          String[] tierValues,
          int[] levelRequirement,
          int affixTypeMod) {
        super(dataId, name, numberOfTiers, tierValues, levelRequirement);
        this.affixTypeMod = affixTypeMod;
        this.prefix = true;
    }

    public int getAffixTypeMod() {
        return affixTypeMod;
    }

    public int getAffixListId() {
        return getDataId() + (affixTypeMod * 255) + affixTypeMod;
    }

    public boolean isPrefix() {
        return prefix;
    }

    @Override
    public String toString() {
        return getName();
    }
}
