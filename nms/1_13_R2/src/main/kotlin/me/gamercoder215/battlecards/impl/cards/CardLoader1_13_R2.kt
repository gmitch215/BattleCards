package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.util.isCard
import me.gamercoder215.battlecards.wrapper.CardLoader
import org.bukkit.entity.Zombie
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityTransformEvent

internal class CardLoader1_13_R2 : CardLoader, Listener {
    override fun loadedCards(): Collection<Class<out IBattleCard<*>>> = listOf()

    @EventHandler
    fun onTransform(event: EntityTransformEvent) {
        val entity = event.entity
        if (!entity.isCard) return

        event.isCancelled = true
    }

    @EventHandler
    fun onSpawn(event: CreatureSpawnEvent) {
        val entity = event.entity
        if (!entity.isCard) return

        if (entity is Zombie)
            entity.conversionTime = Int.MAX_VALUE
    }
}