package me.gamercoder215.battlecards.api.card

/**
 * Represents a [BattleCard]'s Lifetime Statistics
 */
class BattleStatistics internal constructor(
    private val card: BattleCard<*>,
) {

    /**
     * Fetches the [BattleCard] this [BattleStatistics] is for
     */
    fun getCard(): BattleCard<*> = card

    // Statistics

    /**
     * Fetches the total amount of players this Card has killed
     * @return Player Kills
     */
    fun getPlayerKills(): Int = card.statistics["kills.player"] as Int

    /**
     * Fetches the total amount of other BattleCards this Card has killed
     * @return Card Kills
     */
    fun getCardKills(): Int = card.statistics["kills.card"] as Int

    /**
     * Fetches the total amount of entities this Card has killed
     * @return Entity Kills
     */
    fun getEntityKills(): Int = card.statistics["kills.entity"] as Int

    /**
     * Fetches the total amount of kills this Card has Collected
     * @return Total Kills
     */
    fun getKills(): Int = getPlayerKills() + getCardKills() + getEntityKills()

    /**
     * Fetches the total amount of damage this Card has dealt
     * @return Damage Dealt
     */
    fun getDamageDealt(): Int = card.statistics["damage.dealt"] as Int

    /**
     * Fetches the total amount of damage this Card has received
     * @return Damage Received
     */
    fun getDamageReceived(): Int = card.statistics["damage.received"] as Int

    /**
     * Fetches the total amount of experience this Card has
     * @return Card Experience
     */
    fun getCardExperience(): Double = card.statistics["experience"] as Double

    /**
     * Fetches the level that this Card is currently at.
     * @return Card Level
     */
    fun getCardLevel(): Int = BattleCard.toLevel(getCardExperience())

}
