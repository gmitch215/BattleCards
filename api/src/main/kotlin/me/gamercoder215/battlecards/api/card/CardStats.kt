package me.gamercoder215.battlecards.api.card

/**
 * Statistics for a [BattleCard]
 */
class CardStats internal constructor(
    private val strength: Int,
    private val defense: Int,
    private val luck: Double
) {

    internal companion object {

        @JvmStatic
        fun of(strength: Int, defense: Int, luck: Double): CardStats {
            return CardStats(strength, defense, luck)
        }

    }

    /**
     * Fetches the Strength of this [CardStats]
     * @return Strength
     */
    fun getStrength(): Int {
        return strength
    }

    /**
     * Fetches the Defense of this [CardStats]
     * @return Defense
     */
    fun getDefense(): Int {
        return defense
    }

    /**
     * Fetches the Luck of this [CardStats]
     * @return Luck
     */
    fun getLuck(): Double {
        return luck
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CardStats

        if (strength != other.strength) return false
        if (defense != other.defense) return false
        if (luck != other.luck) return false

        return true
    }

    override fun hashCode(): Int {
        var result = strength
        result = 31 * result + defense
        result = 31 * result + luck.hashCode()
        return result
    }
}