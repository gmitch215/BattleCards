package me.gamercoder215.battlecards.wrapper.v1_19_R3

import me.gamercoder215.battlecards.impl.cards.IBattleCard
import me.gamercoder215.battlecards.wrapper.Wrapper
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import net.minecraft.world.entity.ai.goal.FloatGoal
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class Wrapper1_19_R3 : Wrapper {

    override fun sendActionbar(player: Player, component: BaseComponent) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component)
    }

    override fun sendActionbar(player: Player, message: String) {
        sendActionbar(player, TextComponent(message))
    }

}