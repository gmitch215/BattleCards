package me.gamercoder215.battlecards.util.inventory

import me.gamercoder215.battlecards.messages.get
import me.gamercoder215.battlecards.util.BattleMaterial
import me.gamercoder215.battlecards.util.get
import me.gamercoder215.battlecards.util.inventory.Generator.genGUI
import me.gamercoder215.battlecards.util.nbt
import me.gamercoder215.battlecards.util.set
import me.gamercoder215.battlecards.wrapper.BattleInventory
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.function.BiConsumer

val CONTAINERS_CARD_BLOCKS: Map<String, () -> BattleInventory> = mapOf(
    "card_table" to { generateCardTable() },
    "card_combiner" to { generateCardCombiner() }
)

fun generateCardTable(): BattleInventory {
    val inv = genGUI("card_table", 45, get("menu.card_table"))

    inv["on_close"] = BiConsumer { p: Player, inventory: BattleInventory ->
        val items = listOfNotNull(
            inventory[10], inventory[11], inventory[12],
            inventory[19], inventory[20], inventory[21],
            inventory[28], inventory[29], inventory[30]
        ).toTypedArray()

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

fun generateCardCombiner(): BattleInventory {
    val inv = genGUI("card_combiner", 54, get("menu.card_combiner"))

    inv["on_close"] = BiConsumer { p: Player, inventory: BattleInventory ->
        listOf(
            inventory[28..34], inventory[37..43], inventory[13]
        ).map {
            when (it) {
                is Iterable<*> -> it.filterIsInstance<ItemStack?>().filterNotNull()
                is ItemStack? -> listOf(it)
                else -> emptyList()
            }
        }.forEach { items ->
            for (item in items.filterNotNull())
                if (p.inventory.firstEmpty() == -1)
                    p.world.dropItemNaturally(p.location, item)
                else
                    p.inventory.addItem(item)
        }
    }

    inv[10..25] = Items.GUI_BACKGROUND
    inv[13] = null
    inv[22] = BattleMaterial.YELLOW_STAINED_GLASS_PANE.findStack().apply {
        itemMeta = itemMeta.apply {
            displayName = "${ChatColor.YELLOW}${get("constants.place_items")}"
        }
    }.nbt { nbt -> nbt.addTag("_cancel") }

    return inv
}