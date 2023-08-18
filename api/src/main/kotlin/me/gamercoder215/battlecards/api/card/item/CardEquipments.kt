package me.gamercoder215.battlecards.api.card.item

import me.gamercoder215.battlecards.api.card.BattleCard
import me.gamercoder215.battlecards.api.card.item.CardEquipment.Rarity.*
import me.gamercoder215.battlecards.api.card.item.CardEquipments.Util.ability
import me.gamercoder215.battlecards.api.card.item.CardEquipments.Util.mod
import me.gamercoder215.battlecards.api.events.entity.CardUseAbilityEvent
import me.gamercoder215.battlecards.api.events.entity.CardUseAbilityEvent.AbilityType
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.security.SecureRandom

/**
 * Represents Card Equipment built-in to BattleCards
 */
enum class CardEquipments(
    material: Material,
    rarity: CardEquipment.Rarity,
    modifiers: Array<Double>,
    ability: CardEquipment.Ability? = null
) : CardEquipment {

    // Average

    /**
     * Represents an Iron Ingot CardEquipment.
     */
    STEEL_INGOT(Material.IRON_INGOT, AVERAGE,
        mod(health = 0.975, defense = 1.02, speed = 0.96, knockbackResistance = 1.02)
    ),

    /**
     * Represents an Oak Sapling CardEquipment.
     */
    ENCHANTED_SAPLING(Material.matchMaterial("OAK_SAPLING") ?: Material.matchMaterial("SAPLING")!!, AVERAGE,
        mod(health = 1.025, damage = 0.97)
    ),

    /**
     * Represents a Brick CardEquipment.
     */
    RAW_TERRACOTTA(Material.BRICK, AVERAGE,
        mod(damage = 1.035, defense = 0.9675)
    ),

    /**
     * Represents a Stone Axe CardEquipment.
     */
    COBBLED_AXE(Material.STONE_AXE, AVERAGE,
        mod(damage = 1.03, defense = 0.955)
    ),

    /**
     * Represents a Stone Sword CardEquipment.
     */
    COBBLED_SWORD(Material.STONE_SWORD, AVERAGE,
        mod(damage = 1.025, defense = 0.96)
    ),

    /**
     * Represents a Leather Tunic CardEquipment.
     */
    SILK_TUNIC(Material.LEATHER_CHESTPLATE, AVERAGE,
        mod(defense = 1.04, speed = 1.005, knockbackResistance = 0.945)
    ),

    /**
     * Represents a Leather Pants CardEquipment.
     */
    SILK_PANTS(Material.LEATHER_LEGGINGS, AVERAGE,
        mod(defense = 1.035, speed = 1.0025, knockbackResistance = 0.96)
    ),

    /**
     * Represents a Stone CardEquipment.
     */
    ROCK(Material.STONE, AVERAGE,
        mod(damage = 1.008, speed = 0.99)
    ),

    // Frequent

    /**
     * Represents a Nether Star CardEquipment.
     */
    DAMAGE_CRYSTAL(Material.NETHER_STAR, FREQUENT,
        mod(health = 0.96, damage = 1.035, defense = 0.985)
    ),

    /**
     * Represents a Compass CardEquipment.
     */
    SPEED_COMPASS(Material.COMPASS, FREQUENT,
        mod(speed = 1.075, health = 0.988)
    ),

    /**
     * Represents a Cactus CardEquipment.
     */
    HARDENED_CACTUS(Material.CACTUS, FREQUENT,
        mod(damage = 0.955, defense = 1.01), ability("pricking", AbilityType.DEFENSIVE, 0.75) { card, event ->
            val target = event.damager as? LivingEntity ?: return@ability
            target.damage(1.5, card.entity)
        }),

    /**
     * Represents an Ender Eye CardEquipment.
     */
    EYE_OF_BEGINNING(Material.matchMaterial("ENDER_EYE") ?: Material.matchMaterial("EYE_OF_ENDER")!!, FREQUENT,
        mod(damage = 1.025, defense = 0.97, knockbackResistance = 1.01)
    ),

    /**
     * Represents a Feather CardEquipment.
     */
    GHOST_FEATHER(Material.FEATHER, FREQUENT,
        mod(speed = 1.0575, knockbackResistance = 0.96)
    ),

    // Historical

    /**
     * Represents a Golden Carrot CardEquipment.
     */
    GOLDEN_FRUIT(Material.GOLDEN_CARROT, HISTORICAL,
        mod(health = 1.08, damage = 0.9, defense = 1.015, knockbackResistance = 0.91)
    ),

    /**
     * Represents a Blaze Rod CardEquipment.
     */
    FIRE_ROD(Material.BLAZE_ROD, HISTORICAL,
        mod(damage = 1.008), ability("flame", AbilityType.OFFENSIVE, 1.0) { _, event ->
            val target = event.entity as? LivingEntity ?: return@ability
            target.fireTicks += 20 * 3
        }),

    /**
     * Represents a Beacon CardEquipment.
     */
    BEACON_OF_SOULS(Material.BEACON, HISTORICAL,
        mod(health = 1.04, knockbackResistance = 0.965)
    ),

    /**
     * Represents a Furnace CardEquipment.
     */
    INFERNO_OVEN(Material.FURNACE, HISTORICAL,
        mod(damage = 1.085, defense = 0.95, knockbackResistance = 0.95), ability("flame_thorns", AbilityType.DEFENSIVE, 0.8) { _, event ->
            val target = event.damager as? LivingEntity ?: return@ability
            target.fireTicks += 30
        }),

    /**
     * Represents a Sand CardEquipment.
     */
    QUICKSAND(Material.SAND, HISTORICAL,
        mod(speed = 0.86), ability("slowing", AbilityType.OFFENSIVE, 0.75) { _, event ->
            val target = event.entity as? LivingEntity ?: return@ability
            target.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 20 * 2, 0))
            event.damage += 1.5
        }),

    // Mythological

    /**
     * Represents a Nether Wart CardEquipment.
     */
    NETHER_PEARLS(Material.matchMaterial("NETHER_WART") ?: Material.matchMaterial("NETHER_WARTS")!!, MYTHOLOGICAL,
        mod(health = 0.86, damage = 1.17, defense = 0.935)
    ),

    /**
     * Represents a Nether Star CardEquipment.
     */
    LIGHTNING_CRYSTAL(Material.NETHER_STAR, MYTHOLOGICAL,
        mod(), ability("lightning", AbilityType.OFFENSIVE, { card -> 0.5 + (card.level * 0.05)}) { _, event ->
            val target = event.entity as? LivingEntity ?: return@ability
            target.world.strikeLightning(target.location)
            event.damage += 5.0
        }),

    // Special

    /**
     * Represents a Bedrock CardEquipment.
     */
    BEDROCK_SHIELD(Material.BEDROCK, SPECIAL,
        mod(health = 0.5, damage = 0.4, defense = 3.5)
    ),

    ;

    /**
     * Fetches the item icon of this CardEquipment.
     */
    override val item: Material

    /**
     * Fetches the multiplicative health modifier of this CardEquipment.
     */
    override val healthModifier: Double

    /**
     * Fetches the multiplicative damage modifier of this CardEquipment.
     */
    override val damageModifier: Double

    /**
     * Fetches the multiplicative defense modifier of this CardEquipment.
     */
    override val defenseModifier: Double

    /**
     * Fetches the multiplicative speed modifier of this CardEquipment.
     */
    override val speedModifier: Double

    /**
     * Fetches the multiplicative knockback resistance modifier of this CardEquipment.
     */
    override val knockbackResistanceModifier: Double

    /**
     * Fetches the rarity of this CardEquipment.
     */
    override val rarity: CardEquipment.Rarity

    /**
     * Fetches the ability of this CardEquipment.
     */
    override val ability: CardEquipment.Ability?

    init {
        require(modifiers.size == 5) { "Modifiers must be of size 5" }

        this.item = material
        this.rarity = rarity
        this.ability = ability

        this.healthModifier = modifiers[0]
        this.damageModifier = modifiers[1]
        this.defenseModifier = modifiers[2]
        this.speedModifier = modifiers[3]
        this.knockbackResistanceModifier = modifiers[4]
    }

    private object Util {

        fun mod(
            health: Double = 1.0,
            damage: Double = 1.0,
            defense: Double = 1.0,
            speed: Double = 1.0,
            knockbackResistance: Double = 1.0,
        ): Array<Double> = arrayOf(health, damage, defense, speed, knockbackResistance)

        fun ability(
            name: String,
            type: AbilityType,
            probability: (BattleCard<*>) -> Double,
            action: (BattleCard<*>, EntityDamageByEntityEvent) -> Unit
        ): CardEquipment.Ability = CardEquipment.Ability(name, type, probability, action)

        fun ability(
            name: String,
            type: AbilityType,
            probability: Double,
            action: (BattleCard<*>, EntityDamageByEntityEvent) -> Unit
        ): CardEquipment.Ability = CardEquipment.Ability(name, type, { probability }, action)

    }


}