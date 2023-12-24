package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.impl.Type
import org.bukkit.Material
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class TestCardLoader1_14_R1 {

    companion object {
        internal val cardLoader = CardLoader1_14_R1()
    }

    @Test
    @DisplayName("Test 1.14.4 CardLoader")
    fun testCardLoader() {
        for (clazz in cardLoader.loadedCards()) {
            val type = clazz.getAnnotation(Type::class.java).type
            assertTrue { type.craftingMaterial != Material.AIR }
        }

        cardLoader.loadedEquipment()
    }

}