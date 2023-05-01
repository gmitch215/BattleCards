package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.KingWither
import me.gamercoder215.battlecards.api.card.Rarity
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.impl.Attributes
import me.gamercoder215.battlecards.impl.AttributesModifier
import me.gamercoder215.battlecards.impl.CardDetails
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.entity.Wither
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.math.floor
import kotlin.math.roundToInt

@CardDetails("king_wither", "card.king_wither", "card.king_wither.desc", Rarity.ULTIMATE)

@Attributes(5000.0, 55.5, 100.0, 0.2, 150.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 125.0)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 15.0)
@AttributesModifier(CardAttribute.KNOCKBACK_RESISTANCE, CardOperation.MULTIPLY, 1.1)

@BlockAttachment(Material.BEDROCK, 0.0, 2.5, 0.0, true)
class IKingWither : IBattleCard<Wither>(), KingWither {

    override fun init() {
        super.init()
        en.bossBar?.isVisible = false
    }

    @CardAbility("card.king_wither.ability.poison_thorns", "card.king_wither.ability.poison_thorns.desc")
    @Defensive(0.2, CardOperation.ADD, 0.01)
    private fun posionThorns(event: EntityDamageByEntityEvent) {
        val attacker = event.damager as? Player ?: return
        val duration = getStatistics().getAttackDamage() * 1.5
        val amp = floor(getLevel() / 6.0).toInt()

        attacker.addPotionEffect(PotionEffect(PotionEffectType.WITHER, duration.roundToInt(), amp, false))
    }

    @UnlockedAt(5)
    @CardAbility("card.king_wither.ability.lightning_thorns", "card.king_wither.ability.lightning_thorns.desc")
    @Defensive(0.25, CardOperation.ADD, 0.02)
    private fun lightningThorns(event: EntityDamageByEntityEvent) {
        val attacker = event.damager as? Player ?: return

        attacker.world.strikeLightning(attacker.location)
    }

}