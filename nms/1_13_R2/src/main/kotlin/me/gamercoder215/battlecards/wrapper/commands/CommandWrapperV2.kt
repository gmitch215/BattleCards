package me.gamercoder215.battlecards.wrapper.commands

import me.gamercoder215.battlecards.api.BattleConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Description
import revxrsal.commands.annotation.Subcommand
import revxrsal.commands.annotation.Usage
import revxrsal.commands.bukkit.BukkitCommandHandler
import revxrsal.commands.bukkit.annotation.CommandPermission

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

            handler.register(CardCommands(this))

            handler.registerBrigadier()
            handler.locale = BattleConfig.getConfig().locale
        }
    }

    @Command("cardreload", "creload")
    @Description("Reloads the BattleCards Plugin")
    @Usage("/cardreload")
    @CommandPermission("battlecards.admin.reload")
    override fun reloadPlugin(sender: CommandSender) = super.reloadPlugin(sender)

    @Command("bcard", "card", "battlecard")
    @Description("Main BattleCards Card Command")
    @Usage("/bcard")
    @CommandPermission("battlecards.user.card")
    private class CardCommands(private val wrapper: CommandWrapperV2) {

        @Subcommand("info")
        fun cardInfo(p: Player) = wrapper.cardInfo(p)

    }

}