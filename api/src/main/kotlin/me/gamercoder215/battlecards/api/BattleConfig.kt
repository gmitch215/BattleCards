package me.gamercoder215.battlecards.api

import me.gamercoder215.battlecards.api.card.BattleCard
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.util.*
import java.util.logging.Logger

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
         * Fetches the directory that all player information is stored in.
         * @return Player Directory
         */
        @JvmStatic
        fun getPlayerDirectory(): File {
            return File(getDataFolder(), "players").apply { if (!this.exists()) mkdirs() }
        }

        /**
         * Fetches the BattleConfig Instance.
         * @return BattleConfig
         */
        @JvmStatic
        fun getConfig(): BattleConfig {
            return plugin as BattleConfig
        }

        /**
         * Fetches the Configuration File for the BattleCards plugin.
         * @return BattleCards [FileConfiguration]
         */
        @JvmStatic
        fun getConfiguration(): FileConfiguration {
            return plugin.config
        }

        /**
         * Fetches the Plugin's Logger.
         * @return Plugin Logger
         */
        @JvmStatic
        fun getLogger(): Logger {
            return plugin.logger
        }

        /**
         * Prints a Throwable in the Plugin's Namespace.
         * @param t Throwable
         */
        @JvmStatic
        fun print(t: Throwable) {
            getLogger().severe(t::class.java.simpleName)
            getLogger().severe("-----------")
            getLogger().severe(t.localizedMessage)
            for (element in t.stackTrace) getLogger().severe(element.toString())
        }

        /**
         * Loads the Plugin's Configuration.
         * @return Loaded [FileConfiguration]
         */
        @JvmStatic
        fun loadConfig(): FileConfiguration {
            plugin.saveDefaultConfig()
            val config = plugin.config

            if (!config.isConfigurationSection("Cards")) config.createSection("Cards")
            if (!config.isList("Disabled")) config.set("Disabled", listOf<String>())

            if (!config.isConfigurationSection("Cards.Display")) config.createSection("Cards.Display")

            if (!config.isConfigurationSection("Cards.Display.Inventory")) config.createSection("Cards.Display.Inventory")
            if (!config.isBoolean("Cards.Display.Inventory.ShowLevel")) config.set("Cards.Display.Inventory.ShowLevel", true)

            if (!config.isConfigurationSection("Cards.Display.Info")) config.createSection("Cards.Display.Info")
            if (!config.isBoolean("Cards.Display.Info.ShowAbilities")) config.set("Cards.Display.Info.ShowAbilities", true)
            if (!config.isBoolean("Cards.Display.Info.ShowStatistics")) config.set("Cards.Display.Info.ShowStatistics", true)

            return config
        }

    }

    /**
     * Fetches an immutable set of all of the registered BattleCards.
     */
    fun getRegisteredCards(): Set<Class<out BattleCard<*>>>

    /**
     * Registers a BattleCard.
     * @param card BattleCard to register
     * @throws IllegalArgumentException if the BattleCard is already registered
     */
    @Throws(IllegalArgumentException::class)
    fun registerCard(card: Class<out BattleCard<*>>)

}