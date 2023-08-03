package me.gamercoder215.battlecards.wrapper.commands

import com.google.common.collect.ImmutableMap
import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.ICard
import me.gamercoder215.battlecards.util.CardUtils.format
import me.gamercoder215.battlecards.util.cardInHand
import me.gamercoder215.battlecards.util.formatName
import me.gamercoder215.battlecards.util.inventory.CardGenerator
import me.gamercoder215.battlecards.util.inventory.Generator
import me.gamercoder215.battlecards.util.inventory.Items
import me.gamercoder215.battlecards.util.playSuccess
import me.gamercoder215.battlecards.wrapper.Wrapper
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.get
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
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
        fun getSuccess(key: String): String = "${get("plugin.prefix")} ${ChatColor.GREEN}${get(key)}"
    }

    fun reloadPlugin(sender: CommandSender) {
        sender.sendMessage(getSuccess("command.reload.reloading"))

        val plugin = BattleConfig.plugin

        plugin.reloadConfig()
        BattleConfig.loadConfig()
        Wrapper.getCommandWrapper()

        sender.sendMessage(getSuccess("command.reload.reloaded"))
    }

    fun cardInfo(p: Player) {
        if (!p.hasPermission("battlecards.user.info"))
            return p.sendMessage(getError("error.permission"))

        if (p.inventory.itemInHand == null)
            return p.sendMessage(getError("error.argument.item.held"))

        p.openInventory(Generator.generateCardInfo(p.cardInHand ?: return p.sendMessage(getError("error.argument.item.held.card"))))
        p.playSuccess()
    }
    
    fun createCard(p: Player, type: BattleCardType, basicType: EntityType? = null) {
        if (!p.hasPermission("battlecards.admin.card.create"))
            return p.sendMessage(getError("error.permission.argument"))

        if (p.inventory.firstEmpty() == -1)
            return p.sendMessage(getError("error.inventory.full"))

        if (type == BattleCardType.BASIC)
            if (basicType == null || !BattleConfig.getValidBasicCards().contains(basicType))
                return p.sendMessage(getError("error.argument.basic_type"))

        p.inventory.addItem(CardGenerator.toItem(type.createCardData().apply { this as ICard; storedEntityType = basicType } ))
        p.sendMessage(format(getSuccess("success.card.created"), type.formatName()))
        p.playSuccess()
    }

    fun queryCard(p: Player, type: BattleCardType) {
        if (!p.hasPermission("battlecards.user.query"))
            return p.sendMessage(getError("error.permission.argument"))

        p.openInventory(Generator.generateCardInfo(type.createCardData()))
        p.playSuccess()
    }

    fun editCard(p: Player, action: (ICard) -> Unit) {
        if (!p.hasPermission("battlecards.admin.card.edit"))
            return p.sendMessage(getError("error.permission.argument"))

        val card = p.cardInHand ?: return p.sendMessage(getError("error.argument.item.held.card"))

        p.inventory.itemInHand = CardGenerator.toItem(
            card.apply { action(this) }
        )
        p.playSuccess()
    }

    fun giveItem(p: Player, id: String) {
        if (!p.hasPermission("battlecards.admin.items"))
            return p.sendMessage(getError("error.permission.argument"))

        val item = Items.PUBLIC_ITEMS[id] ?: return p.sendMessage(getError("error.argument.item"))

        p.inventory.addItem(item)
        p.sendMessage(getSuccess("success.item.given"))
        p.playSuccess()
    }

}