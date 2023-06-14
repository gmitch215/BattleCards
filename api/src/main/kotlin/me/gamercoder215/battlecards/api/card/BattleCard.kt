package me.gamercoder215.battlecards.api.card

import org.bukkit.OfflinePlayer
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * Represents an instance of a Spawned or Killed Card in BattleCards
 * @param T The Entity type this BattleCard represents
 */
@Suppress("unchecked_cast")
interface BattleCard<T : LivingEntity> {

    val entityClass: Class<T>
        /**
         * Fetches the Entity Class that this BattleCard represents.
         * @return Entity Class
         */
        get() = type.getEntityClass() as Class<T>

    val isSpawned: Boolean
        /**
         * Whether this BattleCard is currently spawend.
         * @return true if spawned, false otherwise
         */
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
     * Fetches the Original ItemStack Card used to spawn this BattleCard.
     * @return Spawned Card
     */
    val itemUsed: ItemStack

    /**
     * Fetches the ItemStack Card that is currenly attached to this BattleCard. This may contained modified statistics based off of [itemUsed].
     * @return Current Card
     */
    val currentItem: ItemStack

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

    val creationDate: Date
        /**
         * Fetches the Date this card was created.
         * @return Creation Date
         */
        get() = data.creationDate

    val statistics: BattleStatistics
        /**
         * Fetches the Statistics of this BattleCard instance.
         * @return BattleCard Statistics
         */
        get() = data.statistics

    val lastUsed: Date
        /**
         * Fetches the Date this BattleCard was last used. Will return null if never used.
         * @return Last Used Date
         */
        get() = data.lastUsed

    val level: Int
        /**
         * Fetches the level of this BattleCard.
         * @return BattleCard Level
         */
        get() = data.level

    val lastUsedPlayer: OfflinePlayer?
        /**
         * Fetches the player that last used this BattleCard. Will return null if never used.
         * @return Last Used Player
         */
        get() = data.lastUsedPlayer

    val experience: Double
        /**
         * Fetches the experience of this BattleCard.
         * @return BattleCard Experience
         */
        get() = data.experience

    val remainingExperience: Double
        /**
         * Fetches the experience required to reach the next level.
         * @return Experience to next level
         */
        get() = data.remainingExperience

    val maxCardLevel: Int
        /**
         * Fetches the maximum level that this Card can be.
         * @return Max Card Level
         */
        get() = data.maxCardLevel

    val maxCardExperience: Double
        /**
         * Fetches the maximum experience that this Card can have.
         * @return Max Card Experience
         */
        get() = data.maxCardExperience

    val generation: Int
        /**
         * Fetches the numerical identifier for the generation of BattleCards this card is from.
         * @return BattleCard Generation
         */
        get() = data.generation

    val type: BattleCardType
        /**
         * Fetches the BattleCardType of this BattleCard.
         * @return [BattleCardType]
         */
        get() = data.type

    val name: String
        /**
         * Fetches the name of this BattleCard.
         * @return BattleCard Name
         */
        get() = data.name

    val deployTime: Int
        /**
         * Fetches the amount of <strong>seconds</strong> this card can be deployed for.
         * @return BattleCard Deploy Time
         */
        get() = data.deployTime

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