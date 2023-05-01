package me.gamercoder215.battlecards.impl

import me.gamercoder215.battlecards.api.card.BattleCard
import me.gamercoder215.battlecards.api.card.BattleStatistics
import me.gamercoder215.battlecards.impl.cards.IBattleCard

class IBattleStatistics(
    private val card: IBattleCard<*>
) : BattleStatistics {

    override fun getCard(): IBattleCard<*> = card

    override fun getPlayerKills(): Int = (card.stats["kills.player"] ?: 0) as Int

    override fun setPlayerKills(kills: Int) {
        card.stats["kills.player"] = kills
    }

    override fun getCardKills(): Int = (card.stats["kills.card"] ?: 0) as Int

    override fun setCardKills(kills: Int) {
        if (kills < 0) throw IllegalArgumentException("Kills must be greater than 0")
        card.stats["kills.card"] = kills
    }

    override fun getEntityKills(): Int = (card.stats["kills.entity"] ?: 0) as Int

    override fun setEntityKills(kills: Int) {
        if (kills < 0) throw IllegalArgumentException("Kills must be greater than 0")
        card.stats["kills.entity"] = kills
    }

    override fun getDamageDealt(): Int = (card.stats["damage.dealt"] ?: 0) as Int

    override fun setDamageDealt(damage: Int) {
        if (damage < 0) throw IllegalArgumentException("Damage must be greater than 0")
        card.stats["damage.dealt"] = damage
    }

    override fun getDamageReceived(): Int = (card.stats["damage.received"] ?: 0) as Int

    override fun setDamageReceived(damage: Int) {
        if (damage < 0) throw IllegalArgumentException("Damage must be greater than 0")
        card.stats["damage.received"] = damage
    }

    override fun getCardExperience(): Double = (card.stats["experience"] ?: 0.0) as Double

    override fun setCardExperience(experience: Double) {
        if (experience < 0 || experience > getMaxCardExperience()) throw IllegalArgumentException("Experience must be between 0 and ${getMaxCardExperience()}")
        card.stats["experience"] = experience
    }

    // Logic

    private fun find(attribute: CardAttribute): Double {
        val base = getCard()::class.java.annotations.find { it is Attributes }?.let { attribute.getAttribute(it as Attributes) } ?: 0.0
        val mod = getCard()::class.java.annotations.filterIsInstance<AttributesModifier>().first { it.attribute == attribute }

        if (mod.value.isNaN()) return base

        var value = base

        for (i in 0 until getCardLevel())
            value = mod.operation.apply(value, mod.value)

        return value
    }

    override fun getMaxHealth(): Double = find(CardAttribute.MAX_HEALTH)

    override fun getAttackDamage(): Double = find(CardAttribute.ATTACK_DAMAGE)

    override fun getDefense(): Double = find(CardAttribute.DEFENSE)

    override fun getSpeed(): Double = find(CardAttribute.SPEED)

    override fun getKnockbackResistance(): Double = find(CardAttribute.KNOCKBACK_RESISTANCE)

    fun getAttributes(): Map<CardAttribute, Double> {
        return mapOf(
            CardAttribute.MAX_HEALTH to getMaxHealth(),
            CardAttribute.ATTACK_DAMAGE to getAttackDamage(),
            CardAttribute.DEFENSE to getDefense(),
            CardAttribute.SPEED to getSpeed(),
            CardAttribute.KNOCKBACK_RESISTANCE to getKnockbackResistance()
        )
    }

}