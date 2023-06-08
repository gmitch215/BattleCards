package me.gamercoder215.battlecards.wrapper.v1_20_R1

import me.gamercoder215.battlecards.impl.cards.IBattleCard
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.target.TargetGoal
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.level.pathfinder.BlockPathTypes
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer
import org.bukkit.event.entity.EntityTargetEvent

internal class FollowCardOwner1_20_R1(
    private val creature: PathfinderMob,
    card: IBattleCard<*>
) : Goal() {

    companion object {
        const val STOP_DISTANCE = 3
    }

    private val player: ServerPlayer = (card.p as CraftPlayer).handle
    private var timeToRecalcPath: Int = 0
    private var oldWaterCost: Float = 0F

    override fun canUse(): Boolean = true

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
                creature.navigation.moveTo(player, 1.0)
            else {
                creature.navigation.stop()
                val sight = player.bukkitEntity.hasLineOfSight(creature.bukkitEntity)

                if (distance <= STOP_DISTANCE || sight) {
                    val dx = player.x - creature.x
                    val dz = player.z - creature.z
                    creature.navigation.moveTo(creature.x - dx, creature.y, creature.z - dz, 1.0)
                }
            }
        }
    }

}

internal class CardOwnerHurtTargetGoal1_20_R1(
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

internal class CardOwnerHurtByTargetGoal1_20_R1(
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