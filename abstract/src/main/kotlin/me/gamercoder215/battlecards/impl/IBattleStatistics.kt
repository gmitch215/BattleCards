package me.gamercoder215.battlecards.impl

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.api.card.BattleStatistics
import me.gamercoder215.battlecards.api.card.CardQuest
import me.gamercoder215.battlecards.api.events.CardExperienceChangeEvent
import me.gamercoder215.battlecards.api.events.CardQuestLevelUpEvent
import me.gamercoder215.battlecards.util.call
import me.gamercoder215.battlecards.util.getModifier
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.w
import kotlin.math.floor
import kotlin.math.pow
import kotlin.reflect.full.findAnnotations

class IBattleStatistics(
    override val card: ICard,
) : BattleStatistics {

    override val rawStatistics: MutableMap<String, Number>
        get() = card.stats

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

    override var deaths: Int
        get() = (card.stats["deaths"] ?: 0).toInt()
        set(value) {
            if (value < 0) throw IllegalArgumentException("Deaths must be greater than 0")
            card.stats["deaths"] = value
        }

    override var damageDealt: Double
        get() = (card.stats["damage.dealt"] ?: 0).toDouble()
        set(value) {
            if (value < 0) throw IllegalArgumentException("Damage must be greater than 0")
            card.stats["damage.dealt"] = value
        }

    override var damageReceived: Double
        get() = (card.stats["damage.received"] ?: 0).toDouble()
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

    override val equipmentSlots: Int
        get() {
            if (cardLevel < 10) return 0

            val interval = (15 - (card.rarity.ordinal * 2)).coerceAtLeast(5)

            var count = 1
            while (count < 5 && (cardLevel - 5) >= count * interval) count++

            return count
        }

    // Logic & Attributes

    private fun findBase(attribute: CardAttribute): Double {
        val base: Double =
            if (card.type == BattleCardType.BASIC) {
                val type = card.entityCardType ?: return 0.0
                w.getDefaultAttribute(type, attribute)
            } else
                card.entityCardClass.kotlin.findAnnotations<Attributes>().firstOrNull()?.let { attribute.getAttribute(it) } ?: 0.0

        val mod = card.entityCardClass.kotlin.findAnnotations<AttributesModifier>().firstOrNull { it.attribute == attribute } ?: return base

        if (mod.value.isNaN() || cardLevel == 1) return base

        var value = base

        for (i in 1 until cardLevel)
            value = mod.operation(value, mod.value)

        val finalMod = card.rarity.experienceModifier
        return (value * finalMod.pow(1 + finalMod)).coerceAtMost(mod.max.coerceAtMost(attribute.max))
    }

    private fun find(attribute: CardAttribute): Double {
        var base = findBase(attribute)

        // Card Equipment
        base += base * card.equipment.sumOf { -(1 - it.getModifier(attribute)) }

        return base
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

    val attributes: Map<CardAttribute, Double>
        get() = mapOf(
            CardAttribute.MAX_HEALTH to maxHealth,
            CardAttribute.ATTACK_DAMAGE to attackDamage,
            CardAttribute.DEFENSE to defense,
            CardAttribute.SPEED to speed,
            CardAttribute.KNOCKBACK_RESISTANCE to knockbackResistance
        )

    // Checkers

    fun checkQuestCompletions() {
        if (cardLevel < floor(maxCardLevel / 2.0)) return

        for (quest in CardQuest.entries) {
            val level = (card.stats["quest.${quest.name.lowercase()}"] ?: 0).toInt()
            if (level >= quest.maxLevel) continue

            val current = card.getQuestLevel(quest)
            if (current > level) {
                var exp = 0.0
                for (i in (level + 1) .. current) exp += quest.getExperienceReward(card, level)

                val event1 = CardQuestLevelUpEvent(card, quest, level, current, exp).apply { call() }
                exp = event1.experienceAdded

                card.stats["quest.${quest.name.lowercase()}"] = current
                if (card.isMaxed) continue

                val event2 = CardExperienceChangeEvent(card, cardExperience, (cardExperience + exp).coerceAtMost(maxCardExperience)).apply { call() }
                if (!event2.isCancelled)
                    cardExperience = (cardExperience + exp).coerceAtMost(maxCardExperience)
            }
        }
    }

    // Other

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IBattleStatistics

        if (card != other.card) return false
        if (playerKills != other.playerKills) return false
        if (cardKills != other.cardKills) return false
        if (entityKills != other.entityKills) return false
        if (deaths != other.deaths) return false
        if (damageDealt != other.damageDealt) return false
        if (damageReceived != other.damageReceived) return false
        if (cardExperience != other.cardExperience) return false
        if (maxHealth != other.maxHealth) return false
        if (attackDamage != other.attackDamage) return false
        if (defense != other.defense) return false
        if (speed != other.speed) return false
        return knockbackResistance == other.knockbackResistance
    }

    override fun hashCode(): Int {
        var result = card.hashCode()
        result = 31 * result + playerKills
        result = 31 * result + cardKills
        result = 31 * result + entityKills
        result = 31 * result + deaths
        result = 31 * result + damageDealt.hashCode()
        result = 31 * result + damageReceived.hashCode()
        result = 31 * result + cardExperience.hashCode()
        result = 31 * result + maxHealth.hashCode()
        result = 31 * result + attackDamage.hashCode()
        result = 31 * result + defense.hashCode()
        result = 31 * result + speed.hashCode()
        result = 31 * result + knockbackResistance.hashCode()
        return result
    }

}