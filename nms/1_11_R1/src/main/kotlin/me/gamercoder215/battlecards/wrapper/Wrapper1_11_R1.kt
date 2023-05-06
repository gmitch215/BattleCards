package me.gamercoder215.battlecards.wrapper

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import net.minecraft.server.v1_11_R1.BossBattleServer
import net.minecraft.server.v1_11_R1.EntityWither
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftWither
import org.bukkit.entity.Player
import org.bukkit.entity.Wither

class Wrapper1_11_R1 : Wrapper {

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

}