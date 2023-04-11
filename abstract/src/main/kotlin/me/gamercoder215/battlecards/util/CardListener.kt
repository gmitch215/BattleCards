package me.gamercoder215.battlecards.util

import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

class CardListener(plugin: Plugin) : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

}