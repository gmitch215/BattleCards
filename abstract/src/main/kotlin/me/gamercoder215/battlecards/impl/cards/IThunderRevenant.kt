package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.util.isCard
import org.bukkit.ChatColor
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Zombie
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

@Type(BattleCardType.THUNDER_REVENANT)
@Attributes(400.0, 16.5, 100.0, 0.29, 50.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 3.275)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 0.32)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 4.55)
@Rideable
class IThunderRevenant(data: ICard) : IBattleCard<Zombie>(data) {

    override fun init() {
        super.init()

        entity.isBaby = false
    }
    
    @CardAbility("card.thunder_revenant.ability.heat_resistance", ChatColor.RED)
    @Damage
    private fun heatResistance(event: EntityDamageEvent) {
        if (event.cause == DamageCause.FIRE || event.cause == DamageCause.FIRE_TICK || event.cause == DamageCause.LIGHTNING)
            event.isCancelled = true
    }

    @CardAbility("card.thunder_revenant.ability.electricity", ChatColor.YELLOW)
    @Offensive(0.2, CardOperation.ADD, 0.05, 0.75)
    private fun electricity(event: EntityDamageByEntityEvent) {
        val target = event.entity as? LivingEntity ?: return
        event.damage += statistics.attackDamage * 1.1
        target.fireTicks += 20 * 2
    }

    @CardAbility("card.thunder_revenant.ability.thunderbolt")
    @Offensive(0.25, CardOperation.ADD, 0.03)
    @UserOffensive(0.1, CardOperation.ADD, 0.02, 0.5)
    @UnlockedAt(5)
    private fun thunderbolt(event: EntityDamageByEntityEvent) {
        val target = event.entity as? LivingEntity ?: return

        target.world.strikeLightning(target.location)
        event.damage += if (event.damager.isCard) statistics.attackDamage * 1.15 else r.nextDouble(5.0, 20.0)
        target.fireTicks += 20 * 4
    }

    @CardAbility("card.thunder_revenant.ability.paralysis", ChatColor.GOLD)
    @Offensive(0.05, CardOperation.ADD, 0.01, 0.25)
    @UnlockedAt(15)
    private fun paralysis(event: EntityDamageByEntityEvent) {
        val target = event.entity as? LivingEntity ?: return

        event.damage += statistics.attackDamage * 0.3
        target.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 20 * 4, 9))
        target.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, 20 * 12, 4))
    }

}