package io.github.kwisatzx.lastepoch.fileoperations;

import com.fasterxml.jackson.databind.MappingJsonFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public abstract class FileStringOperations extends FileOperations {
    protected final MappingJsonFactory jsonFactory;
    Pattern getQuantity;

    protected FileStringOperations(String saveDirectory, String filePath) throws IOException {
        super(saveDirectory, filePath);
        jsonFactory = new MappingJsonFactory();
        getQuantity = Pattern.compile("\"quantity\":(\\d+)\\W");
    }

    public void expandFile() {
        if (isAlreadyExpanded()) return;
        insertNewlinesAndTabs();
        saveToFile();
    }

    protected boolean loadTxtFileIntoArray(String arrayName, String fileName, boolean isObjectArray) {
        Optional<String> txtFileString = loadJsonArrayTxtFile(fileName);
        if (txtFileString.isPresent()) {
            if (isObjectArray) setObjectArray(arrayName, txtFileString.get());
            else setArray(arrayName, txtFileString.get());
            return true;
        } else
            return false; //TODO maybe just throw unchecked exception in the deepest method and catch at the call root?
    }

    protected abstract void insertNewlinesAndTabs();

    private boolean isAlreadyExpanded() {
        if (fileString.toString().contains("\t")) {
            System.out.println("File " + file.getFileName() + " already expanded, skipping..."); //TODO Logger
            return true;
        }
        return false;
    }

    public static String getSubstringBetween(String source, String subStart, String subEnd) {
        return getSubstringBetweenWithOffset(source, subStart, subEnd, 0);
    }

    private static String getSubstringBetweenWithOffset(String source, String subStart, String subEnd, int offset) {
        return source.substring(source.indexOf(subStart) + subStart.length(),
                                source.indexOf(subEnd, source.indexOf(subStart)) + offset);
    }

    private void setFileStringBetween(String beginningStr, String endingStr, String value) {
        setFileStringBetween(beginningStr, endingStr, 0, value);
    }

    private void setFileStringBetween(String beginningStr, String endingStr, int endingStrOffset, String value) {
        int index = fileString.indexOf(beginningStr) + beginningStr.length();
        int end = fileString.indexOf(endingStr, index) + endingStrOffset;
        fileString.replace(index, end, value);
    }

    public <T> T getProperty(String name, Class<T> valueType) {
        int index = fileString.indexOf("\"" + name + "\":") + name.length() + 3;
        String valueStr = fileString.substring(index, fileString.indexOf(",", index));
        T value = null;
        try {
            value = jsonFactory.createParser(valueStr).readValueAs(valueType);
        } catch (IOException e) {
            e.printStackTrace(); //TODO: Logger
        }
        return value;
    }

    protected Optional<String> loadJsonArrayTxtFile(String fileName) {
        return loadTextFile("/json arrays/" + fileName + ".txt");
    }

    public void setProperty(String name, String value) {
        setFileStringBetween("\"" + name + "\":", ",", value);
    }

    public void setAllProperties(String name, String value) { //TODO: change to regex
        String beginningStr = "\"" + name + "\":";
        String endingStr = ",";
        int indexBeginning = 0;
        while (true) {
            indexBeginning = fileString.indexOf(beginningStr, indexBeginning + 1);
            if (indexBeginning == -1) break;
            int index = indexBeginning + beginningStr.length();
            int end = fileString.indexOf(endingStr, index);
            fileString.replace(index, end, value);
        }
    }

    //savedItems, savedSkillTrees, savedQuests, sceneProgresses, monolithRuns,
    protected String getObjectArray(String name) {
        String src = "\"" + name + "\":[";
        if (fileString.charAt(fileString.indexOf(src) + src.length()) == ']') return "";
        return getSubstringBetweenWithOffset(fileString.toString(), src, "}]", 1)
                .replaceAll("\t", "")
                .replaceAll(" ", "");
    }

    protected List<String> getListOfObjectArray(String name) {
        String arrayString = getObjectArray(name);
        List<String> list = new ArrayList<>();
        int beginIndex = 0, endIndex;
        while (arrayString.indexOf("},{", beginIndex) > 0) {
            endIndex = arrayString.indexOf("},{", beginIndex) + 1;
            list.add(arrayString.substring(beginIndex, endIndex));
            beginIndex = endIndex + 1;
        }
        list.add(arrayString.substring(beginIndex));
        return list;
    }

    public void setObjectArray(String name, String value) {
        String src = "\"" + name + "\":";
        if (fileString.charAt(fileString.indexOf(src) + src.length() + 1) == ']')
            setFileStringBetween(src, "]", 1, value);
        else setFileStringBetween(src, "}]", 2, value);
    }

    //nodeIDs + nodePoints, unlockedWaypointScenes, abilityBar, oneTimeEvents, blessingsDiscovered
    protected String getArray(String name) {
        String src = "\"" + name + "\":[";
        return getSubstringBetween(fileString.toString(), src, "]");
    }

    public void setArray(String name, String value) {
        setFileStringBetween("\"" + name + "\":", "]", 1, value);
    }

    protected String getArrayAfterIndex(int index) {
        int start = fileString.indexOf("[", index) + 1;
        int end = fileString.indexOf("]", start);
        return fileString.substring(start, end);
    }

    public void setArrayAfterIndex(int index, String value) {
        int start = fileString.indexOf("[", index);
        int end = fileString.indexOf("]", start) + 1;
        fileString.replace(start, end, value);
    }

    protected void insertAfterStr(String str, String insert) {
        insertAfterStr(str, insert, 0);
    }

    protected void insertAfterStr(String str, String insert, int offset) {
        if (fileString.indexOf(str) != -1) {
            fileString.insert(fileString.indexOf(str) + str.length() + offset, insert);
        }
    }

    protected void insertBeforeStr(String str, String insert) {
        insertBeforeStrOffset(str, insert, 0);
    }

    protected void insertBeforeStrOffset(String str, String insert, int offset) {
        if (fileString.indexOf(str) != -1) {
            fileString.insert(fileString.indexOf(str) + offset, insert);
        }
    }

    protected void prefixAndInsertAfterStr(String prefix, String str, String insert) {
        if (fileString.indexOf(str) != -1) {
            fileString.insert(fileString.indexOf(str), prefix);
            fileString.insert(fileString.indexOf(str) + str.length(), insert);
        }
    }
}
