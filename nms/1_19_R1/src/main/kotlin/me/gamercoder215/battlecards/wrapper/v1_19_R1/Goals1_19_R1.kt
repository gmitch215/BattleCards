package me.gamercoder215.battlecards.wrapper.v1_19_R1

import me.gamercoder215.battlecards.impl.cards.IBattleCard
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.target.TargetGoal
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.level.pathfinder.BlockPathTypes
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftCreature
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer
import org.bukkit.event.entity.EntityTargetEvent

class FollowCardOwner1_19_R1(
    private val creature: Mob,
    card: IBattleCard<*>
) : Goal() {

    companion object {
        const val STOP_DISTANCE = 4
    }

    private val player: ServerPlayer = (card.p as CraftPlayer).handle
    private var timeToRecalcPath: Int = 0
    private var oldWaterCost: Float = 0F

        override fun canUse(): Boolean = when {
        player.isSpectator || player.bukkitEntity.isFlying -> false
        creature.isLeashed || creature.distanceToSqr(player) < STOP_DISTANCE.times(STOP_DISTANCE) -> false
        creature.target != null -> false
        else -> true
    }

    override fun start() {
        timeToRecalcPath = 0
        oldWaterCost = creature.getPathfindingMalus(BlockPathTypes.WATER)
        creature.setPathfindingMalus(BlockPathTypes.WATER, 0.0F)
    }

    override fun stop() {
        creature.navigation.stop()
        creature.setPathfindingMalus(BlockPathTypes.WATER, oldWaterCost)
    }

    override fun tick() {
        creature.lookControl.setLookAt(player, 10.0F, creature.maxHeadXRot.toFloat())
        if (--timeToRecalcPath <= 0) {
            timeToRecalcPath = adjustedTickDelay(10)

            val x = creature.x - player.x
            val y = creature.y - player.y
            val z = creature.z - player.z
            val distance = x * x + y * y + z * z

            if (distance > STOP_DISTANCE.times(STOP_DISTANCE))
                creature.navigation.moveTo(player, 1.2)
            else {
                creature.navigation.stop()
                val sight = player.bukkitEntity.hasLineOfSight(creature.bukkitEntity)

                if (distance <= STOP_DISTANCE || sight) {
                    val dx = player.x - creature.x
                    val dz = player.z - creature.z
                    creature.navigation.moveTo(creature.x - dx, creature.y, creature.z - dz, 1.2)
                }
            }
        }
    }

}

class CardOwnerHurtTargetGoal1_19_R1(
    private val creature: PathfinderMob,
    card: IBattleCard<*>
) : TargetGoal(creature, true, true) {

    private val nms = (card.p as CraftPlayer).handle
    private var timestamp: Int = 0
    private var lastHurtBy = nms.lastHurtMob

    override fun canUse(): Boolean {
        lastHurtBy = nms.lastHurtMob
        return timestamp != nms.lastHurtMobTimestamp && canAttack(lastHurtBy, TargetingConditions.DEFAULT)
    }

    override fun start() {
        creature.setTarget(lastHurtBy, EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET, true)
        timestamp = nms.lastHurtMobTimestamp

        super.start()
    }

}

class CardOwnerHurtByTargetGoal1_19_R1(
    private val creature: PathfinderMob,
    card: IBattleCard<*>
) : TargetGoal(creature, true, true) {

    private val nms = (card.p as CraftPlayer).handle
    private var timestamp: Int = 0
    private var lastHurtBy = nms.lastHurtByPlayer ?: nms.lastHurtByMob

    override fun canUse(): Boolean {
        lastHurtBy = nms.lastHurtByPlayer ?: nms.lastHurtByMob
        return timestamp != nms.lastHurtByMobTimestamp && canAttack(lastHurtBy, TargetingConditions.DEFAULT)
    }

    override fun start() {
        creature.setTarget(lastHurtBy, EntityTargetEvent.TargetReason.TARGET_ATTACKED_OWNER, true)
        timestamp = nms.lastHurtByMobTimestamp

        super.start()
    }

}

internal class CardMasterHurtTargetGoal1_19_R1(
    private val creature: PathfinderMob,
    card: IBattleCard<*>
) : TargetGoal(creature, true, true) {

    private val nms = (card.entity as CraftCreature).handle
    private var timestamp: Int = 0
    private var lastHurtBy = nms.lastHurtMob

    override fun canUse(): Boolean {
        lastHurtBy = nms.lastHurtMob
        return timestamp != nms.lastHurtMobTimestamp && canAttack(lastHurtBy, TargetingConditions.DEFAULT)
    }

    override fun start() {
        creature.setTarget(lastHurtBy, EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET, true)
        timestamp = nms.lastHurtMobTimestamp

        super.start()
    }

}

internal class CardMasterHurtByTargetGoal1_19_R1(
    private val creature: PathfinderMob,
    card: IBattleCard<*>
) : TargetGoal(creature, true, true) {

    private val nms = (card.entity as CraftCreature).handle
    private var timestamp: Int = 0
    private var lastHurtBy = nms.lastHurtByPlayer ?: nms.lastHurtByMob

    override fun canUse(): Boolean {
        lastHurtBy = nms.lastHurtByPlayer ?: nms.lastHurtByMob
        return timestamp != nms.lastHurtByMobTimestamp && canAttack(lastHurtBy, TargetingConditions.DEFAULT)
    }

    override fun start() {
        creature.setTarget(lastHurtBy, EntityTargetEvent.TargetReason.TARGET_ATTACKED_OWNER, true)
        timestamp = nms.lastHurtByMobTimestamp

        super.start()
    }

}