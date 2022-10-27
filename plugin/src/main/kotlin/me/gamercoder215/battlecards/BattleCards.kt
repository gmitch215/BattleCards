package me.gamercoder215.battlecards

import me.gamercoder215.battlecards.api.BattleConfig
import org.bukkit.plugin.java.JavaPlugin

class BattleCards : JavaPlugin(), BattleConfig {

    override fun onEnable() {

        logger.info("Finished!")
    }

    override fun onDisable() {}

    // BattleConfig Implementation

    override fun getLanguage(): String {
        return config.getString("language") ?: "en"
    }

}