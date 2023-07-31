package me.gamercoder215.battlecards.api.card

/**
 * Represents a [Card]'s Lifetime Statistics and Attributes
 */
interface BattleStatistics {

    /**
     * Fetches the [Card] this [BattleStatistics] is for
     */
    val card: Card

    // Attributes

    /**
     * Fetches the maximum health this BattleCard can have.
     * @return Max Health
     */
    val maxHealth: Double

    /**
     * Fetches the attack damage value for this BattleCard.
     * @return Attack Damage Value
     */
    val attackDamage: Double

    /**
     * Fetches the defensive value for this BattleCard.
     * @return Defense Value
     */
    val defense: Double

    /**
     * Fetches the speed modifier value for this BattleCard.
     * @return Speed Value
     */
    val speed: Double

    /**
     * Fetches the knockback resistance value for this BattleCard.
     * @return Knockback Resistance Value
     */
    val knockbackResistance: Double

    // Statistics

    /**
     * Fetches the total amount of players this Card has killed
     * @return Player Kills
     */
    var playerKills: Int

    /**
     * Fetches the total amount of other BattleCards this Card has killed
     * @return Card Kills
     */
    var cardKills: Int

    /**
     * Fetches the total amount of entities this Card has killed
     * @return Entity Kills
     */
    var entityKills: Int

    /**
     * Fetches the total amount of times this Card has died
     * @return Deaths
     */
    var deaths: Int

    /**
     * Fetches the total amount of kills this Card has Collected
     * @return Total Kills
     */
    val kills: Int
        get() = playerKills + cardKills + entityKills

    /**
     * Fetches the total amount of damage this Card has dealt
     * @return Damage Dealt
     */
    var damageDealt: Double

    /**
     * Fetches the total amount of damage this Card has received
     * @return Damage Received
     */
    var damageReceived: Double

    /**
     * Fetches the total amount of experience this Card has
     * @return Card Experience
     */
    var cardExperience: Double

    /**
     * Fetches how much experience is needed for this BattleCard to advance to the next level.
     * @return Experience to next level
     */
    val remainingExperience: Double
        get() = card.remainingExperience

    var cardLevel: Int
        /**
         * Fetches the level that this Card is currently at.
         * @return Card Level
         */
        get() = Card.toLevel(cardExperience, card.rarity)
        /**
         * Sets the level that this Card is currently at.
         * @param value New Level
         */
        set(value) {
            if (value < 0 || value > maxCardLevel) throw IllegalArgumentException("Level must be between 0 and $maxCardLevel")
            cardExperience = Card.toExperience(value, card.rarity)
        }

    /**
     * Fetches the maximum level that this Card can be.
     * @return Max Card Level
     */
    val maxCardLevel: Int
        get() = card.rarity.maxCardLevel

    /**
     * Fetches the maximum experience that this Card can have.
     * @return Max Card Experience
     */
    val maxCardExperience: Double
        get() = card.rarity.maxCardExperience

    /**
     * Fetches the total amount of <strong>seconds</strong> this Card can be deployed for.
     * @return Deploy Time
     */
    val deployTime: Int
        get() = 30 + ((270 * cardLevel.minus(1)) / maxCardLevel.minus(1))

}
