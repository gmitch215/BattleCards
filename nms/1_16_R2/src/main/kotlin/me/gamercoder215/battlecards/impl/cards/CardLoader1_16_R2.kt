package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.wrapper.CardLoader

internal class CardLoader1_16_R2 : CardLoader {

    override fun loadedCards(): Collection<Class<out IBattleCard<*>>> = listOf(
        INetheritePiglin::class.java
    )

}