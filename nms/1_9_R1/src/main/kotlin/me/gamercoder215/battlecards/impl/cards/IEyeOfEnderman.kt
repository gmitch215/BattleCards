package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.util.attackable
import me.gamercoder215.battlecards.util.card
import org.bukkit.ChatColor
import org.bukkit.World
import org.bukkit.entity.EnderCrystal
import org.bukkit.entity.Enderman
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.metadata.FixedMetadataValue

@Type(BattleCardType.EYE_OF_ENDERMAN)
@Attributes(550.0, 13.5, 10.0, 0.3, 2.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 6.5)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.MULTIPLY, 1.015, 275.0)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 2.575)
class IEyeOfEnderman(data: ICard) : IBattleCard<Enderman>(data) {

    private lateinit var crystal: EnderCrystal
    private var crystalTarget: LivingEntity? = null

    override fun init() {
        super.init()

        crystal = world.spawn(entity.eyeLocation, EnderCrystal::class.java).apply {
            isInvulnerable = true
            isShowingBottom = false
            setMetadata("battlecards:nointeract", FixedMetadataValue(BattleConfig.plugin, true))
        }

        entity.passenger = crystal
    }

    override fun uninit() {
        crystal.remove()
        super.uninit()
    }

    @Passive(1)
    private fun beamTarget() {
        if (crystalTarget?.isDead == true) {
            crystal.beamTarget = null
            crystalTarget = null
            return
        }

        val radius = 2 + ((level - 1) * 0.25).coerceAtMost(8.0)
        val entity = entity.target ?: entity.getNearbyEntities(radius, radius, radius)
            .filterIsInstance<LivingEntity>()
            .filter { (it is Player && BattleConfig.config.cardAttackPlayers && it.attackable) || it.card != null }
            .filter { it != p && it != entity && it.card?.p != p }
            .minByOrNull { location.distanceSquared(it.location) } ?: return

        crystal.beamTarget = entity.location
        crystalTarget = entity
    }

    @CardAbility("card.eye_of_enderman.ability.beam", ChatColor.AQUA)
    @Passive(15)
    private fun beam() {
        val target0 = crystalTarget ?: entity.target ?: return
        val damage = 1.0 + (level / 10) * 0.2
        target0.damage(damage, entity)
    }

    @CardAbility("card.eye_of_enderman.ability.end_aspect", ChatColor.DARK_PURPLE)
    @UserOffensive
    private fun endAspect(event: EntityDamageByEntityEvent) {
        if (p.world.environment != World.Environment.THE_END) return
        event.damage *= 2.0
    }

}