package me.gamercoder215.battlecards.util.inventory

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.Rarity
import me.gamercoder215.battlecards.util.*
import me.gamercoder215.battlecards.wrapper.NBTWrapper.Companion.builder
import me.gamercoder215.battlecards.wrapper.Wrapper
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.get
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.r
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Material.matchMaterial
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.plugin.Plugin
import java.util.*


object Items {

    @JvmStatic
    val GUI_BACKGROUND: ItemStack = builder(BattleMaterial.BLACK_STAINED_GLASS_PANE,
        { displayName = " " },
        { nbt -> nbt.id = "gui_background" }
    )

    @JvmStatic
    val LOCKED: ItemStack = builder(Material.BEDROCK,
        { displayName = "${ChatColor.DARK_PURPLE}${get("constants.locked")}" },
        { nbt -> nbt.addTag("_cancel") }
    )

    @JvmStatic
    val COMING_SOON: ItemStack = builder(Material.BEDROCK,
        { displayName = "${ChatColor.YELLOW}${get("constants.coming_soon")}" },
        { nbt -> nbt.addTag("_cancel") }
    )

    @JvmStatic
    fun locked(unlockedAt: Int): ItemStack = LOCKED.clone().apply {
        itemMeta = itemMeta.apply {
            lore = listOf(
                "${ChatColor.YELLOW}${CardUtils.format(get("constants.unlocks_at_level"), unlockedAt.formatInt())}"
            )
        }
    }

    // Card Items

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
    val CARD_COMBINER = builder(BattleMaterial.CAULDRON,
        { displayName = "${ChatColor.RESET}Card Combiner" },
        { nbt -> nbt["card_block"] = true; nbt.id = "card_combiner"
            nbt["container"] = true

            nbt["attach"] = "ender_portal_frame:end_portal_frame"
            nbt["attach.small"] = true
            nbt["attach.mod.y"] = -0.8
        })

    @JvmStatic
    val TINY_EXPERIENCE_BOOK: ItemStack = builder(Material.BOOK,
        { displayName = "${ChatColor.WHITE}Tiny Card Experience Book"; addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true); addItemFlags(ItemFlag.HIDE_ENCHANTS) },
        { nbt -> nbt["exp_book"] = true; nbt["amount"] = 100.0 }
    )

    @JvmStatic
    val SMALL_EXPERIENCE_BOOK: ItemStack = builder(Material.BOOK,
        { displayName = "${ChatColor.GREEN}Small Card Experience Book"; addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true); addItemFlags(ItemFlag.HIDE_ENCHANTS) },
        { nbt -> nbt["exp_book"] = true; nbt["amount"] = 2500.0 }
    )

    @JvmStatic
    val MEDIUM_EXPERIENCE_BOOK: ItemStack = builder(Material.BOOK,
        { displayName = "${ChatColor.BLUE}Medium Card Experience Book"; addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true); addItemFlags(ItemFlag.HIDE_ENCHANTS) },
        { nbt -> nbt["exp_book"] = true; nbt["amount"] = 10000.0 }
    )

    @JvmStatic
    val LARGE_EXPERIENCE_BOOK: ItemStack = builder(Material.BOOK,
        { displayName = "${ChatColor.DARK_PURPLE}Large Card Experience Book"; addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true); addItemFlags(ItemFlag.HIDE_ENCHANTS) },
        { nbt -> nbt["exp_book"] = true; nbt["amount"] = 500000.0 }
    )

    @JvmStatic
    val HUGE_EXPERIENCE_BOOK: ItemStack = builder(Material.BOOK,
        { displayName = "${ChatColor.GOLD}Huge Card Experience Book"; addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true); addItemFlags(ItemFlag.HIDE_ENCHANTS) },
        { nbt -> nbt["exp_book"] = true; nbt["amount"] = 2000000.0 }
    )

    @JvmStatic
    fun cardShard(rarity: Rarity): ItemStack {
        if (rarity == Rarity.BASIC) throw UnsupportedOperationException("Cannot Use Basic Rarity")

        return builder(Material.RABBIT_HIDE,
            { displayName = "${rarity.color}${rarity.name} Card Shard"; addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true); addItemFlags(ItemFlag.HIDE_ENCHANTS) },
            { nbt -> nbt.id = "card_shard"; nbt["rarity"] = rarity.name }
        )
    }

    // Static Util

    @JvmStatic
    fun builder(material: Material, action: ItemMeta.() -> Unit): ItemStack {
        return ItemStack(material).apply {
            itemMeta = itemMeta.apply {
                action(this)
            }
        }
    }

    @JvmStatic
    fun next(key: String = "stored"): ItemStack =
        head("arrow_right") {
            itemMeta = itemMeta.apply {
                displayName = "${ChatColor.GREEN}${get("constants.next")}"
            }
        }.nbt { nbt -> nbt.id = "scroll:$key"; nbt["operation"] = 1 }

    @JvmStatic
    fun prev(key: String = "stored"): ItemStack =
        head("arrow_left") {
            itemMeta = itemMeta.apply {
                displayName = "${ChatColor.AQUA}${get("constants.prev")}"
            }
        }.nbt { nbt -> nbt.id = "scroll:$key"; nbt["operation"] = -1 }

    @JvmStatic
    fun back(key: String = "action"): ItemStack =
        head("arrow_left_log") {
            itemMeta = itemMeta.apply {
                displayName = "${ChatColor.RED}${get("constants.back")}"
            }
        }.nbt { nbt -> nbt.id = "back:$key" }

    @JvmStatic
    fun head(key: String, action: ItemStack.() -> Unit = {}): ItemStack {
        val p = Properties().apply { load(Items::class.java.getResourceAsStream("/util/heads.properties")) }
        val value = p.getProperty(key) ?: throw IllegalArgumentException("Head not found: $key")

        return (if (Wrapper.legacy) ItemStack(matchMaterial("SKULL_ITEM"), 1, 3.toShort()) else ItemStack(matchMaterial("PLAYER_HEAD"))).apply {
            itemMeta = (itemMeta as SkullMeta).apply {
                val profile = GameProfile(UUID.randomUUID(), null).apply {
                    properties.put("textures", Property("textures", value))
                }

                javaClass.getDeclaredMethod("setProfile", GameProfile::class.java).apply { isAccessible = true }.invoke(this, profile)
            }

            action(this)
        }
    }

    @JvmStatic
    fun createShapedRecipe(key: String, result: ItemStack): ShapedRecipe {
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
    val RECIPES: List<Recipe> = mutableListOf(
        createShapedRecipe("card_table", CARD_TABLE).apply {
            shape(" W ", "WPW", " W ")

            setIngredient('W', BattleMaterial.CRAFTING_TABLE.find())
            setIngredient('P', Material.PAPER)
        },
    ).apply {
        // Card Shard Recipes

        for (rarity in Rarity.entries) {
            if (rarity.ordinal <= 1) continue

            val previous = Rarity.entries[rarity.ordinal - 1]

            add(createShapedRecipe("${rarity.name.lowercase()}_card_shard", cardShard(rarity)).apply {
                shape("SSS", "SSS", "SSS")

                val shard = cardShard(previous)
                if (!exactChoice(this, 'S', shard))
                    setIngredient('S', shard.data)
            })
        }
    }

    fun exactChoice(recipe: ShapedRecipe, char: Char, item: ItemStack): Boolean {
        return try {
            val exactChoice = Class.forName("org.bukkit.inventory.RecipeChoice\$ExactChoice")
            val constr = exactChoice.getDeclaredConstructor(ItemStack::class.java)
            constr.isAccessible = true

            val choice = constr.newInstance(item)

            val setIngredient = ShapedRecipe::class.java.getMethod("setIngredient", Char::class.java, exactChoice)
            setIngredient.invoke(recipe, char, choice)

            true
        } catch (ignored: ReflectiveOperationException) {
            false
        }
    }

    @JvmStatic
    val CARD_TABLE_RECIPES: List<CardWorkbenchRecipe> = listOf(
        CardWorkbenchRecipe(
            { matrix ->
                matrix.filter { it.isCard }.sumOf { it.amount } == 1 && matrix.any { it.nbt.getBoolean("exp_book") } && matrix.firstOrNull { it.isCard }?.card?.isMaxed != true
            },
            result@{ matrix ->
                val expBooks = matrix.filter { it.nbt.getBoolean("exp_book") }
                if (expBooks.isEmpty()) return@result null

                val card = matrix.firstOrNull { it.isCard }?.card ?: return@result null

                var nothing = true
                add@for (book in expBooks) {
                    for (i in 0 until book.amount) {
                        if (card.experience + book.nbt.getDouble("amount") >= card.maxCardExperience) break@add

                        card.experience += book.nbt.getDouble("amount")
                        nothing = false
                    }
                }

                if (nothing) return@result null

                return@result card.itemStack
            },
            edit@{ matrix ->
                matrix.apply {
                    val card = this[indexOfFirst { it.isCard }].card!!

                    this[indexOfFirst { it.isCard }] = ItemStack(Material.AIR)

                    while (indexOfFirst { it.nbt.getBoolean("exp_book") } != -1) {
                        val index = indexOfFirst { it.nbt.getBoolean("exp_book") }
                        val expAdd = this[index].nbt.getDouble("amount")

                        if (card.experience + expAdd >= card.maxCardExperience) break

                        this[index] = this[index].apply {
                            if (amount == 1) type = Material.AIR else amount -= 1
                        }

                        card.experience += expAdd
                    }
                }
            }
        )
    )

    @JvmStatic
    val PUBLIC_ITEMS = mutableMapOf(
        "card_table" to CARD_TABLE,
        "card_combiner" to CARD_COMBINER,
        "tiny_experience_book" to TINY_EXPERIENCE_BOOK,
        "small_experience_book" to SMALL_EXPERIENCE_BOOK,
        "medium_experience_book" to MEDIUM_EXPERIENCE_BOOK,
        "large_experience_book" to LARGE_EXPERIENCE_BOOK,
        "huge_experience_book" to HUGE_EXPERIENCE_BOOK
    ).apply {
        putAll(Rarity.entries.filter { it != Rarity.BASIC }.map { "${it.name.lowercase()}_card_shard" to cardShard(it) })
    }

    @JvmStatic
    private val GENERATED_ITEMS: Map<String, Double> = mapOf(
        "tiny_experience_book" to 0.04,
        "small_experience_book" to 0.001,
        "medium_experience_book" to 0.0002,
        "large_experience_book" to 0.00001,
        "huge_experience_book" to 0.0000005,
        "common_card_shard" to 0.5,
        "uncommon_card_shard" to 0.1,
        "rare_card_shard" to 0.05,
        "epic_card_shard" to 0.0075,
        "legendary_card_shard" to 0.001,
        "mythical_card_shard" to 0.00005,
        "ultimate_card_shard" to 0.0000025
    )

    @JvmStatic
    val EFFECTIVE_GENERATED_ITEMS = { GENERATED_ITEMS.mapNotNull {
        val item = PUBLIC_ITEMS[it.key] ?: return@mapNotNull null
        item to (it.value / GENERATED_ITEMS.values.sum())
    }.toMap() }

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

    fun <T> Map<T, Double>.random(reroll: Int = 0): T? {
        val (key, value) = entries.elementAt(r.nextInt(size))
        if (r.nextDouble() < value) return key

        if (reroll > 0)
            return random(reroll - 1)

        return null
    }

    class CardWorkbenchRecipe {

        val result: (Array<ItemStack>) -> ItemStack?
        val predicate: (Array<ItemStack>) -> Boolean
        val editMatrix: (Array<ItemStack>) -> Array<ItemStack>

        constructor(predicate: (Array<ItemStack>) -> Boolean, result: (Array<ItemStack>) -> ItemStack?, editMatrix: (Array<ItemStack>) -> Array<ItemStack>) {
            this.result = result
            this.predicate = predicate
            this.editMatrix = editMatrix
        }

        constructor(predicate: (Array<ItemStack>) -> Boolean, result: ItemStack?, editMatrix: (Array<ItemStack>) -> Array<ItemStack>) {
            this.result = { result }
            this.predicate = predicate
            this.editMatrix = editMatrix
        }

    }

}