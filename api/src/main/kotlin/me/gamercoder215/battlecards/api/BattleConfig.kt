package me.gamercoder215.battlecards.api

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.io.File

/**
 * Main BattleCards Configuration
 */
interface BattleConfig {

    companion object {
        private val plugin: Plugin = getPlugin()

        /**
         * Fetches the BattleCards Plugin Instance.
         * @return Plugin
         */
        @JvmStatic
        fun getPlugin(): Plugin {
            return Bukkit.getPluginManager().getPlugin("BattleCards") ?: throw IllegalStateException("BattleCards is not loaded!")
        }

        /**
         * Fetches the Plugin's Data Folder.
         * @return Plugin Data Folder
         */
        @JvmStatic
        fun getDataFolder(): File {
            return plugin.dataFolder
        }

        /**
         * Fetches the BattleConfig Instance.
         * @return BattleConfig
         */
        @JvmStatic
        fun getConfig(): BattleConfig {
            return plugin as BattleConfig
        }
    }

    /**
     * Fetches the Plugin's Language
     * @return Plugin Language
     */
    fun getLanguage(): String

}