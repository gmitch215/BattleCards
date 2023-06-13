package me.gamercoder215.battlecards.api.card

import org.bukkit.OfflinePlayer
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.floor
import kotlin.math.pow

/**
 * Represents a [BattleCard]'s Data before spawning.
 */
interface Card : ConfigurationSerializable {

    /**
     * Fetches the Card ID of this BattleCard.
     * @return BattleCard ID
     */
    val cardID: String
        get() = type.cardID

    /**
     * Fetches the Rarity of this BattleCard.
     * @return BattleCard Rarity
     */
    val rarity: Rarity
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

    /**
     * Fetches the level of this BattleCard.
     * @return BattleCard Level
     */
    val level: Int
        get() = statistics.cardLevel

    /**
     * Fetches the experience of this BattleCard.
     * @return BattleCard Experience
     */
    val experience: Double
        get() = statistics.cardExperience

    /**
     * Fetches the experience required to reach the next level.
     * @return Experience to next level
     */
    val remainingExperience: Double
        get() {
            if (level == maxCardLevel) return 0.0
            return BattleCard.toExperience(level + 1, rarity) - experience
        }

    /**
     * Fetches the maximum level that this Card can be.
     * @return Max Card Level
     */
    val maxCardLevel: Int
        get() = statistics.maxCardLevel

    /**
     * Fetches the maximum experience that this Card can have.
     * @return Max Card Experience
     */
    val maxCardExperience: Double
        get() = statistics.maxCardExperience

    /**
     * Fetches the numerical identifier for the generation of BattleCards this card is from.
     * @return BattleCard Generation
     */
    val generation: Int
        get() = type.generation

    /**
     * Fetches the BattleCardType of this BattleCard.
     * @return [BattleCardType]
     */
    val type: BattleCardType

    /**
     * Fetches the name of this BattleCard.
     * @return BattleCard Name
     */
    val name: String
        get() = type.name.lowercase().replaceFirstChar { it.uppercase() }

    /**
     * Fetches the amount of <strong>seconds</strong> this card can be deployed for.
     * @return BattleCard Deploy Time
     */
    val deployTime: Int
        get() = statistics.deployTime

    /**
     * Fetches the [BattleCard] class for this Card Data.
     * @return BattleCard Class
     */
    val entityCardClass: Class<out BattleCard<*>>

    /**
     * Spawns this Card Data into a BattleCard.
     * @param owner The Player spawning this BattleCard
     * @return Spawned BattleCard Instance
     */
    fun spawnCard(owner: Player): BattleCard<*>

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
        const val MAX_LEVEL = 150

        /**
         * Converts a BattleCard's Experience to the corresponding level.
         * @param experience Experience to convert
         * @param rarity Rarity of the BattleCard to use for [Rarity.getExperienceModifier]
         * @return BattleCard Level
         */
        @JvmStatic
        fun toLevel(experience: Double, rarity: Rarity = Rarity.COMMON): Int {
            return when(experience) {
                in Double.NEGATIVE_INFINITY..0.0 -> throw IllegalArgumentException("Experience must be positive!")
                in 0.0..1350.0 -> 1
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
         * @param rarity Rarity of the BattleCard to use for [Rarity.getExperienceModifier]
         * @return Minimum Experience required to reach Level
         */
        @JvmStatic
        fun toExperience(level: Int, rarity: Rarity = Rarity.COMMON): Double {
            return when (level) {
                in (MAX_LEVEL + 1)..Int.MAX_VALUE -> throw IllegalArgumentException("Level must be less than or equal to $MAX_LEVEL!")
                in Int.MIN_VALUE.. 0 -> throw IllegalArgumentException("Level must be positive!")
                1 -> 0.0
                else -> {
                    var exp = 0.0
                    for (i in 2..level)
                        exp += floor(1.3.pow(i - 1) * 1000)

                    val rem = exp % 50

                    if (exp >= 25) exp - rem + 50 else exp - rem
                }
            } * rarity.getExperienceModifier()
        }

    }

}