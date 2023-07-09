package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.util.BattleParticle
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Warden
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

@Type(BattleCardType.GRAND_WARDEN)
@Attributes(3000.0, 65.0, 45.75, 0.23, 100.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 20.0)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.MULTIPLY, 1.035)
class IGrandWarden(data: ICard) : IBattleCard<Warden>(data) {

    @Passive(1)
    private fun cosmetics() =
        circle(entity.eyeLocation, BattleParticle.CRIT_MAGIC, 20, 2.0)

    @CardAbility("card.grand_warden.ability.sight")
    @Passive(1)
    private fun removeDarkness() {
        if (p.hasPotionEffect(PotionEffectType.DARKNESS))
            p.removePotionEffect(PotionEffectType.DARKNESS)
    }

    @CardAbility("card.grand_warden.ability.sculk_defense")
    @UserDefensive
    private fun sculkDefense(event: EntityDamageByEntityEvent) {
        if (event.damager is Warden)
            event.damage /= 4.0

        event.damage *= (1 - (level * 0.005))
    }

    @CardAbility("card.grand_warden.ability.sculk_knockback")
    @Offensive(0.25, CardOperation.ADD, 0.01, 0.5)
    @UserOffensive(0.1, CardOperation.ADD, 0.02, 0.3)
    private fun sculkKnockback(event: EntityDamageByEntityEvent) {
        val entity = event.entity as? LivingEntity ?: return
        entity.velocity.add(location.direction.multiply(2 + (level * 0.04)))
    }

    @CardAbility("card.grand_warden.ability.sonic_pulse")
    @Offensive
    private fun sonicPulse(event: EntityDamageByEntityEvent) {
        if (event.cause != EntityDamageEvent.DamageCause.SONIC_BOOM) return
        val entity = event.entity as? LivingEntity ?: return

        entity.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, 20 * 4, level / 15, true))
    }

}