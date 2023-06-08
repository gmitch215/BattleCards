package me.gamercoder215.battlecards.wrapper.commands

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.wrapper.commands.CommandWrapper.Companion.COMMANDS
import me.gamercoder215.battlecards.wrapper.commands.CommandWrapper.Companion.COMMAND_DESCRIPTION
import me.gamercoder215.battlecards.wrapper.commands.CommandWrapper.Companion.COMMAND_PERMISSION
import me.gamercoder215.battlecards.wrapper.commands.CommandWrapper.Companion.COMMAND_USAGE
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.command.*
import org.bukkit.plugin.Plugin
import java.lang.reflect.Constructor


internal class CommandWrapperV1(private val plugin: Plugin) : CommandWrapper, CommandExecutor {

    init {
        loadCommands()
        plugin.logger.info("Loaded Command Wrapper V1 (1.8+)")
    }

    private fun createCommand(name: String, aliases: Iterable<String>): PluginCommand? {
        return try {
            val p: Constructor<PluginCommand> = PluginCommand::class.java.getDeclaredConstructor(String::class.java, Plugin::class.java)
            p.isAccessible = true

            val cmd: PluginCommand = p.newInstance(name, plugin)
            if (aliases.toList().isNotEmpty()) cmd.setAliases(aliases.toList())
            cmd
        } catch (e: Exception) {
            BattleConfig.print(e)
            null
        }
    }

    private fun register(cmd: PluginCommand) {
        val srv: Server = Bukkit.getServer()
        val commands = srv.javaClass.getDeclaredField("commandMap")
        commands.isAccessible = true

        val map = commands.get(srv) as CommandMap
        map.register(cmd.name, cmd)
    }

    private fun loadCommands() {
        for (entry in COMMANDS) {
            val cmd = entry.key
            val aliases = entry.value
            val desc = COMMAND_DESCRIPTION[cmd]!!
            val usage = COMMAND_USAGE[cmd]!!
            val permission = COMMAND_PERMISSION[cmd]

            val pcmd = createCommand(cmd, aliases)
            if (pcmd == null) {
                BattleConfig.getLogger().info("Error loading command: $cmd")
                continue
            }

            pcmd.executor = this
            pcmd.usage = usage
            pcmd.description = desc
            if (permission != null) pcmd.permission = permission

            register(pcmd)
        }
    }

    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        when (cmd.name) {

        }

        return true
    }

}