package me.gamercoder215.battlecards.impl

import me.gamercoder215.battlecards.api.card.BattleStatistics

class IBattleStatistics(
    override val card: ICard
) : BattleStatistics {

    override var playerKills: Int
        get() = (card.stats["kills.player"] ?: 0).toInt()
        set(value) {
            if (value < 0) throw IllegalArgumentException("Kills must be greater than 0")
            card.stats["kills.player"] = value
        }

    override var cardKills: Int
        get() = (card.stats["kills.card"] ?: 0).toInt()
        set(value) {
            if (value < 0) throw IllegalArgumentException("Kills must be greater than 0")
            card.stats["kills.card"] = value
        }

    override var entityKills: Int
        get() = (card.stats["kills.entity"] ?: 0).toInt()
        set(value) {
            if (value < 0) throw IllegalArgumentException("Kills must be greater than 0")
            card.stats["kills.entity"] = value
        }

    override var damageDealt: Int
        get() = (card.stats["damage.dealt"] ?: 0).toInt()
        set(value) {
            if (value < 0) throw IllegalArgumentException("Damage must be greater than 0")
            card.stats["damage.dealt"] = value
        }

    override var damageReceived: Int
        get() = (card.stats["damage.received"] ?: 0).toInt()
        set(value) {
            if (value < 0) throw IllegalArgumentException("Damage must be greater than 0")
            card.stats["damage.received"] = value
        }

    override var cardExperience: Double
        get() = (card.stats["experience"] ?: 0.0).toDouble()
        set(value) {
            if (value < 0 || value > maxCardExperience) throw IllegalArgumentException("Experience must be between 0 and $maxCardExperience")
            card.stats["experience"] = value
        }

    // Logic & Attributes

    private fun find(attribute: CardAttribute): Double {
        val base = card.javaClass.annotations.find { it is Attributes }?.let { attribute.getAttribute(it as Attributes) } ?: 0.0
        val mod = card.javaClass.annotations.filterIsInstance<AttributesModifier>().first { it.attribute == attribute }

        if (mod.value.isNaN()) return base

        var value = base

        for (i in 0 until cardLevel)
            value = mod.operation.apply(value, mod.value)

        return value
    }

    override val maxHealth: Double
        get() = find(CardAttribute.MAX_HEALTH)

    override val attackDamage: Double
        get() = find(CardAttribute.ATTACK_DAMAGE)

    override val defense: Double
        get() = find(CardAttribute.DEFENSE)

    override val speed: Double
        get() = find(CardAttribute.SPEED)

    override val knockbackResistance: Double
        get() = find(CardAttribute.KNOCKBACK_RESISTANCE)

    fun getAttributes(): Map<CardAttribute, Double> {
        return mapOf(
            CardAttribute.MAX_HEALTH to maxHealth,
            CardAttribute.ATTACK_DAMAGE to attackDamage,
            CardAttribute.DEFENSE to defense,
            CardAttribute.SPEED to speed,
            CardAttribute.KNOCKBACK_RESISTANCE to knockbackResistance
        )
    }

}