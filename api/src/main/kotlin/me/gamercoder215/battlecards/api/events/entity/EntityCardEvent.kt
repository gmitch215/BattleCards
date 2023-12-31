package me.gamercoder215.battlecards.api.events.entity

import me.gamercoder215.battlecards.api.card.BattleCard
import org.bukkit.event.HandlerList
import org.bukkit.event.entity.EntityEvent

/**
 * Represents an event involving a [BattleCard] entity.
 */
abstract class EntityCardEvent(
    /**
     * The [BattleCard] involved in this event.
     */
    val card: BattleCard<*>
) : EntityEvent(card.entity) {

    companion object {
        @JvmStatic
        val handlerList: HandlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList = handlerList

}