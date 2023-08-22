package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCard
import me.gamercoder215.battlecards.api.card.item.CardEquipment
import me.gamercoder215.battlecards.api.card.item.CardEquipment.Rarity.MYTHOLOGICAL
import me.gamercoder215.battlecards.api.events.entity.CardUseAbilityEvent
import me.gamercoder215.battlecards.util.BattleUtil.mod
import org.bukkit.Material
import org.bukkit.event.entity.EntityDamageByEntityEvent

internal enum class CardEquipments1_19_R1(
    override val item: Material,
    override val rarity: CardEquipment.Rarity,
    modifiers: Array<Double>,
    override val ability: CardEquipment.Ability? = null
) : CardEquipment {

    ECHOING_CRYSTAL(Material.ECHO_SHARD, MYTHOLOGICAL,
        mod(speed = 0.83, damage = 1.16, defense = 1.12)
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