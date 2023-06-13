package me.gamercoder215.battlecards.api

import com.google.common.collect.ImmutableSet
import me.gamercoder215.battlecards.api.card.BattleCard
import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.api.card.Card
import me.gamercoder215.battlecards.api.card.Rarity
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.EntityType
import org.bukkit.entity.EntityType.*
import org.bukkit.plugin.Plugin
import java.io.File
import java.util.*
import java.util.logging.Logger

/**
 * Main BattleCards Configuration
 */
interface BattleConfig {

    companion object {
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
            return getPlugin().dataFolder
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
            return getPlugin() as BattleConfig
        }

        /**
         * Fetches the Configuration File for the BattleCards plugin.
         * @return BattleCards [FileConfiguration]
         */
        @JvmStatic
        fun getConfiguration(): FileConfiguration {
            return getPlugin().config
        }

        /**
         * Fetches the Plugin's Logger.
         * @return Plugin Logger
         */
        @JvmStatic
        fun getLogger(): Logger {
            return getPlugin().logger
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
            getPlugin().saveDefaultConfig()
            val config = getPlugin().config

            if (!config.isString("Language")) config.set("Language", "en")

            if (!config.isConfigurationSection("Functionality")) config.createSection("Functionality")
            if (!config.isString("Functionality.CommandVersion") && !config.isInt("Functionality.CommandVersion")) config.set("Functionality.CommandVersion", "auto")

            if (!config.isConfigurationSection("Cards")) config.createSection("Cards")
            if (!config.isList("Disabled")) config.set("Disabled", listOf<String>())

            if (!config.isConfigurationSection("Cards.Display")) config.createSection("Cards.Display")

            if (!config.isConfigurationSection("Cards.Display.Inventory")) config.createSection("Cards.Display.Inventory")
            if (!config.isBoolean("Cards.Display.Inventory.ShowLevel")) config.set("Cards.Display.Inventory.ShowLevel", true)

            if (!config.isConfigurationSection("Cards.Display.Info")) config.createSection("Cards.Display.Info")
            if (!config.isBoolean("Cards.Display.Info.ShowAbilities")) config.set("Cards.Display.Info.ShowAbilities", true)
            if (!config.isBoolean("Cards.Display.Info.ShowStatistics")) config.set("Cards.Display.Info.ShowStatistics", true)

            if (!config.isConfigurationSection("Cards.Basic")) config.createSection("Cards.Basic")

            if (!config.isConfigurationSection("Cards.Basic.Drops")) config.createSection("Cards.Basic.Drops")
            if (!config.isBoolean("Cards.Basic.Drops.Enabled")) config.set("Cards.Basic.Drops.Enabled", true)
            if (!config.isString("Cards.Basic.Drops.Ignore")) config.set("Cards.Basic.Drops.Ignore", "")

            return config
        }

        /**
         * Fetches a set of all of the valid entity types that can drop Basic Cards.
         * @return Valid Basic Card Entity Types
         */
        @JvmStatic
        fun getValidBasicCards(): Set<EntityType> {
            return ImmutableSet.copyOf(
                setOf<Any>(
                    SPIDER,
                    CAVE_SPIDER,
                    ENDERMAN,
                    ENDERMITE,
                    ZOMBIE,
                    SKELETON,
                    IRON_GOLEM,
                    BLAZE,
                    CREEPER,
                    WITCH,
                    WITHER,
                    SLIME,
                    MAGMA_CUBE,
                    GUARDIAN,

                    "wither_skeleton",
                    "vindicator",
                    "vex",
                    "stray",
                    "phantom",
                    "drowned",
                    "piglin",
                    "hoglin",
                    "evoker",
                    "pillager",
                    "elder_guardian",
                    "polar_bear",
                    "wolf"
                ).mapNotNull {
                    when (it) {
                        is EntityType -> it
                        is String -> try { valueOf(it.uppercase()) } catch (e: IllegalArgumentException) { null }
                        else -> null
                    }
                })
        }

    }

    /**
     * Fetches an immutable set of all of the registered BattleCards.
     */
    val registeredCards: Set<Class<out BattleCard<*>>>

    /**
     * Registers a BattleCard.
     * @param card BattleCard to register
     * @throws IllegalArgumentException if the BattleCard is already registered
     */
    @Throws(IllegalArgumentException::class)
    fun registerCard(card: Class<out BattleCard<*>>)

    /**
     * Fetches a localized message from the plugin's language file.
     * @param key Key to fetch
     * @return Message
     */
    fun get(key: String): String

    /**
     * Fetches the plugin's language.
     * @return Language Identifier
     */
    val language: String
        get() = getConfiguration().getString("Language", "en")

    /**
     * Fetches a localized message from the plugin's language file, with the plugin prefix.
     * @param key Key to fetch
     * @return Message with prefix
     */
    fun getMessage(key: String): String {
        return "${get("plugin.prefix")} ${get(key)}"
    }

    /**
     * Creates a Card Data object with no data.
     * @param type Card Type
     * @return Constructed Card Data
     */
    fun createCardData(type: BattleCardType): Card

    /**
     * Fetches the plugin's locale.
     * @return Configured Locale
     */
    val locale: Locale
        get() = when (language) {
            "en" -> Locale.ENGLISH
            "fr" -> Locale.FRENCH
            else -> Locale(language)
        }

    /**
     * Fetches whether cards of the [Rarity.BASIC] rarity can be dropped by mobs.
     * @return true if can be dropped, false otherwise
     */
    var isBasicDropsEnabled: Boolean
        get() = getConfiguration().getBoolean("Cards.Basic.Drops.Enabled")
        set(value) {
            getConfiguration().set("Cards.Basic.Drops.Enabled", value)
            getPlugin().saveConfig()
        }
}