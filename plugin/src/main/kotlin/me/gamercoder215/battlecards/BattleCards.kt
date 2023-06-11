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
import me.gamercoder215.battlecards.util.CardListener
import org.bstats.bukkit.Metrics
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import java.io.IOException
import java.util.*

private const val BSTATS_ID = 18166
private const val github = "GamerCoder215/BattleCards"

class BattleCards : JavaPlugin(), BattleConfig {

    fun loadListeners() {
        CardListener(this)
        BattleGUIManager(this)
    }

    override fun onEnable() {
        saveDefaultConfig()

        BattleConfig.loadConfig()
        loadListeners()
        logger.info("Loaded Files...")

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
        Metrics(this, BSTATS_ID)

        logger.info("Loaded Addons...")

        logger.info("Finished!")
    }

    override fun onDisable() {
        for (card in IBattleCard.spawned.values) card.despawn()
        logger.info("Unloaded Cards...")

        logger.info("Finished!")
    }

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
        val lang = if (getLanguage().equals("en", ignoreCase = true)) "" else "_${getLanguage()}"

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

    override fun createCardData(type: BattleCardType): Card {
        return ICard(
            getRegisteredCards().first { it.getAnnotation(Type::class.java).type == type },
            type,
            System.currentTimeMillis()
        )
    }

}