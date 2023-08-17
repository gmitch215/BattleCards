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
     * Represents an Oak Sapling cardEquipment.
     */
    ENCHANTED_SAPLING(Material.matchMaterial("OAK_SAPLING") ?: Material.matchMaterial("SAPLING")!!, AVERAGE,
        mod(health = 1.03, damage = 0.98)
    ),

    // Frequent

    /**
     * Represents a Nether Star CardEquipment.
     */
    DAMAGE_CRYSTAL(Material.NETHER_STAR, FREQUENT,
        mod(health = 0.96, damage = 1.035, defense = 0.985)
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
        mod(), ability("fire_rod", AbilityType.OFFENSIVE, 1.0) { _, event ->
            val target = event.entity as? LivingEntity ?: return@ability
            target.fireTicks += 20 * 3
        }),

    // Mythological

    /**
     * Represents a Nether Wart CardEquipment.
     */
    NETHER_PEARLS(Material.matchMaterial("NETHER_WART") ?: Material.matchMaterial("NETHER_WARTS")!!, MYTHOLOGICAL,
        mod(health = 0.86, damage = 1.17, defense = 0.935)
    ),

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