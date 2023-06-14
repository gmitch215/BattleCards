package me.gamercoder215.battlecards.wrapper.commands

import com.google.common.collect.ImmutableMap
import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.util.CardUtils.format
import me.gamercoder215.battlecards.util.card
import me.gamercoder215.battlecards.util.formatName
import me.gamercoder215.battlecards.util.inventory.CardGenerator
import me.gamercoder215.battlecards.util.inventory.Generator
import me.gamercoder215.battlecards.util.isCard
import me.gamercoder215.battlecards.util.playSuccess
import me.gamercoder215.battlecards.wrapper.Wrapper
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.get
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

interface CommandWrapper {

    companion object {
        @JvmStatic
        val COMMANDS: Map<String, List<String>> = ImmutableMap.builder<String, List<String>>()
            .put("bcard", listOf("card", "battlecard"))
            .put("cardreload", listOf("creload"))
            .put("bquery", listOf("cardquery", "battlequery"))
            .build()

        @JvmStatic
        val COMMAND_PERMISSION: Map<String, String> = ImmutableMap.builder<String, String>()
            .put("bcard", "battlecards.user.card")
            .put("cardreload", "battlecards.admin.reload")
            .put("bquery", "battlecards.user.query")
            .build()

        @JvmStatic
        val COMMAND_DESCRIPTION: Map<String, String> = ImmutableMap.builder<String, String>()
            .put("bcard", "Main BattleCards Card Command")
            .put("cardreload", "Reloads the BattleCards Plugin")
            .put("bquery", "Command for Querying BattleCards Cards")
            .build()

        @JvmStatic
        val COMMAND_USAGE: Map<String, String> = ImmutableMap.builder<String, String>()
            .put("bcard", "/bcard")
            .put("cardreload", "/cardreload")
            .put("bquery", "/bquery <card>")
            .build()

        @JvmStatic
        fun getError(key: String): String = "${get("plugin.prefix")} ${ChatColor.RED}${get(key)}"

        @JvmStatic
        fun getSuccess(key: String): String = "${get("plugin.prefix")} ${ChatColor.RED}${get(key)}"
    }

    fun reloadPlugin(sender: CommandSender) {
        sender.sendMessage(getSuccess("command.reload.reloading"))

        val plugin = BattleConfig.getPlugin()

        plugin.reloadConfig()
        BattleConfig.loadConfig()
        Wrapper.getCommandWrapper()

        sender.sendMessage(getSuccess("command.reload.reloaded"))
    }

    fun cardInfo(p: Player) {
        if (p.inventory.itemInHand == null) {
            p.sendMessage(getError("error.argument.item.held"))
            return
        }

        val item = p.inventory.itemInHand
        if (!item.isCard()) {
            p.sendMessage(getError("error.argument.item.held.card"))
            return
        }

        p.openInventory(Generator.generateCardInfo(item.card!!))
        p.playSuccess()
    }
    
    fun createCard(p: Player, type: BattleCardType) {
        if (p.inventory.firstEmpty() == -1) {
            p.sendMessage(getError("error.inventory.full"))
            return
        }
        
        p.inventory.addItem(CardGenerator.toItem(type.createCardData()))
        p.sendMessage(format(getSuccess("success.card.created"), type.formatName()))
        p.playSuccess()
    }

}