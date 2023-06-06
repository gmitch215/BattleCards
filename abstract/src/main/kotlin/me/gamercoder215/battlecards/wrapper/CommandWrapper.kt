package me.gamercoder215.battlecards.wrapper

import com.google.common.collect.ImmutableMap

interface CommandWrapper {

    companion object {
        @JvmStatic
        val COMMANDS: Map<String, List<String>> = ImmutableMap.builder<String, List<String>>()
            .put("bcard", listOf("card", "battlecard"))
            .build()

        @JvmStatic
        val COMMAND_PERMISSIONS: Map<String, String> = ImmutableMap.builder<String, String>()
            .put("bcard", "battlecards.user.card")
            .build()

        @JvmStatic
        val COMMAND_DESCRIPTIONS: Map<String, String> = ImmutableMap.builder<String, String>()
            .put("bcard", "Main BattleCards Card Command")
            .build()

        @JvmStatic
        val COMMAND_USAGES: Map<String, String> = ImmutableMap.builder<String, String>()
            .put("bcard", "/bcard")
            .build()
    }

}