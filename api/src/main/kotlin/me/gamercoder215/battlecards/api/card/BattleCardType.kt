package me.gamercoder215.battlecards.api.card

import me.gamercoder215.battlecards.api.BattleConfig
import org.bukkit.entity.*
import kotlin.reflect.KClass

/**
 * Represents a type of Card.
 */
@Suppress("unchecked_cast")
enum class BattleCardType {

    /**
     * Represents a Basic BattleCard.
     */
    BASIC(1, null, Rarity.BASIC),

    /**
     * Represents an Iron Golem BattleCard.
     */
    DIAMOND_GOLEM(1, IronGolem::class, Rarity.RARE),

    /**
     * Represents a Wither BattleCard.
     */
    WITHER_KING(1, Wither::class, Rarity.ULTIMATE),

    /**
     * Represents a Husk BattleCard.
     */
    MESA_ZOMBIE(1, "Husk", Rarity.UNCOMMON),

    /**
     * Represents a Skeleton BattleCard.
     */
    SNIPER(1, Skeleton::class, Rarity.COMMON),

    /**
     * Represents a Husk BattleCard.
     */
    PRINCE_HUSK(1, "Husk", Rarity.EPIC),

    /**
     * Represents an Enderman BattleCard.
     */
    WITHERMAN(1, Enderman::class, Rarity.LEGEND),

    /**
     * Represents a Drowned BattleCard.
     */
    LAPIS_DROWNED(1, "Drowned", Rarity.UNCOMMON),

    /**
     * Represents a Wither Skeleton BattleCard.
     */
    GOLD_SKELETON(1, "WitherSkeleton", Rarity.RARE),
    ;

    private val entityClass: Class<out LivingEntity>?

    constructor(generation: Int, entityClass: String, rarity: Rarity) : this(generation, getBukkitClass(entityClass), rarity)

    constructor(generation: Int, entityClass: KClass<out LivingEntity>, rarity: Rarity) : this(generation, entityClass.java, rarity)

    constructor(generation: Int, entityClass: Class<out LivingEntity>?, rarity: Rarity) {
        this.generation = generation
        this.entityClass = entityClass
        this.rarity = rarity
    }

    /**
     * Fetches the generation of this BattleCardType.
     * @return BattleCard Generation
     */
    val generation: Int

    /**
     * Fetches the Card ID of this BattleCardType.
     * @return BattleCard ID
     */
    val cardID: String
        get() = name.lowercase()

    /**
     * Fetches the Rarity of this BattleCardType.
     * @return BattleCard Rarity
     */
    val rarity: Rarity

    /**
     * Fetches the Entity Class that this BattleCardType uses.
     * @return Entity Class found, or null if not found
     */
    fun getEntityClass(): Class<out LivingEntity>? = entityClass

    /**
     * Creates an empty card data object.
     * @return New Card Data
     */
    fun createCardData(): Card = BattleConfig.getConfig().createCardData(this)

    companion object {

        private fun getBukkitClass(clazz: String): Class<out LivingEntity>? {
            return try {
                Class.forName("org.bukkit.entity.${clazz}") as Class<out LivingEntity>
            } catch (e: ClassNotFoundException) {
                null
            }
        }

        /**
         * Fetches a BattleCardType from a BattleCard Class.
         * @param clazz BattleCard Class
         * @return BattleCardType found, or null if not found
         */
        fun fromClass(clazz: Class<out BattleCard<*>>): BattleCardType? {
            return values().firstOrNull { it.getEntityClass() == clazz }
        }

    }

}