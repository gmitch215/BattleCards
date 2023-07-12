package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.wrapper.CardLoader

internal class CardLoader1_14_R1 : CardLoader {

    override fun loadedCards(): Collection<Class<out IBattleCard<*>>> = listOf(
        IBomberman::class.java,
        IMercenary::class.java,
        IPhantomRider::class.java,
        IRaider::class.java
    )

}