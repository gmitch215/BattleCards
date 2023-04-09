package me.gamercoder215.battlecards.api.card

import org.bukkit.ChatColor

/**
 * Represents a BattleCard's Rarity
 */
enum class Rarity(
    private val color: ChatColor,
    private val experienceModifier: Double = 1.0
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
    RARE(ChatColor.BLUE, 1.25),
    /**
     * Represents the Epic rarity
     */
    EPIC(ChatColor.DARK_PURPLE, 1.45),
    /**
     * Represents the Legend rarity
     */
    LEGEND(ChatColor.GOLD, 1.6),
    /**
     * Represents the Mythical rarity
     */
    MYTHICAL(ChatColor.LIGHT_PURPLE, 1.95),
    /**
     * Represents the Ultimate rarity
     */
    ULTIMATE(ChatColor.AQUA, 2.2),

    ;

    override fun toString(): String {
        return "$color${name.uppercase()}"
    }

    /**
     * Fetches the experience modifier for this rarity, which is used to calculate the amount of experience
     * a player needs to increase the Card's Level.
     * @return Experience Modifier
     */
    fun getExperienceModifier(): Double = experienceModifier

}