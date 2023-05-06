package me.gamercoder215.battlecards

import com.google.common.collect.ImmutableSet
import com.jeff_media.updatechecker.UpdateCheckSource
import com.jeff_media.updatechecker.UpdateChecker
import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCard
import me.gamercoder215.battlecards.util.CardListener
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.w
import org.apache.commons.lang.mutable.Mutable
import org.bstats.bukkit.Metrics
import org.bukkit.plugin.java.JavaPlugin

object BattleCards : JavaPlugin(), BattleConfig {

    const val BSTATS_ID = 18166

    fun loadListeners() {
        CardListener(this)
    }

    override fun onEnable() {
        saveDefaultConfig()

        w.init()
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

    override fun getLanguage(): String {
        return config.getString("language", "en")
    }

    override fun getRegisteredCards(): Set<Class<out BattleCard<*>>> {
        return ImmutableSet.copyOf(cards)
    }

    override fun registerCard(card: Class<out BattleCard<*>>) {
        if (cards.contains(card)) throw IllegalArgumentException("Card ${card.simpleName} already registered")
        cards.add(card)
    }

}