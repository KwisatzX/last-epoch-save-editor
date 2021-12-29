package io.github.kwisatzx.lastepoch.itemdata;

import java.util.OptionalInt;

public class ItemAttribute extends Attribute {
    private final String[] tierNames;
    private final int[] itemAffiliation;

    //for JSON serialization
    private ItemAttribute() {
        super(0, null, 0, null);
        this.tierNames = null;
        this.itemAffiliation = null;
    }

    ItemAttribute(int dataId,
                  String name,
                  int numberOfTiers,
                  String[] tierValues,
                  String[] tierNames,
                  int[] levelRequirement,
                  int[] itemAffiliation) {
        super(dataId, name, numberOfTiers, tierValues, levelRequirement);
        this.tierNames = tierNames;
        this.itemAffiliation = itemAffiliation;
    }

    public ItemAttribute(int dataId, String name, int numberOfTiers, String[] tierValues, String[] tierNames) {
        this(dataId, name, numberOfTiers, tierValues, tierNames, new int[]{0}, new int[]{Affiliation.ALL});
    }

    public String[] getTierNames() {
        return tierNames;
    } //TODO return copy for encapsulation, or unmodifiable list

    public int[] getItemAffiliation() {
        return itemAffiliation;
    }

    public int getTierIdFromName(String tierName) {
        for (int i = 0; i < this.tierNames.length; i++) {
            if (tierNames[i].equals(tierName)) return i;
        }
        throw new IllegalArgumentException("No ID found for tier name: " + tierName); //TODO Log
    }

    public OptionalInt getTierIdFromValue(String tierValue) {
        for (int i = 0; i < this.getTierValues().length; i++) {
            if (getTierValues()[i].equals(tierValue)) return OptionalInt.of(i);
        }
        return OptionalInt.empty();
    }

    @Override
    public String toString() {
        return getName();
    }
}
