package me.gamercoder215.battlecards.wrapper.v1_17_R1

import me.gamercoder215.battlecards.impl.cards.IBattleCard
import me.gamercoder215.battlecards.util.isMinion
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.r
import net.minecraft.server.level.EntityPlayer
import net.minecraft.world.entity.EntityCreature
import net.minecraft.world.entity.EntityInsentient
import net.minecraft.world.entity.EntityLiving
import net.minecraft.world.entity.ai.goal.PathfinderGoal
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalTarget
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition
import net.minecraft.world.level.pathfinder.PathType
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftCreature
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer
import org.bukkit.event.entity.EntityTargetEvent

class FollowCardOwner1_17_R1(
    private val creature: EntityInsentient,
    card: IBattleCard<*>
) : PathfinderGoal() {

    companion object {
        const val STOP_DISTANCE = 4
    }

    private val player: EntityPlayer = (card.p as CraftPlayer).handle
    private var timeToRecalcPath: Int = 0
    private var oldWaterCost: Float = 0F

    override fun a(): Boolean = when {
        player.isSpectator || player.bukkitEntity.isFlying -> false
        creature.isLeashed || creature.f(player) < STOP_DISTANCE.times(STOP_DISTANCE) -> false
        creature.goalTarget != null -> false
        else -> true
    }

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
                creature.navigation.a(player, 1.2)
            else {
                creature.navigation.o()
                val sight = player.bukkitEntity.hasLineOfSight(creature.bukkitEntity)

                if (distance <= STOP_DISTANCE || sight) {
                    val dx = player.locX() - creature.locX()
                    val dz = player.locZ() - creature.locZ()
                    creature.navigation.a(creature.locX() - dx, creature.locY(), creature.locZ() - dz, 1.2)
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

internal class CardMasterHurtTargetGoal1_17_R1(
    private val creature: EntityCreature,
    card: IBattleCard<*>
) : PathfinderGoalTarget(creature, true, true) {

    private val nms = (card.entity as CraftCreature).handle
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

internal class CardMasterHurtByTargetGoal1_17_R1(
    private val creature: EntityCreature,
    card: IBattleCard<*>
) : PathfinderGoalTarget(creature, true, true) {

    private val nms = (card.entity as CraftCreature).handle
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

internal class CardNearestAttackableTargetGoal1_17_R1(entity: EntityCreature, private val card: IBattleCard<*>) : PathfinderGoalTarget(entity, false, true) {

    val mob: EntityCreature = (card.entity as CraftCreature).handle
    val randomInterval: Int = 10
    val targetConditions: PathfinderTargetCondition = PathfinderTargetCondition.a().a(k())

    var target: EntityLiving? = null

    override fun a(): Boolean {
        if (r.nextInt(randomInterval) != 0) return false
        findTarget()

        return this.target != null
    }

    fun findTarget() {
        this.target = mob.t.a(
            mob.t.a(EntityLiving::class.java, mob.boundingBox.a(k())) {
                val en = it.bukkitEntity
                en != card.entity && !en.isMinion(card) && en != card.p
            },
            targetConditions,
            mob,
            mob.locX(), mob.headY, mob.locZ()
        )
    }

    override fun c() {
        mob.setGoalTarget(target,
            if ((target is EntityPlayer)) EntityTargetEvent.TargetReason.CLOSEST_PLAYER else EntityTargetEvent.TargetReason.CLOSEST_ENTITY,
            true
        )
        super.c()
    }

}