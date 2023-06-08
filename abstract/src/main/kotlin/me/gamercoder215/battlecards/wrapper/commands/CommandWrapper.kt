package me.gamercoder215.battlecards.wrapper.commands

import com.google.common.collect.ImmutableMap

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

}