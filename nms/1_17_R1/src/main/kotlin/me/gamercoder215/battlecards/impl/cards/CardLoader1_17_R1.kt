package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.item.CardEquipment
import me.gamercoder215.battlecards.util.inventory.Items.randomCumulative
import me.gamercoder215.battlecards.util.itemStack
import me.gamercoder215.battlecards.wrapper.CardLoader
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.r
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PiglinBarterEvent

internal class CardLoader1_17_R1 : CardLoader, Listener {

    override fun loadedCards(): Collection<Class<out IBattleCard<*>>> = listOf(
        IFrostBear::class.java,
        IGoatGladiator::class.java
    )

    override fun loadedEquipment(): Collection<CardEquipment> = CardEquipments1_17_R1.entries

    @EventHandler
    fun onBarter(event: PiglinBarterEvent) {
        if (r.nextDouble() > 0.1) return

        val equipment: Map<CardEquipment, Double> = BattleConfig.config.registeredEquipment
            .filter { it.damageModifier > 1.0 || it.defenseModifier > 1.0 }
            .associateWith { 1.0 / (it.damageModifier + it.defenseModifier).times(it.rarity.ordinal + 1) }

        event.outcome.clear()
        for (i in 0 until r.nextInt(1, 3))
            equipment.randomCumulative(r.nextInt(2)).let {
                if (it == null) return@let
                event.outcome.add(it.itemStack)
            }
    }

}