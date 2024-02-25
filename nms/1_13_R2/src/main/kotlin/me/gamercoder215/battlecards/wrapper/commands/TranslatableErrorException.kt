package me.gamercoder215.battlecards.wrapper.commands

import me.gamercoder215.battlecards.messages.format
import me.gamercoder215.battlecards.messages.get
import me.gamercoder215.battlecards.messages.sendError
import revxrsal.commands.bukkit.BukkitCommandActor
import revxrsal.commands.command.CommandActor
import revxrsal.commands.exception.SendableException

internal class TranslatableErrorException(private val key: String, vararg args: Any) : SendableException(format(get(key), *args)) {
    private val args: Array<Any> = arrayOf(args)

    override fun sendTo(actor: CommandActor) = actor.`as`(BukkitCommandActor::class.java).sender.sendError(key, *args)

    companion object {
        private const val serialVersionUID = 1L
    }
}