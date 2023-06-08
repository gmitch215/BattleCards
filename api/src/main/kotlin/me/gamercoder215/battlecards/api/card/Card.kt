package me.gamercoder215.battlecards.api.card

import org.bukkit.OfflinePlayer
import org.bukkit.configuration.serialization.ConfigurationSerializable
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
    fun getCardID(): String = getType().getCardID()

    /**
     * Fetches the Rarity of this BattleCard.
     * @return BattleCard Rarity
     */
    fun getRarity(): Rarity = getType().getRarity()

    /**
     * Fetches the Date this card was created.
     * @return Creation Date
     */
    fun getCreationDate(): Date

    /**
     * Fetches the Statistics of this BattleCard instance.
     * @return BattleCard Statistics
     */
    fun getStatistics(): BattleStatistics

    /**
     * Fetches the Date this BattleCard was last used. Will return null if never used.
     * @return Last Used Date
     */
    fun getLastUsed(): Date

    /**
     * Fetches the player that last used this BattleCard. Will return null if never used.
     * @return Last Used Player
     */
    fun getLastUsedPlayer(): OfflinePlayer?

    /**
     * Fetches the level of this BattleCard.
     * @return BattleCard Level
     */
    fun getLevel(): Int = getStatistics().getCardLevel()

    /**
     * Fetches the experience of this BattleCard.
     * @return BattleCard Experience
     */
    fun getExperience(): Double = getStatistics().getCardExperience()

    /**
     * Fetches the experience required to reach the next level.
     * @return Experience to next level
     */
    fun getRemainingExperience(): Double {
        if (getLevel() == getMaxCardLevel()) return 0.0

        return BattleCard.toExperience(getLevel() + 1, getRarity()) - getExperience()
    }

    /**
     * Fetches the maximum level that this Card can be.
     * @return Max Card Level
     */
    fun getMaxCardLevel(): Int = getRarity().getMaxCardLevel()

    /**
     * Fetches the maximum experience that this Card can have.
     * @return Max Card Experience
     */
    fun getMaxCardExperience(): Double = getRarity().getMaxCardExperience()

    /**
     * Fetches the numerical identifier for the generation of BattleCards this card is from.
     * @return BattleCard Generation
     */
    fun getGeneration(): Int = getType().getGeneration()

    /**
     * Fetches the BattleCardType of this BattleCard.
     * @return [BattleCardType]
     */
    fun getType(): BattleCardType

    /**
     * Fetches the name of this BattleCard.
     * @return BattleCard Name
     */
    fun getName(): String = getType().name.lowercase().replaceFirstChar { it.uppercase() }

    /**
     * Fetches the amount of <strong>seconds</strong> this card can be deployed for.
     * @return BattleCard Deploy Time
     */
    fun getDeployTime(): Int = (getLevel() + 10).coerceAtMost(60)

    /**
     * Fetches the [BattleCard] class for this Card Data.
     * @return BattleCard Class
     */
    fun getEntityCardClass(): Class<out BattleCard<*>>

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