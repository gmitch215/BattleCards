package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.w
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Wither
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.math.floor
import kotlin.math.roundToInt

@Type(BattleCardType.WITHER_KING)
@Attributes(3500.0, 55.5, 100.0, 0.15, 150.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 25.5)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 9.6)
@AttributesModifier(CardAttribute.KNOCKBACK_RESISTANCE, CardOperation.MULTIPLY, 1.1)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 7.75)

@BlockAttachment(Material.BEDROCK, 0.0, 2.5, 0.0, true)
class IKingWither(data: ICard) : IBattleCard<Wither>(data) {

    override fun init() {
        super.init()
        w.setBossBarVisibility(entity, false)

        p.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, Int.MAX_VALUE, level / 3, true, false))
    }

    override fun uninit() {
        super.uninit()
        p.removePotionEffect(PotionEffectType.INCREASE_DAMAGE)
    }

    @CardAbility("card.ability.king_wither.poison_thorns", ChatColor.DARK_GREEN)
    @Defensive(0.2, CardOperation.ADD, 0.01)
    private fun posionThorns(event: EntityDamageByEntityEvent) {
        val attacker = event.damager as? Player ?: return
        val duration = statistics.attackDamage * 1.5
        val amp = floor(level / 6.0).toInt()

        attacker.addPotionEffect(PotionEffect(PotionEffectType.WITHER, duration.roundToInt(), amp, false))
    }

    @UnlockedAt(5)
    @CardAbility("card.king_wither.ability.lightning_thorns", ChatColor.AQUA)
    @Defensive(0.25, CardOperation.ADD, 0.02)
    private fun lightningThorns(event: EntityDamageByEntityEvent) {
        val attacker = event.damager as? Player ?: return

        attacker.world.strikeLightning(attacker.location)
    }


    @CardAbility("card.king_wither.ability.user.wither_offensive", ChatColor.GRAY)
    @UserOffensive(0.4, CardOperation.ADD, 0.02)
    private fun witherOffensive(event: EntityDamageByEntityEvent) {
        val target = event.entity as? LivingEntity ?: return

        target.addPotionEffect(PotionEffect(PotionEffectType.WITHER, 60, 1, false))
    }

}