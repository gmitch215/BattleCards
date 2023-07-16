package me.gamercoder215.battlecards.util.inventory

import me.gamercoder215.battlecards.api.card.Card
import me.gamercoder215.battlecards.util.BattleMaterial
import me.gamercoder215.battlecards.wrapper.BattleInventory
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.get
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.w
import org.bukkit.ChatColor
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import kotlin.math.floor


object Generator {

    fun genGUI(size: Int, name: String?): BattleInventory {
        return genGUI("", size, name)
    }

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

        val info = CardGenerator.generateCardInfo(card)
        val stats = CardGenerator.generateCardStatistics(card)

        if (stats != null) {
            inv[12] = info
            inv[14] = stats
        } else
            inv[13] = info

        while (inv.firstEmpty() != -1)
            inv[inv.firstEmpty()] = Items.GUI_BACKGROUND

        return inv
    }

}