package me.gamercoder215.battlecards.wrapper.commands

import me.gamercoder215.battlecards.api.BattleConfig
import org.bukkit.plugin.Plugin
import revxrsal.commands.bukkit.BukkitCommandHandler

internal class CommandWrapperV2(private val plugin: Plugin) : CommandWrapper {

    companion object {
        private lateinit var handler: BukkitCommandHandler

        @JvmStatic
        fun hasHandler(): Boolean = ::handler.isInitialized
    }

    init {
        run {
            if (hasHandler()) return@run
            handler = BukkitCommandHandler.create(plugin)

            handler.registerBrigadier()
            handler.locale = BattleConfig.getConfig().locale
        }
    }

}