package me.gamercoder215.battlecards.wrapper.commands

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.messages.sendError
import me.gamercoder215.battlecards.util.cardInHand
import me.gamercoder215.battlecards.wrapper.commands.CommandWrapper.Companion.COMMANDS
import me.gamercoder215.battlecards.wrapper.commands.CommandWrapper.Companion.COMMAND_DESCRIPTION
import me.gamercoder215.battlecards.wrapper.commands.CommandWrapper.Companion.COMMAND_PERMISSION
import me.gamercoder215.battlecards.wrapper.commands.CommandWrapper.Companion.COMMAND_USAGE
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.command.*
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
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
                BattleConfig.logger.info("Error loading command: $cmd")
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
            "bcard" -> {
                if (sender !is Player) return false

                return when {
                    args.isEmpty() -> { cardInfo(sender); true }
                    else -> when (args[0]) {
                        "info" -> { cardInfo(sender); true }
                        "basic" -> {
                            if (args.size < 2)
                                return sender.sendError("error.argument.basic_type", false)

                            return try {
                                createCard(sender, BattleCardType.BASIC, EntityType.valueOf(args[1].uppercase()))
                                true
                            } catch (ignored: IllegalArgumentException) {
                                sender.sendError("error.argument.entity_type", false)
                            }
                        }
                        "create" -> {
                            if (args.size < 2)
                                sender.sendError("error.argument.card", false)

                            return try {
                                createCard(sender, BattleCardType.valueOf(args[1].uppercase()))
                                true
                            } catch (ignored: IllegalArgumentException) {
                                sender.sendError("error.argument.card", false)
                            }
                        }
                        "query" -> {
                            if (args.size < 2)
                                sender.sendError("error.argument.card", false)

                            return try {
                                queryCard(sender, BattleCardType.valueOf(args[1].uppercase()))
                                true
                            } catch (ignored: IllegalArgumentException) {
                                sender.sendError("error.argument.card", false)
                            }
                        }
                        "edit" -> {
                            if (args.size < 4)
                                return sender.sendError("error.argument", false)

                            val card = sender.cardInHand ?: return sender.sendError("error.argument.item.held.card", false)

                            when (args[1]) {
                                "level", "lvl" ->
                                    when (args[2]) {
                                        "set" -> {
                                            val level = args[3].toIntOrNull() ?: return sender.sendError("error.argument.int", false)

                                            if (level < 1 || level > card.maxCardLevel)
                                                return sender.sendError("error.argument.card.level", false)

                                            editCard(sender) { it.level = level }
                                        }
                                        "add" -> {
                                            val add = args[3].toIntOrNull() ?: return sender.sendError("error.argument.int", false)
                                            editCard(sender) { it.level = (card.level + add).coerceAtMost(card.maxCardLevel) }
                                        }
                                        "remove" -> {
                                            val remove = args[3].toIntOrNull() ?: return sender.sendError("error.argument.int", false)
                                            val new = card.level - remove

                                            if (new < 1 || new > card.maxCardLevel)
                                                return sender.sendError("error.argument.card.level", false)

                                            editCard(sender) { it.level = new }
                                        }
                                        else -> {
                                            sender.sendError("error.argument")
                                            return false
                                        }
                                    }

                                "experience", "exp" -> {
                                    when (args[2]) {
                                        "set" -> {
                                            val exp = args[3].toDoubleOrNull() ?: return sender.sendError("error.argument.number", false)

                                            if (exp < 0 || exp > card.maxCardExperience)
                                                return sender.sendError("error.argument.card.exp", false)

                                            editCard(sender) { it.experience = exp }
                                        }
                                        "add" -> {
                                            val add = args[3].toDoubleOrNull() ?: return sender.sendError("error.argument.number", false)
                                            editCard(sender) { it.experience = (card.experience + add).coerceAtMost(card.maxCardExperience) }
                                        }
                                        "remove" -> {
                                            val remove = args[3].toDoubleOrNull() ?: return sender.sendError("error.argument.number", false)
                                            val new = card.experience - remove

                                            if (new < 0 || new > card.maxCardExperience)
                                                return sender.sendError("error.argument.card.exp", false)

                                            editCard(sender) { it.experience = new }
                                        }
                                        else -> {
                                            sender.sendError("error.argument")
                                            return false
                                        }
                                    }
                                }
                                "max" -> editCard(sender) { it.experience = card.maxCardExperience }
                                else -> {
                                    sender.sendError("error.argument")
                                    return false
                                }
                            }

                            true
                        }
                        "item" -> {
                            if (args.size < 2)
                                return sender.sendError("error.argument.item", false)

                            giveItem(sender, args[1])
                            true
                        }
                        "despawn" -> {
                            despawnCards(sender)
                            true
                        }
                        "catalogue" -> {
                            if (args.size < 2)
                                return sender.sendError("error.argument.card", false)

                            return try {
                                catalogue(sender, args[1].uppercase())
                                true
                            } catch (ignored: IllegalArgumentException) {
                                sender.sendError("error.argument.card", false)
                            }
                        }
                        else -> {
                            sender.sendError("error.argument")
                            false
                        }
                    }
                }
            }
            "cardreload" -> reloadPlugin(sender)
        }

        return true
    }

    private fun CommandSender.sendError(key: String, returns: Boolean): Boolean {
        sendError(key)
        return returns
    }

}