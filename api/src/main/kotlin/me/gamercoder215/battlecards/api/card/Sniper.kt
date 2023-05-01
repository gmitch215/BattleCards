package me.gamercoder215.battlecards.api.card

import org.bukkit.entity.Skeleton

/**
 * Represents a [Skeleton] BattleCard.
 */
interface Sniper : BattleCard<Skeleton> {

    override fun getEntityClass(): Class<Skeleton> = Skeleton::class.java

    override fun getGeneration(): Int = 1

}