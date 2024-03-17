package me.gamercoder215.battlecards.wrapper.v1_8_R3

import me.gamercoder215.battlecards.impl.cards.IBattleCard
import me.gamercoder215.battlecards.util.isMinion
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.r
import net.minecraft.server.v1_8_R3.*
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftCreature
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.event.entity.EntityTargetEvent

class FollowCardOwner1_8_R3(
    private val creature: EntityInsentient,
    card: IBattleCard<*>
) : PathfinderGoal() {

    companion object {
        const val STOP_DISTANCE = 4
    }

    private val player: EntityPlayer = (card.p as CraftPlayer).handle
    private var timeToRecalcPath: Int = 0

        override fun a(): Boolean = when {
            player.isSpectator || player.bukkitEntity.isFlying -> false
            creature.h(player) < STOP_DISTANCE.times(STOP_DISTANCE) -> false
            creature.goalTarget != null -> false
            else -> true
        }

    override fun c() {
        timeToRecalcPath = 0
    }

    override fun d() {
        creature.navigation.n()
    }

    override fun e() {
        creature.controllerLook.a(player, 10.0F, creature.bQ().toFloat())
        if (--timeToRecalcPath <= 0) {
            timeToRecalcPath = 10

            val x = creature.locX - player.locX
            val y = creature.locY - player.locY
            val z = creature.locZ - player.locZ
            val distance = x * x + y * y + z * z

            if (distance > STOP_DISTANCE.times(STOP_DISTANCE))
                creature.navigation.a(player, 1.2)
            else {
                creature.navigation.n()
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

class CardOwnerHurtTargetGoal1_8_R3(
    private val creature: EntityCreature,
    card: IBattleCard<*>
) : PathfinderGoalTarget(creature, true, true) {

    private val nms = (card.p as CraftPlayer).handle
    private var timestamp: Int = 0
    private var lastHurt: EntityLiving? = nms.bf()

    override fun a(): Boolean {
        lastHurt = nms.bf()
        return timestamp != nms.bg() && a(lastHurt, true)
    }

    override fun c() {
        creature.setGoalTarget(lastHurt, EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET, true)
        timestamp = nms.bg()

        super.c()
    }

}

class CardOwnerHurtByTargetGoal1_8_R3(
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

internal class CardMasterHurtTargetGoal1_8_R3(
    private val creature: EntityCreature,
    card: IBattleCard<*>
) : PathfinderGoalTarget(creature, true, true) {

    private val nms = (card.entity as CraftCreature).handle
    private var timestamp: Int = 0
    private var lastHurt = nms.bf()

    override fun a(): Boolean {
        lastHurt = nms.bf()
        return timestamp != nms.bg() && a(lastHurt, true)
    }

    override fun c() {
        creature.setGoalTarget(lastHurt, EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET, true)
        timestamp = nms.bg()

        super.c()
    }

}

internal class CardMasterHurtByTargetGoal1_8_R3(
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

internal class CardNearestAttackableTargetGoal1_8_R3(entity: EntityCreature, private val card: IBattleCard<*>) : PathfinderGoalTarget(entity, false, true) {

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