package me.gamercoder215.battlecards.util

import me.gamercoder215.battlecards.wrapper.Wrapper
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack
import java.io.Serializable

// Enums

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

enum class BattleParticle {

    CLOUD, CRIT_MAGIC, FLAME

}

enum class BattleMaterial(
    private val onLegacy: () -> ItemStack,
    private val onModern: () -> ItemStack,
    private val default: Material? = null
) {

    BLACK_STAINED_GLASS_PANE(
        { ItemStack(Material.matchMaterial("stained_glass_pane"), 1, 15) },
        { ItemStack(Material.matchMaterial("black_stained_glass_pane")) }
    ),

    COBWEB(
        { ItemStack(Material.matchMaterial("web")) },
        { ItemStack(Material.matchMaterial("cobweb")) }
    ),

    MAP(
        { ItemStack(Material.matchMaterial("empty_map")) },
        { ItemStack(Material.matchMaterial("map")) }
    ),

    PLAYER_HEAD(
        { ItemStack(Material.matchMaterial("skull_item"), 1, 3) },
        { ItemStack(Material.matchMaterial("player_head")) }
    ),

    WOODEN_SWORD(
        { ItemStack(Material.matchMaterial("wood_sword")) },
        { ItemStack(Material.matchMaterial("wooden_sword")) }
    ),

    FILLED_MAP(
        { ItemStack(Material.matchMaterial("map")) },
        { ItemStack(Material.matchMaterial("filled_map")) }
    ),

    GOLDEN_CHESTPLATE(
        { ItemStack(Material.matchMaterial("gold_chestplate")) },
        { ItemStack(Material.matchMaterial("golden_chestplate")) }
    ),

    GOLDEN_HELMET(
        { ItemStack(Material.matchMaterial("gold_helmet")) },
        { ItemStack(Material.matchMaterial("golden_helmet")) }
    ),

    CRAFTING_TABLE(
        { ItemStack(Material.matchMaterial("workbench")) },
        { ItemStack(Material.matchMaterial("crafting_table")) }
    ),

    GOLDEN_AXE(
        { ItemStack(Material.matchMaterial("gold_axe")) },
        { ItemStack(Material.matchMaterial("golden_axe")) }
    ),

    GOLDEN_PICKAXE(
        { ItemStack(Material.matchMaterial("gold_pickaxe")) },
        { ItemStack(Material.matchMaterial("golden_pickaxe")) }
    ),

    GOLDEN_SWORD(
        { ItemStack(Material.matchMaterial("gold_sword")) },
        { ItemStack(Material.matchMaterial("golden_sword")) }
    ),

    GOLDEN_HOE(
        { ItemStack(Material.matchMaterial("gold_hoe")) },
        { ItemStack(Material.matchMaterial("golden_hoe")) }
    ),

    SPAWNER(
        { ItemStack(Material.matchMaterial("mob_spawner")) },
        { ItemStack(Material.matchMaterial("spawner")) }
    ),

    ;

    fun findStack(): ItemStack = if (Wrapper.legacy) onLegacy() else onModern()

    fun find(): Material = findStack().type ?: default ?: Material.AIR

}

enum class CardAttackType {

    MELEE, BOW, CROSSBOW

}

// Classes

class BattleBlockData(block: Block) : Serializable {

    companion object { private const val serialVersionUID: Long = 2387893434556789648L }

    private val location: Location = block.location

    internal val attributes: MutableMap<String, Any> = mutableMapOf()

}