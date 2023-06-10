package me.gamercoder215.battlecards.util

import org.bukkit.Sound

enum class BattleSound(
    vararg sounds: String
) {

    ENTITY_ARROW_HIT_PLAYER("ARROW_HIT"),

    BLOCK_NOTE_BLOCK_PLING("NOTE_PLING")

    ;

    private val sounds: Set<String>

    init {
        val matches = mutableSetOf<String>()
        matches.add(name.lowercase())
        matches.addAll(listOf(*sounds))

        this.sounds = matches
    }

    fun find(): Sound {
        for (sound in sounds) {
            try {
                return Sound.valueOf(sound.uppercase())
            } catch (e: IllegalArgumentException) {
                continue
            }
        }

        throw IllegalArgumentException("No sound found for $this")
    }

}