package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.item.CardEquipment
import me.gamercoder215.battlecards.api.card.item.CardEquipment.Rarity.AVERAGE
import me.gamercoder215.battlecards.api.card.item.CardEquipment.Rarity.FREQUENT
import me.gamercoder215.battlecards.util.BattleUtil.mod
import org.bukkit.Material

internal enum class CardEquipments1_9_R1(
    override val item: Material,
    override val rarity: CardEquipment.Rarity,
    modifiers: Array<Double>,
    override val ability: CardEquipment.Ability? = null
) : CardEquipment {

    // Average

    BEET_SOUP(Material.BEETROOT_SOUP, AVERAGE,
        mod(health = 1.01, damage = 1.01, defense = 1.01, speed = 1.01, knockbackResistance = 1.01)
    ),

    // Frequent

    SHIELD(Material.SHIELD, FREQUENT,
        mod(defense = 1.1, speed = 0.88)
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