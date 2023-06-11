package me.gamercoder215.battlecards.wrapper.commands

import com.google.common.collect.ImmutableMap
import me.gamercoder215.battlecards.util.getCard
import me.gamercoder215.battlecards.util.inventory.Generator
import me.gamercoder215.battlecards.util.isCard
import me.gamercoder215.battlecards.util.playSuccess
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.get
import org.bukkit.entity.Player

interface CommandWrapper {

    companion object {
        @JvmStatic
        val COMMANDS: Map<String, List<String>> = ImmutableMap.builder<String, List<String>>()
            .put("bcard", listOf("card", "battlecard"))
            .put("bquery", listOf("cardquery", "battlequery"))
            .build()

        @JvmStatic
        val COMMAND_PERMISSION: Map<String, String> = ImmutableMap.builder<String, String>()
            .put("bcard", "battlecards.user.card")
            .put("bquery", "battlecards.user.query")
            .build()

        @JvmStatic
        val COMMAND_DESCRIPTION: Map<String, String> = ImmutableMap.builder<String, String>()
            .put("bcard", "Main BattleCards Card Command")
            .put("bquery", "Command for Querying BattleCards Cards")
            .build()

        @JvmStatic
        val COMMAND_USAGE: Map<String, String> = ImmutableMap.builder<String, String>()
            .put("bcard", "/bcard")
            .put("bquery", "/bquery <card>")
            .build()
    }

    fun cardInfo(p: Player) {
        if (p.inventory.itemInHand == null) {
            p.sendMessage(get("error.argument.item.held"))
            return
        }

        val item = p.inventory.itemInHand
        if (!item.isCard()) {
            p.sendMessage(get("error.argument.item.held.card"))
            return
        }

        p.openInventory(Generator.generateCardInfo(item.getCard()!!))
        p.playSuccess()
    }

}