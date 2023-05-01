package me.gamercoder215.battlecards.api.card

import org.bukkit.entity.Zombie

/**
 * Represents a [Zombie] BattleCard.
 */
interface MesaZombie : BattleCard<Zombie> {

    override fun getEntityClass(): Class<Zombie> = Zombie::class.java

    override fun getGeneration(): Int = 1

}
