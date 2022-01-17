package io.github.kwisatzx.lastepoch.itemdata;

import java.util.Arrays;
import java.util.List;

public enum AffixTier {
    TIER1(0),
    TIER2(16),
    TIER3(32),
    TIER4(48),
    TIER5(64),
    TIER6(80),
    TIER7(96);

    AffixTier(int value) {
        this.value = value;
    }

    private final int value;

    public int getValue() {
        return value;
    }

    public static AffixTier maxTier(Affix affix) {
        return (affix.getNumberOfTiers() > 1) ? TIER7 : TIER1;
    }

    public static List<String> getStringList() {
        return Arrays.stream(values()).map(AffixTier::name).toList();
    }
}
