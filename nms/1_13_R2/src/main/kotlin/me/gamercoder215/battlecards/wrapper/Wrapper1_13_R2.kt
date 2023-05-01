package me.gamercoder215.battlecards.wrapper

import me.gamercoder215.battlecards.impl.cards.IBattleCard
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import net.minecraft.server.v1_13_R2.AttributeInstance
import net.minecraft.server.v1_13_R2.AttributeMapBase
import net.minecraft.server.v1_13_R2.AttributeModifiable
import org.bukkit.craftbukkit.v1_13_R2.attribute.CraftAttributeMap
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftMob
import org.bukkit.entity.Mob
import org.bukkit.entity.Player

class Wrapper1_13_R2 : Wrapper {

    override fun sendActionbar(player: Player, component: BaseComponent) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component)
    }

    override fun sendActionbar(player: Player, message: String) {
        sendActionbar(player, TextComponent(message))
    }

//    override fun loadProperties(entity: Mob, card: IBattleCard<*>) {
//        val nms = (entity as CraftMob).handle
//
//        // Attributes
//
//        for (entry in card.getStatistics().getAttributes())
//            entity.getAttribute(entry.key.toBukkit())?.baseValue = entry.value
//
//        TODO("Finish")
//    }

}