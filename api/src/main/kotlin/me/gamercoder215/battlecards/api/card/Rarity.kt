package me.gamercoder215.battlecards.api.card

import org.bukkit.ChatColor

/**
 * Represents a BattleCard's Rarity
 */
enum class Rarity(
    private val color: ChatColor,
    private val experienceModifier: Double = 1.0,
    private val maxCardLevel: Int = BattleCard.MAX_LEVEL
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
    UNCOMMON(ChatColor.DARK_GREEN, 1.1),
    /**
     * Represents the Rare rarity
     */
    RARE(ChatColor.BLUE, 1.25, 130),
    /**
     * Represents the Epic rarity
     */
    EPIC(ChatColor.DARK_PURPLE, 1.45, 120),
    /**
     * Represents the Legend rarity
     */
    LEGEND(ChatColor.GOLD, 1.6, 100),
    /**
     * Represents the Mythical rarity
     */
    MYTHICAL(ChatColor.LIGHT_PURPLE, 1.95, 60),
    /**
     * Represents the Ultimate rarity
     */
    ULTIMATE(ChatColor.AQUA, 2.2, 30),

    ;

    override fun toString(): String {
        return "$color${name.uppercase()}"
    }

    /**
     * Fetches the color of this rarity.
     * @return Rarity Color
     */
    fun getColor(): ChatColor = color

    /**
     * Fetches the experience modifier for this rarity, which is used to calculate the amount of experience a player needs to increase the Card's Level.
     * @return Experience Modifier
     */
    fun getExperienceModifier(): Double = experienceModifier

    /**
     * Fetches the maximum level a BattleCard can be for this rarity.
     * @return Maximum Card Level
     */
    fun getMaxCardLevel(): Int = maxCardLevel

    /**
     * Fetches the maximum experience a BattleCard can have for this rarity.
     * @return Maximum Card Experience
     */
    fun getMaxCardExperience(): Double = BattleCard.toExperience(maxCardLevel, this)

}