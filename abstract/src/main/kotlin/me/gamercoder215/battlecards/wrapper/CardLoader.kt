package me.gamercoder215.battlecards.wrapper

import me.gamercoder215.battlecards.impl.cards.IBattleCard

interface CardLoader {

    fun loadedCards(): Collection<Class<out IBattleCard<*>>>

}