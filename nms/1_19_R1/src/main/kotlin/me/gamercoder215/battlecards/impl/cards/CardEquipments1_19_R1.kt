package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCard
import me.gamercoder215.battlecards.api.card.item.CardEquipment
import me.gamercoder215.battlecards.api.card.item.CardEquipment.Rarity.MYTHOLOGICAL
import me.gamercoder215.battlecards.api.events.entity.CardUseAbilityEvent
import me.gamercoder215.battlecards.impl.cards.CardEquipments1_19_R1.Util.mod
import org.bukkit.Material
import org.bukkit.event.entity.EntityDamageByEntityEvent

internal enum class CardEquipments1_19_R1(
    override val item: Material,
    override val rarity: CardEquipment.Rarity,
    modifiers: Array<Double>,
    override val ability: CardEquipment.Ability? = null
) : CardEquipment {

    ECHOING_CRYSTAL(Material.ECHO_SHARD, MYTHOLOGICAL,
        mod(speed = 0.83, damage = 1.165)
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
            type: CardUseAbilityEvent.AbilityType,
            probability: (BattleCard<*>) -> Double,
            action: (BattleCard<*>, EntityDamageByEntityEvent) -> Unit
        ): CardEquipment.Ability = CardEquipment.Ability(name, type, probability, action)

        fun ability(
            name: String,
            type: CardUseAbilityEvent.AbilityType,
            probability: Double,
            action: (BattleCard<*>, EntityDamageByEntityEvent) -> Unit
        ): CardEquipment.Ability = CardEquipment.Ability(name, type, { probability }, action)

    }

}