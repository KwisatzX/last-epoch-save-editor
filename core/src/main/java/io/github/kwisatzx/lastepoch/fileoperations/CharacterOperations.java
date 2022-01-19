package io.github.kwisatzx.lastepoch.fileoperations;

import io.github.kwisatzx.lastepoch.itemdata.ChrClass;
import io.github.kwisatzx.lastepoch.itemdata.Selectable;
import io.github.kwisatzx.lastepoch.itemdata.item.Item;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CharacterOperations extends FileStringOperations
        implements Selectable {
    private final Character character;

    public CharacterOperations(Path completeFilePath) throws IOException {
        super(completeFilePath.getParent().toString(),
              completeFilePath.getFileName().toString());
        character = new Character();
    }

    public CharacterOperations copyCharacter() {
        try {
            return FileHandler.addCharacter(copyCharacterFile());
        } catch (IOException e) {e.printStackTrace();} //TODO Logger
        return null;
    }

    public void setEquipmentInFileString() {
        String[] savedItems = new String[character.getEquipment().size()];
        for (int i = 0; i < savedItems.length; i++) {
            savedItems[i] = character.getEquipment().get(i).getInventoryString();
        }
        setObjectArray("savedItems", Arrays.toString(savedItems).replaceAll(" ", ""));
    }

    public void setAbilityBarInFileString(String[] abilityBar) {
        String[] abilityBarWithQuotes = new String[5];
        for (int i = 0; i < 5; i++) {
            abilityBarWithQuotes[i] = "\"" + abilityBar[i] + "\""; //later: get char abilityBar (when it's a list)
        }

        setArray("abilityBar", Arrays.toString(abilityBarWithQuotes).replaceAll(" ", ""));
    }

    public void setSkillTreesInFileString() {
        String[] masteredSkillsArray = new String[character.getMasteredSkills().size()];
        for (int i = 0; i < masteredSkillsArray.length; i++) {
            masteredSkillsArray[i] = character.getMasteredSkills().get(i).getFileString();
        }
        setObjectArray("savedSkillTrees", Arrays.toString(masteredSkillsArray).replaceAll(" ", ""));
    }

    public void setAllStability(int value) {
        setAllProperties("stability", value + "");
    }

    public void setAllCorruption(int value) {
        setAllProperties("corruption", value + "");
    }

    public boolean setQuestsCompleted() {
        boolean success;
        success = loadTxtFileIntoArray("savedQuests", "savedQuestsArray", true);
        success = loadTxtFileIntoArray("sceneProgresses", "sceneProgressesArray", true) && success;
        success = loadTxtFileIntoArray("oneTimeEvents", "oneTimeEventsArray", false) && success;
        setProperty("portalUnlocked", "true");
        setProperty("reachedTown", "true");
        setProperty("closedSkillsTooltip", "true");
        setProperty("closedPassivesTooltip", "true");
        setProperty("closedIdolsTooltip", "true");
        setProperty("closedMinSkillLevelTutorial", "true");
        return success;
    }

    public boolean setWaypointsUnlocked() {
        return loadTxtFileIntoArray("unlockedWaypointScenes", "unlockedWaypointScenesArray", false);
    }

    public boolean setTimelinesUnlocked() {
        boolean success;
        success = loadTxtFileIntoArray("savedMonolithQuests", "savedMonolithQuestsArray", true);
        success = loadTxtFileIntoArray("monolithRuns", "monolithRunsArray", true) && success;
        success = loadTxtFileIntoArray("timelineCompletion", "timelineCompletionArray", true) && success;
        success = loadTxtFileIntoArray("timelineDifficultyCompletion",
                                       "timelineDifficultyCompletionArray", true) && success;
        success = loadTxtFileIntoArray("timelineDifficultyUnlocks", "timelineDifficultyUnlocksArray", true) && success;
        success = loadTxtFileIntoArray("blessingsDiscovered", "blessingsDiscoveredArray", false) && success;
        setProperty("monolithEchoesConquered", "1000");
        setProperty("monolithTimelinesConquered", "100");
        setProperty("closedMonolithTooltip", "true");
        setProperty("previousMonolithEchoTimelineID", "4");
        setProperty("maxWave", "85");
        return success;
    }

    public Character getCharacter() {
        return character;
    }

    @Override
    public String toString() {
        return character.getName();
    }

    @Override
    protected void insertNewlinesAndTabs() {
        String newlineTab = "\r\n\t";
        String newline2Tab = "\r\n\t\t";
        String newline3Tab = newline2Tab + "\t";

        insertAfterStr("EPOCH{", newlineTab);
        insertAfterStr("\"savedItems\":[", newline2Tab);
        prefixAndInsertAfterStr(newline2Tab, "\"islands\":[", newline3Tab);
        prefixAndInsertAfterStr(newline2Tab, "\"options\":[", newline3Tab);
        fileString.insert(fileString.length() - 1, "\r\n");

        Arrays.asList("\"savedSkillTrees\":[", "\"savedQuests\":[", "\"oneTimeEvents\":[",
                      "\"sceneProgresses\":[", "\"monolithRuns\":[")
                .forEach(str -> prefixAndInsertAfterStr("\r\n\t", str, newline2Tab));

        Arrays.asList("\"savedCharacterTree\"", "\"unlockedWaypointScenes\"", "\"abilityBar\"",
                      "\"portalUnlocked\"", "\"savedMonolithQuests\"", "\"timelineCompletion\"")
                .forEach(str -> insertBeforeStr(str, newlineTab));

        Arrays.asList("\"characterTreeNodeProgression\"", "\"savedEchoWeb\"", "\"stability\"")
                .forEach(str -> insertBeforeStr(str, newline2Tab));

        while (fileString.indexOf(",\"") < fileString.indexOf(":[")) {
            insertAfterStr(",\"", newlineTab, -1);
        }

        while (fileString.lastIndexOf(",\"") > fileString.lastIndexOf("}]")) {
            fileString.insert(fileString.lastIndexOf(",\"") + 1, newlineTab);
        }

        while (fileString.indexOf("},{") != -1) {
            if (fileString.indexOf("},{") > fileString.indexOf("\"islands\""))
                insertAfterStr("},{", newline3Tab, -1);
            else insertAfterStr("},{", newline2Tab, -1);
        }

        while (fileString.indexOf(",\"savedEchoWeb\":") != -1) {
            insertBeforeStrOffset(",\"savedEchoWeb\":", newline3Tab, 1);
        }
        while (fileString.indexOf(",\"islands\":") != -1) {
            insertBeforeStrOffset(",\"islands\":", newline3Tab, 1);
        }
        while (fileString.indexOf(",\"stability\":") != -1) {
            insertBeforeStrOffset(",\"stability\":", newline3Tab, 1);
        }
        while (fileString.indexOf(",\"options\":") != -1) {
            insertBeforeStrOffset(",\"options\":", newline3Tab, 1);
        }
    }

    public class Character {
        private final List<Item> equipment;
        private final List<SkillTree> masteredSkills;
        private final String[] abilityBar;

        public Character() {
            String[] items = getObjectArray("savedItems").split(Pattern.quote("},{"));
            if (!items[0].trim().equals("")) {
                equipment = Arrays.stream(items)
                        .map(Item::itemFromInventoryString)
                        .collect(Collectors.toCollection(ArrayList::new));
            } else equipment = new ArrayList<>();


            String[] masteredSkillsStr = getObjectArray("savedSkillTrees").split(Pattern.quote("},{"));
            if (!masteredSkillsStr[0].trim().equals("")) {
                masteredSkills = Arrays.stream(masteredSkillsStr)
                        .map(SkillTree::fileStringToSkillTree)
                        .collect(Collectors.toCollection(ArrayList::new));
            } else masteredSkills = new ArrayList<>();

            abilityBar = new String[5];
            String[] abilityBarWithQuotes = getArray("abilityBar").split(Pattern.quote(","));
            for (int i = 0; i < 5; i++) {
                abilityBar[i] = abilityBarWithQuotes[i].substring(1, abilityBarWithQuotes[i].length() - 1);
            }
        }

        public void addItem(Item item) {
            equipment.add(item);
        }

        public void addOrReplaceEquipmentItem(Item item) {
            List<Item> equipment = getEquipment();
            boolean slotTaken = false;
            for (Item eqItem : equipment) {
                if (eqItem.getItemStashInfo().id == item.getItemStashInfo().id) {
                    slotTaken = true;
                    if (item.getItemStashInfo().id == 9) {
                        fillSecondRingSlotIfEmpty(item, equipment, eqItem);
                    } else {
                        equipment.set(equipment.indexOf(eqItem), item);
                        break;
                    }
                }
            }

            if (!slotTaken) {
                equipment.add(item);
            }
        }

        private void fillSecondRingSlotIfEmpty(Item item, List<Item> equipment, Item eqItem) {
            boolean secondRingSlotTaken = false;
            for (Item eqItem2 : equipment) {
                if (eqItem2.getItemStashInfo().id == 10) {
                    secondRingSlotTaken = true;
                    break;
                }
            }
            if (secondRingSlotTaken) {
                if (item.equals(eqItem)) {
                    item.getItemStashInfo().id = 10;
                    addOrReplaceEquipmentItem(item);
                } else {
                    equipment.set(equipment.indexOf(eqItem), item);
                }

            } else {
                item.getItemStashInfo().id = 10;
                equipment.add(item);
            }
        }

        public void maxMasteryLevels() {
            masteredSkills.forEach(skillTree -> skillTree.xp = SkillTree.MAX_XP);
        }

        public void maxMasteryNodes() {
            int lastIndex = fileString.indexOf("\"savedSkillTrees\":[") + "\"savedSkillTrees\":[".length();
            for (int i = 0; i < getMasteredSkills().size(); i++) {
                int nodeIdsIndex = fileString.indexOf("\"nodeIDs\"", lastIndex);
                String[] nodeIds = getArrayAfterIndex(nodeIdsIndex).split(",");
                if (nodeIds.length > 0) {
                    int numOfNodes = (int) Arrays.stream(nodeIds).mapToInt(Integer::parseInt).count();
                    int[] nodePoints = new int[numOfNodes];
                    Arrays.fill(nodePoints, 9);
                    int nodePointsIndex = fileString.indexOf("\"nodePoints\"", nodeIdsIndex);
                    setArrayAfterIndex(nodePointsIndex, Arrays.toString(nodePoints).replaceAll(" ", ""));
                    lastIndex = nodePointsIndex;
                }
            }
        }

        public void maxPassiveNodes() {
            int savedCharacterTreeIndex = fileString.indexOf("\"savedCharacterTree\":");
            String[] nodeIds = getArrayAfterIndex(savedCharacterTreeIndex).split(",");
            if (nodeIds.length > 0) {
                int numOfNodes = (int) Arrays.stream(nodeIds).mapToInt(Integer::parseInt).count();
                int[] nodePoints = new int[numOfNodes];
                Arrays.fill(nodePoints, 15);
                setArrayAfterIndex(fileString.indexOf("\"nodePoints\"", savedCharacterTreeIndex),
                                   Arrays.toString(nodePoints).replaceAll(" ", ""));
            }
        }

        public String[] getAbilityBar() {
            return abilityBar;
        }

        public String getName() {
            return getProperty("characterName", String.class);
        }

        public int getLevel() {
            return getProperty("level", int.class);
        }

        public boolean isHardcore() {
            return getProperty("hardcore", boolean.class);
        }

        public boolean isMasochist() {
            return getProperty("masochist", boolean.class);
        }

        public boolean isSolo() {
            return getProperty("soloChallenge", boolean.class);
        }

        public ChrClass getChrClass() {
            return ChrClass.fromId(getProperty("characterClass", int.class));
        }

        public ChrClass.ClassMastery getMastery() {
            return getChrClass().getMasteryFromId(getProperty("chosenMastery", int.class));
        }

        public List<Item> getEquipment() {
            return equipment;
        }

        public List<SkillTree> getMasteredSkills() {
            return masteredSkills;
        }

        public static class SkillTree {
            public static final int MAX_XP = 5700000;
            public String treeId;
            public int slotNumber;
            public int xp;
            public int[] nodeIds;
            public int[] nodePoints;

            public SkillTree(String treeId, int slotNumber, int xp,
                             int[] nodeIds, int[] nodePoints) {
                this.treeId = treeId;
                this.slotNumber = slotNumber;
                this.xp = xp;
                this.nodeIds = nodeIds;
                this.nodePoints = nodePoints;
            }

            //TODO JSON
            String getFileString() {
                return "{\"treeID\":\"" + treeId + "\",\"slotNumber\":" + slotNumber + ",\"xp\":" + xp + "," +
                        "\"version\":0,\"nodeIDs\":" + Arrays.toString(nodeIds).replaceAll(" ", "") +
                        ",\"nodePoints\":" + Arrays.toString(nodePoints).replaceAll(" ", "") +
                        ",\"unspentPoints\":25,\"nodesTaken\":[],\"abilityXP\":0.0}";
            }

            public static SkillTree fileStringToSkillTree(String str) {
                String treeId = getSubstringBetween(str, "\"treeID\":\"", "\",\"slot");
                String slotNumber = getSubstringBetween(str, "\"slotNumber\":", ",\"xp\"");
                String xp = getSubstringBetween(str, "\"xp\":", ",\"ver");

                int[] nodeIds;
                String[] nodeIdsStr = getSubstringBetween(str, "\"nodeIDs\":[", "]").split(",");
                if (nodeIdsStr[0].equals("")) nodeIds = new int[0];
                else {
                    nodeIds = new int[nodeIdsStr.length];
                    for (int i = 0; i < nodeIdsStr.length; i++) {
                        try {
                            nodeIds[i] = Integer.parseInt(nodeIdsStr[i]);
                        } catch (NumberFormatException e) {e.printStackTrace();} //TODO Logger
                    }
                }

                int[] nodePoints;
                String[] nodePointsStr = getSubstringBetween(str, "\"nodePoints\":[", "]").split(",");
                if (nodePointsStr[0].equals("")) nodePoints = new int[0];
                else {
                    nodePoints = new int[nodePointsStr.length];
                    for (int i = 0; i < nodePointsStr.length; i++) {
                        try {
                            nodePoints[i] = Integer.parseInt(nodePointsStr[i]);
                        } catch (NumberFormatException e) {e.printStackTrace();} //TODO Logger
                    }
                }

                return new SkillTree(treeId, Integer.parseInt(slotNumber), Integer.parseInt(xp),
                                     nodeIds, nodePoints);
            }
        }
    }
}
