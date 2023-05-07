package me.gamercoder215.battlecards.wrapper.v1_17_R1

import me.gamercoder215.battlecards.impl.CardAttribute
import me.gamercoder215.battlecards.impl.cards.IBattleCard
import me.gamercoder215.battlecards.wrapper.Wrapper
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import net.minecraft.core.IRegistry
import net.minecraft.resources.MinecraftKey
import net.minecraft.world.entity.EntityCreature
import net.minecraft.world.entity.ai.attributes.AttributeBase
import net.minecraft.world.entity.ai.attributes.AttributeMapBase
import net.minecraft.world.entity.ai.attributes.AttributeModifiable
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftCreature
import org.bukkit.craftbukkit.v1_17_R1.util.CraftNamespacedKey
import org.bukkit.entity.Creature
import org.bukkit.entity.Player
import org.bukkit.entity.Wither

@Suppress("unchecked_cast")
class Wrapper1_17_R1 : Wrapper {

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
        return IRegistry.al.get(toNMS(attribute.key)) ?: throw NullPointerException("Attribute ${attribute.key} not found")
    }

    override fun loadProperties(en: Creature, card: IBattleCard<*>) {
        val nms: EntityCreature = (en as CraftCreature).handle

        nms.drops.clear()

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

        nms.bP.a(2, FollowCardOwner1_17_R1(nms, card))

        nms.bQ.a(1, CardOwnerHurtByTargetGoal1_17_R1(nms, card))
        nms.bQ.a(2, CardOwnerHurtTargetGoal1_17_R1(nms, card))
        nms.bQ.a(3, PathfinderGoalHurtByTarget(nms))

        nms.addScoreboardTag("battlecards")
    }

}