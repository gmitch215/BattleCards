package me.gamercoder215.battlecards.wrapper

import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

interface Wrapper {

    fun sendActionbar(player: Player, component: BaseComponent)

    fun sendActionbar(player: Player, message: String)

    companion object {
        @JvmStatic
        fun getWrapper(): Wrapper? {
            return Class.forName("${Wrapper::class.java.`package`.name}.Wrapper${getServerVersion()}")
                .asSubclass(Wrapper::class.java)
                .getDeclaredConstructor()
                .newInstance()
        }

        @JvmStatic
        fun getServerVersion(): String {
            return Bukkit.getServer().javaClass.`package`.name.split("\\.")[3].substring(1)
        }

    }

}