package me.gamercoder215.battlecards.api.card

import org.bukkit.entity.Wither

/**
 * Represents a [Wither] BattleCard.
 */
interface KingWither : BattleCard<Wither> {

    override fun getEntityClass(): Class<Wither> = Wither::class.java

    override fun getGeneration(): Int = 1

}