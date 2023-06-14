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
import me.gamercoder215.battlecards.util.cards
import me.gamercoder215.battlecards.vault.VaultChat
import me.gamercoder215.battlecards.wrapper.Wrapper
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.io.IOException
import java.util.*

private const val bstats = 18166
private const val github = "GamerCoder215/BattleCards"
private const val hourTicks: Long = 20 * 60 * 60

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
        logger.info("Loaded Tasks...")

        // UpdateChecker
        UpdateChecker(this, UpdateCheckSource.GITHUB_RELEASE_TAG, github)
            .setDownloadLink("https://github.com/$github/releases/latest/")
            .setSupportLink("https://discord.gg/WVFNWEvuqX")
            .setNotifyOpsOnJoin(true)
            .setChangelogLink("https://github.com/$github/releases/latest/")
            .setUserAgent("$github v${BattleCards::class.java.`package`.implementationVersion}")
            .setColoredConsoleOutput(true)
            .setDonationLink("https://www.patreon.com/teaminceptus")
            .setNotifyRequesters(true)
            .checkEveryXHours(1.0)
            .checkNow()

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
        logger.info("Stopping Tasks...")

        logger.info("Finished!")
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

    override fun createCardData(type: BattleCardType): Card {
        return ICard(
            registeredCards.first { it.getAnnotation(Type::class.java).type == type },
            type,
            System.currentTimeMillis()
        )
    }

}