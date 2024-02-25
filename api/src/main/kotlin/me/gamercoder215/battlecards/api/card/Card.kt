package me.gamercoder215.battlecards.api.card

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.item.CardEquipment
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Creature
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.util.ChatPaginator
import java.util.*
import kotlin.math.floor
import kotlin.math.pow

/**
 * Represents a [BattleCard]'s Data before spawning.
 */
interface Card : ConfigurationSerializable {

    val cardID: String
        /**
         * Fetches the Card ID of this BattleCard.
         * @return BattleCard ID
         */
        get() = type.cardID

    val rarity: Rarity
        /**
         * Fetches the Rarity of this BattleCard.
         * @return BattleCard Rarity
         */
        get() = type.rarity

    /**
     * Fetches the Date this card was created.
     * @return Creation Date
     */
    val creationDate: Date

    /**
     * Fetches the Statistics of this BattleCard instance.
     * @return BattleCard Statistics
     */
    val statistics: BattleStatistics

    /**
     * Fetches the Date this BattleCard was last used. Will return null if never used.
     * @return Last Used Date, or `Date(0)` if never used
     */
    val lastUsed: Date

    /**
     * Fetches the player that last used this BattleCard. Will return null if never used.
     * @return Last Used Player
     */
    val lastUsedPlayer: OfflinePlayer?

    var level: Int
        /**
         * Fetches the level of this BattleCard.
         * @return BattleCard Level
         */
        get() = statistics.cardLevel
        /**
         * Sets the level of this BattleCard.
         * @param value New Level
         */
        set(value) {
            if (value > maxCardLevel) throw IllegalArgumentException("Level cannot be greater than max card level")
            statistics.cardLevel = value.coerceIn(1, maxCardLevel)
        }

    var experience: Double
        /**
         * Fetches the experience of this BattleCard.
         * @return BattleCard Experience
         */
        get() = statistics.cardExperience
        /**
         * Sets the experience of this BattleCard.
         * @param value New Experience
         */
        set(value) {
            if (value > maxCardExperience) throw IllegalArgumentException("Experience cannot be greater than max card experience")
            statistics.cardExperience = value.coerceIn(0.0, maxCardExperience)
        }

    val remainingExperience: Double
        /**
         * Fetches the experience required to reach the next level.
         * @return Experience to next level
         */
        get() {
            if (level >= maxCardLevel) return 0.0
            return toExperience(level + 1, rarity) - experience
        }

    val maxCardLevel: Int
        /**
         * Fetches the maximum level that this Card can be.
         * @return Max Card Level
         */
        get() = statistics.maxCardLevel

    val maxCardExperience: Double
        /**
         * Fetches the maximum experience that this Card can have.
         * @return Max Card Experience
         */
        get() = statistics.maxCardExperience

    val generation: Int
        /**
         * Fetches the numerical identifier for the generation of BattleCards this card is from.
         * @return BattleCard Generation
         */
        get() = type.generation

    /**
     * Fetches the BattleCardType of this BattleCard.
     * @return [BattleCardType]
     */
    val type: BattleCardType

    val cardClass:BattleCardClass
        /**
         * Fetches the BattleCardClass of this BattleCard.
         * @return [BattleCardClass]
         */
        get() = type.cardClass

    val name: String
        /**
         * Fetches the name of this BattleCard.
         * @return BattleCard Name
         */
        get() = type.name.lowercase(BattleConfig.config.locale).split("_").joinToString(" ") { s -> s.replaceFirstChar { it.uppercase() } }

    val deployTime: Int
        /**
         * Fetches the amount of <strong>seconds</strong> this card can be deployed for.
         * @return BattleCard Deploy Time
         */
        get() = statistics.deployTime

    /**
     * Fetches the [BattleCard] class for this Card Data.
     * @return BattleCard Class
     */
    val entityCardClass: Class<out BattleCard<*>>

    /**
     * Spawns this Card Data into a BattleCard. The Player must be holding a BattleCard Item.
     * @param owner The Player spawning this BattleCard
     * @return Spawned BattleCard Instance
     * @throws IllegalStateException if the player already has 2 BattleCards deployed
     */
    @Throws(IllegalStateException::class)
    fun spawnCard(owner: Player): BattleCard<*>

    val isMaxed: Boolean
        /**
         * Whetehr or not this BattleCard is currently maxed.
         * @return true if reached maximum card experience and level, false otherwise
         */
        get() { return experience >= maxCardExperience }

    val cooldownTime: Long
        /**
         * Fetches how many milliseconds until this BattleCard can be deployed again.
         * @return Cooldown Time
         */
        get() = (BattleConfig.config.cardCooldown.times(1000) - (System.currentTimeMillis() - lastUsed.time)).coerceAtLeast(0)

    val canUse: Boolean
        /**
         * Whether this card can be deployed if [cooldownTime] is `0`.
         * @return Whether this card can be deployed based on the cooldown
         */
        get() = cooldownTime == 0L

    val entityClass: Class<out LivingEntity>?
        /**
         * Fetches the BattleCard Entity class for this Card Data.
         * @return BattleCard Entity Class
         */
        get() = type.entityClass

    val entityCardType: EntityType?
        /**
         * Fetches the EntityType of this BattleCard.
         * @return BattleCard EntityType, or null if not found
         */
        get() = EntityType.entries.firstOrNull {
            it.entityClass == type.entityClass
        }

    /**
     * Fetches the ItemStack icon for this Card.
     * @return Card Icon
     */
    val icon: ItemStack
        get() {
            val item = if (type.icon != null) ItemStack(type.icon) else typeToItem[entityCardType] ?: throw AssertionError("No icon found for card type ${type.name}")

            return ItemStack(item).apply {
                itemMeta = itemMeta.apply {
                    displayName = String.format(BattleConfig.config.locale, BattleConfig.config.get("constants.card"), "${rarity.color}$name")

                    if (rarity != Rarity.BASIC)
                        lore = listOf("$rarity${if (cardClass != BattleCardClass.BASIC) " $cardClass" else ""}", " ") + ChatPaginator.wordWrap("\"${BattleConfig.config.get("card.${this@Card.type.name.lowercase()}")}\"", 30).map { s -> "${ChatColor.YELLOW}$s" } +
                                listOf(" ") +
                                ChatPaginator.wordWrap(BattleConfig.config.get("card.${this@Card.type.name.lowercase()}.desc"), 30).map { s -> "${ChatColor.GRAY}$s" }

                    addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS)
                }
            }
        }

    /**
     * Fetches the current level of the quest for this [Card].
     * @param quest Quest to Use
     * @return Current Quest Level
     * @see CardQuest.getCurrentLevel
     */
    fun getQuestLevel(quest: CardQuest): Int = quest.getCurrentLevel(this)

    /**
     * Fetches the quest completion percentage for this [Card].
     * @param quest Quest to Use
     * @return Percentage Completion until next level
     * @see CardQuest.getProgressPercentage
     */
    fun getQuestCompletion(quest: CardQuest): Double = quest.getProgressPercentage(this)

    /**
     * Fetches an immutable copy of the [CardEquipment] for this [Card].
     */
    val equipment: Set<CardEquipment>

    val equipmentSlots: Int
        /**
         * Fetches the amount of equipment slots this card has.
         * @return Equipment Slots
         */
        get() = statistics.equipmentSlots

    /**
     * Gets whether or not this card can be ridden.
     */
    val isRideable: Boolean

    // Serialization

    /**
     * Serializes this Card Data into a Byte Array.
     * @return Serialized Card Data
     */
    fun toByteArray(): ByteArray

    // Static Methods

    companion object {

        /**
         * The maximum level any BattleCard can be
         */
        const val MAX_LEVEL = 200

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
                    is String -> {
                        if (Bukkit.getServer() == null) return@mapNotNull null

                        val head = Material.matchMaterial("PLAYER_HEAD")
                        (if (head == null) ItemStack(Material.matchMaterial("SKULL_ITEM"), 1, 3) else ItemStack(head)).apply {
                            itemMeta = (itemMeta as SkullMeta).apply { owner = value }
                        }
                    }
                    else -> throw IllegalArgumentException("Invalid Type ${it.value::class.simpleName}")
                }

                type to item
            }.toMap().toMutableMap().apply {
                for (type in EntityType.entries.filter { Creature::class.java.isAssignableFrom(it.entityClass ?: LivingEntity::class.java) })
                    if (!containsKey(type)) this[type] = ItemStack(Material.matchMaterial("${type.name}_SPAWN_EGG") ?: (Material.matchMaterial("FILLED_MAP") ?: Material.matchMaterial("MAP")))
            }

        /**
         * Converts a BattleCard's Experience to the corresponding level.
         * @param experience Experience to convert
         * @param rarity Rarity of the BattleCard to use for [Rarity.experienceModifier]
         * @return BattleCard Level
         */
        @JvmStatic
        fun toLevel(experience: Double, rarity: Rarity = Rarity.COMMON): Int {
            return when {
                experience > rarity.maxCardExperience -> throw IllegalArgumentException("Experience cannot be greater than ${rarity.maxCardExperience}!")
                experience < 0.0 -> throw IllegalArgumentException("Experience cannot be negative!")
                else -> {
                    if (experience == rarity.maxCardExperience) return rarity.maxCardLevel

                    var level = 1
                    while (experience >= toExperience(level + 1, rarity)) level++
                    level
                }
            }
        }

        /**
         * Converts a BattleCard's Level to the minimum experience required to reach that level.
         * @param level Level to convert
         * @param rarity Rarity of the BattleCard to use for [Rarity.experienceModifier]
         * @return Minimum Experience required to reach Level
         */
        @JvmStatic
        fun toExperience(level: Int, rarity: Rarity = Rarity.COMMON): Double {
            return when (level) {
                in (rarity.maxCardLevel + 1)..Int.MAX_VALUE -> throw IllegalArgumentException("Level must be less than or equal to ${rarity.maxCardLevel}!")
                in Int.MIN_VALUE.. 0 -> throw IllegalArgumentException("Level must be positive!")
                else -> {
                    var exp = 0.0
                    for (i in 1 until level)
                        exp += floor(rarity.experienceModifier.pow(i / 2.0) * 500)

                    val rem = exp % 50

                    if (rem >= 25) exp - rem + 50 else exp - rem
                }
            }
        }

    }

}