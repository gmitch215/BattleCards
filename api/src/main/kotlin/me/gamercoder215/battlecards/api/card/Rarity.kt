package me.gamercoder215.battlecards.api.card

import org.bukkit.ChatColor

/**
 * Represents a BattleCard's Rarity
 */
enum class Rarity(
    color: ChatColor,
    experienceModifier: Double = 1.0,
    maxCardLevel: Int = Card.MAX_LEVEL
) {
    /**
     * Represents the Basic rarity
     */
    BASIC(ChatColor.WHITE, 0.9),
    /**
     * Represents the Common rarity
     */
    COMMON(ChatColor.GREEN),
    /**
     * Represents the Uncommon rarity
     */
    UNCOMMON(ChatColor.DARK_GREEN, 1.1, 170),
    /**
     * Represents the Rare rarity
     */
    RARE(ChatColor.BLUE, 1.25, 140),
    /**
     * Represents the Epic rarity
     */
    EPIC(ChatColor.DARK_PURPLE, 1.45, 100),
    /**
     * Represents the Legend rarity
     */
    LEGEND(ChatColor.GOLD, 1.6, 75),
    /**
     * Represents the Mythical rarity
     */
    MYTHICAL(ChatColor.LIGHT_PURPLE, 1.95, 50),
    /**
     * Represents the Ultimate rarity
     */
    ULTIMATE(ChatColor.AQUA, 2.2, 30),

    ;

    /**
     * Fetches the color of this rarity.
     * @return Rarity Color
     */
    val color: ChatColor

    /**
     * Fetches the experience modifier for this rarity, which is used to calculate the amount of experience a player needs to increase the Card's Level.
     * @return Experience Modifier
     */
    val experienceModifier: Double

    /**
     * Fetches the maximum level a BattleCard can be for this rarity.
     * @return Maximum Card Level
     */
    val maxCardLevel: Int

    val maxCardExperience: Double
        /**
         * Fetches the maximum experience a BattleCard can have for this rarity.
         * @return Maximum Card Experience
         */
        get() = BattleCard.toExperience(maxCardLevel, this)

    init {
        this.color = color
        this.experienceModifier = experienceModifier
        this.maxCardLevel = maxCardLevel
    }

    override fun toString(): String {
        return "$color${name.uppercase()}"
    }

}