package me.gamercoder215.battlecards

import com.google.common.collect.ImmutableMap
import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.Card
import me.gamercoder215.battlecards.api.card.CardQuest
import me.gamercoder215.battlecards.api.events.PrepareCardCraftEvent
import me.gamercoder215.battlecards.util.*
import me.gamercoder215.battlecards.util.inventory.Generator
import me.gamercoder215.battlecards.util.inventory.Items
import me.gamercoder215.battlecards.util.inventory.Items.GUI_BACKGROUND
import me.gamercoder215.battlecards.wrapper.BattleInventory
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.*
import org.bukkit.inventory.ItemStack
import java.util.function.BiConsumer
import java.util.function.Consumer

@Suppress("unchecked_cast")
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
                    "equipment" -> p.openInventory(Generator.generateCardEquipment(card))
                }
            }
            .put("scroll:stored") { e, inv ->
                val p = e.whoClicked as Player
                val page = inv["page", Int::class.java] ?: return@put
                val stored = inv["stored", List::class.java] as List<BattleInventory>
                val operation = e.currentItem.nbt.getInt("operation")

                p.openInventory(stored[page + operation])
                BattleSound.ITEM_BOOK_TURN_PAGE.play(p.location)
            }
            .put("back:action") { e, inv ->
                val p = e.whoClicked as Player
                val back = inv["back", Consumer::class.java] as Consumer<Player>

                back.accept(p)
                p.playFailure()
            }
            .put("card:quest_item") { e, inv ->
                val p = e.whoClicked as Player
                val item = e.currentItem
                val quest = CardQuest.entries[item.nbt.getInt("quest")]
                val card = inv["card", Card::class.java] ?: return@put

                p.openInventory(Generator.generateCardQuests(card, quest))
            }
            .build()

        @JvmStatic
        private val CLICK_INVENTORIES = ImmutableMap.builder<String, (InventoryInteractEvent, BattleInventory) -> Unit>()
            .put("card_table") { e, inv ->
                val p = e.whoClicked as Player

                fun matrix(): Array<ItemStack> = cardTableSlots.filter { it != 24 }.run {
                    val matrix = arrayOfNulls<ItemStack>(9)

                    forEachIndexed { i, slot ->
                        if (inv[slot] == null)
                            matrix[i] = ItemStack(Material.AIR)
                        else
                            matrix[i] = inv[slot]
                    }

                    matrix.filterNotNull().toTypedArray()
                }

                val recipe = inv["recipe", Items.CardWorkbenchRecipe::class.java]
                if (recipe != null && e is InventoryClickEvent && e.rawSlot == 24) {
                    if (inv[24] == null) return@put e.setCancelled(true)

                    val newMatrix = recipe.editMatrix(matrix().clone())

                    for ((i, slot) in cardTableSlots.filter { it != 24 }.withIndex())
                        inv[slot] = newMatrix[i]

                    inv["recipe"] = null
                } else
                    BattleUtil.sync({
                        val matrix = matrix()
                        run predicates@{
                            for (r in Items.CARD_TABLE_RECIPES)
                                if (r.predicate(matrix)) {
                                    val event = PrepareCardCraftEvent(p, matrix, r.result(matrix))
                                    if (!event.isCancelled) {
                                        inv[24] = event.result
                                        inv["recipe"] = r
                                    }
                                    return@predicates
                                }

                            inv[24] = null
                            inv["recipe"] = null
                        }
                    })
            }
            .put("card_equipment") { e, inv ->
                val p = e.whoClicked as Player

                val items = when (e) {
                    is InventoryClickEvent -> {
                        if (e.clickedInventory !is BattleInventory) return@put

                        listOf(e.cursor)
                    }
                    is InventoryDragEvent -> {
                        if (e.rawSlots.all { it > 17 }) return@put

                        e.newItems.values.toList()
                    }
                    else -> listOf()
                }.filterNotNull().filter { it.type != Material.AIR }

                if (items.isNotEmpty() && items.any { it.nbt.id != "card_equipment" }) {
                    p.playFailure()
                    return@put e.setCancelled(true)
                }

                BattleUtil.sync({
                    val equipment = listOf(2, 3, 4, 5, 6).mapNotNull {
                        it to (inv[it] ?: return@mapNotNull null)
                    }.mapNotNull { pair ->
                        if (pair.second.nbt.hasTag("_cancel")) return@mapNotNull null

                        pair.first to (BattleConfig.config.registeredEquipment.firstOrNull {
                            it.name == pair.second.nbt.getString(
                                "name"
                            )
                        } ?: return@mapNotNull null)
                    }.toMap()

                    inv[8] = Generator.generateEffectiveModifiers(equipment)
                })
            }
            .build()
    }

    @EventHandler
    fun close(e: InventoryCloseEvent) {
        val p = e.player as? Player ?: return
        val inv = e.inventory as? BattleInventory ?: return

        if (inv["on_close"] != null) {
            val onClose = inv["on_close", BiConsumer::class.java] as BiConsumer<Player, BattleInventory>
            onClose.accept(p, inv)
        }
    }

    @EventHandler
    fun click(e: InventoryClickEvent) {
        if (e.whoClicked !is Player) return

        if (e.view.topInventory is BattleInventory) {
            val inv = e.view.topInventory as? BattleInventory ?: return

            if (inv.id in CLICK_INVENTORIES.keys)
                CLICK_INVENTORIES[inv.id]!!(e, inv)
        }

        val item = e.currentItem ?: return

        if (item.isSimilar(GUI_BACKGROUND)) {
            e.isCancelled = true
            return
        }

        val inv = e.clickedInventory as? BattleInventory ?: return
        e.isCancelled = inv.isCancelled

        if (item.nbt.hasTag("_cancel")) e.isCancelled = true
        if (CLICK_ITEMS.containsKey(item.nbt.id)) {
            CLICK_ITEMS[item.nbt.id]!!(e, inv)
            e.isCancelled = true
        }
    }

    @EventHandler
    fun drag(e: InventoryDragEvent) {
        val inv = e.view.topInventory as? BattleInventory ?: return

        if (inv.id in CLICK_INVENTORIES.keys)
            CLICK_INVENTORIES[inv.id]!!(e, inv)

        for (item in e.newItems.values) {
            if (item == null) continue
            if (item.isSimilar(GUI_BACKGROUND)) e.isCancelled = true
            if (CLICK_ITEMS.containsKey(item.id)) e.isCancelled = true
        }
    }

    @EventHandler
    fun move(e: InventoryMoveItemEvent) {
        val inv = e.destination as? BattleInventory ?: return
        e.isCancelled = inv.isCancelled

        if (e.item == null) return
        val item = e.item

        if (item.isSimilar(GUI_BACKGROUND)) e.isCancelled = true
        if (CLICK_ITEMS.containsKey(item.id)) e.isCancelled = true
    }

}