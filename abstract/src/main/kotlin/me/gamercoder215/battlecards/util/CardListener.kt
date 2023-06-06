package me.gamercoder215.battlecards.util

import me.gamercoder215.battlecards.impl.Defensive
import me.gamercoder215.battlecards.impl.Offensive
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.r
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.plugin.Plugin

class CardListener(plugin: Plugin) : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onHitAttachment(event: EntityDamageEvent) {
        val entity = event.entity
        if (!entity.hasMetadata("battlecards:block_attachment")) return
        event.isCancelled = true
    }

    @EventHandler
    fun onAttack(event: EntityDamageByEntityEvent) {
        if (event.entity.isCard()) {
            val card = event.entity.getCard()
            if (card != null) {
                val defensive = card.javaClass.declaredMethods.filter { it.isAnnotationPresent(Defensive::class.java) }
                for (method in defensive) {
                    val annotation = method.getDeclaredAnnotation(Defensive::class.java)
                    var chance = annotation.chance

                    if (!annotation.value.isNaN())
                        for (i in 1 until card.getLevel()) chance = annotation.operation.apply(chance, annotation.value)

                    chance = chance.coerceAtMost(annotation.max)
                    if ((r.nextDouble() * annotation.max) <= chance)
                        method.invoke(card, event)
                }
            }
        }

        if (event.damager.isCard()) {
            val card = event.damager.getCard()
            if (card != null) {
                val offensive = card.javaClass.declaredMethods.filter { it.isAnnotationPresent(Offensive::class.java) }
                for (method in offensive) {
                    val annotation = method.getDeclaredAnnotation(Defensive::class.java)
                    var chance = annotation.chance

                    if (!annotation.value.isNaN())
                        for (i in 1 until card.getLevel()) chance = annotation.operation.apply(chance, annotation.value)

                    chance = chance.coerceAtMost(annotation.max)
                    if ((r.nextDouble() * annotation.max) <= chance)
                        method.invoke(card, event)
                }
            }
        }
    }

}