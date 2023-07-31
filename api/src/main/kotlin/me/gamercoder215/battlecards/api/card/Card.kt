package me.gamercoder215.battlecards.api.card

import me.gamercoder215.battlecards.api.BattleConfig
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
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
            if (statistics.cardExperience > maxCardExperience) throw IllegalArgumentException("Experience cannot be greater than max card experience")
            statistics.cardExperience = value.coerceIn(0.0, maxCardExperience)
        }

    val remainingExperience: Double
        /**
         * Fetches the experience required to reach the next level.
         * @return Experience to next level
         */
        get() {
            if (level == maxCardLevel) return 0.0
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
        get() = type.getEntityClass()

    val entityCardType: EntityType?
        /**
         * Fetches the EntityType of this BattleCard.
         * @return BattleCard EntityType, or null if not found
         */
        get() = EntityType.entries.firstOrNull {
            it.entityClass == type.getEntityClass()
        }

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

        /**
         * Converts a BattleCard's Experience to the corresponding level.
         * @param experience Experience to convert
         * @param rarity Rarity of the BattleCard to use for [Rarity.experienceModifier]
         * @return BattleCard Level
         */
        @JvmStatic
        fun toLevel(experience: Double, rarity: Rarity = Rarity.COMMON): Int {
            return when (experience) {
                in 0.0..600.0 -> 1
                in Double.NEGATIVE_INFINITY..0.0 -> throw IllegalArgumentException("Experience cannot be negative!")
                else -> {
                    var level = 1
                    while (toExperience(level, rarity) < experience) level++
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
                1 -> 0.0
                else -> {
                    var exp = 0.0
                    for (i in 2..level)
                        exp += floor(rarity.experienceModifier.pow(i / 2.0) * 500)

                    val rem = exp % 50

                    if (exp >= 25) exp - rem + 50 else exp - rem
                }
            }
        }

    }

}