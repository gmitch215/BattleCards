package me.gamercoder215.battlecards.wrapper.v1_11_R1

import me.gamercoder215.battlecards.impl.CardAttribute
import me.gamercoder215.battlecards.impl.cards.IBattleCard
import me.gamercoder215.battlecards.util.BattleParticle
import me.gamercoder215.battlecards.util.CardAttackType
import me.gamercoder215.battlecards.wrapper.BattleInventory
import me.gamercoder215.battlecards.wrapper.NBTWrapper
import me.gamercoder215.battlecards.wrapper.Wrapper
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import net.minecraft.server.v1_11_R1.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftCreature
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftWither
import org.bukkit.entity.Creature
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Wither

@Suppress("unchecked_cast")
internal class Wrapper1_11_R1 : Wrapper {

    override fun getCommandVersion(): Int = 1

    override fun sendActionbar(player: Player, message: String) {
        sendActionbar(player, TextComponent(message))
    }

    override fun sendActionbar(player: Player, component: BaseComponent) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component)
    }

    override fun setBossBarVisibility(boss: Wither, visible: Boolean) {
        val nms = (boss as CraftWither).handle

        val bossBarF = EntityWither::class.java.getDeclaredField("bF")
        bossBarF.isAccessible = true

        val bossBar: BossBattleServer = bossBarF.get(nms) as BossBattleServer
        bossBar.setVisible(visible)
    }

    fun toNMS(attribute: CardAttribute): AttributeBase {
        return when (attribute) {
            CardAttribute.MAX_HEALTH -> GenericAttributes.maxHealth
            CardAttribute.ATTACK_DAMAGE -> GenericAttributes.ATTACK_DAMAGE
            CardAttribute.KNOCKBACK_RESISTANCE -> GenericAttributes.c
            CardAttribute.SPEED -> GenericAttributes.MOVEMENT_SPEED
            CardAttribute.DEFENSE -> GenericAttributes.h
            CardAttribute.FOLLOW_RANGE -> GenericAttributes.FOLLOW_RANGE
        } as AttributeBase
    }

    override fun loadProperties(en: Creature, card: IBattleCard<*>) {
        val nms = (en as CraftCreature).handle
        EntityLiving::class.java.getDeclaredField("drops").apply { isAccessible = true }[nms] = emptyList<ItemStack>()

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
        nms.goalSelector.a(2, FollowCardOwner1_11_R1(nms, card))

        nms.targetSelector.a(1, CardOwnerHurtByTargetGoal1_11_R1(nms, card))
        nms.targetSelector.a(2, CardOwnerHurtTargetGoal1_11_R1(nms, card))
        nms.targetSelector.a(3, PathfinderGoalHurtByTarget(nms, true))

        val tag = NBTTagCompound()
        nms.b(tag)
        tag.setBoolean("battlecard", true)
        nms.a(tag)
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

        en.target = ownerCard.target

        val nms = (en as CraftCreature).handle

        removeGoals(nms.goalSelector, nms.targetSelector)
        nms.goalSelector.a(2, FollowCardOwner1_11_R1(nms, ownerCard))

        nms.targetSelector.a(1, CardMasterHurtByTargetGoal1_11_R1(nms, ownerCard))
        nms.targetSelector.a(2, CardMasterHurtTargetGoal1_11_R1(nms, ownerCard))
        nms.targetSelector.a(3, CardOwnerHurtByTargetGoal1_11_R1(nms, ownerCard))
        nms.targetSelector.a(4, CardOwnerHurtTargetGoal1_11_R1(nms, ownerCard))
        nms.targetSelector.a(5, PathfinderGoalHurtByTarget(nms, true))

        ownerCard.minions.add(en)
        return en
    }

    private fun removeGoals(goalSelector: PathfinderGoalSelector, targetSelector: PathfinderGoalSelector) {
        val goals = PathfinderGoalSelector::class.java.getDeclaredField("b").apply { isAccessible = true }.get(goalSelector).run {
            (this as Set<Any>).map { it::class.java.getDeclaredField("a").apply { isAccessible = true }.get(it).run { this as PathfinderGoal } }
        }
        goals.filter {
            it is PathfinderGoalAvoidTarget<*> || it is PathfinderGoalRestrictSun || it is PathfinderGoalFleeSun || it is PathfinderGoalBeg || it is PathfinderGoalBreed
        }.forEach { goalSelector.a(it) }

        val targets = PathfinderGoalSelector::class.java.getDeclaredField("b").apply { isAccessible = true }.get(targetSelector).run {
            (this as Set<Any>).map { it::class.java.getDeclaredField("a").apply { isAccessible = true }.get(it).run { this as PathfinderGoal } }
        }
        targets.filter {
            it is PathfinderGoalNearestAttackableTarget<*> || it is PathfinderGoalDefendVillage
        }.forEach { targetSelector.a(it) }
    }

    override fun getNBTWrapper(item: org.bukkit.inventory.ItemStack): NBTWrapper {
        return NBTWrapper1_11_R1(item)
    }

    override fun isCard(en: Creature): Boolean {
        val tag = NBTTagCompound()
        (en as CraftCreature).handle.b(tag)
        return tag.getBoolean("battlecard")
    }

    override fun createInventory(id: String, name: String, size: Int): BattleInventory {
        return BattleInventory1_11_R1(id, name, size)
    }

    override fun spawnParticle(
        particle: BattleParticle, location: Location, count: Int,
        dX: Double, dY: Double, dZ: Double,
        speed: Double, force: Boolean
    ) {
        if (location.world == null) return
        location.world!!.spawnParticle(Particle.valueOf(particle.name.uppercase()), location, count, dX, dY, dZ, speed)
    }

    override fun getDefaultAttribute(type: EntityType, attribute: CardAttribute): Double {
        val w = Bukkit.getWorlds()[0] as CraftWorld
        val creature = w.createEntity(Location(w, 0.0, 0.0, 0.0), type.entityClass) as? EntityLiving ?: throw AssertionError("Failed to create dummy creature")

        return creature.getAttributeInstance(toNMS(attribute)).b()
    }

    private fun removeAttackGoals(entity: EntityCreature) {
        val goals = PathfinderGoalSelector::class.java.getDeclaredField("b").apply { isAccessible = true }.get(entity.goalSelector).run {
            (this as Set<Any>).map { it::class.java.getDeclaredField("a").apply { isAccessible = true }.get(it).run { this as PathfinderGoal } }
        }

        goals.filter {
            it is PathfinderGoalMeleeAttack || it is PathfinderGoalArrowAttack || it is PathfinderGoalBowShoot
        }.forEach { entity.goalSelector.a(it) }
    }

    override fun setAttackType(entity: Creature, attackType: CardAttackType) {
        val nms = (entity as CraftCreature).handle
        removeAttackGoals(nms)

        nms.goalSelector.a(3, when (attackType) {
            CardAttackType.MELEE -> PathfinderGoalMeleeAttack(nms, 1.0, false)
            CardAttackType.BOW -> {
                if (nms !is EntitySkeletonAbstract) throw UnsupportedOperationException("Invalid Skeleton Type ${entity::class.java.simpleName}")

                PathfinderGoalBowShoot(nms, 1.0, 20, 15.0F)
            }
            CardAttackType.CROSSBOW -> throw AssertionError("Using Crossbow AttackType below 1.14")
        })
    }

    override fun getAttackType(entity: Creature): CardAttackType {
        val nms = (entity as CraftCreature).handle
        val goals = PathfinderGoalSelector::class.java.getDeclaredField("b").apply { isAccessible = true }.get(nms.goalSelector).run {
            (this as Set<Any>).map {
                Pair(
                    it::class.java.getDeclaredField("a").apply { isAccessible = true }.get(it).run { this as PathfinderGoal },
                    it::class.java.getDeclaredField("b").apply { isAccessible = true }.get(it).run { this as Int }
                )
            }
        }
        val targetGoals = PathfinderGoalSelector::class.java.getDeclaredField("b").apply { isAccessible = true }.get(nms.targetSelector).run {
            (this as Set<Any>).map {
                Pair(
                    it::class.java.getDeclaredField("a").apply { isAccessible = true }.get(it).run { this as PathfinderGoal },
                    it::class.java.getDeclaredField("b").apply { isAccessible = true }.get(it).run { this as Int }
                )
            }
        }

        return (goals + targetGoals)
            .sortedBy { it.second }
            .firstNotNullOf {
                when (it.first) {
                    is PathfinderGoalMeleeAttack -> CardAttackType.MELEE
                    is PathfinderGoalBowShoot -> CardAttackType.BOW
                    else -> null
                } ?: CardAttackType.MELEE
            }
    }

}