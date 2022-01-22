package io.github.kwisatzx.lastepoch.itemdata;

import io.github.kwisatzx.lastepoch.fileoperations.ObjectMapperCache;

import java.util.ArrayList;
import java.util.List;

public class AffixList extends DataList<Affix> {
    private static final AffixList INSTANCE;

    static {
        INSTANCE = new AffixList();
        INSTANCE.readFromFile(ObjectMapperCache.getObjectMapper().getTypeFactory()
                                      .constructCollectionType(ArrayList.class, Affix.class));
    }

    private AffixList() {
    }

    public static List<Affix> getList() {
        return INSTANCE.getDataList();
    }

    public static List<String> getListAsStrings() {
        return INSTANCE.getStringList();
    }

    public static Affix getById(int id, int affixTypeMod) {
        for (Affix affix : INSTANCE.getDataList()) {
            if (affix.getDataId() == id && affix.getAffixTypeMod() == affixTypeMod) return affix;
        }
        return null;
    }

    public static Affix getByName(String name) {
        return INSTANCE.getAttributeByName(name);
    }

    public static Affix getByPartialName(String partialName) {
        return INSTANCE.getAttributeByPartialName(partialName);
    }

    public static List<Affix> getListFromPartialName(String partialName) {
        ArrayList<Affix> list = new ArrayList<>();
        for (Affix affix : INSTANCE.getDataList()) {
            if (affix.getName().contains(partialName.toUpperCase())) list.add(affix);
        }
        return list;
    }
}
