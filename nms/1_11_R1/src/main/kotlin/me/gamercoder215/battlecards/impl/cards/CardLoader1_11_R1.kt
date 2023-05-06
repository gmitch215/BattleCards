package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.wrapper.CardLoader

class CardLoader1_11_R1 : CardLoader {

    override fun loadedCards(): Collection<Class<out IBattleCard<*>>> = listOf(
        IMesaZombie::class.java
    )

}