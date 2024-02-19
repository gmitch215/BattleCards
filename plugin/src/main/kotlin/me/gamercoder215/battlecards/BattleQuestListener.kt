package me.gamercoder215.battlecards

import me.gamercoder215.battlecards.api.card.BattleCard
import org.bukkit.entity.LivingEntity
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

internal object BattleQuestListener {

    fun onKill(card: BattleCard<*>, event: EntityDamageByEntityEvent) {
        val target = event.entity as? LivingEntity ?: return

        val hp = target.maxHealth
        if (hp < card.statistics.maxHealth)
            card.statistics.rawStatistics["quest.titan"] = (card.statistics.rawStatistics["quest.titan"]?.toInt() ?: 0) + 1
        else
            card.statistics.rawStatistics["quest.goliath"] = (card.statistics.rawStatistics["quest.goliath"]?.toInt() ?: 0) + 1
    }

}