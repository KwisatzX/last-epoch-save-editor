package io.github.kwisatzx.lastepoch.itemdata;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AbstractItem implements Selectable, Item {
    private ItemAttribute itemType; //2nd
    private int itemTier; //3rd
    private int affixNumberVisual; //4th
    private int implicitValue1; //5th
    private int implicitValue2; //6th
    private int implicitValue3; //7th
    private int instability; //8th
    private int affixNumber; //9th
    private List<AffixData> affixList;
    private ItemStashInfo itemStashInfo;

    public AbstractItem(ItemAttribute itemType,
                        int itemTier,
                        int affixNumberVisual,
                        int implicitValue1,
                        int implicitValue2,
                        int implicitValue3,
                        int instability,
                        int affixNumber,
                        List<AffixData> affixList) {
        this.itemType = itemType;
        this.itemTier = itemTier;
        this.affixNumberVisual = affixNumberVisual;
        this.implicitValue1 = implicitValue1;
        this.implicitValue2 = implicitValue2;
        this.implicitValue3 = implicitValue3;
        this.instability = instability;
        this.affixNumber = affixNumber;
        this.affixList = affixList;
        this.itemStashInfo = new ItemStashInfo(0, 0, 0, false);
    }

    public AbstractItem(ItemAttribute itemType, int itemTier, int affixNumber, List<AffixData> affixList) {
        this.itemType = itemType;
        this.itemTier = itemTier;
        this.affixNumberVisual = affixNumber;
        this.implicitValue1 = 255;
        this.implicitValue2 = 255;
        this.implicitValue3 = 255;
        this.instability = 0;
        this.affixNumber = affixNumber;
        this.affixList = affixList;
        this.itemStashInfo = new ItemStashInfo(0, 0, 0, false);
    }

    @Override
    public int[] getArray() {
        int[] dataArr = new int[]{0, itemType.getDataId(), itemTier, affixNumberVisual, implicitValue1,
                implicitValue2, implicitValue3, instability, affixNumber};

        if (affixNumber > 0) {
            int affixDataIndex = dataArr.length;
            dataArr = Arrays.copyOf(dataArr, dataArr.length + (affixList.size() * 3));
            for (AffixData affixData : affixList) {
                dataArr[affixDataIndex] = affixData.tier.getValue() + affixData.type.getAffixTypeMod();
                dataArr[affixDataIndex + 1] = affixData.type.getDataId();
                dataArr[affixDataIndex + 2] = affixData.value;
                affixDataIndex += 3;
            }
        }

        dataArr = Arrays.copyOf(dataArr, dataArr.length + 1);
        dataArr[dataArr.length - 1] = 0;
        return dataArr;
    }

    @Override
    public String getDataArrayString() {
        return Arrays.toString(getArray()).replaceAll(" ", "");
    }

    @Override
    public String getInventoryString() {
        int x = itemStashInfo.x;
        int y = itemStashInfo.y;
        int id = itemStashInfo.id;
        if (itemStashInfo.charaEquipment) {
            return "{\"itemData\":\"\",\"data\":" + getDataArrayString() + ",\"inventoryPosition\"" +
                    ":{\"x\":" + x + ",\"y\":" + y + "},\"quantity\":1,\"containerID\":" + id + ",\"formatVersion\":2}";
        } else {
            return "{\"itemData\":\"\",\"data\":" + getDataArrayString() + ",\"inventoryPosition\"" +
                    ":{\"x\":" + x + ",\"y\":" + y + "},\"tabID\":" + id + ",\"quantity\":1,\"formatVersion\":1}";
        }
    }

    public String getInventoryString(int x, int y, int id, boolean charaEquipment) {
        if (charaEquipment) {
            return "{\"itemData\":\"\",\"data\":" + getDataArrayString() + ",\"inventoryPosition\"" +
                    ":{\"x\":" + x + ",\"y\":" + y + "},\"quantity\":1,\"containerID\":" + id + ",\"formatVersion\":2}";
        } else {
            return "{\"itemData\":\"\",\"data\":" + getDataArrayString() + ",\"inventoryPosition\"" +
                    ":{\"x\":" + x + ",\"y\":" + y + "},\"tabID\":" + id + ",\"quantity\":1,\"formatVersion\":1}";
        }
    }

    @Override
    public String toString() {
        if (itemStashInfo.charaEquipment) {
            String returnString = ContainerIds.containerIds.get(itemStashInfo.id);
            if (itemTier == -1) return "";

            if (itemType.getDataId() == 34) {
                returnString += itemType.getTierValues()[itemTier];
            } else {
                returnString += ": " + itemType.getTierNames()[itemTier] + " |"
                        + getAffixNumber() + " Affixes|";
            }
            return returnString;

        } else return itemType.getTierNames()[itemTier] + " [" + itemType.getName() +
                "] |" + getAffixNumber() + " Affixes|";
    }

    @Override
    public ItemAttribute getItemType() {
        return itemType;
    }

    @Override
    public void setItemType(ItemAttribute itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemTier() {
        return itemTier;
    }

    @Override
    public void setItemTier(int itemTier) {
        this.itemTier = itemTier;
    }

    @Override
    public int getAffixNumberVisual() {
        return affixNumberVisual;
    }

    @Override
    public void setAffixNumberVisual(int affixNumberVisual) {
        this.affixNumberVisual = affixNumberVisual;
    }

    @Override
    public int getImplicitValue1() {
        return implicitValue1;
    }

    @Override
    public void setImplicitValue1(int implicitValue1) {
        this.implicitValue1 = implicitValue1;
    }

    @Override
    public int getImplicitValue2() {
        return implicitValue2;
    }

    @Override
    public void setImplicitValue2(int implicitValue2) {
        this.implicitValue2 = implicitValue2;
    }

    @Override
    public int getImplicitValue3() {
        return implicitValue3;
    }

    @Override
    public void setImplicitValue3(int implicitValue3) {
        this.implicitValue3 = implicitValue3;
    }

    @Override
    public int getInstability() {
        return instability;
    }

    @Override
    public void setInstability(int instability) {
        this.instability = instability;
    }

    @Override
    public int getAffixNumber() {
        return affixNumber;
    }

    @Override
    public void setAffixNumber(int affixNumber) {
        this.affixNumber = affixNumber;
    }

    @Override
    public List<AffixData> getAffixList() {
        return affixList;
    }

    @Override
    public void setAffixList(List<AffixData> affixList) {
        this.affixList = affixList;
    }

    @Override
    public void setAffix(AffixData affixData, int slot) {
        if (affixData == null) {
            if (slot <= affixList.size()) affixList.remove(slot - 1);
        } else {
            if (affixList.contains(affixData)) return;
            if (slot > affixList.size()) {
                affixList.add(affixData);
            } else affixList.set(slot - 1, affixData);
            setAffixNumber(affixList.size());
            setAffixNumberVisual(affixList.size());
        }
    }

    @Override
    public void setAffixData(List<AffixData> affixData) {
        this.affixList.clear();
        this.affixList.addAll(affixData);
    }

    @Override
    public ItemStashInfo getItemStashInfo() {
        return itemStashInfo;
    }

    @Override
    public void setItemStashInfo(ItemStashInfo itemStashInfo) {
        this.itemStashInfo = itemStashInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractItem item = (AbstractItem) o;
        if (this.affixList == null || item.affixList == null) return false;

        boolean equalAffixes = true;
        List<AffixData> affixes = getAffixList();
        if (affixes.size() == item.getAffixList().size()) {
            for (int i = 0; i < affixes.size(); i++) {
                if (affixes.get(i).type.getDataId() != item.getAffixList().get(i).type.getDataId() ||
                        affixes.get(i).tier != item.getAffixList().get(i).tier ||
                        affixes.get(i).value != item.getAffixList().get(i).value) {
                    equalAffixes = false;
                    break;
                }
            }
        } else equalAffixes = false;

        return getItemTier() == item.getItemTier() &&
                getImplicitValue1() == item.getImplicitValue1() &&
                getImplicitValue2() == item.getImplicitValue2() &&
                getImplicitValue3() == item.getImplicitValue3() &&
                getInstability() == item.getInstability() &&
                getAffixNumber() == item.getAffixNumber() &&
                getItemType().getDataId() == item.getItemType().getDataId() &&
                getItemStashInfo().id == item.getItemStashInfo().id &&
                equalAffixes;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getItemType(), getItemTier(), getImplicitValue1(), getImplicitValue2(), getImplicitValue3(),
                            getInstability(), getAffixNumber(), getAffixList());
    }
}
