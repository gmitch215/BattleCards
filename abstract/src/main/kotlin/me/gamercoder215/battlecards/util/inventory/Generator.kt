package me.gamercoder215.battlecards.util.inventory

import me.gamercoder215.battlecards.api.card.Card
import me.gamercoder215.battlecards.api.card.Rarity
import me.gamercoder215.battlecards.util.BattleMaterial
import me.gamercoder215.battlecards.util.CardUtils
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
    private val typeToItem: Map<EntityType, ItemStack> =
        mapOf<Any, Any>(
            EntityType.BLAZE to "MHF_Blaze",
            EntityType.CAVE_SPIDER to "MHF_CaveSpider",
            EntityType.CREEPER to "MHF_Creeper",
            EntityType.ENDERMAN to "MHF_Enderman",
            EntityType.IRON_GOLEM to "MHF_Golem",
            EntityType.SKELETON to "MHF_Skeleton",
            EntityType.SPIDER to "MHF_Spider",
            EntityType.WITHER to Material.NETHER_STAR,

            "wither_skeleton" to "MHF_WSkeleton",
        ).mapNotNull {
            val type: EntityType = when (val key = it.key) {
                is EntityType -> key
                is String -> try { EntityType.valueOf(key.uppercase()) } catch (e: IllegalArgumentException) { null }
                else -> throw IllegalArgumentException("Invalid Type ${it.key::class.simpleName}")
            } ?: return@mapNotNull null

            val item: ItemStack = when (val value = it.value) {
                is Material -> ItemStack(value)
                is ItemStack -> value
                is String -> BattleMaterial.PLAYER_HEAD.findStack().apply {
                    itemMeta = (itemMeta as SkullMeta).apply { owner = value }
                }
                else -> throw IllegalArgumentException("Invalid Type ${it.value::class.simpleName}")
            }

            type to item
        }.toMap().toMutableMap().apply {
            for (type in EntityType.entries.filter { Creature::class.java.isAssignableFrom(it.entityClass ?: LivingEntity::class.java) })
                if (!containsKey(type)) this[type] = ItemStack(Material.matchMaterial("${type.name}_SPAWN_EGG") ?: BattleMaterial.FILLED_MAP.find())
        }

    @JvmStatic
    fun generateCardInfo(card: Card): BattleInventory {
        val inv = genGUI(27, get("menu.card.info"))
        inv.isCancelled = true

        inv[4] = (if (card.type.icon == null) typeToItem[card.entityCardType] ?: BattleMaterial.FILLED_MAP.findStack() else ItemStack(card.type.icon)).apply {
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

        while (inv.firstEmpty() != -1)
            inv[inv.firstEmpty()] = Items.GUI_BACKGROUND

        return inv
    }

    @JvmStatic
    fun generateCardTable(): BattleInventory {
        val inv = genGUI(45, get("menu.card_table"))

        for (i in 4..7)
            for (j in 1..3) inv[i + j.times(9)] = Items.GUI_BACKGROUND

        inv[24] = null

        return inv
    }

}