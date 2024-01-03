package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.impl.Type
import me.gamercoder215.battlecards.util.airOrNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse

class TestCardLoader1_19_R1 {

    companion object {
        internal val cardLoader = CardLoader1_19_R1()
    }

    @Test
    @DisplayName("Test 1.19.2 CardLoader")
    fun testCardLoader() {
        for (clazz in cardLoader.loadedCards()) {
            val type = clazz.getAnnotation(Type::class.java).type
            assertFalse { type.craftingMaterial.airOrNull }
        }

        cardLoader.loadedEquipment()
    }

}