package me.gamercoder215.battlecards.api.card

import me.gamercoder215.battlecards.api.BattleConfig
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.*
import org.jetbrains.annotations.Range
import kotlin.reflect.KClass

/**
 * Represents a type of Card.
 */
@Suppress("unchecked_cast")
enum class BattleCardType {

    /**
     * Represents a Basic BattleCard.
     */
    BASIC(1, BattleCardClass.BASIC, null, Rarity.BASIC, null, Material.AIR),

    /**
     * Represents an Iron Golem BattleCard.
     */
    DIAMOND_GOLEM(1, BattleCardClass.TANK, IronGolem::class, Rarity.RARE, Material.DIAMOND_BLOCK),

    /**
     * Represents a Wither BattleCard.
     */
    WITHER_KING(1, BattleCardClass.MAGIC, Wither::class, Rarity.ULTIMATE, Material.matchMaterial("WITHER_ROSE") ?: Material.matchMaterial("WITHER_SKELETON_SKULL"), Material.NETHER_STAR),

    /**
     * Represents a Husk BattleCard.
     */
    MESA_ZOMBIE(1, BattleCardClass.MELEE, "Husk", Rarity.UNCOMMON, Material.RED_SANDSTONE),

    /**
     * Represents a Skeleton BattleCard.
     */
    SNIPER(1, BattleCardClass.RANGED, Skeleton::class, Rarity.COMMON, Material.BOW),

    /**
     * Represents a Husk BattleCard.
     */
    PRINCE_HUSK(1, BattleCardClass.MELEE, "Husk", Rarity.EPIC, Material.GOLD_INGOT),

    /**
     * Represents an Enderman BattleCard.
     */
    WITHERMAN(1, BattleCardClass.TANK, Enderman::class, Rarity.LEGEND, Material.ENDER_PEARL, Material.SOUL_SAND),

    /**
     * Represents a Drowned BattleCard.
     */
    LAPIS_DROWNED(1, BattleCardClass.TANK, "Drowned", Rarity.UNCOMMON, Material.matchMaterial("LAPIS_LAZULI"), Material.LAPIS_BLOCK),

    /**
     * Represents a Wither Skeleton BattleCard.
     */
    GOLD_SKELETON(1, BattleCardClass.MELEE, "WitherSkeleton", Rarity.UNCOMMON, Material.matchMaterial("GOLDEN_CHESTPLATE") ?: Material.matchMaterial("GOLD_CHESTPLATE"), Material.GOLD_BLOCK),

    /**
     * Represents a Zombie BattleCard.
     */
    REDSTONE_ZOMBIE(1, BattleCardClass.MELEE, Zombie::class, Rarity.COMMON, Material.REDSTONE_BLOCK),

    /**
     * Represents a Skeleton BattleCard.
     */
    UNDEAD_LUMBERJACK(1, BattleCardClass.MELEE, Skeleton::class, Rarity.RARE, Material.IRON_AXE),

    /**
     * Represents a Zombie Villager BattleCard.
     */
    MINER(1, BattleCardClass.MELEE, "ZombieVillager", Rarity.UNCOMMON, Material.IRON_PICKAXE),

    /**
     * Represents an Enderman BattleCard.
     */
    EYE_OF_ENDERMAN(1, BattleCardClass.MAGIC, Enderman::class, Rarity.LEGEND, Material.matchMaterial("ENDER_CRYSTAL") ?: Material.ENDER_PEARL, Material.matchMaterial("ENDER_EYE") ?: Material.matchMaterial("EYE_OF_ENDER")!!),

    /**
     * Represents a Polar Bear BattleCard.
     */
    FROST_BEAR(1, BattleCardClass.TANK, "PolarBear", Rarity.EPIC, Material.PACKED_ICE),

    /**
     * Represents a Blaze BattleCard.
     */
    INFERNO_BLAZE(1, BattleCardClass.RANGED, Blaze::class, Rarity.MYTHICAL, Material.matchMaterial("FIRE_CHARGE") ?: Material.matchMaterial("FIREBALL") ?: Material.BLAZE_ROD, Material.LAVA_BUCKET),

    /**
     * Represents a Stray BattleCard.
     */
    BANDIT(1, BattleCardClass.RANGED, "Stray", Rarity.UNCOMMON, Material.STONE_SWORD),

    /**
     * Represents a Pigin Brute BattleCard.
     */
    NETHERITE_PIGLIN(1, BattleCardClass.MELEE, "PiglinBrute", Rarity.EPIC, Material.matchMaterial("NETHERITE_SWORD"), Material.matchMaterial("NETHERITE_INGOT")),

    /**
     * Represents a Spider BattleCard.
     */
    SPIDER_QUEEN(1, BattleCardClass.GROUP, Spider::class, Rarity.LEGEND, Material.STRING),

    /**
     * Represents a Skeleton BattleCard.
     */
    SKELETON_SOLDIER(1, BattleCardClass.TANK, Skeleton::class, Rarity.COMMON, Material.STONE_AXE),

    /**
     * Represents a Wolf BattleCard.
     */
    PITBULL(1, BattleCardClass.MELEE, Wolf::class, Rarity.UNCOMMON, Material.BONE),

    /**
     * Represents a Zombie BattleCard.
     */
    BOMBERMAN(1, BattleCardClass.MAGIC, Zombie::class, Rarity.LEGEND, Material.TNT),

    /**
     * Represents a Pillager BattleCard.
     */
    MERCENARY(1, BattleCardClass.TANK, "Pillager", Rarity.UNCOMMON, Material.DIAMOND_SWORD),

    /**
     * Represents a Drowned BattleCard.
     */
    SEALORD(1, BattleCardClass.GROUP, "Drowned", Rarity.ULTIMATE, Material.matchMaterial("KELP")),

    /**
     * Represents a Vindicator BattleCard.
     */
    KNIGHT(1, BattleCardClass.TANK, "Vindicator", Rarity.UNCOMMON, Material.matchMaterial("SHIELD") ?: Material.IRON_AXE, Material.DIAMOND_AXE),

    /**
     * Represents a Illusioner BattleCard.
     */
    GOLDEN_WIZARD(1, BattleCardClass.MAGIC, "Illusioner", Rarity.RARE, Material.BLAZE_POWDER),

    /**
     * Represents a Cave Spider BattleCard.
     */
    SPIDER_HIVE(1, BattleCardClass.GROUP, CaveSpider::class, Rarity.RARE, Material.SPIDER_EYE),

    /**
     * Represents a Zombie BattleCard.
     */
    SUSPICIOUS_ZOMBIE(1, BattleCardClass.MELEE, Zombie::class, Rarity.EPIC, Material.matchMaterial("SUSPICIOUS_STEW") ?: Material.BLAZE_ROD, Material.GLASS_BOTTLE),

    /**`
     * Represents a Stray BattleCard.
     */
    PHANTOM_RIDER(1, BattleCardClass.RANGED, "Stray", Rarity.RARE, Material.matchMaterial("PHANTOM_MEMBRANE")),

    /**
     * Represents a Pillager BattleCard.
     */
    RAIDER(1, BattleCardClass.TANK, "Pillager", Rarity.EPIC, Material.matchMaterial("CROSSBOW")),

    /**
     * Represents a Wither Skeleton BattleCard.
     */
    NETHER_PRINCE(1, BattleCardClass.MELEE, "WitherSkeleton", Rarity.MYTHICAL, Material.NETHERRACK),

    /**
     * Represents a Skeleton BattleCard.
     */
    STONE_ARCHER(1, BattleCardClass.RANGED, Skeleton::class, Rarity.COMMON, Material.STONE),

    /**
     * Represents a Silverfish BattleCard.
     */
    SILVERFISH_HIVE(1, BattleCardClass.GROUP, Silverfish::class, Rarity.UNCOMMON, Material.matchMaterial("ANDESITE") ?: Material.STONE, Material.STONE_PICKAXE),

    /**
     * Represents a Zombie BattleCard.
     */
    THUNDER_REVENANT(1, BattleCardClass.MAGIC, Zombie::class, Rarity.MYTHICAL, Material.matchMaterial("LIGHTNING_ROD") ?: Material.BLAZE_ROD, Material.GLOWSTONE),

    /**
     * Represents a Husk BattleCard.
     */
    EMERALD_HUSK(1, BattleCardClass.TANK, "Husk", Rarity.UNCOMMON, Material.EMERALD, Material.EMERALD_BLOCK),

    /**
     * Represents a Husk BattleCard.
     */
    WARRIOR_HUSK(1, BattleCardClass.MELEE, "Husk", Rarity.COMMON, Material.DIAMOND_AXE, Material.DIAMOND_CHESTPLATE),

    /**
     * Represents a Skeleton BattleCard.
     */
    NECROMANCER(1, BattleCardClass.MAGIC, Skeleton::class, Rarity.EPIC, Material.ROTTEN_FLESH, Material.LEATHER_CHESTPLATE),

    /**
     * Represents a Husk BattleCard.
     */
    ETERNAL_HUSK(1, BattleCardClass.TANK, "Husk", Rarity.MYTHICAL, Material.matchMaterial("END_PORTAL_FRAME") ?: Material.matchMaterial("ENDER_PORTAL_FRAME") ?: Material.BEDROCK, Material.matchMaterial("TOTEM_OF_UNDYING") ?: Material.matchMaterial("TOTEM")),

    /**
     * Represents a Shulker BattleCard.
     */
    PURPLE_PARASITE(1, BattleCardClass.MAGIC, "Shulker", Rarity.LEGEND, Material.matchMaterial("SHULKER_SHELL")),

    /**
     * Represents a Drowned BattleCard.
     */
    AQUATIC_ASSASSIN(1, BattleCardClass.MELEE, "Drowned", Rarity.EPIC, Material.matchMaterial("CONDUIT"), Material.matchMaterial("TRIDENT")),

    /**
     * Represents a Skeleton BattleCard.
     */
    GOAT_GLADIATOR(1, BattleCardClass.MELEE, Skeleton::class, Rarity.RARE, Material.matchMaterial("GOAT_HORN") ?: Material.matchMaterial("HORN_CORAL_BLOCK")),

    /**
     * Represents a Piglin BattleCard.
     */
    MAGMA_JOCKEY(1, BattleCardClass.RANGED, "Piglin", Rarity.RARE, Material.MAGMA_CREAM),

    /**
     * Represents a Piglin Brute BattleCard.
     */
    PIGLIN_TITAN(1, BattleCardClass.MELEE, "PiglinBrute", Rarity.ULTIMATE, Material.matchMaterial("PIGLIN_HEAD") ?: Material.matchMaterial("NETHERITE_BLOCK"), Material.matchMaterial("NETHERITE_BLOCK")),

    /**
     * Represents a Husk BattleCard.
     */
    SAND_TRAVELER(2, BattleCardClass.MELEE, "Husk", Rarity.EPIC, Material.SAND),

    /**
     * Represents a Wither Skeleton BattleCard.
     */
    THE_IMMORTAL(2, BattleCardClass.TANK, "WitherSkeleton", Rarity.ULTIMATE, Material.matchMaterial("END_CRYSTAL")),

    /**
     * Represents a Snowman BattleCard.
     */
    BLIZZARD(2, BattleCardClass.MAGIC, "Snowman", Rarity.LEGEND, Material.SNOW_BLOCK),
    ;

    constructor(generation: Int, clazz: BattleCardClass, entityClass: String, rarity: Rarity, material: Material? = null, crafting: Material? = material) : this(
        generation,
        clazz,
        try {
            Class.forName("org.bukkit.entity.${entityClass}") as Class<out LivingEntity>
        } catch (e: ReflectiveOperationException) {
            null
        },
        rarity,
        material,
        crafting
    )

    constructor(generation: Int, clazz: BattleCardClass, entityClass: KClass<out LivingEntity>, rarity: Rarity, material: Material? = null, crafting: Material? = material) : this(generation, clazz, entityClass.java, rarity, material, crafting)

    constructor(generation: Int, clazz: BattleCardClass, entityClass: Class<out LivingEntity>?, rarity: Rarity, material: Material? = null, crafting: Material? = material) {
        this.generation = generation
        this.cardClass = clazz
        this.entityClass = entityClass
        this.rarity = rarity
        this.icon = material
        this.craftingMaterial = crafting ?: Material.AIR
    }

    /**
     * Fetches the generation of this BattleCardType.
     * @return BattleCard Generation
     */
    val generation: Int

    /**
     * Fetches the class of this BattleCardType.
     * @return BattleCard Class
     */
    val cardClass: BattleCardClass

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
    val entityClass: Class<out LivingEntity>?

    /**
     * Fetches the Crafting Material used to craft this BattleCardType.
     * @return Crafting Material, or [Material.AIR] if not craftable
     */
    val craftingMaterial: Material

    /**
     * Creates an empty card data object.
     * @return New Card Data
     */
    fun createCardData(): Card = BattleConfig.config.createCardData(this)

    /**
     * Creates a new card data object.
     * @return New Card Data
     * @see [createCardData]
     */
    operator fun invoke(): Card = createCardData()

    companion object {

        /**
         * Fetches a BattleCardType from a BattleCard Class.
         * @param clazz BattleCard Class
         * @return BattleCardType found, or null if not found
         */
        @JvmStatic
        fun fromClass(clazz: Class<out BattleCard<*>>): BattleCardType? {
            return entries.firstOrNull { it.entityClass == clazz }
        }

    }

}

/**
 * Represents the Class for a Card.
 */
enum class BattleCardClass(
    /**
     * The color prefix for the class.
     */
    val color: ChatColor,

    private vararg val mods0: Pair<Int, Double>,
) {

    /**
     * Represents a Basic Class.

     */
    BASIC(ChatColor.WHITE),

    /**
     * Represents a Melee Class.
     */
    MELEE(ChatColor.RED,
        2 to 2.0,
        4 to 3.0),

    /**
     * Represents a Ranged Class.
     */
    RANGED(ChatColor.GREEN,
        4 to 2.0,
        3 to 3.0),

    /**
     * Represents a Magic Class.
     */
    MAGIC(ChatColor.BLUE,
        5 to 2.0,
        1 to 3.0),

    /**
     * Represents a Tank Class.
     */
    TANK(ChatColor.DARK_AQUA,
        3 to 2.0,
        5 to 3.0),

    /**
     * Represents a Support Class.
     */
    GROUP(ChatColor.DARK_PURPLE,
        1 to 2.0,
        2 to 3.0)

    ;


    /**
     * The classes this card is weak against, mapped to its modifier
     */
    val weaknesses: Map<BattleCardClass, Double>
        get() = BattleCardClass.entries.filter { it.strengths.contains(this) }.associateWith { it.strengths[this]!! }

    /**
     * The classes this card is strong against, mapped to its modifier
     */
    val strengths: Map<BattleCardClass, Double>
        get() = mods0.associate { BattleCardClass.entries[it.first] to it.second }

    /**
     * Gets the modifier for this class against another class.
     * @param other The class to get the modifier for
     * @return The modifier, or `1` if not found
     */
    fun modifier(other: BattleCardClass): @Range(from = 1L, to = Int.MAX_VALUE.toLong()) Double {
        return this.strengths[other] ?: 1.0
    }

    override fun toString(): String = "$color${ChatColor.BOLD}$name"

}