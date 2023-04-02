package me.gamercoder215.battlecards.api.card

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.floor
import kotlin.math.pow

/**
 * Represents a Card in BattleCards
 * @param T The Entity type this BattleCard can be used on
 */
abstract class BattleCard<T : Entity> internal constructor(
    private val entityClass: Class<T>,
    private val cardId: String,
    private val name: String,
    private val description: String,
    private val creationDate: Long,
    // Card Details
    private val rarity: Rarity,
    internal val statistics: Map<String, Any>
) {


    /**
     * Fetches the Entity Class that this BattleCard represents.
     * @return Entity Class
     */
    fun getEntityClass(): Class<T> = entityClass

    /**
     * Fetches the Card ID of this BattleCard.
     * @return BattleCard ID
     */
    fun getCardID(): String = cardId

    /**
     * Fetches the Rarity of this BattleCard.
     * @return BattleCard Rarity
     */
    fun getRarity(): Rarity = rarity

    /**
     * Fetches the Statistics of this BattleCard instance.
     * @return BattleCard Statistics
     */
    fun getStatistics(): BattleStatistics = BattleStatistics(this)

    /**
     * Fetches the Name of this BattleCard.
     * @return BattleCard Name
     */
    fun getCreationDate(): Date = Date(creationDate)

    /**
     * Fetches the Date this BattleCard was last used. Will return null if never used.
     * @return Last Used Date
     */
    fun getLastUsed(): Date? {
        if (!statistics.containsKey("last-used")) return null

        return Date(statistics["last-used"] as Long)
    }

    /**
     * Fetches the player that last used this BattleCard. Will return null if never used.
     * @return Last Used Player
     */
    fun getLastUsedPlayer(): Player? {
        if (getLastUsed() == null) return null

        return Bukkit.getPlayer(statistics["last-used-player"] as UUID)
    }


    // Static Methods

    companion object {

        /**
         * The maximum level a BattleCard can be
         */
        const val MAX_LEVEL = 150

        /**
         * Converts a BattleCard's Experience to the corresponding level
         * @param experience Experience to convert
         * @return BattleCard Level
         */
        @JvmStatic
        fun toLevel(experience: Double): Int {
            return when(experience) {
                in Double.NEGATIVE_INFINITY..0.0 -> throw IllegalArgumentException("Experience must be positive!")
                in 0.0..1350.0 -> 1
                else -> {
                    var level = 1;
                    while (toExperience(level) < experience) level++
                    level
                }
            }
        }

        /**
         * Converts a BattleCard's Level to the minimum experience required to reach that level
         * @param level Level to convert
         * @return Minimum Experience required to reach Level
         */
        @JvmStatic
        fun toExperience(level: Int): Double {
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
            }

        }

    }

    // Inner Classes

    /**
     * Represents a BattleCard's Rarity
     */
    enum class Rarity(
        val color: ChatColor
    ) {
        /**
         * Represents the Basic rarity
         */
        BASIC(ChatColor.WHITE),
        /**
         * Represents the Common rarity
         */
        COMMON(ChatColor.GREEN),
        /**
         * Represents the Uncommon rarity
         */
        UNCOMMON(ChatColor.DARK_GREEN),
        /**
         * Represents the Rare rarity
         */
        RARE(ChatColor.BLUE),
        /**
         * Represents the Epic rarity
         */
        EPIC(ChatColor.DARK_PURPLE),
        /**
         * Represents the Legend rarity
         */
        LEGEND(ChatColor.GOLD),
        /**
         * Represents the Mythical rarity
         */
        MYTHICAL(ChatColor.LIGHT_PURPLE),
        /**
         * Represents the Ultimate rarity
         */
        ULTIMATE(ChatColor.AQUA),

        ;

        override fun toString(): String {
            return color.toString().plus(name).plus(ChatColor.RESET)
        }

    }


}