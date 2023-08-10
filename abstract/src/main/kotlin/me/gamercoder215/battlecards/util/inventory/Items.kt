package me.gamercoder215.battlecards.util.inventory

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.util.BattleMaterial
import me.gamercoder215.battlecards.util.card
import me.gamercoder215.battlecards.util.isCard
import me.gamercoder215.battlecards.util.nbt
import me.gamercoder215.battlecards.wrapper.NBTWrapper.Companion.builder
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.r
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
    val TINY_EXPERIENCE_BOOK: ItemStack = builder(Material.BOOK,
        { displayName = "${ChatColor.RESET}Tiny Card Experience Book" },
        { nbt -> nbt["exp_book"] = true; nbt["amount"] = 100.0 }
    )

    @JvmStatic
    val SMALL_EXPERIENCE_BOOK: ItemStack = builder(Material.BOOK,
        { displayName = "${ChatColor.RESET}Small Card Experience Book" },
        { nbt -> nbt["exp_book"] = true; nbt["amount"] = 2500.0 }
    )

    @JvmStatic
    val MEDIUM_EXPERIENCE_BOOK: ItemStack = builder(Material.BOOK,
        { displayName = "${ChatColor.RESET}Medium Card Experience Book" },
        { nbt -> nbt["exp_book"] = true; nbt["amount"] = 10000.0 }
    )

    @JvmStatic
    val LARGE_EXPERIENCE_BOOK: ItemStack = builder(Material.BOOK,
        { displayName = "${ChatColor.RESET}Large Card Experience Book" },
        { nbt -> nbt["exp_book"] = true; nbt["amount"] = 500000.0 }
    )

    @JvmStatic
    val HUGE_EXPERIENCE_BOOK: ItemStack = builder(Material.BOOK,
        { displayName = "${ChatColor.RESET}Huge Card Experience Book" },
        { nbt -> nbt["exp_book"] = true; nbt["amount"] = 2000000.0 }
    )

    // Static Util

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

    // Recipes & Public Items

    @JvmStatic
    val RECIPES: List<Recipe> = listOf(
        createShapedRecipe("card_table", CARD_TABLE).apply {
            shape(" W ", "WPW", " W ")

            setIngredient('W', BattleMaterial.CRAFTING_TABLE.find())
            setIngredient('P', Material.PAPER)
        }
    )

    @JvmStatic
    val CARD_TABLE_RECIPES: List<CardWorkbenchRecipe> = listOf(
        CardWorkbenchRecipe(
            { matrix ->
                matrix.filter { it.isCard }.size == 1 && matrix.any { it.nbt.getBoolean("exp_book") } && matrix.firstOrNull { it.isCard }?.card?.isMaxed != true
            },
            result@{ matrix ->
                val expBooks = matrix.filter { it.nbt.getBoolean("exp_book") }
                if (expBooks.isEmpty()) return@result null

                val card = matrix.firstOrNull { it.isCard }?.card ?: return@result null
                card.experience = (card.experience + expBooks.sumOf { it.nbt.getDouble("amount") }).coerceAtMost(card.maxCardExperience)

                return@result card.itemStack
            }
        )
    )

    @JvmStatic
    val PUBLIC_ITEMS: Map<String, ItemStack> = mapOf(
        "card_table" to CARD_TABLE,
        "tiny_experience_book" to TINY_EXPERIENCE_BOOK,
        "small_experience_book" to SMALL_EXPERIENCE_BOOK,
        "medium_experience_book" to MEDIUM_EXPERIENCE_BOOK,
        "large_experience_book" to LARGE_EXPERIENCE_BOOK,
        "huge_experience_book" to HUGE_EXPERIENCE_BOOK
    )

    @JvmStatic
    private val GENERATED_ITEMS: Map<String, Double> = mapOf(
        "tiny_experience_book" to 0.4,
        "small_experience_book" to 0.1,
        "medium_experience_book" to 0.02,
        "large_experience_book" to 0.001,
        "huge_experience_book" to 0.00005
    )

    @JvmStatic
    val EFFECTIVE_GENERATED_ITEMS = GENERATED_ITEMS.mapNotNull {
        val item = PUBLIC_ITEMS[it.key] ?: return@mapNotNull null
        item to (it.value / GENERATED_ITEMS.values.sum())
    }.toMap()

    fun <T> Map<T, Double>.randomCumulative(reroll: Int = 0): T {
        val distribution = DoubleArray(size)
        var cumulative = 0.0

        for ((i, value) in values.withIndex()) {
            cumulative += value
            distribution[i] = cumulative
        }

        val random = r.nextDouble(); var i = 0
        while (i < distribution.size && random > distribution[i]) i++

        if (reroll > 0)
            return randomCumulative(reroll - 1)

        return keys.elementAt(i)
    }

    class CardWorkbenchRecipe {

        val result: (Array<ItemStack>) -> ItemStack?
        val predicate: (Array<ItemStack>) -> Boolean

        constructor(predicate: (Array<ItemStack>) -> Boolean, result: (Array<ItemStack>) -> ItemStack?) {
            this.result = result
            this.predicate = predicate
        }

        constructor(predicate: (Array<ItemStack>) -> Boolean, result: ItemStack?) {
            this.result = { result }
            this.predicate = predicate
        }

    }

}