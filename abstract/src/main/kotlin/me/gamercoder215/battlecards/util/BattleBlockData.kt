package me.gamercoder215.battlecards.util

import org.bukkit.Location
import org.bukkit.block.Block
import java.io.Serializable

class BattleBlockData(block: Block) : Serializable {

    companion object { private const val serialVersionUID: Long = 2387893434556789648L }

    private val location: Location = block.location

    internal val attributes: MutableMap<String, Any> = mutableMapOf()

}