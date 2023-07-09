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
        @JvmStatic
        val plugin: Plugin
            /**
             * Fetches the BattleCards Plugin Instance.
             * @return Plugin
             */
            get() = Bukkit.getPluginManager().getPlugin("BattleCards") ?: throw IllegalStateException("BattleCards is not loaded!")
        
        @JvmStatic
        val dataFolder: File
            /**
             * Fetches the Plugin's Data Folder.
             * @return Plugin Data Folder
             */
            get() = plugin.dataFolder
        
        @JvmStatic
        val config: BattleConfig
            /**
             * Fetches the BattleConfig Instance.
             * @return BattleConfig
             */
            get() = plugin as BattleConfig
        
        @JvmStatic
        val configuration: FileConfiguration
            /**
             * Fetches the Configuration File for the BattleCards plugin.
             * @return BattleCards [FileConfiguration]
             */
            get() = plugin.config


        @JvmStatic
        val logger: Logger
            /**
             * Fetches the Plugin's Logger.
             * @return Plugin Logger
             */
            get() = plugin.logger

        /**
         * Prints a Throwable in the Plugin's Namespace.
         * @param t Throwable
         */
        @JvmStatic
        fun print(t: Throwable) {
            logger.severe(t::class.java.simpleName)
            logger.severe("-----------")
            logger.severe(t.localizedMessage)
            for (element in t.stackTrace) logger.severe(element.toString())
            
            if (t.cause != null) {
                logger.severe("=== Caused by: ===")
                print(t.cause!!)
            }
        }

        /**
         * Loads the Plugin's Configuration.
         * @return Loaded [FileConfiguration]
         */
        @JvmStatic
        fun loadConfig(): FileConfiguration {
            plugin.saveDefaultConfig()
            val config = plugin.config

            if (!config.isString("Language")) config["Language"] = "en"

            if (!config.isConfigurationSection("Functionality")) config.createSection("Functionality")
            if (!config.isString("Functionality.CommandVersion") && !config.isInt("Functionality.CommandVersion")) config["Functionality.CommandVersion"] = "auto"

            if (!config.isConfigurationSection("Cards")) config.createSection("Cards")
            if (!config.isList("Cards.Disabled")) config["Cards.Disabled"] = listOf<String>()
            if (!config.isInt("Cards.Cooldown")) config["Cards.Cooldown"] = 30
            if (!config.isBoolean("Cards.AttackPlayers")) config["Cards.AttackPlayers"] = true

            if (!config.isConfigurationSection("Cards.Display")) config.createSection("Cards.Display")

            if (!config.isConfigurationSection("Cards.Display.Inventory")) config.createSection("Cards.Display.Inventory")
            if (!config.isBoolean("Cards.Display.Inventory.ShowLevel")) config["Cards.Display.Inventory.ShowLevel"] = true

            if (!config.isConfigurationSection("Cards.Display.Info")) config.createSection("Cards.Display.Info")
            if (!config.isBoolean("Cards.Display.Info.ShowAbilities")) config["Cards.Display.Info.ShowAbilities"] = true
            if (!config.isBoolean("Cards.Display.Info.ShowStatistics")) config["Cards.Display.Info.ShowStatistics"] = true

            if (!config.isConfigurationSection("Cards.Growth")) config.createSection("Cards.Growth")
            if (!config.isNumber("Cards.Growth.KillMultiplier")) config["Cards.Growth.KillMultiplier"] = 1.0
            if (!config.isNumber("Cards.Growth.KillCardMultiplier")) config["Cards.Growth.KillCardMultiplier"] = 2.0
            if (!config.isNumber("Cards.Growth.UseMultiplier")) config["Cards.Growth.UseMultiplier"] = 5.0
            if (!config.isNumber("Cards.Growth.PassiveAmount")) config["Cards.Growth.PassiveAmount"] = 2.0

            if (!config.isConfigurationSection("Cards.Basic")) config.createSection("Cards.Basic")

            if (!config.isConfigurationSection("Cards.Basic.Drops")) config.createSection("Cards.Basic.Drops")
            if (!config.isBoolean("Cards.Basic.Drops.Enabled")) config["Cards.Basic.Drops.Enabled"] = true
            if (!config.isString("Cards.Basic.Drops.Ignore")) config["Cards.Basic.Drops.Ignore"] = ""

            return config
        }

        private fun FileConfiguration.isNumber(path: String): Boolean = this.isInt(path) || this.isDouble(path) || this.isLong(path)

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
                    ZOMBIE,
                    SKELETON,
                    IRON_GOLEM,
                    BLAZE,
                    WITHER,
                    GUARDIAN,
                    WOLF,

                    "wither_skeleton",
                    "vindicator",
                    "stray",
                    "phantom",
                    "drowned",
                    "piglin",
                    "illusioner",
                    "hoglin",
                    "evoker",
                    "pillager",
                    "elder_guardian",
                    "polar_bear",
                    "warden",
                    "zoglin"
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

    val language: String
        /**
         * Fetches the plugin's language.
         * @return Language Identifier
         */
        get() = configuration.getString("Language", "en")

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
     * Fetches whether this BattleCardType is available on this current MC Version.
     * @param type Card Type
     * @return true if available, false otherwise
     */
    fun isAvailable(type: BattleCardType): Boolean

    val locale: Locale
        /**
         * Fetches the plugin's locale.
         * @return Configured Locale
         */
        get() = when (language) {
            "en" -> Locale.ENGLISH
            "fr" -> Locale.FRENCH
            else -> Locale(language)
        }

    private fun setConfig(key: String, value: Any) {
        configuration.set(key, value)
        plugin.saveConfig()
    }

    var isBasicDropsEnabled: Boolean
        /**
         * Fetches whether cards of the [Rarity.BASIC] rarity can be dropped by mobs.
         * @return true if can be dropped, false otherwise
         */
        get() = configuration.getBoolean("Cards.Basic.Drops.Enabled")
        /**
         * Sets whether cards of the [Rarity.BASIC] rarity can be dropped by mobs.
         * @param value true if can be dropped, false otherwise
         */
        set(value) = setConfig("Cards.Basic.Drops.Enabled", value)

    var growthPassiveAmount: Double
        /**
         * Fetches the amount of set experience added to cards in a player's inventory every hour.
         * @return Experience Amount
         */
        get() = configuration.getDouble("Cards.Growth.PassiveAmount")
        /**
         * Sets the amount of set experience added to cards in a player's inventory every hour.
         * @param value Experience Amount
         */
        set(value) = setConfig("Cards.Growth.PassiveAmount", value)

    var growthUseMultiplier: Double
        /**
         * Fetches the multiplier by the Card's Level for how much experience will be added upon this card being used.
         * @return Use Multiplier
         */
        get() = configuration.getDouble("Cards.Growth.UseMultiplier")
        /**
         * Sets the multiplier by the Card's Level for how much experience will be added upon this card being used.
         * @param value Use Multiplier
         */
        set(value) = setConfig("Cards.Growth.UseMultiplier", value)

    var growthKillMultiplier: Double
        /**
         * Fetches the multiplier that will be added to a target's maximum health upon a card killing it to be added to its experience.
         * @return Kill Multiplier
         */
        get() = configuration.getDouble("Cards.Growth.KillMultiplier")
        /**
         * Sets the multiplier that will be added to a target's maximum health upon a card killing it to be added to its experience.
         * @param value Kill Multiplier
         */
        set(value) = setConfig("Cards.Growth.KillMultiplier", value)

    var growthKillCardMultiplier: Double
        /**
         * Fetches the multiplier for the kill multiplier when the target is a Battle Card.
         * @return Kill Card Multiplier
         * @see [growthKillMultiplier]
         */
        get() = configuration.getDouble("Cards.Growth.KillCardMultiplier")
        /**
         * Sets the multiplier for the kill multiplier when the target is a Battle Card.
         * @param value Kill Card Multiplier
         * @see [growthKillMultiplier]
         */
        set(value) = setConfig("Cards.Growth.KillCardMultiplier", value)

    var cardCooldown: Int
        /**
         * Fetches the cooldown, in seconds, between when the same BattleCard can be deployed again
         * @return Cooldown
         */
        get() = configuration.getInt("Cards.Cooldown")
        /**
         * Sets the cooldown, in seconds, between when the same BattleCard can be deployed again
         * @param value Cooldown
         * @see [cardCooldown]
         */
        set(value) = setConfig("Cards.Cooldown", value)

    var playerCooldownCount: Int
        /**
         * Fetches the number of cards that can be deployed by a player before the cooldown is activated.
         * @return Card Count
         */
        get() = configuration.getInt("Cards.PlayerCooldown.Count")
        /**
         * Sets the number of cards that can be deployed by a player before the cooldown is activated.
         * @param value Card Count
         */
        set(value) = setConfig("Cards.PlayerCooldown.Count", value)

    var playerCooldownTime: Int
        /**
         * Fetches the time, in seconds, that the cooldown will be activated for when the player has deployed the maximum number of cards.
         * @return Cooldown Time
         */
        get() = configuration.getInt("Cards.PlayerCooldown.Cooldown")
        /**
         * Sets the time, in seconds, that the cooldown will be activated for when the player has deployed the maximum number of cards.
         * @param value Cooldown Time
         */
        set(value) = setConfig("Cards.PlayerCooldown.Cooldown", value)

    var playerCooldownIgnored: List<String>
        /**
         * Fetches a list of players, permissions, and vault group patterns that will not be affected by the player cooldown.
         * @return List of Players
         */
        get() = configuration.getStringList("Cards.PlayerCooldown.Ignored")
        /**
         * Sets a list of players, permissions, and vault group patterns that will not be affected by the player cooldown.
         * @param value List of Players
         */
        set(value) = setConfig("Cards.PlayerCooldown.Ignored", value)

    var cardAttackPlayers: Boolean
        /**
         * Fetches whether Battle Cards can attack players.
         * @return true if can attack players, false otherwise
         */
        get() = configuration.getBoolean("Cards.AttackPlayers")
        /**
         * Sets whether Battle Cards can attack players.
         * @param value true if can attack players, false otherwise
         */
        set(value) = setConfig("Cards.AttackPlayers", value)

}