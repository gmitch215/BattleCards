package me.gamercoder215.battlecards.api.card

import org.bukkit.ChatColor

/**
 * Represents a BattleCard's Rarity
 */
enum class Rarity(
    color: ChatColor,
    experienceModifier: Double,
    maxCardLevel: Int = Card.MAX_LEVEL
) {
    /**
     * Represents the Basic rarity
     */
    BASIC(ChatColor.WHITE, 1.05),
    /**
     * Represents the Common rarity
     */
    COMMON(ChatColor.GREEN, 1.075),
    /**
     * Represents the Uncommon rarity
     */
    UNCOMMON(ChatColor.DARK_GREEN, 1.1, 175),
    /**
     * Represents the Rare rarity
     */
    RARE(ChatColor.BLUE, 1.15, 150),
    /**
     * Represents the Epic rarity
     */
    EPIC(ChatColor.DARK_PURPLE, 1.25, 125),
    /**
     * Represents the Legend rarity
     */
    LEGEND(ChatColor.GOLD, 1.4, 100),
    /**
     * Represents the Mythical rarity
     */
    MYTHICAL(ChatColor.LIGHT_PURPLE, 1.65, 75),
    /**
     * Represents the Ultimate rarity
     */
    ULTIMATE(ChatColor.AQUA, 2.3, 50),

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
        get() = Card.toExperience(maxCardLevel, this)

    init {
        this.color = color
        this.experienceModifier = experienceModifier
        this.maxCardLevel = maxCardLevel
    }

    override fun toString(): String = "$color${ChatColor.BOLD}${name.uppercase()}"

}