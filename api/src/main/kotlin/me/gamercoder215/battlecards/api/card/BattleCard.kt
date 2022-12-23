package me.gamercoder215.battlecards.api.card

import org.bukkit.entity.EntityType

/**
 * Represents a BattleCard for the BattleCards plugin
 */
enum class BattleCard(
    private val type: EntityType,
    private val stats: CardStats
) {

    /**
     * Represents a Zombie BattleCard
     */
    ZOMBIE(EntityType.ZOMBIE, CardStats.of(1, 1, 0.0)),
    ;

    /**
     * Fetches the EntityType associated with this BattleCard.
     * @return Entity Type
     */
    fun getType(): EntityType {
        return type
    }

    companion object {
        @JvmStatic
        fun getCard(type: EntityType): BattleCard {
            return values().first { it.type == type }
        }
    }
    
}