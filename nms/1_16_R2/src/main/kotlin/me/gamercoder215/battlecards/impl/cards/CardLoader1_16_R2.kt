package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.events.entity.CardSpawnEvent
import me.gamercoder215.battlecards.wrapper.CardLoader
import org.bukkit.entity.PiglinAbstract
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

internal class CardLoader1_16_R2 : CardLoader, Listener {

    override fun loadedCards(): Collection<Class<out IBattleCard<*>>> = listOf(
        INetheritePiglin::class.java,
        IGoldenWizard::class.java,
        IMagmaJockey::class.java
    )

    @EventHandler
    fun onSpawn(event: CardSpawnEvent) {
        val entity = event.entity
        if (entity is PiglinAbstract)
            entity.isImmuneToZombification = true
    }

}