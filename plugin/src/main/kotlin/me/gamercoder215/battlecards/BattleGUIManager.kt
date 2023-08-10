package me.gamercoder215.battlecards

import com.google.common.collect.ImmutableMap
import me.gamercoder215.battlecards.api.card.Card
import me.gamercoder215.battlecards.api.events.PrepareCardCraftEvent
import me.gamercoder215.battlecards.util.id
import me.gamercoder215.battlecards.util.inventory.Generator
import me.gamercoder215.battlecards.util.inventory.Items
import me.gamercoder215.battlecards.util.inventory.Items.GUI_BACKGROUND
import me.gamercoder215.battlecards.util.nbt
import me.gamercoder215.battlecards.wrapper.BattleInventory
import me.gamercoder215.battlecards.wrapper.NBTWrapper.Companion.of
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.inventory.ItemStack


internal class BattleGUIManager(private val plugin: BattleCards) : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    companion object {

        @JvmStatic
        private val cardTableSlots = listOf(
            10, 11, 12, 19, 20, 21, 24, 28, 29, 30
        )

        @JvmStatic
        private val CLICK_ITEMS = ImmutableMap.builder<String, (InventoryClickEvent, BattleInventory) -> Unit>()
            .put("card:info_item") { e, inv ->
                val p = e.whoClicked as Player
                val item = e.currentItem
                val card = inv["card", Card::class.java] ?: return@put

                when (item.nbt.getString("type")) {
                    "quests" -> p.openInventory(Generator.generateCardQuests(card))
                }
            }
            .build()

        @JvmStatic
        private val CLICK_INVENTORIES = ImmutableMap.builder<String, (InventoryClickEvent, BattleInventory) -> Unit>()
            .put("card_table") { e, inv ->
                val p = e.whoClicked as Player
                if (e.slot !in cardTableSlots) return@put

                when (e.slot) {
                    24 -> cardTableSlots.filter { it != 24 }.forEach { inv[it] = null }
                    else -> {
                        val matrix = arrayOf(
                            inv[10], inv[11], inv[12],
                            inv[19], inv[20], inv[21],
                            inv[28], inv[29], inv[30]
                        ).run {
                            forEachIndexed { i, stack -> if (stack == null) this[i] = ItemStack(Material.AIR) }

                            filterNotNull().toTypedArray()
                        }

                        for (recipe in Items.CARD_TABLE_RECIPES)
                            if (recipe.predicate(matrix)) {
                                val event = PrepareCardCraftEvent(p, matrix, recipe.result(matrix))
                                if (!event.isCancelled)
                                    inv[24] = event.result
                                break
                            }
                    }
                }
            }
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
        if (CLICK_INVENTORIES.containsKey(inv.id)) CLICK_INVENTORIES[inv.id]!!(e, inv)
        if (CLICK_ITEMS.containsKey(w.id)) {
            CLICK_ITEMS[w.id]!!(e, inv)
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