package io.github.kwisatzx.lastepoch.itemdata;

import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Stream;

import static io.github.kwisatzx.lastepoch.itemdata.ChrClass.ClassMastery.*;

public enum ChrClass {
    PRIMALIST(0, List.of(ClassMastery.PRIMALIST, BEASTMASTER, SHAMAN, DRUID)),
    MAGE(1, List.of(ClassMastery.MAGE, SORCERER, SPELLBLADE, RUNEMASTER)),
    SENTINEL(2, List.of(ClassMastery.SENTINEL, VOID_KNIGHT, FORGE_GUARD, PALADIN)),
    ACOLYTE(3, List.of(ClassMastery.ACOLYTE, NECROMANCER, LICH, WARLOCK)),
    ROGUE(4, List.of(ClassMastery.ROGUE, BLADEDANCER, MARKSMAN, FALCONER));

    ChrClass(int id, List<ClassMastery> masteries) {
        this.id = id;
        this.masteries = masteries;
    }

    private final int id;
    private final List<ClassMastery> masteries;

    public int getId() {
        return id;
    }

    public static ChrClass fromId(int id) {
        for (ChrClass chrClass : values()) {
            if (chrClass.id == id) return chrClass;
        }
        LoggerFactory.getLogger(ChrClass.class).error("Could not find Class for given id: " + id);
        throw new IllegalArgumentException("Could not find Class for given id: " + id);
    }

    public ChrClass.ClassMastery getMasteryFromId(int id) {
        for (ChrClass.ClassMastery classMastery : masteries) {
            if (classMastery.id == id) return classMastery;
        }
        return null;
    }

    public List<String> getMasteryStringList() {
        return masteries.stream().map(Enum::name).toList();
    }

    public static List<ClassMastery> getMasteryGroupFromMastery(ClassMastery mastery) {
        for (ChrClass chrClass : values()) {
            if (chrClass.masteries.contains(mastery)) return chrClass.masteries;
        }
        throw new IllegalArgumentException("Mastery not found in character classes: " + mastery);
    }

    public static List<String> getChrClassesStringList() {
        return Stream.of(values()).map(Enum::name).toList();
    }

    @Override
    public String toString() {
        return name();
    }

    public enum ClassMastery {
        PRIMALIST(0),
        BEASTMASTER(1),
        SHAMAN(2),
        DRUID(3),

        MAGE(0),
        SORCERER(1),
        SPELLBLADE(2),
        RUNEMASTER(3),

        SENTINEL(0),
        VOID_KNIGHT(1),
        FORGE_GUARD(2),
        PALADIN(3),

        ACOLYTE(0),
        NECROMANCER(1),
        LICH(2),
        WARLOCK(3),

        ROGUE(0),
        BLADEDANCER(1),
        MARKSMAN(2),
        FALCONER(3);

        ClassMastery(int id) {
            this.id = id;
        }

        private final int id;

        public int getId() {
            return id;
        }
    }
}
