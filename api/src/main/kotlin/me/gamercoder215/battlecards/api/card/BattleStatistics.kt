package me.gamercoder215.battlecards.api.card

import kotlin.jvm.Throws

/**
 * Represents a [BattleCard]'s Lifetime Statistics and Attributes
 */
interface BattleStatistics {

    /**
     * Fetches the [BattleCard] this [BattleStatistics] is for
     */
    fun getCard(): BattleCard<*>

    // Attributes

    /**
     * Fetches the maximum health this BattleCard can have.
     * @return Max Health
     */
    fun getMaxHealth(): Double

    /**
     * Fetches the attack damage value for this BattleCard.
     * @return Attack Damage Value
     */
    fun getAttackDamage(): Double

    /**
     * Fetches the defensive value for this BattleCard.
     * @return Defense Value
     */
    fun getDefense(): Double

    /**
     * Fetches the speed modifier value for this BattleCard.
     * @return Speed Value
     */
    fun getSpeed(): Double

    /**
     * Fetches the knockback resistance value for this BattleCard.
     * @return Knockback Resistance Value
     */
    fun getKnockbackResistance(): Double

    // Statistics

    /**
     * Fetches the total amount of players this Card has killed
     * @return Player Kills
     */
    fun getPlayerKills(): Int

    /**
     * Sets the total amount of players this Card has killed
     * @param kills Player Kills
     * @return Player Kills
     * @throws IllegalArgumentException if kills is negative
     */
    @Throws(IllegalArgumentException::class)
    fun setPlayerKills(kills: Int)

    /**
     * Fetches the total amount of other BattleCards this Card has killed
     * @return Card Kills
     */
    fun getCardKills(): Int

    /**
     * Sets the total amount of other BattleCards this Card has killed
     * @param kills Card Kills
     * @return Card Kills
     * @throws IllegalArgumentException if kills is negative
     */
    @Throws(IllegalArgumentException::class)
    fun setCardKills(kills: Int)

    /**
     * Fetches the total amount of entities this Card has killed
     * @return Entity Kills
     */
    fun getEntityKills(): Int

    /**
     * Sets the total amount of entities this Card has killed
     * @param kills Entity Kills
     * @return Entity Kills
     * @throws IllegalArgumentException if kills is negative
     */
    @Throws(IllegalArgumentException::class)
    fun setEntityKills(kills: Int)

    /**
     * Fetches the total amount of kills this Card has Collected
     * @return Total Kills
     */
    fun getKills(): Int = getPlayerKills() + getCardKills() + getEntityKills()

    /**
     * Fetches the total amount of damage this Card has dealt
     * @return Damage Dealt
     */
    fun getDamageDealt(): Int

    /**
     * Sets the total amount of damage this Card has dealt
     * @param damage Damage Dealt
     * @return Damage Dealt
     * @throws IllegalArgumentException if damage is negative
     */
    @Throws(IllegalArgumentException::class)
    fun setDamageDealt(damage: Int)

    /**
     * Fetches the total amount of damage this Card has received
     * @return Damage Received
     */
    fun getDamageReceived(): Int

    /**
     * Sets the total amount of damage this Card has received
     * @param damage Damage Received
     * @return Damage Received
     * @throws IllegalArgumentException if damage is negative
     */
    @Throws(IllegalArgumentException::class)
    fun setDamageReceived(damage: Int)

    /**
     * Fetches the total amount of experience this Card has
     * @return Card Experience
     */
    fun getCardExperience(): Double

    /**
     * Adds experience to this BattleCard.
     * @param experience Experience to add
     */
    fun addCardExperience(experience: Double) = setCardExperience(getCardExperience() + experience)

    /**
     * Removes experience from this BattleCard.
     * @param experience Experience to remove
     */
    fun removeCardExperience(experience: Double) = setCardExperience(getCardExperience() - experience)

    /**
     * Sets the total amount of experience this Card has
     * @param experience Card Experience
     * @return Card Experience
     */
    fun setCardExperience(experience: Double)

    /**
     * Fetches how much experience is needed for this BattleCard to advance to the next level.
     * @return Experience to next level
     */
    fun getAdvancingExperience(): Double {
        if (getCardLevel() >= BattleCard.MAX_LEVEL) return 0.0

        return (BattleCard.toExperience(getCardLevel() + 1) - getCardExperience()) * getCard().getRarity().getExperienceModifier()
    }

    /**
     * Fetches the level that this Card is currently at.
     * @return Card Level
     */
    fun getCardLevel(): Int = BattleCard.toLevel(getCardExperience(), getCard().getRarity())

    /**
     * Sets the level that this Card is currently at.
     * @param level Card Level
     * @return Card Level
     * @throws IllegalArgumentException If the Card's level is greater than the maximum level
     */
    @Throws(IllegalArgumentException::class)
    fun setCardLevel(level: Int) {
        if (level > getMaxCardLevel()) throw IllegalArgumentException("Level cannot be greater than ${getMaxCardLevel()} for this card!")
        setCardExperience(BattleCard.toExperience(level, getCard().getRarity()))
    }

    /**
     * Fetches the maximum level that this Card can be.
     * @return Max Card Level
     */
    fun getMaxCardLevel(): Int = getCard().getMaxCardLevel()

    /**
     * Fetches the maximum experience that this Card can have.
     * @return Max Card Experience
     */
    fun getMaxCardExperience(): Double = getCard().getMaxCardExperience()


}
