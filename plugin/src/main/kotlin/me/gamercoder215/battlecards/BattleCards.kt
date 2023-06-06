package me.gamercoder215.battlecards

import com.google.common.collect.ImmutableSet
import com.jeff_media.updatechecker.UpdateCheckSource
import com.jeff_media.updatechecker.UpdateChecker
import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.BattleConfig.Companion.print
import me.gamercoder215.battlecards.api.card.BattleCard
import me.gamercoder215.battlecards.util.CardListener
import org.bstats.bukkit.Metrics
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import java.io.IOException
import java.util.*


class BattleCards : JavaPlugin(), BattleConfig {

    companion object {
        const val BSTATS_ID = 18166
    }

    fun loadListeners() {
        CardListener(this)
    }

    override fun onEnable() {
        saveDefaultConfig()

        BattleConfig.loadConfig()
        loadListeners()
        logger.info("Loaded Files...")

        // UpdateChecker
        UpdateChecker(this, UpdateCheckSource.GITHUB_RELEASE_TAG, "GamerCoder215/BattleCards")
            .setDownloadLink("https://github.com/GamerCoder215/BattleCards/releases/latest/")
            .setSupportLink("https://discord.gg/WVFNWEvuqX")
            .setNotifyOpsOnJoin(true)
            .setChangelogLink("https://github.com/GamerCoder215/BattleCards/releases/latest/")
            .setUserAgent("GamerCoder/BattleCards v${BattleCards::class.java.`package`.implementationVersion}")
            .setColoredConsoleOutput(true)
            .setDonationLink("https://www.patreon.com/teaminceptus")
            .setNotifyRequesters(true)
            .checkEveryXHours(1.0)
            .checkNow()

        // bStats
        Metrics(this, BSTATS_ID)

        logger.info("Loaded Addons...")

        logger.info("Finished!")
    }

    override fun onDisable() {}

    // BattleConfig Implementation

    val cards: MutableSet<Class<out BattleCard<*>>> = mutableSetOf()

    override fun getRegisteredCards(): Set<Class<out BattleCard<*>>> {
        return ImmutableSet.copyOf(cards)
    }

    override fun registerCard(card: Class<out BattleCard<*>>) {
        if (cards.contains(card)) throw IllegalArgumentException("Card ${card.simpleName} already registered")
        cards.add(card)
    }

    override fun get(key: String): String {
        val p = Properties()
        val lang = if (getLanguage().equals("en", ignoreCase = true)) "" else "_" + getLanguage()

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

    override fun getLanguage(): String {
        return config.getString("Language", "en")
    }

}