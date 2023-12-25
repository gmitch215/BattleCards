package me.gamercoder215.battlecards

import com.google.common.collect.ImmutableMap
import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.api.card.Card
import me.gamercoder215.battlecards.api.card.CardQuest
import me.gamercoder215.battlecards.api.card.Rarity
import me.gamercoder215.battlecards.api.card.item.CardEquipment
import me.gamercoder215.battlecards.api.events.PrepareCardCraftEvent
import me.gamercoder215.battlecards.util.*
import me.gamercoder215.battlecards.util.CardUtils.format
import me.gamercoder215.battlecards.util.inventory.CardGenerator
import me.gamercoder215.battlecards.util.inventory.Generator
import me.gamercoder215.battlecards.util.inventory.Items
import me.gamercoder215.battlecards.util.inventory.Items.GUI_BACKGROUND
import me.gamercoder215.battlecards.util.inventory.Items.randomCumulative
import me.gamercoder215.battlecards.wrapper.BattleInventory
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.get
import me.gamercoder215.battlecards.wrapper.commands.CommandWrapper.Companion.getError
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.*
import org.bukkit.inventory.ItemStack
import java.util.function.BiConsumer
import java.util.function.Consumer
import kotlin.math.abs

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
            .put("plugin_info:link") { e, _ ->
                val p = e.whoClicked as Player
                val link = e.currentItem.nbt.getString("link")

                val text = TextComponent("${ChatColor.YELLOW}Link: $link").apply {
                    clickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, link)
                    hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, arrayOf(TextComponent("${ChatColor.AQUA}$link")))
                }

                try {
                    p.spigot().sendMessage(text)
                } catch (ignored: UnsupportedOperationException) {
                    p.sendMessage(text.toLegacyText())
                }
            }
            .put("card_combiner:start") { e, inv ->
                val p = e.whoClicked as Player

                fun stopped() = inv["stopped", Boolean::class.java, false]
                fun matrix() = listOf(
                    inv[28..34], inv[37..43]
                ).flatten()
                    .filterNotNull()
                    .filter { it.type != Material.AIR }
                    .filter { it.isCard || it.id == "card_shard" }

                if (matrix().isEmpty()) return@put p.playFailure()
                if (inv["running", Boolean::class.java, false]) return@put e.setCancelled(true)

                inv["running"] = true
                inv["stopped"] = false
                inv[22] = BattleMaterial.RED_WOOL.findStack().apply {
                    itemMeta = itemMeta.apply {
                        displayName = "${ChatColor.RED}${get("constants.cancel")}"
                    }
                }.nbt { nbt -> nbt.id = "card_combiner:stop"; nbt.addTag("_cancel") }

                sync( { if (stopped()) return@sync cancel(); BattleSound.ENTITY_ARROW_HIT_PLAYER.play(p.location, 1F, 0F); inv[23]?.amount = 3 }, 20)
                sync( { if (stopped()) return@sync cancel(); BattleSound.ENTITY_ARROW_HIT_PLAYER.play(p.location, 1F, 0.75F); inv[23]?.amount = 2 }, 40)
                sync( { if (stopped()) return@sync cancel(); BattleSound.ENTITY_ARROW_HIT_PLAYER.play(p.location, 1F, 1F); inv[23]?.amount = 1 }, 60)
                sync({
                    if (stopped()) return@sync cancel()

                    val matrix = matrix()
                    inv[28..34] = null
                    inv[37..43] = null

                    inv["running"] = false

                    val chosen = CardUtils.calculateCardChances(matrix).randomCumulative() ?: Rarity.COMMON

                    val card = BattleCardType.entries.filter { it.rarity == chosen && !it.isDisabled }.random()().apply {
                        var total = 0.0

                        for ((amount, card) in matrix.map { it.amount to it.card }) {
                            if (card == null) continue

                            val diff = rarity.ordinal - card.rarity.ordinal

                            total += (when {
                                diff > 0 -> card.experience / (diff + 1)
                                diff < 0 -> card.experience * (abs(diff) + 1)
                                else -> card.experience
                            }) * amount
                        }

                        experience = total.coerceAtMost(maxCardExperience)
                    }

                    inv[13] = CardGenerator.toItem(card)
                    BattleSound.ENTITY_PLAYER_LEVELUP.play(p.location, 1F, 0F)

                    inv[22] = BattleMaterial.YELLOW_STAINED_GLASS_PANE.findStack().apply {
                        itemMeta = itemMeta.apply {
                            displayName = "${ChatColor.YELLOW}${get("constants.place_items")}"
                        }
                    }.nbt { nbt -> nbt.addTag("_cancel") }
                    inv[23] = GUI_BACKGROUND
                }, 90)
            }
            .put("card_combiner:stop") { e, inv ->
                val p = e.whoClicked as Player
                inv["stopped"] = true; inv["running"] = false
                p.playFailure()

                inv[22] = BattleMaterial.YELLOW_STAINED_GLASS_PANE.findStack().apply {
                    itemMeta = itemMeta.apply {
                        displayName = "${ChatColor.YELLOW}${get("constants.place_items")}"
                    }
                }.nbt { nbt -> nbt.addTag("_cancel") }
                inv[23] = GUI_BACKGROUND
            }
            .build()

        @JvmStatic
        private val CLICK_INVENTORIES = ImmutableMap.builder<String, (InventoryInteractEvent, BattleInventory) -> Unit>()
            .put("card_table") { e, inv ->
                val p = e.whoClicked as Player

                if (e is InventoryClickEvent)
                    if (when (e.action) {
                        InventoryAction.MOVE_TO_OTHER_INVENTORY -> inv.firstEmpty()
                        else -> e.rawSlot
                    } == 24) return@put e.setCancelled(true)

                if (e is InventoryDragEvent && 24 in e.rawSlots)
                    return@put e.setCancelled(true)

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
                    sync {
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
                    }
            }
            .put("card_equipment") { e, inv ->
                val p = e.whoClicked as Player

                val items = when (e) {
                    is InventoryClickEvent -> {
                        if (e.action != InventoryAction.MOVE_TO_OTHER_INVENTORY && e.clickedInventory !is BattleInventory) return@put

                        listOf(if (e.action == InventoryAction.MOVE_TO_OTHER_INVENTORY) e.currentItem else e.cursor)
                    }
                    is InventoryDragEvent -> {
                        if (e.rawSlots.all { it > 17 }  ) return@put

                        e.newItems.values.toList()
                    }
                    else -> listOf()
                }.filterNotNull().filter { it.type != Material.AIR }

                if (items.isNotEmpty()) {
                    if (items.any { it.nbt.id != "card_equipment" }) {
                        p.playFailure()
                        return@put e.setCancelled(true)
                    }

                    val itemEquipment = items.mapNotNull { item -> BattleConfig.config.registeredEquipment.firstOrNull { it.name == item.nbt.getString("name") } }
                    val equipment = listOf(2, 3, 4, 5, 6).mapNotNull { inv[it] }.mapNotNull { item -> BattleConfig.config.registeredEquipment.firstOrNull { it.name == item.nbt.getString("name") } }

                    if (
                        (if (e is InventoryClickEvent && e.action == InventoryAction.MOVE_TO_OTHER_INVENTORY) e.clickedInventory !is BattleInventory else true) &&
                        (equipment.filter { it.rarity == CardEquipment.Rarity.SPECIAL }.size + itemEquipment.filter { it.rarity == CardEquipment.Rarity.SPECIAL }.size) > 1)
                    {
                        p.sendMessage(getError("error.card.equipment.one_special"))
                        p.playFailure()
                        return@put e.setCancelled(true)
                    }
                }

                sync {
                    val equipment = listOf(2, 3, 4, 5, 6).mapNotNull {
                        it to (inv[it] ?: return@mapNotNull null)
                    }.mapNotNull { pair ->
                        if (pair.second.nbt.hasTag("_cancel")) return@mapNotNull null

                        pair.first to (BattleConfig.config.registeredEquipment.firstOrNull {
                            it.name == pair.second.nbt.getString("name")
                        } ?: return@mapNotNull null)
                    }.toMap()

                    inv[8] = Generator.generateEffectiveModifiers(equipment)
                }
            }
            .put("card_combiner") { e, inv ->
                val p = e.whoClicked as Player
                if (inv["running", Boolean::class.java, false]) {
                    if (e is InventoryClickEvent && (e.currentItem?.type ?: Material.AIR) == Material.AIR) return@put
                    if (e is InventoryDragEvent && e.newItems.values.all { it.type == Material.AIR }) return@put

                    inv["stopped"] = true; inv["running"] = false
                    p.playFailure()

                    inv[22] = BattleMaterial.YELLOW_STAINED_GLASS_PANE.findStack().apply {
                        itemMeta = itemMeta.apply {
                            displayName = "${ChatColor.YELLOW}${get("constants.place_items")}"
                        }
                    }.nbt { nbt -> nbt.addTag("_cancel") }
                    inv[23] = GUI_BACKGROUND
                    return@put
                }

                val filter = { item: ItemStack? -> item != null && item.type != Material.AIR && (item.isCard || item.id == "card_shard") }
                fun matrix() = listOf(
                    inv[28..34], inv[37..43]
                ).flatten().filterNotNull().filter { it.type != Material.AIR }

                if (e is InventoryClickEvent) {
                    if (when (e.action) {
                            InventoryAction.MOVE_TO_OTHER_INVENTORY -> inv.firstEmpty()
                            else -> e.rawSlot
                        } == 13
                    ) return@put e.setCancelled(true)

                    if (if (e.action == InventoryAction.MOVE_TO_OTHER_INVENTORY) !filter(e.currentItem) else !filter(e.cursor) && e.clickedInventory is BattleInventory)
                        return@put e.setCancelled(true)
                }

                if (e is InventoryDragEvent) {
                    if (e.rawSlots.any { it < 54 } && inv["running", Boolean::class.java, false])
                        return@put e.setCancelled(true)

                    if (13 in e.rawSlots || e.newItems.values.any { !filter(it) })
                        return@put e.setCancelled(true)
                }

                sync {
                    val matrix = matrix()
                    if (matrix.isEmpty() || inv[13] != null) {
                        inv[22] = BattleMaterial.YELLOW_STAINED_GLASS_PANE.findStack().apply {
                            itemMeta = itemMeta.apply {
                                displayName = "${ChatColor.YELLOW}${get("constants.place_items")}"
                            }
                        }.nbt { nbt -> nbt.addTag("_cancel") }
                        inv[23] = GUI_BACKGROUND
                    } else {
                        val power = CardUtils.getCardPower(matrix)

                        if (power < 50) {
                            inv[22] = ItemStack(Material.BARRIER).apply {
                                itemMeta = itemMeta.apply {
                                    displayName = "${ChatColor.RED}${get("menu.card_combiner.not_enough_power")}"
                                    lore = listOf(
                                        "${ChatColor.DARK_RED}${format(get("constants.card_power"), power.format())}",
                                    )
                                }
                            }.nbt { nbt -> nbt.addTag("_cancel") }

                            inv[23] = GUI_BACKGROUND
                            return@sync
                        }

                        inv[22] = ItemStack(Material.PAPER).apply {
                            itemMeta = itemMeta.apply {
                                displayName = "${ChatColor.YELLOW}${get("constants.card_chances")}"
                                val chances = CardUtils.calculateCardChances(matrix).filterValues { it != 0.0 }
                                lore = listOf(
                                    " ",
                                    "${ChatColor.GOLD}${format(get("constants.card_power"), power.format())}",
                                    " "
                                ) + chances.map {
                                    it.key to "${it.key.color}${format(get("constants.chance"), "${it.value.times(100).format()}%")} ${it.key}"
                                }.sortedBy { it.first.ordinal }.map { it.second }
                            }
                        }.nbt { nbt -> nbt.addTag("_cancel") }

                        inv[23] = ItemStack(Material.BEACON).apply {
                            itemMeta = itemMeta.apply {
                                displayName = "${ChatColor.AQUA}${get("constants.confirm")}"
                            }
                        }.nbt { nbt -> nbt.id = "card_combiner:start"; nbt.addTag("_cancel") }
                    }
                }
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