package me.gamercoder215.battlecards.api.card

import org.bukkit.Material
import org.bukkit.entity.IronGolem
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

/**
 * Represents an [IronGolem] BattleCard
 */
interface GolemCard : BattleCard<IronGolem> {

    override fun getEntityClass(): Class<IronGolem> = IronGolem::class.java

}