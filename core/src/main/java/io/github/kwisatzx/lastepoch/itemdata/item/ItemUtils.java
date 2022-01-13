package io.github.kwisatzx.lastepoch.itemdata.item;

import io.github.kwisatzx.lastepoch.itemdata.Affiliation;
import io.github.kwisatzx.lastepoch.itemdata.ItemAttribute;

public final class ItemUtils {
    private ItemUtils() {
    }

    public static String getItemTierNameString(ItemAttribute itemType, int tierId) {
        return itemType.getTierNames()[tierId] + " ["
                + itemType.getTierValues()[tierId] + "] ("
                + Affiliation.asString(itemType.getItemAffiliation()[tierId]) + ")";
    }
}
