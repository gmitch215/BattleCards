package me.gamercoder215.battlecards.wrapper

import me.gamercoder215.battlecards.api.card.item.CardEquipment
import me.gamercoder215.battlecards.impl.cards.IBattleCard

interface CardLoader {

    fun loadedCards(): Collection<Class<out IBattleCard<*>>>

    fun loadedEquipment(): Collection<CardEquipment> = setOf()

}