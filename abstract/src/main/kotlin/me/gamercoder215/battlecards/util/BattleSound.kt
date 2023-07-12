package me.gamercoder215.battlecards.util

import org.bukkit.Location
import org.bukkit.Sound

enum class BattleSound(
    vararg sounds: String
) {

    ENTITY_ARROW_HIT_PLAYER("ARROW_HIT"),

    BLOCK_NOTE_BLOCK_PLING("NOTE_PLING"),

    ENTITY_GHAST_SCREAM("GHAST_SCREAM"),

    ITEM_SHIELD_BLOCK,

    ENTITY_ENDER_DRAGON_GROWL("ENDERDRAGON_GROWL", "ENTITY_ENDERDRAGON_GROWL"),

    ENTITY_WITHER_AMBIENT("WITHER_IDLE")

    ;

    private val sounds: Set<String>

    init {
        val matches = mutableSetOf<String>()
        matches.add(name.lowercase())
        matches.addAll(listOf(*sounds))

        this.sounds = matches
    }

    fun find(): Sound =
        findOrNull() ?: throw IllegalArgumentException("No sound found for $this")

    fun findOrNull(): Sound? {
        for (sound in sounds)
            try { return Sound.valueOf(sound.uppercase()) }
            catch (e: IllegalArgumentException) { continue }

        return null
    }

    fun play(location: Location, volume: Float = 1F, pitch: Float = 1F) {
        val sound = findOrNull() ?: return
        location.world.playSound(location, sound, volume, pitch)
    }

}