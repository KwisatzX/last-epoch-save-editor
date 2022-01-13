package io.github.kwisatzx.lastepoch.gui.views.elements;

import io.github.kwisatzx.lastepoch.itemdata.Affix;
import io.github.kwisatzx.lastepoch.itemdata.AffixList;

import java.util.List;

public final class AffixDisplayer {
    private static String previewChoice = "For Tier 7";
    private final Affix affix;

    public AffixDisplayer(Affix affix) {
        this.affix = affix;
    }

    public static List<AffixDisplayer> getList() {
        return AffixList.getList().stream()
                .map(AffixDisplayer::new)
                .toList();
    }

    public static List<AffixDisplayer> getList(List<Affix> affixList) {
        return affixList.stream()
                .map(AffixDisplayer::new)
                .toList();
    }

    public static void setPreviewChoice(String previewChoice) {
        AffixDisplayer.previewChoice = previewChoice;
    }

    public static Affix getAffixFromDisplayName(String name) {
        String filtered;
        if (name.contains("Idol Affix")) {
            filtered = name.substring(0, name.length() - " [Idol Affix]".length());
        } else filtered = name;
        for (Affix affix : AffixList.getList()) {
            for (String tierValue : affix.getTierValues()) {
                if (tierValue.equals(filtered)) return affix;
            }
        }
        return null;
    }

    public Affix getAffix() {
        return affix;
    }

    @Override
    public String toString() {
        boolean oneTier = affix.getNumberOfTiers() == 1;
        String affixText;
        if (oneTier) affixText = affix.getTierValues()[0];
        else {
            switch (previewChoice) {
                case "For Tier 7" -> affixText = affix.getTierValues()[6];
                case "For Tier 6" -> affixText = affix.getTierValues()[5];
                case "For Tier 5" -> affixText = affix.getTierValues()[4];
                case "For Tier 4" -> affixText = affix.getTierValues()[3];
                case "For Tier 3" -> affixText = affix.getTierValues()[2];
                case "For Tier 2" -> affixText = affix.getTierValues()[1];
                case "For Tier 1" -> affixText = affix.getTierValues()[0];
                default -> affixText = affix.getName();
            }
        }
        if (oneTier) affixText += " [Idol Affix]";
        return affixText;
    }
}
