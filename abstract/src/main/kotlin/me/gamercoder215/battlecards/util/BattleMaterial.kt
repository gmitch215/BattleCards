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
    ),

    MAP(
        { ItemStack(matchMaterial("empty_map")) },
        { ItemStack(matchMaterial("map")) }
    ),

    PLAYER_HEAD(
        { ItemStack(matchMaterial("skull_item"), 1, 3) },
        { ItemStack(matchMaterial("player_head")) }
    ),

    WOODEN_SWORD(
        { ItemStack(matchMaterial("wood_sword")) },
        { ItemStack(matchMaterial("wooden_sword")) }
    ),

    FILLED_MAP(
        { ItemStack(matchMaterial("map")) },
        { ItemStack(matchMaterial("filled_map")) }
    ),

    GOLDEN_CHESTPLATE(
        { ItemStack(matchMaterial("gold_chestplate")) },
        { ItemStack(matchMaterial("golden_chestplate")) }
    ),

    GOLDEN_HELMET(
        { ItemStack(matchMaterial("gold_helmet")) },
        { ItemStack(matchMaterial("golden_helmet")) }
    ),

    CRAFTING_TABLE(
        { ItemStack(matchMaterial("workbench")) },
        { ItemStack(matchMaterial("crafting_table")) }
    ),

    GOLDEN_AXE(
        { ItemStack(matchMaterial("gold_axe")) },
        { ItemStack(matchMaterial("golden_axe")) }
    ),

    GOLDEN_PICKAXE(
        { ItemStack(matchMaterial("gold_pickaxe")) },
        { ItemStack(matchMaterial("golden_pickaxe")) }
    ),

    ;

    fun findStack(): ItemStack = if (Wrapper.legacy) onLegacy() else onModern()

    fun find(): Material = findStack().type ?: default ?: Material.AIR

}