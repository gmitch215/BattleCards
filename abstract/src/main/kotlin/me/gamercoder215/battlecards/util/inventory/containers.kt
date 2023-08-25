package me.gamercoder215.battlecards.util.inventory

import me.gamercoder215.battlecards.wrapper.BattleInventory

val CONTAINERS_CARD_BLOCKS: Map<String, () -> BattleInventory> = mapOf(
    "card_table" to { Generator.generateCardTable() },
    "card_combiner" to { Generator.generateCardCombiner() }
)