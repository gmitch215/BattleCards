package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Zombie
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

@Type(BattleCardType.THUNDER_REVENANT)
@Attributes(400.0, 16.5, 100.0, 0.29, 50.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 3.25)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 0.32)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 4.55)
class IThunderRevenant(data: ICard) : IBattleCard<Zombie>(data) {

    @CardAbility("card.thunder_revenant.ability.electricity")
    @Offensive(0.2, CardOperation.ADD, 0.05, 0.75)
    private fun electricity(event: EntityDamageByEntityEvent) {
        val target = event.entity as? LivingEntity ?: return
        target.damage(statistics.attackDamage * 1.1, entity)
        target.fireTicks += 20 * 2
    }

    @CardAbility("card.thunder_revenant.ability.thunderbolt")
    @Offensive(0.25, CardOperation.ADD, 0.03)
    @UserOffensive(0.1, CardOperation.ADD, 0.02, 0.5)
    @UnlockedAt(5)
    private fun thunderbolt(event: EntityDamageByEntityEvent) {
        val target = event.entity as? LivingEntity ?: return

        target.world.strikeLightning(target.location)
        target.damage(statistics.attackDamage * 1.15, entity)
        target.fireTicks += 20 * 4
    }

    @CardAbility("card.thunder_revenant.ability.paralysis")
    @Offensive(0.4, CardOperation.ADD, 0.05)
    @UnlockedAt(15)
    private fun paralysis(event: EntityDamageByEntityEvent) {
        val target = event.entity as? LivingEntity ?: return

        target.damage(statistics.attackDamage * 0.3, entity)
        target.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 20 * 7, 9))
        target.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, 20 * 10, 4))
    }

}