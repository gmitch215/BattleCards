package me.gamercoder215.battlecards.placeholderapi

import com.google.common.collect.ImmutableMap
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import me.gamercoder215.battlecards.BattleCards
import me.gamercoder215.battlecards.util.cards
import me.gamercoder215.battlecards.util.formatInt
import me.gamercoder215.battlecards.util.spawnedCards
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player


internal class BattlePlaceholders(private val plugin: BattleCards) : PlaceholderExpansion() {

    init {
        register()
    }

    companion object {

        private val PLACEHOLDERS = ImmutableMap.builder<String, (OfflinePlayer) -> Any>()
            .put("card_count") { p ->
                if (p !is Player) return@put 0
                p.inventory.cards.size.formatInt()
            }
            .put("spawned_card_count") { p ->
                if (p !is Player) return@put 0
                p.spawnedCards.size.formatInt()
            }
            .put("total_card_level") { p ->
                if (p !is Player) return@put 0
                p.inventory.cards.values.sumOf { it.level }.formatInt()
            }
            .build()
    }

    override fun getIdentifier(): String = plugin.name.lowercase()
    override fun getAuthor(): String = plugin.description.authors[0]
    override fun getVersion(): String = plugin.description.version

    // Implementation

    override fun getPlaceholders(): List<String> = PLACEHOLDERS.keys.asList()
    override fun onRequest(p: OfflinePlayer, arg: String): String = PLACEHOLDERS[arg]?.let { it(p) }.toString()
}