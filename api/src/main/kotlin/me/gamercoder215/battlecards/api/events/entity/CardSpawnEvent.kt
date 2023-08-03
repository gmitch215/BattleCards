package me.gamercoder215.battlecards.api.events.entity

import me.gamercoder215.battlecards.api.card.BattleCard

/**
 * Called when a BattleCard Spawns
 */
open class CardSpawnEvent(card: BattleCard<*>) : EntityCardEvent(card)