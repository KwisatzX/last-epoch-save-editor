package io.github.kwisatzx.lastepoch.itemdata.item;

import io.github.kwisatzx.lastepoch.fileoperations.CharacterOperations;
import io.github.kwisatzx.lastepoch.fileoperations.FileHandler;
import io.github.kwisatzx.lastepoch.fileoperations.GlobalDataOperations;
import io.github.kwisatzx.lastepoch.itemdata.*;
import org.slf4j.LoggerFactory;

import java.util.*;

import static io.github.kwisatzx.lastepoch.fileoperations.FileStringOperations.getSubstringBetween;

public interface Item extends Selectable {
    Item UNKNOWN_ITEM =
            new AbstractItem(new ItemAttribute(999, "Unique/Set", 1, new String[]{"Unknown"}, new String[]{"Unknown"}),
                             0, 0, new ArrayList<>());

    static CharacterOperations getItemOwner(Item item) {
        for (CharacterOperations charaOp : FileHandler.getCharacterFileList()) {
            if (charaOp.getCharacter().getEquipment().contains(item)) return charaOp;
        }
        return null;
    }

    static GlobalDataOperations.StashTab getItemStash(Item item) {
        for (var stashTab : FileHandler.getStashFile().getStashTabs()) {
            if (stashTab.getItemsInTab().contains(item)) return stashTab;
        }
        return null;
    }

    static Item itemFromInventoryString(String inventoryString) {
        String[] dataStr = getSubstringBetween(inventoryString, "[", "]").split(",");
        int[] data = new int[0];
        try {
            data = Arrays.stream(dataStr).mapToInt(Integer::parseInt).toArray();
        } catch (NumberFormatException e) {
            LoggerFactory.getLogger(Item.class).error("Couldn't convert item data from string", e);
        }

        if (data.length < 10 || data[3] > 4) return UNKNOWN_ITEM;

        List<AffixData> affixData = new ArrayList<>(data[8]);
        int i = 9;
        for (int j = 0; j < data[8]; j++) {
            affixData.add(new AffixData());
            for (AffixTier affixTier : AffixTier.values()) {
                int val = affixTier.getValue();
                if (data[i] >= val && data[i] <= val + 2) {
                    affixData.get(j).tier = affixTier;
                    int typeMod = 0;
                    if (data[i] == val + 1) typeMod = 1;
                    if (data[i] == val + 2) typeMod = 2;
                    affixData.get(j).type = AffixList.getById(data[i + 1], typeMod);
                    affixData.get(j).value = data[i + 2];
                    break;
                }
            }
            i += 3;
        }

        Item item = new AbstractItem(ItemAttributeList.getById(data[1]), data[2], data[3],
                                     data[4], data[5], data[6], data[7], data[8], affixData);

        String x = getSubstringBetween(inventoryString, "\"x\":", ",\"y\"");
        String y = getSubstringBetween(inventoryString, "\"y\":", "}");
        String id = inventoryString.contains("\"containerID\":") ?
                getSubstringBetween(inventoryString, "\"containerID\":", ",\"format") :
                getSubstringBetween(inventoryString, "\"tabID\":", ",\"quantity");

        boolean charaEq = inventoryString.contains("\"containerID\":");
        item.setItemStashInfo(new AbstractItem.ItemStashInfo(Integer.parseInt(x),
                                                             Integer.parseInt(y),
                                                             Integer.parseInt(id),
                                                             charaEq));

        return item;
    }

    static int valueToPercentOf255(int value) {
        return (int) Math.floor((value * 100) / 255f);
    }

    static int percentOf255ToValue(int percent) {
        percent = Math.max(0, Math.min(255, percent));
        return (int) Math.floor((percent / 100f) * 255);
    }

    static int percentOf255ToValue(String percent) {
        return percentOf255ToValue(Integer.parseInt(percent));
    }

    int[] getArray();

    String getDataArrayString();

    String getInventoryString();

    ItemAttribute getItemType();

    void setItemType(ItemAttribute itemType);

    int getItemTier();

    void setItemTier(int itemTier);

    int getAffixNumberVisual();

    void setAffixNumberVisual(int affixNumberVisual);

    int getImplicitValue1();

    void setImplicitValue1(int implicitValue1);

    int getImplicitValue2();

    void setImplicitValue2(int implicitValue2);

    int getImplicitValue3();

    void setImplicitValue3(int implicitValue3);

    int getInstability();

    void setInstability(int instability);

    int getAffixNumber();

    void setAffixNumber(int affixNumber);

    List<AffixData> getAffixList();

    void setAffixList(List<AffixData> affixList);

    void setAffix(AffixData affixData, int slot);

    void setAffixData(List<AffixData> affixData);

    ItemStashInfo getItemStashInfo();

    void setItemStashInfo(ItemStashInfo itemStashInfo);

    class AffixData {
        public AffixTier tier; //10th
        public Affix type; //11th
        public int value; //12th

        public AffixData(AffixTier tier, Affix type, int value) {
            this.tier = tier;
            this.type = type;
            this.value = value;
        }

        public AffixData() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AffixData affixData = (AffixData) o;
            return type.getDataId() == affixData.type.getDataId() &&
                    type.getAffixTypeMod() == affixData.type.getAffixTypeMod();
        }

        @Override
        public int hashCode() {
            return Objects.hash(type);
        }
    }

    class ItemStashInfo {
        public int x;
        public int y;
        public int id;
        public boolean charaEquipment;

        public ItemStashInfo() {
        }

        public ItemStashInfo(int x, int y, int id, boolean charaEquipment) {
            this.x = x;
            this.y = y;
            this.id = id;
            this.charaEquipment = charaEquipment;
        }
    }

    class ContainerIds {
        public static Map<Integer, String> containerIds;
        public static Map<Integer, Integer> containerIdToItemTypeId;

        static { //TODO save and load from json?
            containerIds = new HashMap<>();
            containerIds.put(1, "Inv");
            containerIds.put(2, "Helmet");
            containerIds.put(3, "Armour");
            containerIds.put(4, "L.Hand");
            containerIds.put(5, "R.Hand");
            containerIds.put(6, "Gloves");
            containerIds.put(7, "Belt");
            containerIds.put(8, "Boots");
            containerIds.put(9, "L.Ring");
            containerIds.put(10, "R.Ring");
            containerIds.put(11, "Amulet");
            containerIds.put(12, "Relic");
            for (int i = 13; i <= 50; i++) {
                containerIds.put(i, "C" + i);
            }
            containerIds.put(29, "Idol");
            for (int i = 33; i <= 39; i++) {
                containerIds.put(i, "Blessing " + (i - 32) + ": ");
            }
            containerIds.put(43, "Blessing 8: ");
            containerIds.put(44, "Blessing 9: ");
            containerIds.put(45, "Blessing 10: ");

            containerIdToItemTypeId = new HashMap<>();
            containerIdToItemTypeId.put(2, 0);
            containerIdToItemTypeId.put(3, 1);
            containerIdToItemTypeId.put(6, 4);
            containerIdToItemTypeId.put(7, 2);
            containerIdToItemTypeId.put(8, 3);
            containerIdToItemTypeId.put(9, 21);
            containerIdToItemTypeId.put(10, 21);
            containerIdToItemTypeId.put(11, 20);
            containerIdToItemTypeId.put(12, 22);
        }

        public static ItemAttribute getItemTypeFromContainerId(int containerId) {
            return ItemAttributeList.getById(containerIdToItemTypeId.get(containerId));
        }

        public static int getContainerIdFromItemType(ItemAttribute type) {
            for (int id : containerIdToItemTypeId.values()) {
                if (id == type.getDataId()) return id;
            }
            return -1; //TODO Throw unchecked exception
        }
    }
}
