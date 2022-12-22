package me.gamercoder215.battlecards.wrapper

import net.md_5.bungee.api.chat.BaseComponent
import net.minecraft.server.v1_8_R2.ChatComponentText
import net.minecraft.server.v1_8_R2.PacketPlayOutChat
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer
import org.bukkit.entity.Player

class Wrapper1_8_R2 : Wrapper {

    override fun sendActionbar(player: Player, message: String) {
        val packet = PacketPlayOutChat(ChatComponentText(message), 2.toByte())
        (player as CraftPlayer).handle.playerConnection.sendPacket(packet)
    }

    override fun sendActionbar(player: Player, component: BaseComponent) {
        sendActionbar(player, component.toLegacyText())
    }

}