package me.gamercoder215.battlecards.impl

import me.gamercoder215.battlecards.api.card.BattleStatistics

class IBattleStatistics(
    private val card: IBattleCard<*>
) : BattleStatistics {

    override fun getCard(): IBattleCard<*> = card

    override fun getPlayerKills(): Int = (card.statistics["kills.player"] ?: 0) as Int

    override fun setPlayerKills(kills: Int) = card.statistics.set("kills.player", kills)

    override fun getCardKills(): Int = (card.statistics["kills.card"] ?: 0) as Int

    override fun setCardKills(kills: Int) = card.statistics.set("kills.card", kills)

    override fun getEntityKills(): Int = (card.statistics["kills.entity"] ?: 0) as Int

    override fun setEntityKills(kills: Int) = card.statistics.set("kills.entity", kills)

    override fun getDamageDealt(): Int = (card.statistics["damage.dealt"] ?: 0) as Int

    override fun setDamageDealt(damage: Int) = card.statistics.set("damage.dealt", damage)

    override fun getDamageReceived(): Int = (card.statistics["damage.received"] ?: 0) as Int

    override fun setDamageReceived(damage: Int) = card.statistics.set("damage.received", damage)

    override fun getCardExperience(): Double = (card.statistics["experience"] ?: 0.0) as Double

    override fun setCardExperience(experience: Double) = card.statistics.set("experience", experience)

}