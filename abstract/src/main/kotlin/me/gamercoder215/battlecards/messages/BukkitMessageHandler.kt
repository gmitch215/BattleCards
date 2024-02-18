package me.gamercoder215.battlecards.messages

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin

internal class BukkitMessageHandler(plugin: Plugin) : MessageHandler {

    init {
        plugin.logger.info("Loaded Bukkit MessageHandler")
    }

    override fun send(sender: CommandSender, key: String, vararg args: Any) = sendRaw(sender, format(get(key), *args))
    override fun sendMessage(sender: CommandSender, key: String, vararg args: Any) = sendRawMessage(sender, format(get(key), *args))
    override fun sendError(sender: CommandSender, key: String, vararg args: Any) = sendRawMessage(sender, "${ChatColor.RED}${format(get(key), *args)}")
    override fun sendSuccess(sender: CommandSender, key: String, vararg args: Any) = sendRawMessage(sender, "${ChatColor.GREEN}${format(get(key), *args)}")

    override fun sendRaw(sender: CommandSender, message: String) = sender.sendMessage(message)
    override fun sendRaw(sender: CommandSender, vararg messages: String) = sender.sendMessage(messages)
    override fun sendRawMessage(sender: CommandSender, message: String) = sender.sendMessage(prefix + message)

}