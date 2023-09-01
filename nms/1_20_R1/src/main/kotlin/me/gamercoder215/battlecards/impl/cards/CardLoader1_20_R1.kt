package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.item.CardEquipment
import me.gamercoder215.battlecards.util.nbt
import me.gamercoder215.battlecards.wrapper.CardLoader
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareInventoryResultEvent

internal class CardLoader1_20_R1 : CardLoader, Listener {

    override fun loadedCards(): Collection<Class<out IBattleCard<*>>> = listOf(
        ISeaLord::class.java,
        IPurpleParasite::class.java
    )

    override fun loadedEquipment(): Collection<CardEquipment> = CardEquipments1_20_R1.entries

    @EventHandler
    fun result(event: PrepareInventoryResultEvent) {
        val inv = event.inventory

        if (inv.filterNotNull().any { it.nbt.hasTag("nointeract") })
            event.result = null
    }

}