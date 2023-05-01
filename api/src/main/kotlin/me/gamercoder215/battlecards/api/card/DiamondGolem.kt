package me.gamercoder215.battlecards.api.card

import org.bukkit.entity.IronGolem

/**
 * Represents an [IronGolem] BattleCard.
 */
interface DiamondGolem : BattleCard<IronGolem> {

    override fun getEntityClass(): Class<IronGolem> = IronGolem::class.java

    override fun getGeneration(): Int = 1

}