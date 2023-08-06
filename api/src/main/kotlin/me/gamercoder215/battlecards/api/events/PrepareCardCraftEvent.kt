package me.gamercoder215.battlecards.api.events

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent
import org.bukkit.inventory.ItemStack

/**
 * Called before an item is crafted in the Card Workbench.
 */
open class PrepareCardCraftEvent(player: Player, matrix: Array<ItemStack>, result: ItemStack?) : PlayerEvent(player), Cancellable {

    private var cancelled = false

    /**
     * The matrix of items used to craft the item.
     */
    val matrix: Array<ItemStack>

    /**
     * The result of the crafting.
     */
    var result: ItemStack?

    init {
        this.matrix = matrix
        this.result = result
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }

    override fun getHandlers() = handlerList
    override fun isCancelled(): Boolean = cancelled
    override fun setCancelled(cancel: Boolean) { cancelled = cancel }

}