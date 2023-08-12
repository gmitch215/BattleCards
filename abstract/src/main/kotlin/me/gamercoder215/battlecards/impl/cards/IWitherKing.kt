package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.util.BattleSound
import me.gamercoder215.battlecards.util.isCard
import me.gamercoder215.battlecards.util.isMinion
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.w
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.*
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.math.floor

@Type(BattleCardType.WITHER_KING)
@Attributes(2000.0, 35.5, 85.5, 0.15, 150.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 6.32, 6500.0)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 4.1)
@AttributesModifier(CardAttribute.KNOCKBACK_RESISTANCE, CardOperation.MULTIPLY, 1.1, 2500.0)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 2.67)

@BlockAttachment(Material.BEDROCK, 0.0, 2.5, 0.0, small = true, local = false)
class IWitherKing(data: ICard) : IBattleCard<Wither>(data) {

    override fun init() {
        super.init()
        w.setBossBarVisibility(entity, false)

        p.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, Int.MAX_VALUE, level / 3, true, false))
    }

    override fun uninit() {
        p.removePotionEffect(PotionEffectType.INCREASE_DAMAGE)
        super.uninit()
    }

    @CardAbility("card.wither_king.ability.poison_thorns", ChatColor.DARK_GREEN)
    @Defensive(0.2, CardOperation.ADD, 0.01)
    private fun posionThorns(event: EntityDamageByEntityEvent) {
        val attacker = event.damager as? Player ?: return
        val duration = (5 + (level - 1)) * 20
        val amp = floor(level / 6.0).toInt().coerceAtMost(4)

        attacker.addPotionEffect(PotionEffect(PotionEffectType.WITHER, duration.coerceAtMost(20 * 30), amp, false))
    }

    @UnlockedAt(5)
    @CardAbility("card.wither_king.ability.lightning_thorns", ChatColor.AQUA)
    @Defensive(0.25, CardOperation.ADD, 0.02)
    private fun lightningThorns(event: EntityDamageByEntityEvent) {
        val attacker = event.damager as? Player ?: return

        attacker.world.strikeLightning(attacker.location)
    }


    @CardAbility("card.wither_king.ability.user.wither_offensive", ChatColor.GRAY)
    @UserOffensive(0.4, CardOperation.ADD, 0.02)
    private fun witherOffensive(event: EntityDamageByEntityEvent) {
        val target = event.entity as? LivingEntity ?: return

        target.addPotionEffect(PotionEffect(PotionEffectType.WITHER, 60, 1, false))
    }

    private companion object {
        @JvmStatic
        private val types: List<EntityType> = listOf<Any>(
            EntityType.BLAZE,
            EntityType.SKELETON,
            EntityType.GHAST,
            EntityType.ENDERMAN,
            EntityType.MAGMA_CUBE,

            "piglin_brute",
            "piglin",
            "zombified_piglin",
            "hoglin",
            "zoglin",
            "wither_skeleton"
        ).mapNotNull {
            when (it) {
                is EntityType -> it
                is String -> try { EntityType.valueOf(it.uppercase()) } catch (e: IllegalArgumentException) { null }
                else -> null
            }
        }
    }

    @CardAbility("card.wither_king.ability.decree", ChatColor.DARK_AQUA)
    @Passive(300, CardOperation.SUBTRACT, 2, min = 100)
    @UnlockedAt(30)
    private fun decree() {
        val distance = (25.0 + (level - 30) * 2.0).coerceAtMost(60.0)
        entity.getNearbyEntities(distance, distance, distance)
            .filterIsInstance<Creature>()
            .filter { !it.isCard && !it.isMinion }
            .filter { it.type in types }
            .forEach {
                it.target = entity.target
            }

        entity.world.playSound(entity.location, BattleSound.ENTITY_WITHER_AMBIENT.find(), 5F, 0.75F)
    }

    @CardAbility("card.wither_king.ability.decay", ChatColor.DARK_GRAY)
    @UserDefensive(0.6, CardOperation.ADD, 0.035)
    @Defensive
    @UnlockedAt(15)
    private fun decay(event: EntityDamageByEntityEvent) {
        val target = event.damager as? LivingEntity ?: return

        target.addPotionEffect(PotionEffect(PotionEffectType.WITHER, 120, 1, false))
    }

}