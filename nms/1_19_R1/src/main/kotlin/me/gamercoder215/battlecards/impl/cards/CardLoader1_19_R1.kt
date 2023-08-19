package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.item.CardEquipment
import me.gamercoder215.battlecards.wrapper.CardLoader

internal class CardLoader1_19_R1 : CardLoader {

    override fun loadedCards(): Collection<Class<out IBattleCard<*>>> = listOf()

    override fun loadedEquipment(): Collection<CardEquipment> = CardEquipments1_19_R1.entries

}