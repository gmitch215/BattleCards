package me.gamercoder215.battlecards.api

import me.gamercoder215.battlecards.api.card.BattleCard
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException
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
         * Fetches a localized message from the Plugin's Language File.
         * @param key Language Key
         * @return Localized Message
         */
        @JvmStatic
        fun getLocalizedString(key: String): String? {
            val id = if (getConfig().getLanguage() == "en") "" else "_" + getConfig().getLanguage()

            val p = Properties()
            val str = plugin::class.java.getResourceAsStream("/lang/battlecards$id.properties") ?: return "Unknown Value"
            p.load(str)
            str.close()

            return ChatColor.translateAlternateColorCodes('&', p.getProperty(key, "Unknown Value"))
        }
    }

    /**
     * Fetches the Plugin's Language
     * @return Plugin Language
     */
    fun getLanguage(): String

    /**
     * Fetches the [Locale] based on [getLanguage].
     * @return Locale
     */
    fun getLocale(): Locale {
        return when (getLanguage()) {
            "en" -> Locale.ENGLISH
            "fr" -> Locale.FRENCH
            "de" -> Locale.GERMAN
            "ja" -> Locale.JAPANESE
            "zh" -> Locale.CHINESE
            else -> Locale(getLanguage())
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