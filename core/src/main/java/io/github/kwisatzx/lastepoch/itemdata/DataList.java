package io.github.kwisatzx.lastepoch.itemdata;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract class DataList<E extends Attribute> {
    private ArrayList<E> list;

    protected DataList() {
        list = new ArrayList<>();
    }

    public List<E> getDataList() {
        return Collections.unmodifiableList(list);
    }

    public List<String> getStringList() {
        return list.stream()
                .map(Attribute::getName)
                .toList();
    }

    public E getAttributeById(int id) {
        return list.get(id);
    }

    public E getAttributeByName(String name) {
        for (E e : list) {
            if (e.getName().equals(name)) return e;
        }
        return null;
    }

    public E getAttributeByPartialName(String partialName) {
        for (E e : list) {
            if (e.getName().toUpperCase().contains(partialName.toUpperCase())) return e;
        }
        return null;
    }

    public void readFromFile(JavaType listType) {
//        Path file = Paths.get("item data/" + this.getClass().getSimpleName() + ".json");
        InputStream fileStream = this.getClass()
                .getResourceAsStream("/item data/" + this.getClass().getSimpleName() + ".json");
        if (fileStream != null) {
            ObjectMapper objectMapper = new ObjectMapper()
                    .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                    .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
                list = objectMapper.readValue(fileStream, listType);
            } catch (IOException e) {e.printStackTrace();}
        } else System.err.println("JSON list not found for " + this.getClass().getSimpleName() + "!"); //TODO: Logger
    }
}
