package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.item.CardEquipment
import me.gamercoder215.battlecards.util.isCard
import me.gamercoder215.battlecards.util.nbt
import me.gamercoder215.battlecards.wrapper.CardLoader
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.r
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Creature
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.PrepareAnvilEvent

internal class CardLoader1_9_R1 : CardLoader, Listener {

    override fun loadedCards(): Collection<Class<out IBattleCard<*>>> = listOf(
        IEyeOfEnderman::class.java
    )

    override fun loadedEquipment(): Collection<CardEquipment> = CardEquipments1_9_R1.entries

    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        if (!event.entity.isCard) return

        val entity = event.entity as? Creature ?: return
        if (entity.equipment.itemInOffHand.type == Material.SHIELD)
            if (r.nextDouble() < 0.3) {
                event.isCancelled = true
                entity.world.playSound(entity.location, Sound.ITEM_SHIELD_BLOCK, 2F, 1F)
            }
    }

    @EventHandler
    fun anvil(event: PrepareAnvilEvent) {
        val inv = event.inventory

        if (inv.filterNotNull().any { it.nbt.hasTag("nointeract") })
            event.result = null
    }

}