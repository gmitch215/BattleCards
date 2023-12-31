package me.gamercoder215.battlecards.api.events

import me.gamercoder215.battlecards.api.card.Card
import me.gamercoder215.battlecards.api.card.CardQuest

/**
 * Called when a Card Levels Up a Quest
 */
open class CardQuestLevelUpEvent(
    card: Card,
    /**
     * The Quest involved in this Event
     */
    val quest: CardQuest,
    /**
     * The old Level of the Quest
     */
    val oldLevel: Int,
    /**
     * The new Level of the Quest
     */
    val newLevel: Int,
    /**
     * The amount of Card Experience added to the Card as a reward
     */
    var experienceAdded: Double
) : CardEvent(card)