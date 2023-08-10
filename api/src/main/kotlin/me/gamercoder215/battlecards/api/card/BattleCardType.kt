package me.gamercoder215.battlecards.api.card

import me.gamercoder215.battlecards.api.BattleConfig
import org.bukkit.Material
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
    BASIC(1, null, Rarity.BASIC, null),

    /**
     * Represents an Iron Golem BattleCard.
     */
    DIAMOND_GOLEM(1, IronGolem::class, Rarity.RARE, Material.DIAMOND_BLOCK),

    /**
     * Represents a Wither BattleCard.
     */
    WITHER_KING(1, Wither::class, Rarity.ULTIMATE, Material.matchMaterial("WITHER_ROSE") ?: Material.matchMaterial("WITHER_SKELETON_SKULL")),

    /**
     * Represents a Husk BattleCard.
     */
    MESA_ZOMBIE(1, "Husk", Rarity.UNCOMMON, Material.RED_SANDSTONE),

    /**
     * Represents a Skeleton BattleCard.
     */
    SNIPER(1, Skeleton::class, Rarity.COMMON, Material.BOW),

    /**
     * Represents a Husk BattleCard.
     */
    PRINCE_HUSK(1, "Husk", Rarity.EPIC, Material.GOLD_INGOT),

    /**
     * Represents an Enderman BattleCard.
     */
    WITHERMAN(1, Enderman::class, Rarity.LEGEND, Material.ENDER_PEARL),

    /**
     * Represents a Drowned BattleCard.
     */
    LAPIS_DROWNED(1, "Drowned", Rarity.UNCOMMON, Material.matchMaterial("LAPIS_LAZULI")),

    /**
     * Represents a Wither Skeleton BattleCard.
     */
    GOLD_SKELETON(1, "WitherSkeleton", Rarity.UNCOMMON, Material.matchMaterial("GOLDEN_CHESTPLATE") ?: Material.matchMaterial("GOLD_CHESTPLATE")),

    /**
     * Represents a Zombie BattleCard.
     */
    REDSTONE_ZOMBIE(1, Zombie::class, Rarity.COMMON, Material.REDSTONE_BLOCK),

    /**
     * Represents a Skeleton BattleCard.
     */
    UNDEAD_LUMBERJACK(1, Skeleton::class, Rarity.RARE, Material.IRON_AXE),

    /**
     * Represents a Zombie Villager BattleCard.
     */
    MINER(1, "ZombieVillager", Rarity.UNCOMMON, Material.IRON_PICKAXE),

    /**
     * Represents an Enderman BattleCard.
     */
    EYE_OF_ENDERMAN(1, Enderman::class, Rarity.LEGEND, Material.matchMaterial("ENDER_CRYSTAL") ?: Material.ENDER_PEARL),

    /**
     * Represents a Polar Bear BattleCard.
     */
    FROST_BEAR(1, "PolarBear", Rarity.EPIC, Material.PACKED_ICE),

    /**
     * Represents a Blaze BattleCard.
     */
    INFERNO_BLAZE(1, Blaze::class, Rarity.MYTHICAL, Material.matchMaterial("FIRE_CHARGE") ?: Material.matchMaterial("FIREBALL") ?: Material.BLAZE_ROD),

    /**
     * Represents a Stray BattleCard.
     */
    BANDIT(1, "Stray", Rarity.UNCOMMON, Material.STONE_SWORD),

    /**
     * Represents a Pigin Brute BattleCard.
     */
    NETHERITE_PIGLIN(1, "PiglinBrute", Rarity.EPIC, Material.matchMaterial("NETHERITE_SWORD")),

    /**
     * Represents a Spider BattleCard.
     */
    SPIDER_QUEEN(1, Spider::class, Rarity.LEGEND, Material.STRING),

    /**
     * Represents a Skeleton BattleCard.
     */
    SKELETON_SOLDIER(1, Skeleton::class, Rarity.COMMON, Material.STONE_AXE),

    /**
     * Represents a Wolf BattleCard.
     */
    PITBULL(1, Wolf::class, Rarity.UNCOMMON, Material.BONE),

    /**
     * Represents a Zombie BattleCard.
     */
    BOMBERMAN(1, Zombie::class, Rarity.LEGEND, Material.TNT),

    /**
     * Represents a Pillager BattleCard.
     */
    MERCENARY(1, "Pillager", Rarity.UNCOMMON, Material.DIAMOND_SWORD),

    /**
     * Represents a Drowned BattleCard.
     */
    SEALORD(1, "Drowned", Rarity.ULTIMATE, Material.matchMaterial("KELP")),

    /**
     * Represents a Vindicator BattleCard.
     */
    KNIGHT(1, "Vindicator", Rarity.UNCOMMON, Material.matchMaterial("SHIELD") ?: Material.IRON_AXE),

    /**
     * Represents a Illusioner BattleCard.
     */
    GOLDEN_WIZARD(1, "Illusioner", Rarity.RARE, Material.BLAZE_POWDER),

    /**
     * Represents a Cave Spider BattleCard.
     */
    SPIDER_HIVE(1, CaveSpider::class, Rarity.RARE, Material.SPIDER_EYE),

    /**
     * Represents a Zombie BattleCard.
     */
    SUSPICIOUS_ZOMBIE(1, Zombie::class, Rarity.EPIC, Material.matchMaterial("SUSPICIOUS_STEW") ?: Material.BLAZE_ROD),

    /**`
     * Represents a Stray BattleCard.
     */
    PHANTOM_RIDER(1, "Stray", Rarity.RARE, Material.matchMaterial("PHANTOM_MEMBRANE")),

    /**
     * Represents a Pillager BattleCard.
     */
    RAIDER(1, "Pillager", Rarity.EPIC, Material.matchMaterial("CROSSBOW")),

    /**
     * Represents a Wither Skeleton BattleCard.
     */
    NETHER_PRINCE(1, "WitherSkeleton", Rarity.MYTHICAL, Material.NETHERRACK),

    /**
     * Represents a Skeleton BattleCard.
     */
    STONE_ARCHER(1, Skeleton::class, Rarity.COMMON, Material.STONE),

    /**
     * Represents a Silverfish BattleCard.
     */
    SILVERFISH_HIVE(1, Silverfish::class, Rarity.UNCOMMON),

    /**
     * Represents a Zombie BattleCard.
     */
    THUNDER_REVENANT(1, Zombie::class, Rarity.MYTHICAL, Material.matchMaterial("LIGHTNING_ROD") ?: Material.BLAZE_ROD),

    /**
     * Represents a Husk BattleCard.
     */
    EMERALD_HUSK(1, "Husk", Rarity.UNCOMMON, Material.EMERALD),

    /**
     * Represents a Husk BattleCard.
     */
    WARRIOR_HUSK(1, "Husk", Rarity.COMMON, Material.DIAMOND_AXE),

    /**
     * Represents a Skeleton BattleCard.
     */
    NECROMANCER(1, Skeleton::class, Rarity.EPIC, Material.ROTTEN_FLESH),

    /**
     * Represents a Husk BattleCard.
     */
    ETERNAL_HUSK(1, "Husk", Rarity.MYTHICAL, Material.matchMaterial("END_PORTAL_FRAME") ?: Material.matchMaterial("ENDER_PORTAL_FRAME") ?: Material.BEDROCK)
    ;

    private val entityClass: Class<out LivingEntity>?

    constructor(generation: Int, entityClass: String, rarity: Rarity, material: Material? = null) : this(
        generation,
        try {
            Class.forName("org.bukkit.entity.${entityClass}") as Class<out LivingEntity>
        } catch (e: ClassNotFoundException) {
            null
        },
        rarity,
        material
    )

    constructor(generation: Int, entityClass: KClass<out LivingEntity>, rarity: Rarity, material: Material? = null) : this(generation, entityClass.java, rarity, material)

    constructor(generation: Int, entityClass: Class<out LivingEntity>?, rarity: Rarity, icon: Material? = null) {
        this.generation = generation
        this.entityClass = entityClass
        this.rarity = rarity
        this.icon = icon
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
     * Fetches the material icon used in the Card Information GUI.
     * @return Material Icon
     */
    val icon: Material?

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