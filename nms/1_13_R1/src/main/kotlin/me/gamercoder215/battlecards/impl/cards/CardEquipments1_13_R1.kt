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