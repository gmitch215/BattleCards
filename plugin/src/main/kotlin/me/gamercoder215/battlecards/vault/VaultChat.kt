package me.gamercoder215.battlecards.vault

import net.milkbowl.vault.chat.Chat
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.concurrent.atomic.AtomicBoolean

internal object VaultChat {

    private lateinit var chat: Chat

    fun loadChat() {
        if (this::chat.isInitialized) return

        val rsp = Bukkit.getServicesManager().getRegistration(Chat::class.java)
        if (rsp.provider != null) chat = rsp.provider
    }

    fun isInGroup(player: Player, vararg groups: String): Boolean {
        if (!this::chat.isInitialized) return false

        val b = AtomicBoolean()
        val pGroups = chat.getPlayerGroups(player)

        for (group in groups) {
            val patt = group.toRegex()

            for (pGroup in pGroups) {
                if (patt.matches(pGroup)) {
                    b.set(true)
                    break
                }
            }
        }

        return b.get()
    }

}