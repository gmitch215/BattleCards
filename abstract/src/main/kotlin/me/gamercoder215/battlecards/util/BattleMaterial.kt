package me.gamercoder215.battlecards.util

import me.gamercoder215.battlecards.wrapper.Wrapper
import org.bukkit.Material
import org.bukkit.Material.matchMaterial
import org.bukkit.inventory.ItemStack

enum class BattleMaterial(
    private val onLegacy: () -> ItemStack,
    private val onModern: () -> ItemStack,
    private val default: Material? = null
) {

    BLACK_STAINED_GLASS_PANE(
        { ItemStack(matchMaterial("stained_glass_pane"), 1, 15) },
        { ItemStack(matchMaterial("black_stained_glass_pane")) }
    ),

    COBWEB(
        { ItemStack(matchMaterial("web")) },
        { ItemStack(matchMaterial("cobweb")) }
    )

    ;

    fun findStack(): ItemStack = if (Wrapper.legacy) onLegacy() else onModern()

    fun find(): Material = findStack().type ?: default ?: Material.AIR

}