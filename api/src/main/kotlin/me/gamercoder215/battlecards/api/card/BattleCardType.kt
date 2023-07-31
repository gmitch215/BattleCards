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

    /**
     * Represents a Zombie BattleCard.
     */
    REDSTONE_ZOMBIE(1, Zombie::class, Rarity.COMMON),

    /**
     * Represents a Skeleton BattleCard.
     */
    UNDEAD_LUMBERJACK(1, Skeleton::class, Rarity.RARE),

    /**
     * Represents a Zombie Villager BattleCard.
     */
    MINER(1, "ZombieVillager", Rarity.UNCOMMON),

    /**
     * Represents an Enderman BattleCard.
     */
    EYE_OF_ENDERMAN(1, Enderman::class, Rarity.LEGEND),

    /**
     * Represents a Polar Bear BattleCard.
     */
    FROST_BEAR(1, "PolarBear", Rarity.EPIC),

    /**
     * Represents a Blaze BattleCard.
     */
    INFERNO_BLAZE(1, Blaze::class, Rarity.RARE),

    /**
     * Represents a Stray BattleCard.
     */
    BANDIT(1, "Stray", Rarity.UNCOMMON),

    /**
     * Represents a Pigin Brute BattleCard.
     */
    NETHERITE_PIGLIN(1, "PiglinBrute", Rarity.EPIC),

    /**
     * Represents a Spider BattleCard.
     */
    SPIDER_QUEEN(1, Spider::class, Rarity.LEGEND),

    /**
     * Represents a Skeleton BattleCard.
     */
    SKELETON_SOLDIER(1, Skeleton::class, Rarity.COMMON),

    /**
     * Represents a Wolf BattleCard.
     */
    PITBULL(1, Wolf::class, Rarity.UNCOMMON),

    /**
     * Represents a Zombie BattleCard.
     */
    BOMBERMAN(1, Zombie::class, Rarity.EPIC),

    /**
     * Represents a Pillager BattleCard.
     */
    MERCENARY(1, "Pillager", Rarity.UNCOMMON),

    /**
     * Represents a Drowned BattleCard.
     */
    SEALORD(1, "Drowned", Rarity.ULTIMATE),

    /**
     * Represents a Vindicator BattleCard.
     */
    KNIGHT(1, "Vindicator", Rarity.UNCOMMON),

    /**
     * Represents a Illusioner BattleCard.
     */
    GOLDEN_WIZARD(1, "Illusioner", Rarity.RARE),

    /**
     * Represents a Cave Spider BattleCard.
     */
    SPIDER_HIVE(1, CaveSpider::class, Rarity.UNCOMMON),

    /**
     * Represents a Zombie BattleCard.
     */
    SUSPICIOUS_ZOMBIE(1, Zombie::class, Rarity.RARE),

    /**`
     * Represents a Stray BattleCard.
     */
    PHANTOM_RIDER(1, "Stray", Rarity.UNCOMMON),

    /**
     * Represents a Pillager BattleCard.
     */
    RAIDER(1, "Pillager", Rarity.EPIC),

    /**
     * Represents a Wither Skeleton BattleCard.
     */
    NETHER_PRINCE(1, "WitherSkeleton", Rarity.MYTHICAL),

    /**
     * Represents a Skeleton BattleCard.
     */
    STONE_ARCHER(1, Skeleton::class, Rarity.COMMON),

    /**
     * Represents a Silverfish BattleCard.
     */
    SILVERFISH_HIVE(1, Silverfish::class, Rarity.RARE),

    /**
     * Represents a Zombie BattleCard.
     */
    THUNDER_REVENANT(1, Zombie::class, Rarity.MYTHICAL),

    /**
     * Represents a Husk BattleCard.
     */
    EMERALD_HUSK(1, "Husk", Rarity.UNCOMMON),

    /**
     * Represents a Husk BattleCard.
     */
    WARRIOR_HUSK(1, "Husk", Rarity.EPIC),

    /**
     * Represents a Snow Golem BattleCard.
     */
    ICE_GOLEM(1, Snowman::class, Rarity.RARE),
    ;

    private val entityClass: Class<out LivingEntity>?

    constructor(generation: Int, entityClass: String, rarity: Rarity) : this(
        generation,
        try {
            Class.forName("org.bukkit.entity.${entityClass}") as Class<out LivingEntity>
        } catch (e: ClassNotFoundException) {
            null
        },
        rarity
    )

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
    fun createCardData(): Card = BattleConfig.config.createCardData(this)

    companion object {

        /**
         * Fetches a BattleCardType from a BattleCard Class.
         * @param clazz BattleCard Class
         * @return BattleCardType found, or null if not found
         */
        @JvmStatic
        fun fromClass(clazz: Class<out BattleCard<*>>): BattleCardType? {
            return entries.firstOrNull { it.getEntityClass() == clazz }
        }

    }

}