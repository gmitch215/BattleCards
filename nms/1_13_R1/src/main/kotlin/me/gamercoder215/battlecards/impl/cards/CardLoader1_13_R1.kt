package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.wrapper.CardLoader

internal class CardLoader1_13_R1 : CardLoader {

    override fun loadedCards(): Collection<Class<out IBattleCard<*>>> = listOf(
        ILapisDrowned::class.java,
        IKnight::class.java,
        IInfernoBlaze::class.java,
        INecromancer::class.java
    )

}