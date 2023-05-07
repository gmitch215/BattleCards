package me.gamercoder215.battlecards.wrapper.v1_15_R1

import me.gamercoder215.battlecards.impl.CardAttribute
import me.gamercoder215.battlecards.impl.cards.IBattleCard
import me.gamercoder215.battlecards.wrapper.Wrapper
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import net.minecraft.server.v1_15_R1.*
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftCreature
import org.bukkit.entity.Creature
import org.bukkit.entity.Player
import org.bukkit.entity.Wither

@Suppress("unchecked_cast")
class Wrapper1_15_R1 : Wrapper {

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
        val nms: EntityCreature = (en as CraftCreature).handle

        val dropsF = EntityLiving::class.java.getDeclaredField("drops")
        dropsF.isAccessible = true
        val drops = dropsF.get(nms) as MutableList<ItemStack>
        drops.clear()

        for (entry in card.getStatistics().getAttributes()) {
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

        nms.goalSelector.a(2, FollowCardOwner1_15_R1(nms, card))

        nms.targetSelector.a(1, CardOwnerHurtByTargetGoal1_15_R1(nms, card))
        nms.targetSelector.a(2, CardOwnerHurtTargetGoal1_15_R1(nms, card))
        nms.targetSelector.a(3, PathfinderGoalHurtByTarget(nms))

        nms.addScoreboardTag("battlecards")
    }

}