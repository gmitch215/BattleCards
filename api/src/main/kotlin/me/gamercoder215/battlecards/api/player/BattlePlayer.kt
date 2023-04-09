package me.gamercoder215.battlecards.api.player

import me.gamercoder215.battlecards.api.BattleConfig
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

/**
 * Represents a BattleCards Player
 */
class BattlePlayer(
    val p: Player
) {

    private val file: File = File(BattleConfig.getPlayerDirectory(), "${p.uniqueId}.yml").apply { if (!this.exists()) createNewFile() }
    private val config: FileConfiguration = YamlConfiguration.loadConfiguration(file)

    /**
     * Fetches the [File] that all configuration information for the Player is stored in.
     * @return Player File
     */
    fun getConfigFile(): File = file

    /**
     * Fetches the Player's [FileConfiguration].
     * @return Player Configuration
     */
    fun getConfig(): FileConfiguration = config

    /**
     * Fethces the Player that this [BattlePlayer] represents.
     */
    fun getPlayer(): Player = p

}