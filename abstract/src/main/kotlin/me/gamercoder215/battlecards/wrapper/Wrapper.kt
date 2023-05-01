package me.gamercoder215.battlecards.wrapper

import me.gamercoder215.battlecards.impl.cards.IBattleCard
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Bukkit
import org.bukkit.boss.BossBar
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.entity.Wither

interface Wrapper {

    fun sendActionbar(player: Player, component: BaseComponent)

    fun sendActionbar(player: Player, message: String)

//    fun loadProperties(entity: Mob, card: IBattleCard<*>)

    companion object {
        val w = getWrapper()

        @JvmStatic
        fun getWrapper(): Wrapper {
            return Class.forName("${Wrapper::class.java.`package`.name}.v${getServerVersion()}.Wrapper${getServerVersion()}")
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