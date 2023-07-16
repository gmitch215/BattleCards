package me.gamercoder215.battlecards

import com.google.common.collect.ImmutableMap
import me.gamercoder215.battlecards.util.id
import me.gamercoder215.battlecards.util.inventory.Items.GUI_BACKGROUND
import me.gamercoder215.battlecards.wrapper.BattleInventory
import me.gamercoder215.battlecards.wrapper.NBTWrapper.Companion.of
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent


internal class BattleGUIManager(private val plugin: BattleCards) : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    companion object {

        @JvmStatic
        private val CLICK_ITEMS = ImmutableMap.builder<String, (BattleInventory, InventoryClickEvent) -> Unit>()

            .build()

        @JvmStatic
        private val CLICK_INVENTORIES = ImmutableMap.builder<String, (BattleInventory, InventoryClickEvent) -> Unit>()

            .build()
    }

    @EventHandler
    fun click(e: InventoryClickEvent) {
        if (e.clickedInventory !is BattleInventory) return
        if (e.whoClicked !is Player) return

        val inv = e.clickedInventory as BattleInventory
        e.isCancelled = inv.isCancelled

        if (e.currentItem == null) return
        val item = e.currentItem

        if (item.isSimilar(GUI_BACKGROUND)) {
            e.isCancelled = true
            return
        }

        val w = of(item)

        if (w.hasTag("_cancel")) e.isCancelled = true
        if (CLICK_INVENTORIES.containsKey(inv.id)) CLICK_INVENTORIES[inv.id]!!(inv, e)
        if (CLICK_ITEMS.containsKey(w.id)) {
            CLICK_ITEMS[w.id]!!(inv, e)
            e.isCancelled = true
        }
    }

    @EventHandler
    fun drag(e: InventoryDragEvent) {
        if (e.view.topInventory !is BattleInventory) return
        val inv: BattleInventory = e.view.topInventory as BattleInventory
        e.isCancelled = inv.isCancelled

        for (item in e.newItems.values) {
            if (item == null) continue
            if (item.isSimilar(GUI_BACKGROUND)) e.isCancelled = true
            if (CLICK_ITEMS.containsKey(item.id)) e.isCancelled = true
        }
    }

    @EventHandler
    fun move(e: InventoryMoveItemEvent) {
        if (e.item == null) return
        val item = e.item

        if (e.destination !is BattleInventory) return
        val inv: BattleInventory = e.destination as BattleInventory

        e.isCancelled = inv.isCancelled

        if (item.isSimilar(GUI_BACKGROUND)) e.isCancelled = true
        if (CLICK_ITEMS.containsKey(item.id)) e.isCancelled = true
    }

}