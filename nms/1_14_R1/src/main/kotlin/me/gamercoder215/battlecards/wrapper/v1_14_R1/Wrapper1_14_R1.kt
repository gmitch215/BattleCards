package me.gamercoder215.battlecards.wrapper.v1_14_R1

import me.gamercoder215.battlecards.impl.CardAttribute
import me.gamercoder215.battlecards.impl.cards.IBattleCard
import me.gamercoder215.battlecards.util.BattleParticle
import me.gamercoder215.battlecards.wrapper.BattleInventory
import me.gamercoder215.battlecards.wrapper.NBTWrapper
import me.gamercoder215.battlecards.wrapper.Wrapper
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import net.minecraft.server.v1_14_R1.*
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftCreature
import org.bukkit.entity.Creature
import org.bukkit.entity.Player
import org.bukkit.entity.Wither

@Suppress("unchecked_cast", "KotlinConstantConditions")
internal class Wrapper1_14_R1 : Wrapper {

    override fun sendActionbar(player: Player, component: BaseComponent) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component)
    }

    override fun sendActionbar(player: Player, message: String) {
        sendActionbar(player, TextComponent(message))
    }

    override fun setBossBarVisibility(boss: Wither, visible: Boolean) {
        boss.bossBar?.isVisible = visible
    }

    fun toNMS(attribute: CardAttribute): AttributeBase {
        return when (attribute) {
            CardAttribute.MAX_HEALTH -> GenericAttributes.MAX_HEALTH
            CardAttribute.ATTACK_DAMAGE -> GenericAttributes.ATTACK_DAMAGE
            CardAttribute.KNOCKBACK_RESISTANCE -> GenericAttributes.KNOCKBACK_RESISTANCE
            CardAttribute.SPEED -> GenericAttributes.MOVEMENT_SPEED
            CardAttribute.DEFENSE -> GenericAttributes.ARMOR
        } as AttributeBase
    }

    override fun loadProperties(en: Creature, card: IBattleCard<*>) {
        val nms = (en as CraftCreature).handle

        for (entry in card.statistics.attributes) {
            val attribute = toNMS(entry.key)
            val value = entry.value

            var handle = nms.getAttributeInstance(attribute)
            if (handle == null) {
                val attributesF = AttributeMapBase::class.java.getDeclaredField("b")
                attributesF.isAccessible = true
                val attributes = attributesF.get(nms) as MutableMap<String, AttributeInstance>

                handle = AttributeModifiable(nms.attributeMap, attribute)
                attributes[attribute.name] = handle
            }

            handle.value = value
        }

        removeGoals(nms.goalSelector, nms.targetSelector)
        nms.goalSelector.a(2, FollowCardOwner1_14_R1(nms, card))

        nms.targetSelector.a(1, CardOwnerHurtByTargetGoal1_14_R1(nms, card))
        nms.targetSelector.a(2, CardOwnerHurtTargetGoal1_14_R1(nms, card))
        nms.targetSelector.a(3, PathfinderGoalHurtByTarget(nms))

        nms.addScoreboardTag("battlecards")
    }

    override fun <T : Creature> spawnMinion(clazz: Class<T>, ownerCard: IBattleCard<*>): T {
        val card = ownerCard.entity
        val en = card.world.spawn(card.location, clazz)

        en.isCustomNameVisible = true
        en.customName = "${ownerCard.rarity.color}${ownerCard.name}'s Minion"

        val equipment = en.equipment!!
        equipment.itemInMainHandDropChance = 0F
        equipment.itemInOffHandDropChance = 0F
        equipment.helmetDropChance = 0F
        equipment.chestplateDropChance = 0F
        equipment.leggingsDropChance = 0F
        equipment.bootsDropChance = 0F

        en.target = card.target

        val nms = (en as CraftCreature).handle

        removeGoals(nms.goalSelector, nms.targetSelector)
        nms.goalSelector.a(2, FollowCardOwner1_14_R1(nms, ownerCard))

        nms.targetSelector.a(1, CardMasterHurtByTargetGoal1_14_R1(nms, ownerCard))
        nms.targetSelector.a(2, CardMasterHurtTargetGoal1_14_R1(nms, ownerCard))
        nms.targetSelector.a(3, CardOwnerHurtByTargetGoal1_14_R1(nms, ownerCard))
        nms.targetSelector.a(4, CardOwnerHurtTargetGoal1_14_R1(nms, ownerCard))
        nms.targetSelector.a(5, PathfinderGoalHurtByTarget(nms))

        ownerCard.minions.add(en)
        return en
    }

    private fun removeGoals(goalSelector: PathfinderGoalSelector, targetSelector: PathfinderGoalSelector) {
        val field = PathfinderGoalSelector::class.java.getDeclaredField("d").apply { isAccessible = true }
        (field.get(goalSelector) as Set<PathfinderGoalWrapped>).map { it.j() }.filter {
            it is PathfinderGoalAvoidTarget<*> || it is PathfinderGoalRestrictSun || it is PathfinderGoalFleeSun || it is PathfinderGoalBeg || it is PathfinderGoalBreed
        }.forEach { goalSelector.a(it) }

        (field.get(targetSelector) as Set<PathfinderGoalWrapped>).map { it.j() }.filter {
            it is PathfinderGoalNearestAttackableTarget<*> || it is PathfinderGoalNearestAttackableTargetWitch<*> || it is PathfinderGoalNearestHealableRaider<*> || it is PathfinderGoalDefendVillage
        }.forEach { targetSelector.a(it) }
    }

    override fun getNBTWrapper(item: org.bukkit.inventory.ItemStack): NBTWrapper {
        return NBTWrapper1_14_R1(item)
    }

    override fun isCard(en: Creature): Boolean {
        return (en as CraftCreature).handle.scoreboardTags.contains("battlecards")
    }

    override fun createInventory(id: String, name: String, size: Int): BattleInventory {
        return BattleInventory1_14_R1(id, name, size)
    }

    override fun spawnParticle(
        particle: BattleParticle, location: Location, count: Int,
        dX: Double, dY: Double, dZ: Double,
        speed: Double, force: Boolean
    ) {
        if (location.world == null) return
        location.world!!.spawnParticle(Particle.valueOf(particle.name.uppercase()), location, count, dX, dY, dZ, speed, force)
    }

}