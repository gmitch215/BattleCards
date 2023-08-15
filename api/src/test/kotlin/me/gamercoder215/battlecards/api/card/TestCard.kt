package me.gamercoder215.battlecards.api.card

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class TestCard {

    @Test
    @DisplayName("Test BattleCard#toExperience")
    fun testToExperience() {
        Assertions.assertEquals(0.0, Card.toExperience(1))
        Assertions.assertEquals(1, Card.toLevel(1.0))
        Assertions.assertEquals(1, Card.toLevel(0.0))

        Assertions.assertThrows(IllegalArgumentException::class.java) { Card.toExperience(0) }
        Assertions.assertThrows(IllegalArgumentException::class.java) { Card.toExperience(-1) }
        Assertions.assertThrows(IllegalArgumentException::class.java) { Card.toExperience(Card.MAX_LEVEL + 1) }

        for (rarity in Rarity.entries) {
            Assertions.assertEquals(2, Card.toLevel(Card.toExperience(2, rarity) + 1.0, rarity))
            Assertions.assertEquals(1, Card.toLevel(Card.toExperience(2, rarity) - 1.0, rarity))

            Assertions.assertEquals(2, Card.toLevel(Card.toExperience(2, rarity), rarity))
            Assertions.assertEquals(4, Card.toLevel(Card.toExperience(4, rarity), rarity))

            Assertions.assertEquals(rarity.maxCardExperience, Card.toExperience(rarity.maxCardLevel, rarity))
            Assertions.assertEquals(rarity.maxCardLevel, Card.toLevel(rarity.maxCardExperience, rarity))
        }
    }

}