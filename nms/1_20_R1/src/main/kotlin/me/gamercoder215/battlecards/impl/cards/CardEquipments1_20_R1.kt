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

internal enum class CardEquipments1_20_R1(
    override val item: Material,
    override val rarity: CardEquipment.Rarity,
    modifiers: Array<Double>,
    override val ability: CardEquipment.Ability? = null,
    override val effects: Set<Potion> = emptySet(),
) : CardEquipment {

    // Average

    LAVENDER_PETALS(Material.PINK_PETALS, AVERAGE,
        mod(speed = 1.03, damage = 1.01, defense = 0.97)
    ),

    // Historical

    DINOSAUR_EGG(Material.SNIFFER_EGG, HISTORICAL,
        mod(health = 1.11, damage = 1.055, speed = 1.04)
    ),

    // Mythological

    ENDER_RUNE(Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, MYTHOLOGICAL,
        mod(health = 1.2, damage = 1.08, defense = 0.935, speed = 1.1, knockbackResistance = 0.87),
        effects = setOf(
            Potion(PotionEffectType.SLOW_FALLING, 0, Potion.Status.BOTH)
        )
    ),

    COASTAL_RUNE(Material.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, MYTHOLOGICAL,
        mod(health = 0.72, damage = 1.13, defense = 1.045, speed = 1.02, knockbackResistance = 0.98),
        effects = setOf(
            Potion(PotionEffectType.WATER_BREATHING, 0, Potion.Status.BOTH)
        )
    ),

    JUNGLE_RUNE(Material.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, MYTHOLOGICAL,
        mod(health = 1.12, damage = 0.71, defense = 1.05, speed = 1.05, knockbackResistance = 0.94)
    ),

    AQUATIC_RUNE(Material.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, MYTHOLOGICAL,
        mod(health = 1.04, damage = 1.025, defense = 1.3, speed = 0.65, knockbackResistance = 0.89),
        effects = setOf(
            Potion(PotionEffectType.DOLPHINS_GRACE, 2, Potion.Status.USER_ONLY)
        )
    ),

    NETHER_RUNE(Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, MYTHOLOGICAL,
        mod(health = 0.96, damage = 1.34, defense = 0.685, speed = 1.05, knockbackResistance = 1.095),
        effects = setOf(
            Potion(PotionEffectType.INCREASE_DAMAGE, 2, Potion.Status.USER_ONLY)
        )
    ),

    // Special

    ENCHANTED_RUNE(Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, MYTHOLOGICAL,
        mod(health = 1.8, damage = 1.45, defense = 1.3, knockbackResistance = 1.12)
    )

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