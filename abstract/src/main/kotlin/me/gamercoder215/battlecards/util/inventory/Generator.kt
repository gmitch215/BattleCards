package me.gamercoder215.battlecards.util.inventory

import me.gamercoder215.battlecards.api.card.Card
import me.gamercoder215.battlecards.api.card.CardQuest
import me.gamercoder215.battlecards.util.*
import me.gamercoder215.battlecards.util.CardUtils.format
import me.gamercoder215.battlecards.wrapper.BattleInventory
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.get
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.w
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.function.BiConsumer
import java.util.function.Consumer
import kotlin.math.ceil
import kotlin.math.floor

@Suppress("unchecked_cast")
object Generator {

    @JvmStatic
    fun genGUI(size: Int, name: String?): BattleInventory {
        return genGUI("", size, name)
    }

    @JvmStatic
    fun genGUI(key: String, size: Int, name: String?): BattleInventory {
        if (size < 9 || size > 54) throw IllegalArgumentException("Size must be between 9 and 54")
        if (size % 9 > 0) throw IllegalArgumentException("Size must be a multiple of 9")

        val inv = w.createInventory(key, name ?: "", size)
        val bg: ItemStack = Items.GUI_BACKGROUND
        if (size < 27) return inv

        for (i in 0..8) inv[i] = bg
        for (i in size - 9 until size) inv[i] = bg

        var i = 1
        while (i < floor(size.toDouble() / 9.0) - 1) {
            inv[i * 9] = bg
            inv[(i + 1) * 9 - 1] = bg
            i++
        }

        return inv
    }

    @JvmStatic
    fun generatePluginInfo(): BattleInventory {
        val inv = genGUI(27, get("menu.plugin_info"))

        inv[4] = BattleMaterial.PLAYER_HEAD.findStack().apply {
            itemMeta = (itemMeta as SkullMeta).apply {
                displayName = "${ChatColor.AQUA}${get("constants.created_by")}"
                owner = "GamerCoder"
            }
        }

        // TODO Finish Plugin Information

        return inv
    }

    @JvmStatic
    fun generateCardInfo(card: Card): BattleInventory {
        val inv = genGUI(27, get("menu.card.info"))
        inv.isCancelled = true
        inv["card"] = card

        inv[4] = card.icon

        val info = CardGenerator.generateCardInfo(card)
        val stats = CardGenerator.generateCardStatistics(card)

        if (stats != null) {
            inv[12] = info
            inv[14] = stats
        } else
            inv[13] = info

        inv[22] = ItemStack(Material.CHEST).apply {
            itemMeta = itemMeta.apply {
                displayName = "${ChatColor.GOLD}${get("menu.card_quests")}" // TODO Translate
            }
        }.nbt { nbt ->
            nbt.id = "card:info_item"
            nbt["type"] = "quests"
        }

        while (inv.firstEmpty() != -1)
            inv[inv.firstEmpty()] = Items.GUI_BACKGROUND

        return inv
    }

    @JvmStatic
    fun generateCardTable(): BattleInventory {
        val inv = genGUI("card_table", 45, get("menu.card_table"))

        inv["on_close"] = BiConsumer { p: Player, inventory: BattleInventory ->
            val items = listOf(
                inventory[10], inventory[11], inventory[12],
                inventory[19], inventory[20], inventory[21],
                inventory[28], inventory[29], inventory[30]
            ).filterNotNull().toTypedArray()

            items.withIndex().forEach { (i, item) ->
                if (p.inventory.firstEmpty() == -1)
                    p.world.dropItemNaturally(p.location, item)
                else
                    p.inventory.addItem(item)

                inventory[i] = null
            }
        }

        for (i in 4..7)
            for (j in 1..3) inv[i + j.times(9)] = Items.GUI_BACKGROUND

        inv[24] = null

        return inv
    }

    @JvmStatic
    fun generateCardQuests(card: Card, quest: CardQuest? = null): BattleInventory {
        val inv: BattleInventory

        if (quest == null) {
            inv = genGUI(27, get("menu.card_quests"))
            inv["card"] = card
            inv["back"] = Consumer { p: Player -> p.openInventory(generateCardInfo(card)) }

            for (q in CardQuest.entries)
                inv.addItem(ItemStack(q.icon).apply {
                    itemMeta = itemMeta.apply {
                        displayName = "${ChatColor.GOLD}${get("menu.card_quests.${q.name.lowercase()}")}"

                        if (card.getQuestLevel(q) > 0)
                            lore = listOf(
                                "${ChatColor.AQUA}${format(get("constants.level"), card.getQuestLevel(q).formatInt())}"
                            )

                        addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS)
                    }
                }.nbt { nbt ->
                    nbt.id = "card:quest_item"
                    nbt["quest"] = q.ordinal
                })

            inv[22] = Items.back()
        } else {
            val count = ceil(quest.maxLevel / progressString.size.toDouble()).toInt()
            val invs = mutableListOf<BattleInventory>()

            var lvl = 1
            val currentLvl = card.getQuestLevel(quest)

            for (page in 0 until count) {
                val gui = genGUI(54, "${get("menu.card_quests")} | ${get("menu.card_quests.${quest.name.lowercase()}")}")
                gui["page"] = page
                gui["back"] = Consumer { p: Player -> p.openInventory(generateCardQuests(card)) }

                gui[4] = card.icon
                gui[9] = ItemStack(quest.icon).apply {
                    itemMeta = itemMeta.apply {
                        displayName = "${ChatColor.GOLD}${get("menu.card_quests.${quest.name.lowercase()}")}"

                        lore = listOf(
                            "${ChatColor.AQUA}${format(get("constants.level"), card.getQuestLevel(quest).formatInt())}",
                            "${ChatColor.YELLOW}${format(get("constants.total_experience_reward"), quest.getTotalExperience(card).withSuffix())}"
                        )

                        addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS)
                    }
                }

                if (count > 1) {
                    if (page < count - 1)
                        gui[51] = Items.next()

                    if (page > 0)
                        gui[47] = Items.prev()
                }

                for (index in progressString) {
                    if (lvl > quest.maxLevel) break

                    gui[index] = when {
                        lvl == quest.maxLevel -> ItemStack(Material.BEACON)
                        lvl == (currentLvl + 1) -> BattleMaterial.YELLOW_STAINED_GLASS_PANE.findStack()
                        lvl < (currentLvl + 1) -> BattleMaterial.LIME_STAINED_GLASS_PANE.findStack()
                        else -> BattleMaterial.RED_STAINED_GLASS_PANE.findStack()
                    }.apply {
                        val unlocked = lvl < currentLvl
                        val color = if (unlocked) ChatColor.GREEN else ChatColor.AQUA

                        itemMeta = itemMeta.apply {
                            displayName = "$color${format(get("constants.level"), lvl.formatInt())}"
                            lore = listOf(
                                "${ChatColor.YELLOW}${quest.getLocalizedProgress(card, lvl)}",
                                " ",
                                "${ChatColor.DARK_GREEN}${format(get("constants.completed"), "${quest.getProgressPercentage(card, lvl).times(100).format()}%")}",
                                "${if (unlocked) ChatColor.DARK_AQUA else ChatColor.BLUE}${quest.getExperienceReward(card, lvl).withSuffix()} XP"
                            )

                            if (unlocked) addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true)
                            addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)
                        }
                    }

                    lvl++
                }

                gui[49] = Items.back()

                while (gui.firstEmpty() != -1)
                    gui[gui.firstEmpty()] = Items.GUI_BACKGROUND

                invs.add(gui)
            }

            invs.forEach { it["stored"] = invs }

            inv = invs[0]
        }

        inv.isCancelled = true
        inv[4] = card.icon

        return inv
    }

    @JvmStatic
    private val progressString: List<Int> = listOf(
        10 nineTo 37,
        37..39,
        39 nineTo 12,
        12..14,
        14 nineTo 41,
        41..43,
        43 nineTo 16,
        17
    ).map {
        when (it) {
            is Int -> listOf(it)
            is IntRange -> it.toList()
            is Collection<*> -> it as Collection<Int>
            else -> throw IllegalArgumentException("Invalid progress type")
        }
    }.flatten().distinct()

    private infix fun Int.nineTo(other: Int): List<Int> {
        val remainder = this % 9

        val (start, reverse) = if (this > other) other to true else this to false
        val end = if (this > other) this else other

        return (start..end).filter { (it % 9) - remainder == 0 }.run { if (reverse) reversed() else this }
    }

}