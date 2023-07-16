package me.gamercoder215.battlecards.api.events

import me.gamercoder215.battlecards.api.card.Card
import org.bukkit.event.Cancellable

/**
 * Called when a Card's Experience Changes naturally.
 */
open class CardExperienceChangeEvent(card: Card, old: Double, new: Double) : CardEvent(card), Cancellable {

    private var cancelled: Boolean = false

    /**
     * The old experience amount of the [Card].
     */
    val oldExperience: Double

    /**
     * The new experience amount of the [Card].
     */
    var newExperience: Double

    init {
        this.oldExperience = old
        this.newExperience = new
    }

    override fun isCancelled(): Boolean = cancelled
    override fun setCancelled(cancel: Boolean) { cancelled = cancel }

}