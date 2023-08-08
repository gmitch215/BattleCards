package me.gamercoder215.battlecards.api.card

import org.bukkit.Material

/**
 * Represents a Quest Road for a BattleCard
 */
enum class CardQuest(
    icon: Material,
    maxLevel: Int,
    private val completion: (Card, Int) -> Double,
    private val expReward: (Int) -> Double
) {

    /**
     * Card Kills Quest
     */
    CARD_HUNTER(Material.matchMaterial("FILLED_MAP") ?: Material.MAP, 50, completion@{ card, current ->
        val needed = 15 + (10 * current) + (5 * current.minus(1))
        return@completion card.statistics.cardKills.toDouble() / needed
    }, { level -> (100.0 * level) + (50.0 * level.minus(1)) }),

    /**
     * Entity & Player Kills Quest
     */
    ENTITY_HUNTER(Material.matchMaterial("SPAWNER") ?: Material.matchMaterial("MOB_SPAWNER")!!, 100, completion@{ card, current ->
        val needed = 30 + (20 * current) + (15 * current.minus(1))
        return@completion (card.statistics.entityKills + card.statistics.playerKills).toDouble() / needed
    }, { level -> (75.0 * level) + (25.0 * level.minus(1)) }),

    /**
     * Damage Dealt Quest
     */
    DAMAGER(Material.DIAMOND_SWORD, 60, completion@{ card, current ->
        val needed = 250.0 + (100.0 * current) + (50.0 * current.minus(1))
        return@completion card.statistics.damageDealt / needed
    }, { level -> (100.0 * level) + (50.0 * level.minus(1)) }),

    /**
     * Damage Received Quest
     */
    TANK(Material.IRON_CHESTPLATE, 60, completion@{ card, current ->
        val needed = 350.0 + (125.0 * current) + (55.0 * current.minus(1))
        return@completion card.statistics.damageReceived / needed
    }, { level -> (150.0 * level) + (50.0 * level.minus(1)) }),

    /**
     * Deaths Quest
     */
    REVIVER(Material.matchMaterial("TOTEM_OF_UNDYING") ?: Material.matchMaterial("TOTEM") ?: Material.DIAMOND, 30, completion@{ card, current ->
        val needed = 20 + (5 * current) + (5 * current)
        return@completion card.statistics.deaths.toDouble() / needed
    }, { level -> (40.0 * level) + (20.0 * level.minus(1)) })

    ;

    /**
     * The maximum level for this Card Achievement.
     */
    val maxLevel: Int

    /**
     * The icon for the Card Quest.
     */
    val icon: Material

    init {
        this.maxLevel = maxLevel
        this.icon = icon
    }

    /**
     * Fetches the current quest level for a Card.
     * @param card Card to Check
     * @return Current Quest Level
     */
    fun getCurrentLevel(card: Card): Int {
        var i = 0
        while (completion(card, i) >= 1.0)
            i++

        return i
    }

    /**
     * Fetches the current percentage progress of this Quest, between 0.0 and 1.0.
     * @param card Card to Check
     * @return Current Quest Level
     */
    fun getProgress(card: Card): Double {
        return completion(card, getCurrentLevel(card))
    }

}