package me.gamercoder215.battlecards

import com.jeff_media.updatechecker.UpdateCheckSource
import com.jeff_media.updatechecker.UpdateChecker
import me.gamercoder215.battlecards.api.BattleConfig
import org.bstats.bukkit.Metrics
import org.bukkit.plugin.java.JavaPlugin

object BattleCards : JavaPlugin(), BattleConfig {

    const val BSTATS_ID = 18166

    override fun onEnable() {
        // UpdateChecker
        UpdateChecker(this, UpdateCheckSource.GITHUB_RELEASE_TAG, "GamerCoder215/BattleCards")
            .setDownloadLink("https://github.com/GamerCoder215/BattleCards/releases/latest/")
            .setSupportLink("https://discord.gg/WVFNWEvuqX")
            .setNotifyOpsOnJoin(true)
            .setChangelogLink("https://github.com/GamerCoder215/BattleCards/releases/latest/")
            .setUserAgent("Kotlin 1.8.20 BattleCards User Agent")
            .setColoredConsoleOutput(true)
            .setDonationLink("https://www.patreon.com/teaminceptus")
            .setNotifyRequesters(true)
            .checkEveryXHours(1.0)
            .checkNow()

        // bStats
        Metrics(this, BSTATS_ID)

        logger.info("Finished!")
    }

    override fun onDisable() {}

    // BattleConfig Implementation

    override fun getLanguage(): String {
        return config.getString("language") ?: "en"
    }

}