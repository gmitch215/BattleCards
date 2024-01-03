package me.gamercoder215.battlecards

import me.gamercoder215.battlecards.api.card.Rarity
import me.gamercoder215.battlecards.util.inventory.Items
import me.gamercoder215.battlecards.util.inventory.Items.random
import me.gamercoder215.battlecards.util.inventory.Items.randomCumulative
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.r
import org.bukkit.World
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDeathEvent
import kotlin.math.pow

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
        val item = Items.EFFECTIVE_GENERATED_ITEMS().random(rerolls) ?: return
        event.drops.add(item)
    }

    private companion object {

        private val rarityChances = Rarity.entries.filter { it != Rarity.BASIC }.associateWith {
            (2.0 / 10.0.pow(it.ordinal))
        }

    }

    @EventHandler
    fun onMine(event: BlockBreakEvent) {
        val block = event.block
        val p = event.player

        var luck = 0.0
        if (p.itemInHand.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS))
            luck += p.itemInHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS)

        if (block.world.environment == World.Environment.NORMAL && block.y < 48)
            if (r.nextDouble() < 0.08.plus(luck / 100.0).coerceAtMost(0.5))
                block.world.dropItemNaturally(block.location, Items.cardShard(rarityChances.randomCumulative()!!))
    }
}