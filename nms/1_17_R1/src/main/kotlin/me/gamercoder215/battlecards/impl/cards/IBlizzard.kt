package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.util.cardByMinion
import me.gamercoder215.battlecards.util.sync
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.block.EntityBlockFormEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.entity.ProjectileHitEvent

@Type(BattleCardType.BLIZZARD)
@Attributes(50.0, 6.5, 60.0, 0.2, 100.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 3.42)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 4.38)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 2.26)
@AttributesModifier(CardAttribute.KNOCKBACK_RESISTANCE, CardOperation.ADD, 4.55)
@BlockAttachment(Material.BLUE_ICE, 0.0, -0.05, 0.0, local = false)
class IBlizzard(data: ICard) : IBattleCard<Snowman>(data) {

    override fun init() {
        super.init()

        entity.isDerp = true
    }

    @CardAbility("card.blizzard.ability.iceball", ChatColor.WHITE)
    @EventHandler
    private fun onShoot(event: ProjectileHitEvent) {
        val proj = event.entity as? Snowball ?: return
        if (proj.shooter != entity) return

        val target = event.hitEntity as? LivingEntity ?: return
        if (target == p || target.cardByMinion == this) return

        target.damage(statistics.attackDamage / 2.0, entity)
        target.freezeTicks += 20 * r.nextInt(3, 6)
    }

    @CardAbility("card.blizzard.ability.storm", ChatColor.DARK_AQUA)
    @Passive(20)
    @UnlockedAt(10)
    private fun storm() {
        if (entity.world.environment == World.Environment.NETHER) return

        val targets = entity.getNearbyEntities(3.5, 0.5, 3.5)
            .asSequence()
            .filterIsInstance<LivingEntity>()
            .filter { it !is ArmorStand }
            .filter { it.cardByMinion != this }
            .filter { if (BattleConfig.config.cardAttackPlayers) it !is Player else true }
            .filter { it.fireTicks <= 0 }
            .toList()

        targets.forEach {
            it.freezeTicks += 20 * r.nextInt(1, 5)
            it.world.spawnParticle(Particle.SNOWFLAKE, it.eyeLocation, r.nextInt(3, 5), 0.0, 0.0, 0.0, 0.01)
        }
    }

    @CardAbility("card.blizzard.ability.blue_flame", ChatColor.AQUA)
    @UserOffensive
    @UnlockedAt(15)
    private fun blueFlame(event: EntityDamageByEntityEvent) {
        val target = event.entity as? LivingEntity ?: return

        target.freezeTicks += 40
    }

    @Damage
    private fun damage(event: EntityDamageEvent) {
        if (event.cause == DamageCause.DRYOUT || event.cause == DamageCause.DROWNING)
            event.isCancelled = true
    }

    @EventHandler
    private fun onCreateTrail(event: EntityBlockFormEvent) {
        if (event.entity != entity) return
        if (event.newState.type != Material.SNOW) return

        event.isCancelled = true
        val oldData = event.block.blockData
        sync({
           event.block.blockData = oldData
        }, 1)
    }

}