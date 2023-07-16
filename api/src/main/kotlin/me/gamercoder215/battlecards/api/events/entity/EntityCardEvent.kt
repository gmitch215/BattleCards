package me.gamercoder215.battlecards.api.events.entity

import me.gamercoder215.battlecards.api.card.BattleCard
import me.gamercoder215.battlecards.api.events.CardEvent
import org.bukkit.event.HandlerList
import org.bukkit.event.entity.EntityEvent

/**
 * Represents an event involving a [BattleCard] entity.
 */
abstract class EntityCardEvent(card: BattleCard<*>) : EntityEvent(card.entity) {

    companion object {
        @JvmStatic
        val handlerList: HandlerList = HandlerList()
    }

    /**
     * The [BattleCard] involved in this event.
     */
    val card: BattleCard<*>

    init {
        this.card = card
    }

    override fun getHandlers(): HandlerList = handlerList

}