package me.gamercoder215.battlecards.api.card

import org.bukkit.Material
import org.bukkit.entity.IronGolem
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

/**
 * Represents an [IronGolem] BattleCard
 */
@Suppress("deprecation")
interface GolemCard : BattleCard<IronGolem> {

    override fun getEntityClass(): Class<IronGolem> = IronGolem::class.java

    @CardDetails("golem", "card.golem", "card.golem.description", Rarity.RARE)

    @Attributes(50.0, 5.3, 1.0, 5.5, 0.3, 1.0)
    @AttributesModifier(CardAttribute.MAX_HEALTH, CardAttributeOperation.ADD)
    @AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardAttributeOperation.ADD)
    @AttributesModifier(CardAttribute.ATTACK_KNOCKBACK, CardAttributeOperation.ADD, 0.25)
    @AttributesModifier(CardAttribute.DEFENSE, CardAttributeOperation.ADD)

    @BlockAttachment(Material.DIAMOND_BLOCK, 0.0, 0.0, 0.2)
    private fun init() {
        getEntity()!!.isPlayerCreated = true
    }

    @CardAbility("card.golem.ability.thorns", "card.golem.ability.thorns.description")
    @Defensive(0.1)
    private fun thorns(event: EntityDamageEvent) {
        val attacker = event.entity as? Player ?: return
        val dmg = getStatistics().getAttackDamage() * 0.2

        attacker.damage(dmg, getEntity())
        attacker.lastDamageCause = EntityDamageByEntityEvent(attacker, getEntity(), EntityDamageEvent.DamageCause.THORNS, dmg)
    }

}