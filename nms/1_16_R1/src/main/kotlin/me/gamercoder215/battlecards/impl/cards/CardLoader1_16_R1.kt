package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.util.inventory.Items
import me.gamercoder215.battlecards.util.inventory.Items.randomCumulative
import me.gamercoder215.battlecards.wrapper.CardLoader
import me.gamercoder215.battlecards.wrapper.Wrapper
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.r
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkPopulateEvent
import org.bukkit.event.world.LootGenerateEvent
import kotlin.math.roundToInt

internal class CardLoader1_16_R1 : CardLoader, Listener {

    override fun loadedCards(): Collection<Class<out IBattleCard<*>>> = listOf(
        INetherPrince::class.java
    )

    @EventHandler
    fun onGenerate(event: LootGenerateEvent) {
        if (event.isPlugin || event.isCancelled) return
        if (event.lootContext.lootedEntity != null) return
        if (event.inventoryHolder?.inventory == null) return

        val luck = event.lootContext.luck
        if (r.nextDouble() < 0.3) return

        val limit = (luck / 0.5F).toInt().coerceAtMost(3)

        for (i in 0 until limit) {
            val item = Items.EFFECTIVE_GENERATED_ITEMS.randomCumulative(luck.toInt().coerceAtMost(10))
            event.loot.add(item)
        }
    }

}