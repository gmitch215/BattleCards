package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.ChatColor
import org.bukkit.entity.Enderman
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

@Type(BattleCardType.WITHERMAN)
@Attributes(650.0, 16.5, 25.5, 0.25, 0.15)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 7.6)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 5.75)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 4.5)
@AttributesModifier(CardAttribute.SPEED, CardOperation.MULTIPLY, 1.015)
class IWitherman(data: ICard) : IBattleCard<Enderman>(data) {

    @CardAbility("card.witherman.ability.wither", ChatColor.DARK_GRAY)
    @Offensive(0.5, CardOperation.ADD, 0.05)
    @UserOffensive(0.1, CardOperation.ADD, 0.025, 0.5)
    private fun wither(event: EntityDamageByEntityEvent) {
        val target = event.entity as? LivingEntity ?: return

        target.addPotionEffect(PotionEffect(PotionEffectType.WITHER, 100, 0))
    }

    @CardAbility("card.witherman.ability.wither_immune", ChatColor.AQUA)
    @UserDefensive
    private fun witherImmune(event: EntityDamageEvent) {
        if (event.cause == EntityDamageEvent.DamageCause.WITHER)
            event.isCancelled = true
    }


}