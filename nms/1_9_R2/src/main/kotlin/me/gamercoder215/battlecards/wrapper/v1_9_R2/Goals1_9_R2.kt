package me.gamercoder215.battlecards.wrapper.v1_9_R2

import me.gamercoder215.battlecards.impl.cards.IBattleCard
import me.gamercoder215.battlecards.util.isMinion
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.r
import net.minecraft.server.v1_9_R2.*
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftCreature
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer
import org.bukkit.event.entity.EntityTargetEvent

class FollowCardOwner1_9_R2(
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
            creature.isLeashed || creature.h(player) < STOP_DISTANCE.times(STOP_DISTANCE) -> false
            creature.goalTarget != null -> false
            else -> true
        }

    override fun c() {
        timeToRecalcPath = 0
        oldWaterCost = creature.a(PathType.WATER)
        creature.a(PathType.WATER, 0.0F)
    }

    override fun d() {
        creature.navigation.o()
        creature.a(PathType.WATER, oldWaterCost)
    }

    override fun e() {
        creature.controllerLook.a(player, 10.0F, creature.N().toFloat())
        if (--timeToRecalcPath <= 0) {
            timeToRecalcPath = 10

            val x = creature.locX - player.locX
            val y = creature.locY - player.locY
            val z = creature.locZ - player.locZ
            val distance = x * x + y * y + z * z

            if (distance > STOP_DISTANCE.times(STOP_DISTANCE))
                creature.navigation.a(player, 1.2)
            else {
                creature.navigation.o()
                val sight = player.bukkitEntity.hasLineOfSight(creature.bukkitEntity)

                if (distance <= STOP_DISTANCE || sight) {
                    val dx = player.locX - creature.locX
                    val dz = player.locZ - creature.locZ
                    creature.navigation.a(creature.locX - dx, creature.locY, creature.locZ - dz, 1.2)
                }
            }
        }
    }

}

class CardOwnerHurtTargetGoal1_9_R2(
    private val creature: EntityCreature,
    card: IBattleCard<*>
) : PathfinderGoalTarget(creature, true, true) {

    private val nms = (card.p as CraftPlayer).handle
    private var timestamp: Int = 0
    private var lastHurt: EntityLiving? = nms.bJ()

    override fun a(): Boolean {
        lastHurt = nms.bJ()
        return timestamp != nms.bK() && a(lastHurt, true)
    }

    override fun c() {
        creature.setGoalTarget(lastHurt, EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET, true)
        timestamp = nms.bK()

        super.c()
    }

}

class CardOwnerHurtByTargetGoal1_9_R2(
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

internal class CardMasterHurtTargetGoal1_9_R2(
    private val creature: EntityCreature,
    card: IBattleCard<*>
) : PathfinderGoalTarget(creature, true, true) {

    private val nms = (card.entity as CraftCreature).handle
    private var timestamp: Int = 0
    private var lastHurt = nms.bJ()

    override fun a(): Boolean {
        lastHurt = nms.bJ()
        return timestamp != nms.bK() && a(lastHurt, true)
    }

    override fun c() {
        creature.setGoalTarget(lastHurt, EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET, true)
        timestamp = nms.bK()

        super.c()
    }

}

internal class CardMasterHurtByTargetGoal1_9_R2(
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

internal class CardNearestAttackableTargetGoal1_9_R2(entity: EntityCreature, private val card: IBattleCard<*>) : PathfinderGoalTarget(entity, false, true) {

    val mob: EntityCreature = (card.entity as CraftCreature).handle
    val randomInterval: Int = 10

    var target: EntityLiving? = null

    override fun a(): Boolean {
        if (r.nextInt(randomInterval) != 0) return false
        findTarget()

        return this.target != null
    }

    fun findTarget() {
        this.target = mob.world.a(EntityLiving::class.java, mob.boundingBox.grow(f(), f(), f()))
            .filterNotNull()
            .filter {
                val en = it.bukkitEntity
                en != card.entity && !en.isMinion(card) && en != card.p
            }
            .minBy { mob.h(it) }
    }

    override fun c() {
        mob.setGoalTarget(target,
            if ((target is EntityPlayer)) EntityTargetEvent.TargetReason.CLOSEST_PLAYER else EntityTargetEvent.TargetReason.CLOSEST_ENTITY,
            true
        )
        super.c()
    }

}