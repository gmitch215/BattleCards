package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.IronGolem
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

@Type(BattleCardType.DIAMOND_GOLEM)
@Attributes(500.0, 18.3, 85.5, 0.23, 60.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 2.6)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 1.06)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.MULTIPLY, 1.02, 800.0)

@BlockAttachment(Material.DIAMOND_BLOCK, 0.0, 0.0, 0.2)
@Rideable
@Suppress("deprecation")
class IDiamondGolem(data: ICard) : IBattleCard<IronGolem>(data) {

    override fun init() {
        super.init()
        entity.isPlayerCreated = true
    }

    @CardAbility("card.diamond_golem.ability.launch", ChatColor.YELLOW)
    @Offensive(0.15, CardOperation.ADD, 0.025)
    private fun launch(event: EntityDamageByEntityEvent) {
        val target = event.entity as? Player ?: return
        val amplifier: Double = ((level / 20.0) + 1.25).coerceAtMost(10.0)

        target.velocity = entity.location.direction.multiply(amplifier)
    }

    @CardAbility("card.diamond_golem.ability.thorns", ChatColor.RED)
    @Defensive(0.07)
    private fun thorns(event: EntityDamageByEntityEvent) {
        val attacker = event.damager as? Player ?: return
        val dmg = statistics.attackDamage * 0.2

        attacker.damage(dmg, entity)
        attacker.lastDamageCause = EntityDamageByEntityEvent(attacker, entity, EntityDamageEvent.DamageCause.THORNS, dmg)
    }

}