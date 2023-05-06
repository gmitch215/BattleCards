package me.gamercoder215.battlecards.wrapper

import net.md_5.bungee.api.chat.BaseComponent
import net.minecraft.server.v1_9_R2.BossBattleServer
import net.minecraft.server.v1_9_R2.ChatComponentText
import net.minecraft.server.v1_9_R2.EntityWither
import net.minecraft.server.v1_9_R2.PacketPlayOutChat
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftWither
import org.bukkit.entity.Player
import org.bukkit.entity.Wither

class Wrapper1_9_R2 : Wrapper {

    override fun sendActionbar(player: Player, message: String) {
        val packet = PacketPlayOutChat(ChatComponentText(message), 2.toByte())
        (player as CraftPlayer).handle.playerConnection.sendPacket(packet)
    }

    override fun sendActionbar(player: Player, component: BaseComponent) {
        sendActionbar(player, component.toLegacyText())
    }

    override fun setBossBarVisibility(boss: Wither, visible: Boolean) {
        val nms = (boss as CraftWither).handle

        val bossBarF = EntityWither::class.java.getDeclaredField("bL")
        bossBarF.isAccessible = true

        val bossBar: BossBattleServer = bossBarF.get(nms) as BossBattleServer
        bossBar.setVisible(visible)
    }

}