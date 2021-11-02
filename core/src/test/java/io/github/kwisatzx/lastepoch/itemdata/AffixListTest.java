package io.github.kwisatzx.lastepoch.itemdata;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("itemdata-test")
class AffixListTest {

    @Test
    void getById() {
        Affix meleeAttackSpeed = AffixList.getById(2, 0);
        assertEquals(2, meleeAttackSpeed.getDataId());
        assertEquals(2, meleeAttackSpeed.getAffixListId());
        assertEquals("INCREASED MELEE ATTACK SPEED", meleeAttackSpeed.getName().toUpperCase());

        Affix incIgniteEffect = AffixList.getById(211, 1);
        assertEquals(211, incIgniteEffect.getDataId());
        assertEquals(467, incIgniteEffect.getAffixListId());
        assertEquals("INCREASED IGNITE EFFECT", incIgniteEffect.getName().toUpperCase());
    }

    @Test
    void getByName() {
        Affix critMulti = AffixList.getByName("CRITICAL STRIKE MULTIPLIER WHILE DUAL WIELDING");
        assertEquals(200, critMulti.getDataId());
        assertEquals(1, critMulti.getAffixTypeMod());
    }

    @Test
    void getByPartialName() {
        Affix duskShroudWhenMeleeAttack = AffixList.getByPartialName("GAIN DUSK SHROUD");
        assertEquals(193, duskShroudWhenMeleeAttack.getDataId());
        assertEquals(1, duskShroudWhenMeleeAttack.getAffixTypeMod());
    }

    @Test
    void getListFromPartialName() {
        List<Affix> healthAffixes = AffixList.getListFromPartialName("HEALTH");
        assertTrue(healthAffixes.contains(AffixList.getById(199, 1)));
        assertTrue(healthAffixes.contains(AffixList.getById(44, 0)));
        assertTrue(healthAffixes.contains(AffixList.getById(33, 2)));
    }
}