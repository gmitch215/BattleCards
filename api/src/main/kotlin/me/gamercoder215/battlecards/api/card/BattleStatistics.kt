package me.gamercoder215.battlecards.api.card

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
    fun getMaxHealth(): Double {
        val base = getCard()::class.java.annotations.find { it is Attributes }?.let { (it as Attributes).maxHealth } ?: 0.0
        val mod = getCard()::class.java.annotations.filterIsInstance<AttributesModifier>().first { it.attribute == CardAttribute.MAX_HEALTH }

        var value = base

        for (i in 0 until getCardLevel())
            value = mod.operation.apply(value, if (mod.value.isNaN()) base else mod.value)

        return value
    }

    /**
     * Fetches the attack damage value for this BattleCard.
     * @return Attack Damage Value
     */
    fun getAttackDamage(): Double {
        val base = getCard()::class.java.annotations.find { it is Attributes }?.let { (it as Attributes).attackDamage } ?: 0.0
        val mod = getCard()::class.java.annotations.filterIsInstance<AttributesModifier>().first { it.attribute == CardAttribute.ATTACK_DAMAGE }

        var value = base

        for (i in 0 until getCardLevel())
            value = mod.operation.apply(value, if (mod.value.isNaN()) base else mod.value)

        return value
    }

    /**
     * Fetches the attack knockback value for this BattleCard.
     * @return Attack Knockback Value
     */
    fun getAttackKnockback(): Double {
        val base = getCard()::class.java.annotations.find { it is Attributes }?.let { (it as Attributes).attackKnockback } ?: 0.0
        val mod = getCard()::class.java.annotations.filterIsInstance<AttributesModifier>().first { it.attribute == CardAttribute.ATTACK_KNOCKBACK }

        var value = base

        for (i in 0 until getCardLevel())
            value = mod.operation.apply(value, if (mod.value.isNaN()) base else mod.value)

        return value
    }

    /**
     * Fetches the defensive value for this BattleCard.
     * @return Defense Value
     */
    fun getDefense(): Double {
        val base = getCard()::class.java.annotations.find { it is Attributes }?.let { (it as Attributes).defense } ?: 0.0
        val mod = getCard()::class.java.annotations.filterIsInstance<AttributesModifier>().first { it.attribute == CardAttribute.DEFENSE }

        var value = base

        for (i in 0 until getCardLevel())
            value = mod.operation.apply(value, if (mod.value.isNaN()) base else mod.value)

        return value
    }

    /**
     * Fetches the speed modifier value for this BattleCard.
     * @return Speed Value
     */
    fun getSpeed(): Double {
        val base = getCard()::class.java.annotations.find { it is Attributes }?.let { (it as Attributes).speed } ?: 0.0
        val mod = getCard()::class.java.annotations.filterIsInstance<AttributesModifier>().first { it.attribute == CardAttribute.SPEED }

        var value = base

        for (i in 0 until getCardLevel())
            value = mod.operation.apply(value, mod.value)

        return value
    }

    /**
     * Fetches the knockback resistance value for this BattleCard.
     * @return Knockback Resistance Value
     */
    fun getKnockbackResistance(): Double {
        val base = getCard()::class.java.annotations.find { it is Attributes }?.let { (it as Attributes).knockbackResistance } ?: 0.0
        val mod = getCard()::class.java.annotations.filterIsInstance<AttributesModifier>().first { it.attribute == CardAttribute.KNOCKBACK_RESISTANCE }

        var value = base

        for (i in 0 until getCardLevel())
            value = mod.operation.apply(value, mod.value)

        return value
    }

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
     */
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
     */
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
     */
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
     */
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
     */
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
     */
    fun setCardLevel(level: Int) = setCardExperience(BattleCard.toExperience(level, getCard().getRarity()))

}
