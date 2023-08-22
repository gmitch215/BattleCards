package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCard
import me.gamercoder215.battlecards.api.card.item.CardEquipment
import me.gamercoder215.battlecards.api.card.item.CardEquipment.Rarity.HISTORICAL
import me.gamercoder215.battlecards.api.events.entity.CardUseAbilityEvent
import me.gamercoder215.battlecards.util.BattleUtil.mod
import org.bukkit.Material
import org.bukkit.event.entity.EntityDamageByEntityEvent

internal enum class CardEquipments1_14_R1(
    override val item: Material,
    override val rarity: CardEquipment.Rarity,
    modifiers: Array<Double>,
    override val ability: CardEquipment.Ability? = null,
    override val effects: Set<CardEquipment.Potion> = emptySet()
) : CardEquipment {

    // Historical

    DYE_OF_HASTE(Material.YELLOW_DYE, HISTORICAL,
        mod(speed = 1.03)
    ),

    DYE_OF_STRENGTH(Material.RED_DYE, HISTORICAL,
        mod(damage = 1.04)
    ),

    DYE_OF_DEFENSE(Material.GREEN_DYE, HISTORICAL,
        mod(defense = 1.05)
    ),

    DYE_OF_HEALTH(Material.PINK_DYE, HISTORICAL,
        mod(health = 1.035)
    ),

    DYE_OF_SHIELD(Material.BLACK_DYE, HISTORICAL,
        mod(knockbackResistance = 1.045)
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