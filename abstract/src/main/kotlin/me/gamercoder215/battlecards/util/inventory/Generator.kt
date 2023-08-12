package me.gamercoder215.battlecards.util.inventory

import me.gamercoder215.battlecards.api.card.Card
import me.gamercoder215.battlecards.api.card.CardQuest
import me.gamercoder215.battlecards.api.card.Rarity
import me.gamercoder215.battlecards.util.BattleMaterial
import me.gamercoder215.battlecards.util.CardUtils
import me.gamercoder215.battlecards.util.nbt
import me.gamercoder215.battlecards.wrapper.BattleInventory
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.get
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.w
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Creature
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.util.ChatPaginator
import kotlin.math.floor


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

        for (i in 0..8) inv.setItem(i, bg)
        for (i in size - 9 until size) inv.setItem(i, bg)

        var i = 1
        while (i < floor(size.toDouble() / 9.0) - 1) {
            inv.setItem(i * 9, bg)
            inv.setItem((i + 1) * 9 - 1, bg)
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

        inv[4] = ItemStack(card.icon).apply {
            itemMeta = itemMeta.apply {
                displayName = CardUtils.format(get("constants.card"), "${card.rarity.color}${card.name}")

                if (card.rarity != Rarity.BASIC)
                    lore = ChatPaginator.wordWrap("\"${get("card.${card.type.name.lowercase()}")}\"", 30).map { s -> "${ChatColor.YELLOW}$s" } +
                            arrayOf(" ") +
                            ChatPaginator.wordWrap(get("card.${card.type.name.lowercase()}.desc"), 30).map { s -> "${ChatColor.GRAY}$s" }

                addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS)
            }
        }

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

            nbt { nbt ->
                nbt.id = "card:info_item"
                nbt["type"] = "quests"
            }
        }

        while (inv.firstEmpty() != -1)
            inv[inv.firstEmpty()] = Items.GUI_BACKGROUND

        return inv
    }

    @JvmStatic
    fun generateCardTable(): BattleInventory {
        val inv = genGUI("card_table", 45, get("menu.card_table"))

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

            for (q in CardQuest.entries)
                inv.addItem(ItemStack(q.icon).apply {
                    itemMeta = itemMeta.apply {
                        displayName = "${ChatColor.GOLD}${get("menu.card_quests.${q.name.lowercase()}")}"
                    }

                    nbt { nbt ->
                        nbt.id = "card:quest_item"
                        nbt["quest"] = q.ordinal
                    }
                })
        } else {
            inv = genGUI(54, "${get("menu.card_quests")} | ${get("menu.card_quests.${quest.name.lowercase()}")}")
        }

        inv[4] = card.icon

        return inv
    }

}