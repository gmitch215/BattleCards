package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.wrapper.CardLoader

internal class CardLoader1_20_R1 : CardLoader {

    override fun loadedCards(): Collection<Class<out IBattleCard<*>>> = listOf(
        ISeaLord::class.java,
        IPurpleParasite::class.java
    )

}