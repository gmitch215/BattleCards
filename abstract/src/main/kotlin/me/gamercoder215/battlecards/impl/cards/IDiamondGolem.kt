package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.Material
import org.bukkit.entity.IronGolem
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

@Attributes(500.0, 15.3, 85.5, 0.3, 1.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.MULTIPLY, 1.04)

@BlockAttachment(Material.DIAMOND_BLOCK, 0.0, 0.0, 0.2)
@Suppress("deprecation")
class IDiamondGolem : IBattleCard<IronGolem>(BattleCardType.DIAMOND_GOLEM) {

    override fun init() {
        super.init()
        en.isPlayerCreated = true
    }

    @CardAbility("card.diamond_golem.ability.launch")
    @Offensive(0.15, CardOperation.ADD, 0.025)
    private fun launch(event: EntityDamageByEntityEvent) {
        val target = event.entity as? Player ?: return
        val amplifier: Double = (getLevel() / 5) + 1.25

        target.velocity = en.location.direction.multiply(amplifier)
    }

    @CardAbility("card.diamond_golem.ability.thorns")
    @Defensive(0.1)
    private fun thorns(event: EntityDamageByEntityEvent) {
        val attacker = event.damager as? Player ?: return
        val dmg = getStatistics().getAttackDamage() * 0.2

        attacker.damage(dmg, en)
        attacker.lastDamageCause = EntityDamageByEntityEvent(attacker, en, EntityDamageEvent.DamageCause.THORNS, dmg)
    }

}