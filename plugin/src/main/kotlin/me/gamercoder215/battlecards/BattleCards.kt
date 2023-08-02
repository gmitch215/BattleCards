package me.gamercoder215.battlecards

import com.google.common.collect.ImmutableSet
import com.jeff_media.updatechecker.UpdateCheckSource
import com.jeff_media.updatechecker.UpdateChecker
import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.BattleConfig.Companion.print
import me.gamercoder215.battlecards.api.card.BattleCard
import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.api.card.Card
import me.gamercoder215.battlecards.impl.ICard
import me.gamercoder215.battlecards.impl.Type
import me.gamercoder215.battlecards.impl.cards.IBattleCard
import me.gamercoder215.battlecards.impl.cards.IBattleCardListener
import me.gamercoder215.battlecards.placeholderapi.BattlePlaceholders
import me.gamercoder215.battlecards.util.BattleBlockData
import me.gamercoder215.battlecards.util.CardUtils
import me.gamercoder215.battlecards.util.cards
import me.gamercoder215.battlecards.util.inventory.Items
import me.gamercoder215.battlecards.vault.VaultChat
import me.gamercoder215.battlecards.wrapper.Wrapper
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.w
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.EOFException
import java.io.File
import java.io.IOException
import java.util.*

private const val bstats = 18166
private const val github = "GamerCoder215/BattleCards"
private const val hourTicks: Long = 20 * 60 * 60

@Suppress("unchecked_cast")
class BattleCards : JavaPlugin(), BattleConfig {

    fun loadListeners() {
        BattleCardListener(this)
        BattleGUIManager(this)

        IBattleCardListener(this)
    }

    val tasks: MutableSet<BukkitTask> = mutableSetOf()

    fun loadTasks() {
        tasks.addAll(listOf(
                object : BukkitRunnable() {
                    override fun run() {
                        Bukkit.getOnlinePlayers().forEach {
                            it.inventory.cards.forEach { (slot, card) ->
                                card.statistics.cardExperience += this@BattleCards.growthPassiveAmount
                                it.inventory.setItem(slot, card.itemStack)
                            }
                        }
                    }
                }.runTaskTimer(this, hourTicks, hourTicks)
            )
        )
    }

    fun checkIntegrations() {
        if (hasVault()) {
            logger.info("Vault Integration Found!")
            VaultChat.loadChat()
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            logger.info("Placeholder API Found! Hooking...")
            BattlePlaceholders(this)
            logger.info("Hooked into Placeholder API!")
        }
    }

    internal fun hasVault(): Boolean {
        return Bukkit.getPluginManager().getPlugin("Vault") != null
    }

    override fun onEnable() {
        saveDefaultConfig()

        BattleConfig.loadConfig()
        loadListeners()
        Wrapper.getCommandWrapper()
        logger.info("Loaded Files...")

        loadTasks()
        Bukkit.getOnlinePlayers().forEach { w.addPacketInjector(it) }
        logger.info("Loaded Tasks...")

        Wrapper.loadCards()
        logger.info("Registered ${registeredCards.size} Cards")

        loadMetadata()
        Items.RECIPES.forEach { Bukkit.addRecipe(it) }
        logger.info("Loaded Metadata...")

        // UpdateChecker
        UpdateChecker(this, UpdateCheckSource.GITHUB_RELEASE_TAG, github).apply {
            supportLink = "https://discord.gg/WVFNWEvuqX"
            isNotifyOpsOnJoin = true
            changelogLink = "https://github.com/$github/releases/latest/"
            isColoredConsoleOutput = true
            donationLink = "https://www.patreon.com/teaminceptus"
            isNotifyRequesters = true

            setUserAgent("$github v${BattleCards::class.java.`package`.implementationVersion}")
            checkEveryXHours(1.0)
            checkNow()
        }

        // bStats
        Metrics(this, bstats)

        logger.info("Loaded Dependencies...")

        checkIntegrations()
        logger.info("Loaded Addons...")

        logger.info("Finished!")
    }

    override fun onDisable() {
        for (card in IBattleCard.spawned.values) card.despawn()
        logger.info("Unloaded Cards...")

        tasks.forEach(BukkitTask::cancel)
        Bukkit.getOnlinePlayers().forEach { w.removePacketInjector(it) }
        logger.info("Stopping Tasks...")

        saveMetadata()
        logger.info("Saved Metadata...")

        logger.info("Finished!")
    }

    private fun loadMetadata() {
        val metadata = File(dataFolder, "metadata").apply { if (!exists()) mkdir() }

        val blockData = File(metadata, "blockdata.dat").apply { if (!exists()) createNewFile() }

        try {
            BukkitObjectInputStream(blockData.inputStream()).use {
                CardUtils.BLOCK_DATA.putAll(it.readObject() as MutableMap<Location, BattleBlockData>)
            }
        } catch (ignored: EOFException) {}
    }

    private fun saveMetadata() {
        val metadata = File(dataFolder, "metadata").apply { if (!exists()) mkdir() }

        val blockData = File(metadata, "blockdata.dat").apply { if (!exists()) createNewFile() }

        BukkitObjectOutputStream(blockData.outputStream()).use {
            it.writeObject(CardUtils.BLOCK_DATA)
        }
    }

    // BattleConfig Implementation

    val cards: MutableSet<Class<out BattleCard<*>>> = mutableSetOf()

    override val registeredCards: Set<Class<out BattleCard<*>>>
        get() = ImmutableSet.copyOf(cards)

    override fun registerCard(card: Class<out BattleCard<*>>) {
        if (cards.contains(card)) throw IllegalArgumentException("Card ${card.simpleName} already registered")
        cards.add(card)
    }

    override fun get(key: String): String {
        val p = Properties()
        val lang = if (language.equals("en", ignoreCase = true)) "" else "_$language"

        return try {
            javaClass.getResourceAsStream("/lang/battlecards$lang.properties").use { str ->
                if (str == null) return "Unknown Value"

                p.load(str)
                str.close()

                ChatColor.translateAlternateColorCodes('&', p.getProperty(key, "Unknown Value"))
            }
        } catch (e: IOException) {
            print(e)
            "Unknown Value"
        }
    }

    override fun isAvailable(type: BattleCardType): Boolean =
        registeredCards.firstOrNull { it.getAnnotation(Type::class.java).type == type } != null

    override fun createCardData(type: BattleCardType): Card {
        val clazz = registeredCards.firstOrNull { it.getAnnotation(Type::class.java).type == type } ?: throw IllegalStateException("$type is not available on this Minecraft Version")
        return ICard(
            clazz,
            type,
            System.currentTimeMillis()
        )
    }

}