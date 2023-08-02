package me.gamercoder215.battlecards.util.inventory

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.util.BattleMaterial
import me.gamercoder215.battlecards.wrapper.NBTWrapper.Companion.builder
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.Plugin

object Items {

    @JvmStatic
    val GUI_BACKGROUND: ItemStack = builder(BattleMaterial.BLACK_STAINED_GLASS_PANE,
        { displayName = " " },
        { nbt -> nbt.id = "gui_background" }
    )

    @JvmStatic
    val CARD_TABLE: ItemStack = builder(BattleMaterial.CRAFTING_TABLE,
        { displayName = "${ChatColor.RESET}Card Table" },
        { nbt -> nbt["card_block"] = true; nbt.id = "card_table"
            nbt["container"] = true

            nbt["attach"] = "enchanting_table"
            nbt["attach.small"] = true
            nbt["attach.mod.y"] = -0.4
        }
    )

    @JvmStatic
    fun builder(material: Material, action: (ItemMeta) -> Unit): ItemStack {
        return ItemStack(material).apply {
            itemMeta = itemMeta.apply {
                action(this)
            }
        }
    }

    private fun createShapedRecipe(key: String, result: ItemStack): ShapedRecipe {
        return try {
            val namespacedKey = Class.forName("org.bukkit.NamespacedKey").run {
                val constr = getDeclaredConstructor(Plugin::class.java, String::class.java)
                constr.newInstance(BattleConfig.plugin, key)
            }

            val constr = ShapedRecipe::class.java.getDeclaredConstructor(Class.forName("org.bukkit.NamespacedKey"), ItemStack::class.java)
            constr.newInstance(namespacedKey, result)
        } catch (ex: ReflectiveOperationException) {
            ShapedRecipe(result)
        }
    }

    @JvmStatic
    val RECIPES: List<Recipe> = listOf(
        createShapedRecipe("card_table", CARD_TABLE).apply {
            shape(" W ", "WPW", " W ")

            setIngredient('W', BattleMaterial.CRAFTING_TABLE.find())
            setIngredient('P', Material.PAPER)
        }
    )

    @JvmStatic
    val PUBLIC_ITEMS: Map<String, ItemStack> = mapOf(
        "card_table" to CARD_TABLE
    )

}