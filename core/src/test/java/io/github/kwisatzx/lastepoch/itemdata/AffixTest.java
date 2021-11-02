package io.github.kwisatzx.lastepoch.itemdata;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Affix correct returns test")
class AffixTest {
    @Test
    void getCorrectAffixData() {
        Affix hpGainedWhenStunned = AffixList.getById(21, 1);

        assertAll("Affix returns correct data",
                  () -> assertEquals(21, hpGainedWhenStunned.getDataId()),
                  () -> assertEquals(1, hpGainedWhenStunned.getAffixTypeMod()),
                  () -> assertEquals(277, hpGainedWhenStunned.getAffixListId()),
                  () -> assertEquals("HEALTH GAINED WHEN STUNNED", hpGainedWhenStunned.getName().toUpperCase()),
                  () -> assertEquals(1, hpGainedWhenStunned.getNumberOfTiers()),
                  () -> assertEquals(List.of("+150 HEALTH GAINED WHEN STUNNED"),
                                     List.of(hpGainedWhenStunned.getTierValues())),
                  () -> assertEquals(List.of(0),
                                     Arrays.stream(hpGainedWhenStunned.getLevelRequirement()).boxed().toList())
        );
    }
}