package me.gamercoder215.battlecards.wrapper.v1_18_R2

import me.gamercoder215.battlecards.impl.CardAttribute
import me.gamercoder215.battlecards.impl.cards.IBattleCard
import me.gamercoder215.battlecards.wrapper.Wrapper
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.attributes.AttributeInstance
import net.minecraft.world.entity.ai.attributes.AttributeMap
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftCreature
import org.bukkit.craftbukkit.v1_18_R2.util.CraftNamespacedKey
import org.bukkit.entity.Creature
import org.bukkit.entity.Player
import org.bukkit.entity.Wither

@Suppress("unchecked_cast")
class Wrapper1_18_R2 : Wrapper {
    override fun sendActionbar(player: Player, component: BaseComponent) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component)
    }

    override fun sendActionbar(player: Player, message: String) {
        sendActionbar(player, TextComponent(message))
    }

    override fun setBossBarVisibility(boss: Wither, visible: Boolean) {
        boss.bossBar?.isVisible = visible
    }

    fun toNMS(key: NamespacedKey): ResourceLocation {
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

    fun toNMS(attribute: Attribute): net.minecraft.world.entity.ai.attributes.Attribute {
        return Registry.ATTRIBUTE.get(toNMS(attribute.key)) ?: throw NullPointerException("Attribute ${attribute.key} not found")
    }

    override fun loadProperties(en: Creature, card: IBattleCard<*>) {
        val nms: PathfinderMob = (en as CraftCreature).handle

        nms.drops.clear()

        for (entry in card.getStatistics().getAttributes()) {
            val attribute = toNMS(toBukkit(entry.key))
            val value = entry.value

            var handle: AttributeInstance? = nms.getAttribute(attribute)
            if (handle == null) {
                val attributesF = AttributeMap::class.java.getDeclaredField("b")
                attributesF.isAccessible = true
                val attributes = attributesF.get(nms) as MutableMap<net.minecraft.world.entity.ai.attributes.Attribute, AttributeInstance>

                handle = AttributeInstance(attribute) {}
                attributes[attribute] = handle
            }

            handle.baseValue = value
        }

        nms.goalSelector.addGoal(2, FollowCardOwner1_18_R2(nms, card))

        nms.targetSelector.addGoal(1, CardOwnerHurtByTargetGoal1_18_R2(nms, card))
        nms.targetSelector.addGoal(2, CardOwnerHurtTargetGoal1_18_R2(nms, card))
        nms.targetSelector.addGoal(3, HurtByTargetGoal(nms))

        nms.addTag("battlecards")
    }
}