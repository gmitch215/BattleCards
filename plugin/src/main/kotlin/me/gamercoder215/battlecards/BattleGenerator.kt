package me.gamercoder215.battlecards

import me.gamercoder215.battlecards.util.inventory.Items
import me.gamercoder215.battlecards.util.inventory.Items.randomCumulative
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.r
import org.bukkit.Chunk
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.Chest
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.world.ChunkPopulateEvent

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

        if (r.nextDouble() < (1.0 / entity.maxHealth)) return

        val item = Items.EFFECTIVE_GENERATED_ITEMS.randomCumulative(rerolls)
        event.drops.add(item)
    }
}