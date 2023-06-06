package me.gamercoder215.battlecards.wrapper.v1_8_R3

import me.gamercoder215.battlecards.impl.CardAttribute
import me.gamercoder215.battlecards.impl.cards.IBattleCard
import me.gamercoder215.battlecards.wrapper.BattleInventory
import me.gamercoder215.battlecards.wrapper.NBTWrapper
import me.gamercoder215.battlecards.wrapper.Wrapper
import net.md_5.bungee.api.chat.BaseComponent
import net.minecraft.server.v1_8_R3.*
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftCreature
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Creature
import org.bukkit.entity.Player
import org.bukkit.entity.Wither

@Suppress("unchecked_cast")
internal class Wrapper1_8_R3 : Wrapper {

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
            CardAttribute.ATTACK_DAMAGE -> GenericAttributes.ATTACK_DAMAGE
            CardAttribute.KNOCKBACK_RESISTANCE -> GenericAttributes.c
            CardAttribute.SPEED -> GenericAttributes.MOVEMENT_SPEED
            else -> throw IllegalArgumentException("Invalid attribute: $attribute")
        } as AttributeBase
    }

    override fun loadProperties(en: Creature, card: IBattleCard<*>) {
        val nms: EntityCreature = (en as CraftCreature).handle

        val dropsF = EntityLiving::class.java.getDeclaredField("drops")
        dropsF.isAccessible = true
        val drops = dropsF.get(nms) as MutableList<ItemStack>
        drops.clear()

        for (entry in card.getStatistics().getAttributes()) {
            if (entry.key == CardAttribute.DEFENSE) continue // TODO Finish Defense Calculation for 1.8
            val attribute = toNMS(entry.key)
            val value = entry.value

            var handle: AttributeInstance? = nms.getAttributeInstance(attribute)
            if (handle == null) {
                val attributesF = AttributeMapBase::class.java.getDeclaredField("b")
                attributesF.isAccessible = true
                val attributes = attributesF.get(nms) as MutableMap<String, AttributeInstance>

                handle = AttributeModifiable(nms.attributeMap, attribute)
                attributes[attribute.name] = handle
            }

            handle.value = value
        }

        nms.goalSelector.a(2, FollowCardOwner1_8_R3(nms, card))

        nms.targetSelector.a(1, CardOwnerHurtByTargetGoal1_8_R3(nms, card))
        nms.targetSelector.a(2, CardOwnerHurtTargetGoal1_8_R3(nms, card))
        nms.targetSelector.a(3, PathfinderGoalHurtByTarget(nms, true))

        val tag = NBTTagCompound()
        nms.b(tag)
        tag.setBoolean("battlecard", true)
        nms.a(tag)
    }

    override fun getNBTWrapper(item: org.bukkit.inventory.ItemStack): NBTWrapper {
        return NBTWrapper1_8_R3(item)
    }

    override fun isCard(en: Creature): Boolean {
        val tag = NBTTagCompound()
        (en as CraftCreature).handle.b(tag)
        return tag.getBoolean("battlecard")
    }

    override fun createInventory(id: String, name: String, size: Int): BattleInventory {
        return BattleInventory1_8_R3(id, name, size)
    }

}