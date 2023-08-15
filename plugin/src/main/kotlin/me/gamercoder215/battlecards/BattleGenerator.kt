package me.gamercoder215.battlecards

import me.gamercoder215.battlecards.util.inventory.Items
import me.gamercoder215.battlecards.util.inventory.Items.random
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

internal class BattleGenerator(private val plugin: BattleCards) : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onDeath(event: EntityDeathEvent) {
        val entity = event.entity
        val p = entity.killer ?: return
        var rerolls = 0

        if (p.itemInHand.containsEnchantment(Enchantment.LOOT_BONUS_MOBS))
            rerolls += p.itemInHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS)

        rerolls = rerolls.coerceAtMost(10)
        val item = Items.EFFECTIVE_GENERATED_ITEMS.random(rerolls) ?: return
        event.drops.add(item)
    }
}