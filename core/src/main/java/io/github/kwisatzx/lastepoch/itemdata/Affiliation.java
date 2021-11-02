package io.github.kwisatzx.lastepoch.itemdata;

import java.util.List;

public final class Affiliation {
    public static final int PRIMALIST = 1;
    public static final int MAGE = 1 << 1;
    public static final int SENTINEL = 1 << 2;
    public static final int ACOLYTE = 1 << 3;
    public static final int ROGUE = 1 << 4;
    public static final int ALL = PRIMALIST | MAGE | SENTINEL | ACOLYTE | ROGUE;

    public static String asString(int affiliation) { //TODO: change == to > 0, test
        StringBuilder flagNames = new StringBuilder();
        if (affiliation == ALL) return "Any";
        if ((affiliation & PRIMALIST) == PRIMALIST) flagNames.append("Primalist, ");
        if ((affiliation & MAGE) == MAGE) flagNames.append("Mage, ");
        if ((affiliation & SENTINEL) == SENTINEL) flagNames.append("Sentinel, ");
        if ((affiliation & ACOLYTE) == ACOLYTE) flagNames.append("Acolyte, ");
        if ((affiliation & ROGUE) == ROGUE) flagNames.append("Rogue, ");
        return flagNames.substring(0, flagNames.length() - 2);
    }

    public static int asInt(String affiliation) {
        return switch (affiliation.toLowerCase()) {
            case "primalist" -> PRIMALIST;
            case "mage" -> MAGE;
            case "sentinel" -> SENTINEL;
            case "acolyte" -> ACOLYTE;
            case "rogue" -> ROGUE;
            case "any" -> ALL;
            default -> -1;
        };
    }

    public static List<String> getStringList() {
        return List.of("Primalist", "Mage", "Sentinel", "Acolyte", "Rogue", "Any");
    }
}
