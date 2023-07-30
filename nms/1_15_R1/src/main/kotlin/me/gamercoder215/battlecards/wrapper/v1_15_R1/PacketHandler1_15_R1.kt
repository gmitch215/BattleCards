package me.gamercoder215.battlecards.wrapper.v1_15_R1

import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import me.gamercoder215.battlecards.util.sync
import net.minecraft.server.v1_15_R1.Packet
import org.bukkit.entity.Player
import java.util.*

internal class PacketHandler1_15_R1(private val p: Player) : ChannelDuplexHandler() {

    override fun channelRead(ctx: ChannelHandlerContext, packetO: Any) {
        if (packetO !is Packet<*>) {
            super.channelRead(ctx, packetO)
            return
        }

        PACKET_HANDLERS[p.uniqueId]?.let {
            sync { it(packetO) }
        }

        super.channelRead(ctx, packetO)
    }

    companion object {
        @JvmStatic
        val PACKET_HANDLERS: MutableMap<UUID, (Packet<*>) -> Unit> = mutableMapOf()
    }
}