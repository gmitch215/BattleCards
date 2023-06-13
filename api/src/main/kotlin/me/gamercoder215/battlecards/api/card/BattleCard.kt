package me.gamercoder215.battlecards.api.card

import org.bukkit.OfflinePlayer
import org.bukkit.entity.LivingEntity
import java.util.*

/**
 * Represents an instance of a Spawned or Killed Card in BattleCards
 * @param T The Entity type this BattleCard represents
 */
@Suppress("unchecked_cast")
interface BattleCard<T : LivingEntity> {

    /**
     * Fetches the Entity Class that this BattleCard represents.
     * @return Entity Class
     */
    val entityClass: Class<T>
        get() = type.getEntityClass() as Class<T>

    /**
     * Whether this BattleCard is currently spawend.
     * @return true if spawned, false otherwise
     */
    val isSpawned: Boolean
        get() = entity != null

    /**
     * Fetches the Entity that this BattleCard is spawned as, or null if not spawned.
     * @return Spawned Entity
     */
    val entity: T?

    /**
     * Fetches this BattleCard's Card Data.
     * @return [Card] Data
     */
    val data: Card

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
        get() = data.creationDate

    /**
     * Fetches the Statistics of this BattleCard instance.
     * @return BattleCard Statistics
     */
    val statistics: BattleStatistics
        get() = data.statistics

    /**
     * Fetches the Date this BattleCard was last used. Will return null if never used.
     * @return Last Used Date
     */
    val lastUsed: Date?
        get() = data.lastUsed

    /**
     * Fetches the level of this BattleCard.
     * @return BattleCard Level
     */
    val level: Int
        get() = data.level

    /**
     * Fetches the player that last used this BattleCard. Will return null if never used.
     * @return Last Used Player
     */
    val lastUsedPlayer: OfflinePlayer? 
        get() = data.lastUsedPlayer

    /**
     * Fetches the experience of this BattleCard.
     * @return BattleCard Experience
     */
    val experience: Double
        get() = data.experience

    /**
     * Fetches the experience required to reach the next level.
     * @return Experience to next level
     */
    val remainingExperience: Double
        get() = data.remainingExperience

    /**
     * Fetches the maximum level that this Card can be.
     * @return Max Card Level
     */
    val maxCardLevel: Int
        get() = data.maxCardLevel

    /**
     * Fetches the maximum experience that this Card can have.
     * @return Max Card Experience
     */
    val maxCardExperience: Double
        get() = data.maxCardExperience

    /**
     * Fetches the numerical identifier for the generation of BattleCards this card is from.
     * @return BattleCard Generation
     */
    val generation: Int
        get() = data.generation

    /**
     * Fetches the BattleCardType of this BattleCard.
     * @return [BattleCardType]
     */
    val type: BattleCardType
        get() = data.type

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
        get() = (level + 10).coerceAtMost(60)

    // Static Methods

    companion object {

        /**
         * The maximum level any BattleCard can be
         */
        const val MAX_LEVEL = Card.MAX_LEVEL

        /**
         * Converts a BattleCard's Experience to the corresponding level.
         * @param experience Experience to convert
         * @param rarity Rarity of the BattleCard to use for [Rarity.experienceModifier]
         * @return BattleCard Level
         */
        @JvmStatic
        fun toLevel(experience: Double, rarity: Rarity = Rarity.COMMON): Int = Card.toLevel(experience, rarity)

        /**
         * Converts a BattleCard's Level to the minimum experience required to reach that level.
         * @param level Level to convert
         * @param rarity Rarity of the BattleCard to use for [Rarity.experienceModifier]
         * @return Minimum Experience required to reach Level
         */
        @JvmStatic
        fun toExperience(level: Int, rarity: Rarity = Rarity.COMMON): Double = Card.toExperience(level, rarity)

    }

}