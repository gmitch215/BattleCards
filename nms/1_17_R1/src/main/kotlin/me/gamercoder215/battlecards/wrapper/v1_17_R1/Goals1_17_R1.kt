package me.gamercoder215.battlecards.wrapper.v1_17_R1

import me.gamercoder215.battlecards.impl.cards.IBattleCard
import net.minecraft.server.level.EntityPlayer
import net.minecraft.world.entity.EntityCreature
import net.minecraft.world.entity.ai.goal.PathfinderGoal
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalTarget
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition
import net.minecraft.world.level.pathfinder.PathType
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer
import org.bukkit.event.entity.EntityTargetEvent

class FollowCardOwner1_17_R1(
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
        oldWaterCost = creature.a(PathType.i)
        creature.a(PathType.i, 0.0F)
    }

    override fun d() {
        creature.navigation.o()
        creature.a(PathType.i, oldWaterCost)
    }

    override fun e() {
        creature.controllerLook.a(player, 10.0F, creature.eZ().toFloat())
        if (--timeToRecalcPath <= 0) {
            timeToRecalcPath = 10

            val x = creature.locX() - player.locX()
            val y = creature.locY() - player.locY()
            val z = creature.locZ() - player.locZ()
            val distance = x * x + y * y + z * z

            if (distance > STOP_DISTANCE.times(STOP_DISTANCE))
                creature.navigation.a(player, 1.0)
            else {
                creature.navigation.o()
                val sight = player.bukkitEntity.hasLineOfSight(creature.bukkitEntity)

                if (distance <= STOP_DISTANCE || sight) {
                    val dx = player.locX() - creature.locX()
                    val dz = player.locZ() - creature.locZ()
                    creature.navigation.a(creature.locX() - dx, creature.locY(), creature.locZ() - dz, 1.0)
                }
            }
        }
    }

}

class CardOwnerHurtTargetGoal1_17_R1(
    private val creature: EntityCreature,
    card: IBattleCard<*>
) : PathfinderGoalTarget(creature, true, true) {

    private val nms = (card.p as CraftPlayer).handle
    private var timestamp: Int = 0
    private var lastHurt = nms.dI()

    override fun a(): Boolean {
        lastHurt = nms.dI()
        return timestamp != nms.dJ() && a(lastHurt, PathfinderTargetCondition.a)
    }

    override fun c() {
        creature.setGoalTarget(lastHurt, EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET, true)
        timestamp = nms.dJ()

        super.c()
    }

}

class CardOwnerHurtByTargetGoal1_17_R1(
    private val creature: EntityCreature,
    card: IBattleCard<*>
) : PathfinderGoalTarget(creature, true, true) {

    private val nms = (card.p as CraftPlayer).handle
    private var timestamp: Int = 0
    private var lastHurtBy = nms.bc ?: nms.lastDamager

    override fun a(): Boolean {
        lastHurtBy = nms.bc ?: nms.lastDamager
        return timestamp != nms.dH() && a(lastHurtBy, PathfinderTargetCondition.a)
    }

    override fun c() {
        creature.setGoalTarget(lastHurtBy, EntityTargetEvent.TargetReason.TARGET_ATTACKED_OWNER, true)
        timestamp = nms.dH()

        super.c()
    }

}