package me.gamercoder215.battlecards.advancements

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.Rarity
import me.gamercoder215.battlecards.api.events.CardQuestLevelUpEvent
import me.gamercoder215.battlecards.api.events.PrepareCardCombineEvent
import me.gamercoder215.battlecards.api.events.entity.CardSpawnEvent
import me.gamercoder215.battlecards.api.events.entity.CardUseAbilityEvent
import me.gamercoder215.battlecards.util.BattleMaterial
import me.gamercoder215.battlecards.util.card
import me.gamercoder215.battlecards.util.inventory.Items
import me.gamercoder215.battlecards.util.isCard
import me.gamercoder215.battlecards.util.playSuccess
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.get
import me.gamercoder215.superadvancements.advancement.*
import me.gamercoder215.superadvancements.advancement.criteria.trigger.ATrigger
import me.gamercoder215.superadvancements.spigot.BukkitDisplay
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.*
import org.bukkit.event.block.BlockEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.ServerEvent
import org.bukkit.event.vehicle.VehicleEvent
import org.bukkit.event.weather.WeatherEvent
import org.bukkit.event.world.WorldEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.RegisteredListener

class BattleAdvancements : Listener {

    companion object {

        var instance: BattleAdvancements? = null

        // Advancements

        val root = Advancement.builder()
            .key(battlecards("root"))
            .criteria("criteria", ATrigger.tick())
            .display(BukkitDisplay.builder(AFrame.TASK)
                .title("BattleCards")
                .desc("advancement.root.desc")
                .backgroundTexture(BattleMaterial.STONE_BRICKS.find())
                .icon(Material.PAPER)
                .coordinates(-2.0F, 0.0F)
                .build())
            .build().apply {
                setFlags()
            }

        val firstOfMany = Advancement.builder()
            .parent(root)
            .key(battlecards("first_of_many"))
            .criteria("criteria", ATrigger.impossible())
            .display(BukkitDisplay.builder(AFrame.TASK)
                .name("advancement.first_of_many")
                .desc("advancement.first_of_many.desc")
                .icon(Material.BOOK)
                .coordinates(-1.0F, 0.0F)
                .build())
            .reward(AReward(20))
            .build()

        val iChooseYou = Advancement.builder()
            .parent(firstOfMany)
            .key(battlecards("i_choose_you"))
            .criteria("criteria", ATrigger.impossible())
            .display(BukkitDisplay.builder(AFrame.TASK)
                .name("advancement.i_choose_you")
                .desc("advancement.i_choose_you.desc")
                .icon(BattleMaterial.SPIDER_SPAWN_EGG.findStack())
                .coordinates(0.0F, 0.0F)
                .build())
            .reward(AReward(15))
            .build()

        val emeraldSplash = Advancement.builder()
            .parent(iChooseYou)
            .key(battlecards("emerald_splash"))
            .criteria("criteria", ATrigger.impossible())
            .display(BukkitDisplay.builder(AFrame.GOAL)
                .name("advancement.emerald_splash")
                .desc("advancement.emerald_splash.desc")
                .icon(Material.EMERALD)
                .coordinates(2.0F, -0.5F)
                .build())
            .reward(AReward(30))
            .build()

        val firstKill = Advancement.builder()
            .parent(iChooseYou)
            .key(battlecards("first_kill"))
            .criteria("criteria", ATrigger.impossible())
            .display(BukkitDisplay.builder(AFrame.TASK)
                .name("advancement.first_kill")
                .desc("advancement.first_kill.desc")
                .icon(Material.IRON_SWORD)
                .coordinates(2.0F, 0.5F)
                .build())
            .reward(AReward(20))
            .build()

        val notTheFirstNotTheLast = Advancement.builder()
            .parent(firstKill)
            .key(battlecards("not_the_first_not_the_last"))
            .criteria("criteria", ATrigger.impossible())
            .display(BukkitDisplay.builder(AFrame.TASK)
                .name("advancement.not_the_first_not_the_last")
                .desc("advancement.not_the_first_not_the_last.desc")
                .icon(Material.DIAMOND_SWORD)
                .coordinates(3.0F, 0.5F)
                .build())
            .reward(AReward(25))
            .build()

        val mainCharacter = Advancement.builder()
            .parent(notTheFirstNotTheLast)
            .key(battlecards("the_main_character"))
            .criteria("criteria", ATrigger.impossible())
            .display(BukkitDisplay.builder(AFrame.CHALLENGE)
                .name("advancement.the_main_character")
                .desc("advancement.the_main_character.desc")
                .icon(BattleMaterial.PLAYER_HEAD.findStack())
                .coordinates(4.0F, 0.5F)
                .build())
            .flags(AFlag.HIDDEN)
            .reward(AReward(50))
            .build()

        val hog26 = Advancement.builder()
            .parent(mainCharacter)
            .key(battlecards("hog26"))
            .criteria("criteria", ATrigger.impossible())
            .display(BukkitDisplay.builder(AFrame.CHALLENGE)
                .name("advancement.hog26")
                .desc("advancement.hog26.desc")
                .icon(Material.GOLDEN_APPLE)
                .coordinates(5.0F, 0.5F)
                .build())
            .flags(AFlag.HIDDEN)
            .reward(AReward(100))
            .build()

        val legendOfCards = Advancement.builder()
            .parent(firstOfMany)
            .key(battlecards("legend_of_cards"))
            .criteria("criteria", ATrigger.impossible())
            .display(BukkitDisplay.builder(AFrame.TASK)
                .name("advancement.legend_of_cards")
                .desc("advancement.legend_of_cards.desc")
                .icon(BattleMaterial.FILLED_MAP.find())
                .coordinates(0.0F, -1.0F)
                .build())
            .reward(AReward(10))
            .build()

        val lordOfTheCards = Advancement.builder()
            .parent(legendOfCards)
            .key(battlecards("lord_of_the_cards"))
            .criteria("criteria", ATrigger.impossible())
            .display(BukkitDisplay.builder(AFrame.GOAL)
                .name("advancement.lord_of_the_cards")
                .desc("advancement.lord_of_the_cards.desc")
                .icon(Material.CHEST)
                .coordinates(1.0F, -1.0F)
                .build())
            .reward(AReward(20))
            .build()

        val witchcraft = Advancement.builder()
            .parent(firstOfMany)
            .key(battlecards("witchcraft"))
            .criteria("criteria", ATrigger.impossible())
            .display(BukkitDisplay.builder(AFrame.TASK)
                .name("advancement.witchcraft")
                .desc("advancement.witchcraft.desc")
                .icon(Material.POTION)
                .coordinates(0.0F, 1.5F)
                .build())
            .reward(AReward(20))
            .build()

        val luckOfTheDraw = Advancement.builder()
            .parent(witchcraft)
            .key(battlecards("luck_of_the_draw"))
            .criteria("criteria", ATrigger.impossible())
            .display(BukkitDisplay.builder(AFrame.GOAL)
                .name("advancement.luck_of_the_draw")
                .desc("advancement.luck_of_the_draw.desc")
                .icon(Material.GOLD_INGOT)
                .coordinates(1.0F, 1.0F)
                .build())
            .reward(AReward(35))
            .build()

        val unluckOfTheDraw = Advancement.builder()
            .parent(witchcraft)
            .key(battlecards("unluck_of_the_draw"))
            .criteria("criteria", ATrigger.impossible())
            .display(BukkitDisplay.builder(AFrame.GOAL)
                .name("advancement.unluck_of_the_draw")
                .desc("advancement.unluck_of_the_draw.desc")
                .icon(Material.ROTTEN_FLESH)
                .coordinates(1.0F, 2.0F)
                .build())
            .reward(AReward(35))
            .build()

        val advancements: Set<Advancement>
            get() = BattleAdvancements::class.java.declaredFields.filter { it.type == Advancement::class.java }.map {
                it.isAccessible = true
                it.get(this) as Advancement
            }.toSet()

        // Triggers

        val TRIGGERS = mutableMapOf<Class<out Event>, Event.() -> Unit>()
            .add(EntityPickupItemEvent::class.java) {
                val p = entity as? Player ?: return@add
                if (!item.isCard) return@add

                grant(p, firstOfMany)
            }
            .add(CardSpawnEvent::class.java) {
                grant(card.owner, iChooseYou)
            }
            .add(CardUseAbilityEvent::class.java) {
                grant(card.owner, emeraldSplash)
            }
            .add(EntityDamageByEntityEvent::class.java) {
                val target = entity as? LivingEntity ?: return@add
                if (target.health - finalDamage > 0) return@add

                val card = damager.card ?: return@add
                val p = card.owner

                if (target.isCard) {
                    grant(p, notTheFirstNotTheLast)

                    val tcard = target.card!!

                    if (tcard.rarity.ordinal - card.rarity.ordinal >= 2)
                        grant(p, mainCharacter)

                    if (tcard.rarity == Rarity.ULTIMATE && card.rarity == Rarity.COMMON)
                        grant(p, hog26)
                }
                else
                    grant(p, firstKill)
            }
            .add(CardQuestLevelUpEvent::class.java) {
                val p = card.lastUsedPlayer?.player ?: return@add

                grant(p, legendOfCards)

                if (quest.maxLevel <= newLevel)
                    grant(p, lordOfTheCards)
            }
            .add(PrepareCardCombineEvent::class.java) {
                if (player == null) return@add

                val rcard = result.card ?: return@add
                val card = matrix.mapNotNull { it?.card }.minByOrNull { it.rarity.ordinal } ?: return@add

                if (card.rarity.ordinal < rcard.rarity.ordinal)
                    grant(player, luckOfTheDraw)

                if (card.rarity.ordinal > rcard.rarity.ordinal)
                    grant(player, unluckOfTheDraw)
            }

        val REWARDS = mapOf<String, (PlayerAdvancementDoneEvent) -> Unit>(
            "the_main_character" to { e ->
                val p = e.player
                grant(p, Items.SMALL_EXPERIENCE_BOOK)
            },
            "hog26" to { e ->
                val p = e.player
                grant(p, Items.LARGE_EXPERIENCE_BOOK)
            }
        )

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
                p.getAdvancementManager().addAdvancement(advancements)

            instance = BattleAdvancements()

            Bukkit.getPluginManager().registerEvents(instance, BattleConfig.plugin)
            val reg = RegisteredListener(instance, { _, e -> instance!!.onEvent(e) }, EventPriority.MONITOR, BattleConfig.plugin, false)
            HandlerList.getHandlerLists().forEach { it.register(reg) }
        }

        fun unload() {
            advancements.forEach { it.unregister() }

            for (p in Bukkit.getOnlinePlayers())
                p.getAdvancementManager().removeAdvancement(advancements.map { it.key })

            HandlerList.unregisterAll(instance); instance = null
        }

        // Other

        // Util

        fun grant(p: Player, item: ItemStack) {
            if (disabled()) return

            if (p.inventory.firstEmpty() == -1)
                p.world.dropItemNaturally(p.location, item)
            else
                p.inventory.addItem(item)

            p.playSuccess()
        }

        fun grant(p: Player, advancement: Advancement) {
            if (disabled()) return

            val manager = p.getAdvancementManager()
            if (manager.getProgress(advancement).isDone) return

            if (!BukkitAdvancementManager.isRegistered(advancement.key))
                advancement.register()

            manager.grant(advancement)
        }

        fun battlecards(key: String) = NamespacedKey(BattleConfig.plugin, "advancement/$key")
        fun disabled() = !BattleConfig.config.isAdvancementsEnabled

        // Extension Util

        private fun BukkitDisplay.Builder.desc(key: String) = description(get(key))
        private fun BukkitDisplay.Builder.name(key: String) = title(get(key))

        private inline val PlayerAdvancementDoneEvent.superAdvancements
            get() = advancement.toSuperAdvancements()

        private inline fun <reified T : Event> MutableMap<Class<out Event>, Event.() -> Unit>.add(event: Class<T>, crossinline trigger: T.() -> Unit): MutableMap<Class<out Event>, Event.() -> Unit> {
            this[event] = {
                if (this is T)
                    trigger(this)
            }

            return this
        }
    }

    // Events - Triggers

    fun onEvent(e: Event) {
        if (e is WorldEvent || e is VehicleEvent || e is ServerEvent || e is WeatherEvent || e is BlockEvent) return
        if (e is Cancellable && e.isCancelled) return

        for ((event, trigger) in TRIGGERS)
            if (event.isAssignableFrom(e.javaClass))
                trigger(e)
    }

    // Events - Util

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if (disabled()) return

        event.player.getAdvancementManager().addAdvancement(root)
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        if (disabled()) return

        event.player.getAdvancementManager().removeAdvancement(root)
    }

    @EventHandler
    fun onReward(event: PlayerAdvancementDoneEvent) {
        if (disabled()) return

        val key = event.superAdvancements.key
        if (key.namespace != BattleConfig.plugin.name.lowercase()) return

        val id = key.key.split("/")[1]

        if (REWARDS.containsKey(id)) REWARDS[id]!!(event)
    }


}