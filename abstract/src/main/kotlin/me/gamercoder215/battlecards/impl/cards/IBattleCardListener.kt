package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.impl.Listener
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.RegisteredListener

class IBattleCardListener(plugin: Plugin) : org.bukkit.event.Listener {

    init {
        Bukkit.getPluginManager().registerEvents(this, plugin)

        val reg = RegisteredListener(this, { _, e -> onEvent(e) }, EventPriority.NORMAL, plugin, false)
        HandlerList.getHandlerLists().forEach { it.register(reg) }
    }

    fun onEvent(e: Event) {
        IBattleCard.spawned.values.forEach { card ->
            card.javaClass.declaredMethods.filter { it.isAnnotationPresent(Listener::class.java) }.forEach {
                if (it.parameterCount != 1) throw IllegalStateException("Listener method must have 1 parameter: ${it.name} in ${card.javaClass.name}")
                val parameter = it.parameterTypes[0]

                if (parameter.isAssignableFrom(e.javaClass)) {
                    it.isAccessible = true
                    it.invoke(card, e)
                }
            }
        }
    }

}