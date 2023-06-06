package me.gamercoder215.battlecards.wrapper.v1_16_R3

import me.gamercoder215.battlecards.impl.CardAttribute
import me.gamercoder215.battlecards.impl.cards.IBattleCard
import me.gamercoder215.battlecards.wrapper.BattleInventory
import me.gamercoder215.battlecards.wrapper.NBTWrapper
import me.gamercoder215.battlecards.wrapper.Wrapper
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import net.minecraft.server.v1_16_R3.*
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftCreature
import org.bukkit.craftbukkit.v1_16_R3.util.CraftNamespacedKey
import org.bukkit.entity.Creature
import org.bukkit.entity.Player
import org.bukkit.entity.Wither

@Suppress("unchecked_cast")
internal class Wrapper1_16_R3 : Wrapper {

    override fun sendActionbar(player: Player, component: BaseComponent) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component)
    }

    override fun sendActionbar(player: Player, message: String) {
        sendActionbar(player, TextComponent(message))
    }

    override fun setBossBarVisibility(boss: Wither, visible: Boolean) {
        boss.bossBar?.isVisible = visible
    }

    fun toNMS(key: NamespacedKey): MinecraftKey {
        return CraftNamespacedKey.toMinecraft(key)
    }

    fun toBukkit(attribute: CardAttribute): Attribute {
        return when (attribute) {
            CardAttribute.MAX_HEALTH -> Attribute.GENERIC_MAX_HEALTH
            CardAttribute.ATTACK_DAMAGE -> Attribute.GENERIC_ATTACK_DAMAGE
            CardAttribute.KNOCKBACK_RESISTANCE -> Attribute.GENERIC_KNOCKBACK_RESISTANCE
            CardAttribute.SPEED -> Attribute.GENERIC_MOVEMENT_SPEED
            CardAttribute.DEFENSE -> Attribute.GENERIC_ARMOR
        }
    }

    fun toNMS(attribute: Attribute): AttributeBase {
        return IRegistry.ATTRIBUTE.get(toNMS(attribute.key)) ?: throw NullPointerException("Attribute ${attribute.key} not found")
    }

    override fun loadProperties(en: Creature, card: IBattleCard<*>) {
        val nms: EntityCreature = (en as CraftCreature).handle

        val dropsF = EntityLiving::class.java.getDeclaredField("drops")
        dropsF.isAccessible = true
        val drops = dropsF.get(nms) as MutableList<ItemStack>
        drops.clear()

        for (entry in card.getStatistics().getAttributes()) {
            val attribute = toNMS(toBukkit(entry.key))
            val value = entry.value

            var handle: AttributeModifiable? = nms.getAttributeInstance(attribute)
            if (handle == null) {
                val attributesF = AttributeMapBase::class.java.getDeclaredField("b")
                attributesF.isAccessible = true
                val attributes = attributesF.get(nms) as MutableMap<AttributeBase, AttributeModifiable>

                handle = AttributeModifiable(attribute) {}
                attributes[attribute] = handle
            }

            handle.value = value
        }

        nms.goalSelector.a(2, FollowCardOwner1_16_R3(nms, card))

        nms.targetSelector.a(1, CardOwnerHurtByTargetGoal1_16_R3(nms, card))
        nms.targetSelector.a(2, CardOwnerHurtTargetGoal1_16_R3(nms, card))
        nms.targetSelector.a(3, PathfinderGoalHurtByTarget(nms))

        nms.addScoreboardTag("battlecards")
    }

    override fun getNBTWrapper(item: org.bukkit.inventory.ItemStack): NBTWrapper {
        return NBTWrapper1_16_R3(item)
    }

    override fun isCard(en: Creature): Boolean {
        return (en as CraftCreature).handle.scoreboardTags.contains("battlecards")
    }

    override fun createInventory(id: String, name: String, size: Int): BattleInventory {
        return BattleInventory1_16_R3(id, name, size)
    }

}