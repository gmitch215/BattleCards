package me.gamercoder215.battlecards.wrapper.v1_8_R1

import me.gamercoder215.battlecards.impl.CardAttribute
import me.gamercoder215.battlecards.impl.cards.IBattleCard
import me.gamercoder215.battlecards.util.BattleParticle
import me.gamercoder215.battlecards.wrapper.BattleInventory
import me.gamercoder215.battlecards.wrapper.NBTWrapper
import me.gamercoder215.battlecards.wrapper.Wrapper
import net.md_5.bungee.api.chat.BaseComponent
import net.minecraft.server.v1_8_R1.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftCreature
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer
import org.bukkit.entity.Creature
import org.bukkit.entity.Player
import org.bukkit.entity.Wither

@Suppress("unchecked_cast")
internal class Wrapper1_8_R1 : Wrapper {

    override fun getCommandVersion(): Int = 1

    override fun sendActionbar(player: Player, message: String) {
        val packet = PacketPlayOutChat(ChatComponentText(message), 2.toByte())
        (player as CraftPlayer).handle.playerConnection.sendPacket(packet)
    }

    override fun sendActionbar(player: Player, component: BaseComponent) {
        sendActionbar(player, component.toLegacyText())
    }

    override fun setBossBarVisibility(boss: Wither, visible: Boolean) {
        // Withers do not have boss bars until 1.9
    }

    fun toNMS(attribute: CardAttribute): AttributeBase {
        return when (attribute) {
            CardAttribute.MAX_HEALTH -> GenericAttributes.maxHealth
            CardAttribute.ATTACK_DAMAGE -> GenericAttributes.e
            CardAttribute.KNOCKBACK_RESISTANCE -> GenericAttributes.c
            CardAttribute.SPEED -> GenericAttributes.d
            else -> throw IllegalArgumentException("Invalid attribute: $attribute")
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
        nms.goalSelector.a(2, FollowCardOwner1_8_R1(nms, card))

        nms.targetSelector.a(1, CardOwnerHurtByTargetGoal1_8_R1(nms, card))
        nms.targetSelector.a(2, CardOwnerHurtTargetGoal1_8_R1(nms, card))
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
        equipment.itemInHandDropChance = 0F
        equipment.helmetDropChance = 0F
        equipment.chestplateDropChance = 0F
        equipment.leggingsDropChance = 0F
        equipment.bootsDropChance = 0F

        en.target = card.target

        val nms = (en as CraftCreature).handle

        removeGoals(nms.goalSelector, nms.targetSelector)
        nms.goalSelector.a(2, FollowCardOwner1_8_R1(nms, ownerCard))

        nms.targetSelector.a(1, CardMasterHurtByTargetGoal1_8_R1(nms, ownerCard))
        nms.targetSelector.a(2, CardMasterHurtTargetGoal1_8_R1(nms, ownerCard))
        nms.targetSelector.a(3, CardOwnerHurtByTargetGoal1_8_R1(nms, ownerCard))
        nms.targetSelector.a(4, CardOwnerHurtTargetGoal1_8_R1(nms, ownerCard))
        nms.targetSelector.a(5, PathfinderGoalHurtByTarget(nms, true))

        ownerCard.minions.add(en)
        return en
    }

    private fun removeGoals(goalSelector: PathfinderGoalSelector, targetSelector: PathfinderGoalSelector) {
        val goals = PathfinderGoalSelector::class.java.getDeclaredField("b").apply { isAccessible = true }.get(goalSelector).run {
            (this as Set<Any>).map { it::class.java.getDeclaredField("a").apply { isAccessible = true }.get(it).run { this as PathfinderGoal } }
        }
        goals.filter {
            it is PathfinderGoalAvoidTarget || it is PathfinderGoalRestrictSun || it is PathfinderGoalFleeSun || it is PathfinderGoalBeg || it is PathfinderGoalBreed
        }.forEach { goalSelector.a(it) }

        val targets = PathfinderGoalSelector::class.java.getDeclaredField("b").apply { isAccessible = true }.get(targetSelector).run {
            (this as Set<Any>).map { it::class.java.getDeclaredField("a").apply { isAccessible = true }.get(it).run { this as PathfinderGoal } }
        }
        targets.filter {
            it is PathfinderGoalNearestAttackableTarget || it is PathfinderGoalDefendVillage
        }.forEach { targetSelector.a(it) }
    }

    override fun getNBTWrapper(item: org.bukkit.inventory.ItemStack): NBTWrapper {
        return NBTWrapper1_8_R1(item)
    }

    override fun isCard(en: Creature): Boolean {
        val tag = NBTTagCompound()
        (en as CraftCreature).handle.b(tag)
        return tag.getBoolean("battlecard")
    }

    override fun createInventory(id: String, name: String, size: Int): BattleInventory {
        return BattleInventory1_8_R1(id, name, size)
    }

    private fun toNMS(particle: BattleParticle): EnumParticle {
        return when (particle) {
            BattleParticle.CLOUD -> EnumParticle.CLOUD
            else -> throw IllegalArgumentException("Invalid particle: $particle")
        }
    }

    override fun spawnParticle(
        particle: BattleParticle, location: Location, count: Int,
        dX: Double, dY: Double, dZ: Double,
        speed: Double, force: Boolean
    ) {
        if (location.world == null) return

        val packet = PacketPlayOutWorldParticles(toNMS(particle), force, location.x.toFloat(), location.y.toFloat(), location.z.toFloat(), dX.toFloat(), dY.toFloat(), dZ.toFloat(), speed.toFloat(), count)
        Bukkit.getOnlinePlayers().forEach{ (it as CraftPlayer).handle.playerConnection.sendPacket(packet) }
    }

}