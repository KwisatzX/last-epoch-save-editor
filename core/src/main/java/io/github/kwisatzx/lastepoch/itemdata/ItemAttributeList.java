package io.github.kwisatzx.lastepoch.itemdata;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public final class ItemAttributeList extends DataList<ItemAttribute> {
    private static final ItemAttributeList INSTANCE;

    static {
        INSTANCE = new ItemAttributeList();
        INSTANCE.readFromFile(new ObjectMapper().getTypeFactory()
                                      .constructCollectionType(ArrayList.class, ItemAttribute.class));
    }

    private ItemAttributeList() {
    }

    public static List<ItemAttribute> getList() {
        return INSTANCE.getDataList();
    }

    public static List<String> getListAsStrings() {
        return INSTANCE.getStringList();
    }

    public static ItemAttribute getById(int id) {
        return INSTANCE.getAttributeById(id);
    }

    public static ItemAttribute getByName(String name) {
        return INSTANCE.getAttributeByName(name);
    }

    public static ItemAttribute getByPartialName(String partialName) {
        return INSTANCE.getAttributeByPartialName(partialName);
    }
}
