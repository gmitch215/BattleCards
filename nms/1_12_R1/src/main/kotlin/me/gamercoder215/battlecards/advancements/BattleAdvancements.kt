package me.gamercoder215.battlecards.advancements

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.util.BattleMaterial
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.get
import me.gamercoder215.superadvancements.advancement.*
import me.gamercoder215.superadvancements.advancement.criteria.trigger.ATrigger
import me.gamercoder215.superadvancements.spigot.BukkitDisplay
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class BattleAdvancements : Listener {

    companion object {

        var instance: BattleAdvancements? = null

        // Advancements

        val root = Advancement.builder()
            .key(battlecards("root"))
            .criteria("criteria", ATrigger.impossible())
            .display(BukkitDisplay.builder(AFrame.TASK)
                .title("BattleCards")
                .desc("advancement.root.desc")
                .backgroundTexture(BattleMaterial.STONE_BRICKS.find())
                .icon(Material.PAPER)
                .build())
            .reward(AReward(20))
            .build()

        val advancements: Set<Advancement>
            get() = BattleAdvancements::class.java.declaredFields.filter { it.type == Advancement::class.java }.map {
                it.isAccessible = true
                it.get(this) as Advancement
            }.toSet()

        // Loading & Unloading

        @JvmStatic
        fun check(reload: Boolean = false) {
            if (BattleConfig.config.isAdvancementsEnabled) {
                try {
                    Class.forName("org.bukkit.advancement.Advancement")
                } catch (ignored: ClassNotFoundException) {
                    Bukkit.getPluginManager().disablePlugin(BattleConfig.plugin)
                    throw IllegalStateException("Advancements are not available on this Minecraft Version. Please disable them in the configuration.")
                }

                load()
            } else {
                if (reload) unload()
            }
        }

        fun load() {
            advancements.forEach { it.register() }

            for (p in Bukkit.getOnlinePlayers())
                p.getAdvancementManager().addAdvancement(root)


            instance = BattleAdvancements(); Bukkit.getPluginManager().registerEvents(instance, BattleConfig.plugin)
        }

        fun unload() {
            advancements.forEach { it.unregister() }

            for (p in Bukkit.getOnlinePlayers())
                p.getAdvancementManager().removeAdvancement(root)

            HandlerList.unregisterAll(instance); instance = null
        }

        // Other

        private fun BukkitDisplay.Builder.desc(key: String) = description(get(key))

        fun battlecards(key: String) = NamespacedKey(BattleConfig.plugin, "advancment:$key")

    }

    // Events

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if (!BattleConfig.config.isAdvancementsEnabled) return

        event.player.getAdvancementManager().addAdvancement(root)
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        if (!BattleConfig.config.isAdvancementsEnabled) return

        event.player.getAdvancementManager().removeAdvancement(root)
    }


}