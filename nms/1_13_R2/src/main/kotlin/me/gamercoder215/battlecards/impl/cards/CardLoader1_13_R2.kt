package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.util.isCard
import me.gamercoder215.battlecards.util.isMinion
import me.gamercoder215.battlecards.wrapper.CardLoader
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityTransformEvent

internal class CardLoader1_13_R2 : CardLoader, Listener {
    override fun loadedCards(): Collection<Class<out IBattleCard<*>>> = listOf()

    @EventHandler
    fun onTransform(event: EntityTransformEvent) {
        val entity = event.entity

        if (entity.isCard || entity.isMinion)
            event.isCancelled = true
    }
}