package me.gamercoder215.battlecards.api.events

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent
import org.bukkit.inventory.ItemStack

open class PrepareCardCombineEvent(
    player: Player,
    /**
     * The matrix of items used to combine the cards.
     */
    val matrix: Array<ItemStack?>,
    /**
     * The result of the combining.
     */
    var result: ItemStack
) : PlayerEvent(player), Cancellable {

    private var cancelled = false

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }

    override fun getHandlers() = handlerList
    override fun isCancelled() = cancelled
    override fun setCancelled(cancel: Boolean) { cancelled = cancel }


}