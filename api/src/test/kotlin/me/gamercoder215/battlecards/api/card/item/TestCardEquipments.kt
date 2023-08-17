package me.gamercoder215.battlecards.api.card.item

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class TestCardEquipments {

    @Test
    @DisplayName("Test CardEquipments Modifiers")
    fun testCardEquipment() {
        CardEquipments.entries.forEach {
            Assertions.assertTrue(it.healthModifier >= 0.0)
            Assertions.assertTrue(it.damageModifier >= 0.0)
            Assertions.assertTrue(it.defenseModifier >= 0.0)
            Assertions.assertTrue(it.speedModifier >= 0.0)
            Assertions.assertTrue(it.knockbackResistanceModifier >= 0.0)
        }
    }

}