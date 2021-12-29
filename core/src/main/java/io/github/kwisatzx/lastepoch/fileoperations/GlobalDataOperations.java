package io.github.kwisatzx.lastepoch.fileoperations;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kwisatzx.lastepoch.itemdata.Selectable;
import io.github.kwisatzx.lastepoch.itemdata.item.Item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GlobalDataOperations extends FileStringOperations {
    private final ObjectMapper objectMapper;
    private final List<StashTabCategory> stashTabCategories;
    private final List<StashTab> stashTabs;
    private final List<Item> stashItems;

    public GlobalDataOperations(String saveDirectory) throws IOException {
        super(saveDirectory, "Epoch_Local_Global_Data_Beta");
        objectMapper = new ObjectMapper()
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        stashTabCategories = serializeStashTabCategories();
        stashTabs = serializeStashTabs();
        stashItems = serializeStashItems();
    }

    @Override
    protected void insertNewlinesAndTabs() {
        String newlineTab = "\r\n\t";
        String newlineTabTab = "\r\n\t\t";

        insertAfterStr("EPOCH{", "\r\n");
        insertAfterStr("\"stashList\":[", newlineTab);
        insertBeforeStr("\"gold\"", newlineTabTab);
        fileString.insert(fileString.length() - 1, "\r\n");

        Arrays.asList("\"savedShards\":[", "\"savedStashItems\":[",
                      "\"materialsList\":[", "\"categories\":[", "\"tabs\":[")
                .forEach(str -> prefixAndInsertAfterStr(newlineTab, str, newlineTabTab));

        while (fileString.indexOf("},{") != -1) {
            insertAfterStr("},{", newlineTabTab, -1);
        }
    }

    public List<StashTabCategory> getStashTabCategories() {
        return stashTabCategories;
    }

    public List<StashTab> getStashTabs() {
        return stashTabs;
    }

    public List<Item> getStashItems() {
        return stashItems;
    }

    private List<StashTabCategory> serializeStashTabCategories() {
        List<String> categoriesJson = getListOfObjectArray("categories");
        List<StashTabCategory> list = new ArrayList<>();
        for (String json : categoriesJson) {
            try {
                list.add(objectMapper.readValue(json, StashTabCategory.class));
            } catch (JsonProcessingException e) {
                e.printStackTrace(); //TODO logger
            }
        }
        return list;
    }

    private List<StashTab> serializeStashTabs() {
        List<String> tabJson = getListOfObjectArray("tabs");
        List<StashTab> list = new ArrayList<>();
        for (String json : tabJson) {
            try {
                list.add(objectMapper.readValue(json, StashTab.class));
            } catch (JsonProcessingException e) {
                e.printStackTrace(); //TODO logger
            }
        }
        return list;
    }

    private List<Item> serializeStashItems() {
        String[] itemJson = getObjectArray("savedStashItems").split("},\\{");
        List<Item> list = new ArrayList<>();
        for (String json : itemJson) {
            list.add(Item.itemFromInventoryString(json));
        }
        return list;
    }

    public StashTab getStashTabByName(String name) {
        for (StashTab stashTab : stashTabs) {
            if (stashTab.getDisplayName().equals(name)) return stashTab;
        }
        return null;
    }

    public void setStashItemsInFileString() {
        String[] savedItems = new String[getStashItems().size()];
        for (int i = 0; i < savedItems.length; i++) {
            savedItems[i] = getStashItems().get(i).getInventoryString();
        }
        setObjectArray("savedStashItems", Arrays.toString(savedItems).replaceAll(" ", ""));
    }

    public void setStashTabsInFileString() {
        setObjectArray("tabs", Arrays.toString(getStashTabs().toArray()).replaceAll(" ", ""));
    }

    public void addAllAffixShards() {
        fileString.delete(fileString.indexOf("{\"shard"), fileString.indexOf("}],") + 1);
        String shards = IntStream.rangeClosed(1, 700)
                .mapToObj(n -> "{\"shardType\":" + n + ",\"quantity\":999}")
                .collect(Collectors.joining(","));
        insertAfterStr("\"savedShards\":[", shards);
    }

    public boolean addUseableAffixShards() {
        return loadTxtFileIntoArray("savedShards", "savedShardsArray", true);
    }

    public void addGlyphsAndRunes() {
        List<String> materialsList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            materialsList.add("{\"itemData\":\"\",\"data\":[0,102," + i + "],\"inventoryPosition\":" +
                                      "{\"x\":-4,\"y\":-4},\"tabID\":0,\"quantity\":999,\"formatVersion\":0}");
        }

        for (int i = 0; i < 2; i++) {
            materialsList.add("{\"itemData\":\"\",\"data\":[0,103," + i + "],\"inventoryPosition\":" +
                                      "{\"x\":-4,\"y\":-4},\"tabID\":0,\"quantity\":999,\"formatVersion\":0}");
        }

        setObjectArray("materialsList", Arrays.toString(materialsList.toArray()).replaceAll(" ", ""));
    }

    private int getQuantitySumFromJsonList(List<String> jsonList) {
        int count = 0;
        for (String json : jsonList) {
            Matcher matcher = getQuantity.matcher(json);
            while (matcher.find()) {
                count += Integer.parseInt(matcher.group(1));
            }
        }
        return count;
    }

    public int getGold() {
        return getProperty("gold", int.class);
    }

    public int getStashTabCount() {
        return getStashTabs().size();
    }

    public int getGlyphCount() {
        List<String> glyphs = getListOfObjectArray("materialsList").subList(0, 5);
        if (glyphs.isEmpty()) return 0;
        return getQuantitySumFromJsonList(glyphs);
    }

    public int getRuneCount() {
        List<String> materialsList = getListOfObjectArray("materialsList");
        if (materialsList.size() < 6) return 0;
        List<String> runes = materialsList.subList(5, materialsList.size());
        return getQuantitySumFromJsonList(runes);
    }

    public int getAffixShardCount() {
        String savedShards = getObjectArray("savedShards");
        if (savedShards.equals("")) return 0;
        return getQuantitySumFromJsonList(List.of(savedShards));
    }

    public String getStashTabNameFromId(int tabID) {
        for (StashTab stashTab : getStashTabs()) {
            if (stashTab.tabID == tabID) return stashTab.getDisplayName();
        }
        return "";
    }

    public int getStashIdFromName(String displayName) {
        for (StashTab stashTab : getStashTabs()) {
            if (stashTab.displayName.equals(displayName)) return stashTab.tabID;
        }
        return -1;
    }

    public static class StashTabCategory implements Selectable {
        protected int categoryID;
        protected int iconID;
        protected int colorID;
        protected String displayName;
        protected int displayOrder;

        private StashTabCategory() {
        }

        protected StashTabCategory(int categoryID, String displayName, int displayOrder) {
            this.categoryID = categoryID;
            this.iconID = 0;
            this.colorID = 0;
            this.displayName = displayName;
            this.displayOrder = displayOrder;
        }

        public int getCategoryID() {
            return categoryID;
        }

        public void setCategoryID(int categoryID) {
            this.categoryID = categoryID;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    public static class StashTab extends StashTabCategory {
        private int tabID;

        private StashTab() {
        }

        public StashTab(int categoryID, String displayName, int displayOrder, int tabID) {
            super(categoryID, displayName, displayOrder);
            this.tabID = tabID;
        }

        public int getTabID() {
            return tabID;
        }

        public void setTabID(int tabID) {
            this.tabID = tabID;
        }

        public List<Item> getItemsInTab() {
            return FileHandler.getStashFile().getStashItems().stream()
                    .filter(item -> item.getItemStashInfo().id == tabID)
                    .toList();
        }
    }
}
