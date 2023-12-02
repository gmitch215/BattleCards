package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCard
import me.gamercoder215.battlecards.api.card.item.CardEquipment
import me.gamercoder215.battlecards.api.card.item.CardEquipment.Potion
import me.gamercoder215.battlecards.api.card.item.CardEquipment.Rarity.*
import me.gamercoder215.battlecards.api.events.entity.CardUseAbilityEvent
import me.gamercoder215.battlecards.util.BattleUtil.mod
import org.bukkit.Material
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffectType

internal enum class CardEquipments1_13_R1(
    override val item: Material,
    override val rarity: CardEquipment.Rarity,
    modifiers: Array<Double>,
    override val ability: CardEquipment.Ability? = null,
    override val effects: Set<Potion> = emptySet()
) : CardEquipment {

    // Average

    SAND_SHELL(Material.NAUTILUS_SHELL, AVERAGE,
        mod(damage = 1.01, speed = 0.995)
    ),

    FERN_BLADE(Material.FERN, AVERAGE,
        mod(damage = 1.03, defense = 0.98)
    ),

    // Frequent

    SHINING_MELON(Material.GLISTERING_MELON_SLICE, FREQUENT,
        mod(health = 1.04)
    ),

    ORGANIC_ELYTRA(Material.PHANTOM_MEMBRANE, FREQUENT,
        mod(health = 1.025, defense = 0.95, speed = 1.055)
    ),

    // Historical

    CHOCOLATE(Material.DARK_OAK_TRAPDOOR, HISTORICAL,
        mod(health = 1.037, damage = 1.045, speed = 1.06, defense = 0.86)
    ),

    DAMAGE_ELEMENT(Material.FIRE_CORAL, HISTORICAL,
        mod(damage = 1.1, defense = 0.9)
    ),

    DEFENSE_ELEMENT(Material.HORN_CORAL, HISTORICAL,
        mod(damage = 0.9, defense = 1.1)
    ),

    SPEED_ELEMENT(Material.TUBE_CORAL, HISTORICAL,
        mod(speed = 1.1, knockbackResistance = 0.9)
    ),

    KNOCKBACK_ELEMENT(Material.BUBBLE_CORAL, HISTORICAL,
        mod(speed = 0.9, knockbackResistance = 1.1)
    ),

    HEALTH_ELEMENT(Material.BRAIN_CORAL, HISTORICAL,
        mod(health = 1.1, damage = 0.9)
    ),

    // Mythological

    SUPER_ICE(Material.BLUE_ICE, MYTHOLOGICAL,
        mod(speed = 0.5625, knockbackResistance = 1.32, defense = 1.475)
    ),

    FORK_OF_DESTINY(Material.TRIDENT, MYTHOLOGICAL,
        mod(damage = 1.365, knockbackResistance = 0.75, defense = 0.845, speed = 1.09)
    ),

    // Special

    DRAGON_BLOOD(Material.DRAGON_BREATH, SPECIAL,
        mod(damage = 2.65, defense = 0.45, knockbackResistance = 1.25),
        effects = setOf(
            Potion(PotionEffectType.DAMAGE_RESISTANCE, 1, Potion.Status.BOTH)
        )
    ),

    ;

    override val healthModifier: Double
    override val damageModifier: Double
    override val defenseModifier: Double
    override val speedModifier: Double
    override val knockbackResistanceModifier: Double

    init {
        require(modifiers.size == 5) { "Modifiers must be of size 5" }

        this.healthModifier = modifiers[0]
        this.damageModifier = modifiers[1]
        this.defenseModifier = modifiers[2]
        this.speedModifier = modifiers[3]
        this.knockbackResistanceModifier = modifiers[4]
    }

}