package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.util.card
import org.bukkit.ChatColor
import org.bukkit.World
import org.bukkit.entity.EnderCrystal
import org.bukkit.entity.Enderman
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent

@Type(BattleCardType.EYE_OF_ENDERMAN)
@Attributes(550.0, 13.5, 10.0, 0.4, 2.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 7.5)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.MULTIPLY, 1.015)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 2.55)
// TODO Block Attachments
class IEyeOfEnderman(data: ICard) : IBattleCard<Enderman>(data) {

    private lateinit var crystal: EnderCrystal
    private var crystalTarget: LivingEntity? = null

    override fun init() {
        super.init()

        crystal = entity.world.spawn(entity.eyeLocation, EnderCrystal::class.java).apply {
            isInvulnerable = true
            isShowingBottom = false
        }

        entity.passenger = crystal
    }

    @Passive(1)
    private fun beamTarget() {
        val radius = 2 + ((level - 1) * 0.25).coerceAtMost(8.0)
        val entity = entity.getNearbyEntities(radius, radius, radius)
            .filterIsInstance<LivingEntity>()
            .filter { it is Player || it.card != null}
            .minByOrNull { entity.location.distanceSquared(it.location) } ?: return

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