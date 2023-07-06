package me.gamercoder215.battlecards.wrapper.v1_13_R2

import me.gamercoder215.battlecards.impl.cards.IBattleCard
import net.minecraft.server.v1_13_R2.*
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftCreature
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer
import org.bukkit.event.entity.EntityTargetEvent

class FollowCardOwner1_13_R2(
    private val creature: EntityCreature,
    card: IBattleCard<*>
) : PathfinderGoal() {

    companion object {
        const val STOP_DISTANCE = 3
    }

    private val player: EntityPlayer = (card.p as CraftPlayer).handle
    private var timeToRecalcPath: Int = 0
    private var oldWaterCost: Float = 0F

    override fun a(): Boolean = true

    override fun c() {
        timeToRecalcPath = 0
        oldWaterCost = creature.a(PathType.WATER)
        creature.a(PathType.WATER, 0.0F)
    }

    override fun d() {
        creature.navigation.q()
        creature.a(PathType.WATER, oldWaterCost)
    }

    override fun e() {
        creature.controllerLook.a(player, 10.0F, creature.K().toFloat())
        if (--timeToRecalcPath <= 0) {
            timeToRecalcPath = 10

            val x = creature.locX - player.locX
            val y = creature.locY - player.locY
            val z = creature.locZ - player.locZ
            val distance = x * x + y * y + z * z

            if (distance > STOP_DISTANCE.times(STOP_DISTANCE))
                creature.navigation.a(player, 1.0)
            else {
                creature.navigation.q()
                val sight = player.bukkitEntity.hasLineOfSight(creature.bukkitEntity)

                if (distance <= STOP_DISTANCE || sight) {
                    val dx = player.locX - creature.locX
                    val dz = player.locZ - creature.locZ
                    creature.navigation.a(creature.locX - dx, creature.locY, creature.locZ - dz, 1.0)
                }
            }
        }
    }

}

class CardOwnerHurtTargetGoal1_13_R2(
    private val creature: EntityCreature,
    card: IBattleCard<*>
) : PathfinderGoalTarget(creature, true, true) {

    private val nms = (card.p as CraftPlayer).handle
    private var timestamp: Int = 0
    private var lastHurt: EntityLiving? = nms.ch()

    override fun a(): Boolean {
        lastHurt = nms.ch()
        return timestamp != nms.ci() && a(lastHurt, true)
    }

    override fun c() {
        creature.setGoalTarget(lastHurt, EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET, true)
        timestamp = nms.ci()

        super.c()
    }

}

class CardOwnerHurtByTargetGoal1_13_R2(
    private val creature: EntityCreature,
    card: IBattleCard<*>
) : PathfinderGoalTarget(creature, true, true) {

    private val nms = (card.p as CraftPlayer).handle
    private var timestamp: Int = 0
    private var lastHurtBy = nms.lastDamager

    override fun a(): Boolean {
        lastHurtBy = nms.lastDamager
        return timestamp != nms.hurtTimestamp && a(lastHurtBy, true)
    }

    override fun c() {
        creature.setGoalTarget(lastHurtBy, EntityTargetEvent.TargetReason.TARGET_ATTACKED_OWNER, true)
        timestamp = nms.hurtTimestamp

        super.c()
    }

}

internal class CardMasterHurtTargetGoal1_13_R2(
    private val creature: EntityCreature,
    card: IBattleCard<*>
) : PathfinderGoalTarget(creature, true, true) {

    private val nms = (card.entity as CraftCreature).handle
    private var timestamp: Int = 0
    private var lastHurt = nms.ch()

    override fun a(): Boolean {
        lastHurt = nms.ch()
        return timestamp != nms.ci() && a(lastHurt, true)
    }

    override fun c() {
        creature.setGoalTarget(lastHurt, EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET, true)
        timestamp = nms.ci()

        super.c()
    }

}

internal class CardMasterHurtByTargetGoal1_13_R2(
    private val creature: EntityCreature,
    card: IBattleCard<*>
) : PathfinderGoalTarget(creature, true, true) {

    private val nms = (card.entity as CraftCreature).handle
    private var timestamp: Int = 0
    private var lastHurtBy = nms.lastDamager

    override fun a(): Boolean {
        lastHurtBy = nms.lastDamager
        return timestamp != nms.hurtTimestamp && a(lastHurtBy, true)
    }

    override fun c() {
        creature.setGoalTarget(lastHurtBy, EntityTargetEvent.TargetReason.TARGET_ATTACKED_OWNER, true)
        timestamp = nms.hurtTimestamp

        super.c()
    }

}