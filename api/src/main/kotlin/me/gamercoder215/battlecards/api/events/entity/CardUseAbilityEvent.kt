package me.gamercoder215.battlecards.api.events.entity

import me.gamercoder215.battlecards.api.card.BattleCard
import org.bukkit.event.Cancellable

/**
 * Called when a [BattleCard] uses an ability.
 */
open class CardUseAbilityEvent(card: BattleCard<*>, type: AbilityType) : EntityCardEvent(card), Cancellable {

    private var cancelled: Boolean = false

    /**
     * The [AbilityType] of the ability used.
     */
    val abilityType: AbilityType

    init {
        this.abilityType = type
    }

    override fun isCancelled(): Boolean = cancelled
    override fun setCancelled(cancel: Boolean) { cancelled = cancel }

    /**
     * The [AbilityType] of the ability used.
     */
    enum class AbilityType {
        /**
         * Abilities activated when the card attacks its target on a specific chance.
         */
        OFFENSIVE,

        /**
         * Abilities activated when the card is damaged on a specific chance.
         */
        DEFENSIVE,

        /**
         * Abilities activated when the card takes any kind of damage.
         */
        DAMAGE,

        /**
         * Abilities activated when the card's owner attacks its target on a specific chance.
         */
        USER_OFFENSIVE,

        /**
         * Abilities activated when the card's owner is damaged on a specific chance.
         */
        USER_DEFENSIVE,

        /**
         * Abilities activated when the card's owner takes any kind of damage.
         */
        USER_DAMAGE,

        /**
         * Abilities used at specific intervals.
         */
        PASSIVE
    }

}