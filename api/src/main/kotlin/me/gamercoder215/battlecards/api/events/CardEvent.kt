package me.gamercoder215.battlecards.api.events

import me.gamercoder215.battlecards.api.card.Card
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Represents an event involving a [Card].
 */
abstract class CardEvent(card: Card, async: Boolean = false) : Event(async) {

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }

    /**
     * The [Card] involved in this event.
     */
    val card: Card

    init {
        this.card = card
    }

    override fun getHandlers() = handlerList

}