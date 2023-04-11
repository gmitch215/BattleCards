package me.gamercoder215.battlecards.wrapper

import me.gamercoder215.battlecards.api.card.BattleCard
import me.gamercoder215.battlecards.impl.IBattleCard
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

interface Wrapper {

    fun sendActionbar(player: Player, component: BaseComponent)

    fun sendActionbar(player: Player, message: String)

    fun editCard(entity: LivingEntity, card: IBattleCard<*>)

    companion object {
        val w = getWrapper()

        @JvmStatic
        fun getWrapper(): Wrapper? {
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