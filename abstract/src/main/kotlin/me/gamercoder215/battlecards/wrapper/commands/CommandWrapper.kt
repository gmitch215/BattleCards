package me.gamercoder215.battlecards.wrapper.commands

import com.google.common.collect.ImmutableMap
import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.ICard
import me.gamercoder215.battlecards.messages.sendError
import me.gamercoder215.battlecards.messages.sendSuccess
import me.gamercoder215.battlecards.util.*
import me.gamercoder215.battlecards.util.inventory.CardGenerator
import me.gamercoder215.battlecards.util.inventory.Generator
import me.gamercoder215.battlecards.util.inventory.Items
import me.gamercoder215.battlecards.wrapper.Wrapper
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.util.*

interface CommandWrapper {

    companion object {
        val COMMANDS: Map<String, List<String>> = ImmutableMap.builder<String, List<String>>()
            .put("bcard", listOf("card", "battlecard"))
            .put("cardreload", listOf("creload"))
            .put("bquery", listOf("cardquery", "battlequery"))
            .build()

        val COMMAND_PERMISSION: Map<String, String> = ImmutableMap.builder<String, String>()
            .put("bcard", "battlecards.user.card")
            .put("cardreload", "battlecards.admin.reload")
            .put("bquery", "battlecards.user.query")
            .build()

        val COMMAND_DESCRIPTION: Map<String, String> = ImmutableMap.builder<String, String>()
            .put("bcard", "Main BattleCards Card Command")
            .put("cardreload", "Reloads the BattleCards Plugin")
            .put("bquery", "Command for Querying BattleCards Cards")
            .build()

        val COMMAND_USAGE: Map<String, String> = ImmutableMap.builder<String, String>()
            .put("bcard", "/bcard")
            .put("cardreload", "/cardreload")
            .put("bquery", "/bquery <card>")
            .build()

        private val COOLDOWN: MutableMap<String, MutableMap<UUID, Long>> = mutableMapOf()
    }

    fun reloadPlugin(sender: CommandSender) {
        sender.sendSuccess("command.reload.reloading")

        val plugin = BattleConfig.plugin

        plugin.reloadConfig()
        BattleConfig.loadConfig()
        Wrapper.getCommandWrapper()
        sender.sendSuccess("command.reload.reloaded")
    }

    fun cardInfo(p: Player) {
        if (!p.hasPermission("battlecards.user.info"))
            return p.sendError("error.permission")

        if (p.inventory.itemInHand == null)
            return p.sendError("error.argument.item.held")

        val card = p.cardInHand ?: return p.sendError("error.argument.item.held.card")
        card.statistics.checkQuestCompletions()
        p.inventory.itemInHand = CardGenerator.toItem(card)

        p.openInventory(Generator.generateCardInfo(card))
        p.playSuccess()
    }
    
    fun createCard(p: Player, type: BattleCardType, basicType: EntityType? = null) {
        if (!p.hasPermission("battlecards.admin.card.create"))
            return p.sendError("error.permission.argument")

        if (type.isDisabled)
            return p.sendError("error.card.disabled")

        if (p.inventory.firstEmpty() == -1)
            return p.sendError("error.inventory.full")

        if (type == BattleCardType.BASIC)
            if (basicType == null || !BattleConfig.getValidBasicCards().contains(basicType))
                return p.sendError("error.argument.basic_type")

        p.inventory.addItem(CardGenerator.toItem(type().apply { this as ICard; storedEntityType = basicType } ))
        p.sendSuccess("success.card.created", type.formatName())
        p.playSuccess()
    }

    fun queryCard(p: Player, type: BattleCardType) {
        if (!p.hasPermission("battlecards.user.query"))
            return p.sendError("error.permission.argument")

        if (type.isDisabled)
            return p.sendError("error.card.disabled")

        p.openInventory(Generator.generateCardInfo(type()))
        p.playSuccess()
    }

    fun editCard(p: Player, action: (ICard) -> Unit) {
        if (!p.hasPermission("battlecards.admin.card.edit"))
            return p.sendError("error.permission.argument")

        val card = p.cardInHand ?: return p.sendError("error.argument.item.held.card")

        p.inventory.itemInHand = CardGenerator.toItem(
            card.apply { action(this) }
        )
        p.playSuccess()
    }

    fun catalogue(p: Player, input: String) {
        if (!p.hasPermission("battlecards.user.query"))
            return p.sendError("error.permission.argument")

        if (BattleCardType.entries.map { it.name.lowercase() }.contains(input.lowercase())) {
            val type = BattleCardType.valueOf(input.uppercase())

            if (type.isDisabled)
                return p.sendError("error.card.disabled")

            if (type == BattleCardType.BASIC)
                return p.sendError("error.argument.basic_type")

            p.openInventory(Generator.generateCatalogue(type()))
        }

        if (BattleConfig.config.registeredEquipment.any { it.name.lowercase() == input.lowercase() }) {
            val equipment = BattleConfig.config.registeredEquipment.first { it.name.lowercase() == input.lowercase() }

            p.openInventory(Generator.generateCatalogue(equipment))
        }

        p.playSuccess()
    }

    fun giveItem(p: Player, id: String) {
        if (!p.hasPermission("battlecards.admin.items"))
            return p.sendError("error.permission.argument")

        val item = Items.PUBLIC_ITEMS[id] ?: return p.sendError("error.argument.item")

        p.inventory.addItem(item)
        p.sendSuccess("success.item.given")
        p.playSuccess()
    }

    private fun cooldown(id: String, p: Player): Long {
        val map = COOLDOWN[id] ?: mutableMapOf()
        return map[p.uniqueId] ?: 0
    }

    private fun cooldown(id: String, p: Player, amount: Long) {
        val map = COOLDOWN[id] ?: mutableMapOf()

        map[p.uniqueId] = System.currentTimeMillis() + amount
        COOLDOWN[id] = map
    }

    fun despawnCards(p: Player) {
        if (cooldown("despawn", p) > System.currentTimeMillis() && !p.hasPermission("battlecards.admin.cooldown"))
            return p.sendError("error.cooldown", (cooldown("despawn", p) - System.currentTimeMillis()).formatTime())

        if (!p.hasPermission("battlecards.admin.cooldown"))
            cooldown("despawn", p, 1000 * 30)

        p.spawnedCards.forEach { it.despawn() }
        p.playSuccess()
    }

}