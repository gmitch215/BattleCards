package me.gamercoder215.battlecards.api.card

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class TestBattleCard {

    @Test
    @DisplayName("Test BattleCard#toExperience")
    fun testToExperience() {
        Assertions.assertEquals(0.0, BattleCard.toExperience(1))
        Assertions.assertEquals(1350.0, BattleCard.toExperience(2))
        Assertions.assertThrows(IllegalArgumentException::class.java) { BattleCard.toExperience(0) }
        Assertions.assertThrows(IllegalArgumentException::class.java) { BattleCard.toExperience(-1) }
        Assertions.assertThrows(IllegalArgumentException::class.java) { BattleCard.toExperience(BattleCard.MAX_LEVEL + 1) }
    }

}