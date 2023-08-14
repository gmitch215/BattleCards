package me.gamercoder215.battlecards.api.card

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.CardQuest.Util.roundTo
import org.bukkit.Material
import java.lang.String.format
import kotlin.math.pow
import kotlin.math.roundToLong

/**
 * Represents a Quest Road for a BattleCard
 */
enum class CardQuest(
    icon: Material,
    maxLevel: Int,
    private val needed: (Card, Int) -> Number,
    private val completion: (Card, Double) -> Number,
    private val expReward: (Card, Int) -> Double,
) {

    /**
     * Card Kills Quest
     */
    CARD_HUNTER(Material.matchMaterial("FILLED_MAP") ?: Material.MAP, 50,
        { card, current -> (15.0 + (10 * current) + (5 * current.minus(1))).pow(1 + card.rarity.ordinal.times(0.07)).roundTo(50) },
        { card, needed -> card.statistics.cardKills / needed },
        { card, level -> (100.0 * level) + (50.0 * level.minus(1)).pow(1 + card.rarity.ordinal.times(0.21)) }
    ),

    /**
     * Entity & Player Kills Quest
     */
    ENTITY_HUNTER(Material.matchMaterial("SPAWNER") ?: Material.matchMaterial("MOB_SPAWNER")!!, 100,
        { card, current -> (30.0 + (20 * current) + (15 * current.minus(1))).pow(1 + card.rarity.ordinal.times(0.07)).roundTo(100) },
        { card, needed -> (card.statistics.entityKills + card.statistics.playerKills) / needed },
        { card, level -> (75.0 * level) + (25.0 * level.minus(1)).pow(1 + card.rarity.ordinal.times(0.21)) }
    ),

    /**
     * Damage Dealt Quest
     */
    DAMAGER(Material.DIAMOND_SWORD, 60,
        { card, current -> (250.0 + (100.0 * current) + (50.0 * current.minus(1))).pow(1 + card.rarity.ordinal.times(0.07)).roundTo(500) },
        { card, needed -> card.statistics.damageDealt / needed },
        { card, level -> (100.0 * level) + (50.0 * level.minus(1)).pow(1 + card.rarity.ordinal.times(0.17)) }
    ),

    /**
     * Damage Received Quest
     */
    TANK(Material.IRON_CHESTPLATE, 60,
        { card, current -> (350.0 + (125.0 * current) + (55.0 * current.minus(1))).pow(1 + card.rarity.ordinal.times(0.07)).roundTo(500) },
        { card, needed -> card.statistics.damageReceived / needed },
        { card, level -> (150.0 * level) + (50.0 * level.minus(1)).pow(1 + card.rarity.ordinal.times(0.185)) }
    ),

    /**
     * Deaths Quest
     */
    REVIVER(Material.matchMaterial("TOTEM_OF_UNDYING") ?: Material.matchMaterial("TOTEM") ?: Material.DIAMOND, 30,
        { card, current -> (20.0 + (5 * current) + (5 * current)).pow(1 + card.rarity.ordinal.times(0.07)).roundTo(5) },
        { card, needed -> card.statistics.deaths.toDouble() / needed },
        { card, level -> (40.0 * level) + (20.0 * level.minus(1)).pow(1 + card.rarity.ordinal.times(0.26)) }
    )

    ;

    private val completionKey = "menu.card_quests.${name.lowercase()}.completion"

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

    private val stackedNeeded: (Card, Int) -> Double = needed@{ card, level ->
        var amount = 0.0
        for (i in level downTo 1)
            amount += needed(card, i).toDouble()

        return@needed amount
    }

    /**
     * Fetches the latest quest level completed for a Card.
     * @param card Card to Check
     * @return Current Quest Level
     */
    fun getCurrentLevel(card: Card): Int {
        var i = 0
        while (completion(card, stackedNeeded(card, i)).toDouble() >= 1.0)
            i++

        return i - 1
    }

    /**
     * Fetches the current percentage progress of this Quest at a specific level, between 0.0 and 1.0 for the completion until the next level.
     * @param card Card to Check
     * @param level Current Quest Level of Card, defaults to [getCurrentLevel] `+ 1`
     * @return Current Quest Level
     */
    fun getProgressPercentage(card: Card, level: Int = getCurrentLevel(card) + 1): Double {
        return completion(card, stackedNeeded(card, level)).toDouble().coerceAtMost(1.0)
    }

    /**
     * Fetches the localized progress of this Quest, formatted for the Card Menu.
     * @param card Card to Use
     * @param level Level to Use, defaults to [getCurrentLevel] `+ 1`
     * @return Localized Progress Message according to Card
     */
    fun getLocalizedProgress(card: Card, level: Int = getCurrentLevel(card) + 1): String = format(
        BattleConfig.config.locale, BattleConfig.config.get(completionKey),
        format(BattleConfig.config.locale, "%,.2f", stackedNeeded(card, level)).dropLastWhile { it == '0' }.dropLastWhile { it == '.' }
    )

    /**
     * Fetches the card experience reward amount for this Quest. Experience Rewards can be Card-Specific and can factor in things like rarity.
     * @param card Card to Use
     * @param level Level to Use, Defaults to Next Unlocked Level
     * @return Experience Reward Amount
     */
    fun getExperienceReward(card: Card, level: Int = getCurrentLevel(card) + 1): Double {
        if (level < 0) throw IllegalArgumentException("Level cannot be less than 0!")
        if (level == 0) return 0.0

        return expReward(card, level)
    }

    private object Util {

        fun Number.roundTo(factor: Int): Long {
            val l = this.toDouble().roundToLong()
            val rem = l % factor

            return if (l >= factor / 2) l - rem + factor else l - rem
        }

    }

}