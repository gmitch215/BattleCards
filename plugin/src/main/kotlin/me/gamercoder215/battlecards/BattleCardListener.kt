package me.gamercoder215.battlecards

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.events.CardExperienceChangeEvent
import me.gamercoder215.battlecards.api.events.entity.CardUseAbilityEvent
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.impl.cards.IBattleCard
import me.gamercoder215.battlecards.util.*
import me.gamercoder215.battlecards.util.CardUtils.BLOCK_DATA
import me.gamercoder215.battlecards.util.CardUtils.format
import me.gamercoder215.battlecards.util.inventory.CONTAINERS_CARD_BLOCKS
import me.gamercoder215.battlecards.util.inventory.CardGenerator
import me.gamercoder215.battlecards.util.inventory.Generator
import me.gamercoder215.battlecards.util.inventory.Items
import me.gamercoder215.battlecards.vault.VaultChat
import me.gamercoder215.battlecards.wrapper.NBTWrapper
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.r
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.w
import me.gamercoder215.battlecards.wrapper.commands.CommandWrapper.Companion.getError
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.*
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.*
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.lang.reflect.Method
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.set

@Suppress("unchecked_cast")
internal class BattleCardListener(private val plugin: BattleCards) : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    private fun isIgnoredByCooldown(p: Player): Boolean {
        if (p.isOp) return true

        val ignored = plugin.playerCooldownIgnored

        val b = AtomicBoolean()

        for (str in ignored) {
            val patt = str.toRegex()

            if (patt.matches(p.name)) {
                b.set(true)
                break
            }

            for (permission in p.effectivePermissions.filter { it.value }.map { it.permission }) {
                if (patt.matches(permission)) {
                    b.set(true)
                    break
                }
            }

            if (plugin.hasVault()) {
                if (VaultChat.isInGroup(p, *ignored.toTypedArray())) {
                    b.set(true)
                    break
                }
            }
        }

        return b.get()
    }

    private companion object {
        @JvmStatic
        private val uses = mutableMapOf<UUID, Int>()

        @JvmStatic
        private val basicCardDrops: List<DamageCause> = listOf(
            "ENTITY_ATTACK",
            "ENTITY_SWEEP_ATTACK",
            "ENTITY_EXPLOSION")
            .mapNotNull {
                try {
                    DamageCause.valueOf(it)
                } catch (ignored: IllegalArgumentException) {
                    null
                }
            }
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val p = event.player
        val item = (event.item ?: return).clone().apply { amount = 1 }
        val card = item.card ?: return

        when (event.action) {
            Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> {
                p.openInventory(Generator.generateCardInfo(card))
                p.playSuccess()
            }
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> {
                val cooldown = !isIgnoredByCooldown(p)
                if ((uses[p.uniqueId] ?: 0) > plugin.playerCooldownCount && cooldown) {
                    p.sendMessage(format(getError("error.card.use_limit"), plugin.playerCooldownCount.formatInt(), plugin.playerCooldownTime))
                    p.playFailure()
                    return
                }

                if (!card.canUse && cooldown) {
                    p.sendMessage(format(getError("error.card.cooldown"), (card.cooldownTime / 1000).formatInt()))
                    p.playFailure()
                    return
                }

                addExperience(card, plugin.growthUseMultiplier * card.level)
                card.spawnCard(p, item)
                w.spawnParticle(BattleParticle.CLOUD, p.location, 30, 0.0, 1.0, 0.0, 0.2)

                object : BukkitRunnable() {
                    override fun run() {
                        uses[p.uniqueId] = (uses[p.uniqueId] ?: return) - 1
                    }
                }.runTaskLater(plugin, (plugin.playerCooldownTime + card.deployTime) * 20L)
            }
            else -> return
        }
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEntityEvent) {
        val p = event.player
        val entity = event.rightClicked

        if (entity.hasMetadata("battlecards:nointeract"))
            event.isCancelled = true

        val card = entity.card ?: return

        if (entity.type == EntityType.IRON_GOLEM && p.itemInHand?.type == Material.IRON_INGOT)
            event.isCancelled = true

        if (card.p != p) return

        if (p.isSneaking && p.gameMode == GameMode.CREATIVE) {
            card.despawn()
            return
        }

        if (card.isRideable)
            entity.passenger = p
    }

    private fun checkUnlockedAt(method: Method, card: IBattleCard<*>): Boolean =
        card.level >= unlockedAt(method)

    private fun unlockedAt(method: Method): Int {
        val annotation = method.getAnnotation(UnlockedAt::class.java) ?: return 0
        return annotation.level
    }

    private fun addExperience(card: ICard, amount: Number) {
        if (card.isMaxed) return

        val new = (card.experience + amount.toDouble()).coerceAtMost(card.maxCardExperience)
        val event = CardExperienceChangeEvent(card, card.experience, new).apply { call() }

        if (!event.isCancelled)
            card.experience = event.newExperience
    }

    @EventHandler
    fun damage(event: EntityDamageEvent) {
        if (event.isCancelled) return
        val entity = event.entity as? LivingEntity ?: return

        // Damage

        if (entity.isCard) {
            val card = entity.card!!

            val healthColor = when (entity.health) {
                in 0.0..(entity.maxHealth / 4) -> ChatColor.RED
                in (entity.maxHealth / 4)..(entity.maxHealth / 2) -> ChatColor.YELLOW
                else -> ChatColor.GREEN
            }
            card.healthHologram.customName = "$healthColor${entity.health.format()} HP"

            val damage = card.javaClass.declaredMethods.filter { it.isAnnotationPresent(Damage::class.java) }
            for (m in damage) {
                if (!checkUnlockedAt(m, card)) continue
                val annotation = m.getDeclaredAnnotation(Damage::class.java)
                m.isAccessible = true

                if (r.nextDouble() <= annotation.getChance(card.level, unlockedAt(m)))
                    m.invoke(card, event)
            }
        }
    }

    @EventHandler
    fun attack(event: EntityDamageByEntityEvent) {
        if (event.isCancelled) return
        val entity: LivingEntity = when (event.entity) {
            is LivingEntity -> event.entity as? LivingEntity
            is Projectile -> (event.entity as? Projectile)?.shooter as? LivingEntity
            else -> return
        } ?: return

        val damager: LivingEntity = when (event.damager) {
            is LivingEntity -> event.damager as? LivingEntity
            is Projectile -> (event.damager as? Projectile)?.shooter as? LivingEntity
            else -> return
        } ?: return

        // Defensive

        if (entity.isCard) {
            val card = entity.card!!

            if (event.damager is Player && (event.damager.uniqueId == card.p.uniqueId || !BattleConfig.config.cardAttackPlayers)) {
                event.isCancelled = true
                return
            }

            val defensive = card.javaClass.declaredMethods.filter { it.isAnnotationPresent(Defensive::class.java) }
            if (defensive.isNotEmpty())
                for (m in defensive) {
                    if (!checkUnlockedAt(m, card)) continue
                    val annotation = m.getDeclaredAnnotation(Defensive::class.java)
                    m.isAccessible = true

                    if (r.nextDouble() <= annotation.getChance(card.level, unlockedAt(m))) {
                        val use = CardUseAbilityEvent(card, CardUseAbilityEvent.AbilityType.DEFENSIVE).apply { call() }
                        if (!use.isCancelled)
                            m.invoke(card, event)
                    }
                }

            card.data.statistics.damageReceived += event.finalDamage
            card.currentItem = card.data.itemStack
        }

        if (entity is Player && entity.spawnedCards.isNotEmpty()) {
            entity.spawnedCards.forEach { card ->
                val userDefensive = card.javaClass.declaredMethods.filter { it.isAnnotationPresent(UserDefensive::class.java) }
                if (userDefensive.isNotEmpty())
                    for (m in userDefensive) {
                        if (!checkUnlockedAt(m, card)) continue
                        val annotation = m.getAnnotation(UserDefensive::class.java)
                        m.isAccessible = true

                        if (r.nextDouble() <= annotation.getChance(card.level, unlockedAt(m)))
                            m.invoke(card, event)
                    }
            }
        }

        // Offensive

        if (damager.isMinion && damager.cardByMinion == entity.cardByMinion) {
            event.isCancelled = true
            return
        }

        if (damager.isCard) {
            val card = damager.card!!

            if (entity.isMinion && entity.cardByMinion == card) {
                event.isCancelled = true
                return
            }

            val offensive = card.javaClass.declaredMethods.filter { it.isAnnotationPresent(Offensive::class.java) }
            for (m in offensive) {
                if (!checkUnlockedAt(m, card)) continue
                val annotation = m.getDeclaredAnnotation(Offensive::class.java)
                m.isAccessible = true

                if (r.nextDouble() <= annotation.getChance(card.level, unlockedAt(m))) {
                    val use = CardUseAbilityEvent(card, CardUseAbilityEvent.AbilityType.OFFENSIVE).apply { call() }
                    if (!use.isCancelled)
                        m.invoke(card, event)
                }
            }

            card.data.statistics.damageDealt += event.finalDamage

            if (entity.health - event.finalDamage <= 0) {
                var modifier: Double = plugin.growthKillMultiplier
                when {
                    entity is Player -> card.data.statistics.playerKills++
                    entity.isCard -> {
                        card.data.statistics.cardKills++
                        modifier = plugin.growthKillCardMultiplier
                    }
                    else -> card.data.statistics.entityKills++
                }

                addExperience(card.data, modifier * entity.maxHealth * (if (entity.isCard) plugin.growthKillCardMultiplier else 1.0))
            }
            card.currentItem = card.data.itemStack
        }

        if (event.damager is Player && (event.damager as Player).spawnedCards.isNotEmpty()) {
            val p = event.damager as Player
            p.spawnedCards.forEach { card ->
                val userOffensive = card.javaClass.declaredMethods.filter { it.isAnnotationPresent(UserOffensive::class.java) }
                if (userOffensive.isNotEmpty())
                    for (m in userOffensive) {
                        if (!checkUnlockedAt(m, card)) continue
                        val annotation = m.getAnnotation(UserOffensive::class.java)
                        m.isAccessible = true

                        if (r.nextDouble() <= annotation.getChance(card.level))
                            m.invoke(card, event)
                    }
            }
        }
    }

    // Cleanup & Functionality Events

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        w.addPacketInjector(event.player)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        event.player.spawnedCards.forEach { it.despawn() }

        w.removePacketInjector(event.player)
    }

    @EventHandler
    fun onTarget(event: EntityTargetEvent) {
        if (event.target == null) return

        if (!BattleConfig.config.targetCards && !event.entity.isCard && event.target.isCard)
            event.isCancelled = true

        if (event.entity.isCard) {
            val card = event.entity.card!!

            if (!BattleConfig.config.cardAttackPlayers && event.target is Player)
                event.isCancelled = true

            if (card.p.uniqueId == event.target?.uniqueId || event.reason.name == "TEMPT")
                event.isCancelled = true

            if (event.target.isCard) {
                val tCard = event.target.card!!

                if (tCard.p.uniqueId == card.p.uniqueId)
                    event.isCancelled = true
            }
        }

        if (event.entity.isMinion) {
            val minions = event.target.card?.minions?.map { it.uniqueId } ?: event.target.cardByMinion?.minions?.map { it.uniqueId }

            if (minions?.contains(event.entity.uniqueId) == true)
                event.isCancelled = true
        }
    }

    @EventHandler
    fun onHitAttachment(event: EntityDamageEvent) {
        val entity = event.entity
        if (entity.hasMetadata("battlecards:block_attachment") || entity["battlecards:block_attachment"] == true)
            event.isCancelled = true
    }

    @EventHandler
    fun onInteractAttachment(event: PlayerArmorStandManipulateEvent) {
        val entity = event.rightClicked
        if (entity.hasMetadata("battlecards:block_attachment") || entity["battlecards:block_attachment"] == true)
            event.isCancelled = true
    }

    @EventHandler
    fun onSplit(event: SlimeSplitEvent) {
        if (event.entity.isMinion || event.entity.isCard)
            event.isCancelled = true
    }

    @EventHandler
    fun onDeath(event: EntityDeathEvent) {
        val entity = event.entity
        val lastDamage = entity.lastDamageCause ?: return

        if (entity !is Creature) return

        if (BattleConfig.getValidBasicCards().contains(entity.type) && !entity.isCard && basicCardDrops.contains(lastDamage.cause)) {
            val chance = 2 / entity.maxHealth
            if (r.nextDouble() < chance)
                entity.world.dropItemNaturally(entity.location, CardGenerator.createBasicCard(entity))

            return
        }

        if (entity.isCard || entity.isMinion) {
            event.droppedExp = 0
            event.drops.clear()

            val card = entity.card ?: return

            card.data.statistics.deaths++
            card.currentItem = card.data.itemStack

            card.uninit()
        }
    }

    @EventHandler
    fun onHealthChange(event: EntityRegainHealthEvent) {
        val entity = event.entity as? Creature ?: return
        val card = entity.card ?: return

        val healthColor = when (entity.health) {
            in 0.0..(entity.maxHealth / 4) -> ChatColor.RED
            in (entity.maxHealth / 4)..(entity.maxHealth / 2) -> ChatColor.YELLOW
            else -> ChatColor.GREEN
        }
        card.healthHologram.customName = "$healthColor${entity.health.format()} HP"
    }

    @EventHandler
    fun onCombust(event: EntityCombustEvent) { checkCardDestruction(event.entity as? Item ?: return, event, DamageCause.FIRE) }

    @EventHandler
    fun onCombust(event: EntityDamageEvent) { checkCardDestruction(event.entity as? Item ?: return, event, event.cause) }

    private fun checkCardDestruction(entity: Item, event: Cancellable, cause: DamageCause) {
        if (!entity.isCard) return

        when (cause) {
            DamageCause.FIRE, DamageCause.FIRE_TICK, DamageCause.LAVA ->
                event.isCancelled = !BattleConfig.config.isCardDestroyedFire
            DamageCause.THORNS, DamageCause.CONTACT ->
                event.isCancelled = !BattleConfig.config.isCardDestroyedThorns
            DamageCause.ENTITY_EXPLOSION, DamageCause.BLOCK_EXPLOSION ->
                event.isCancelled = !BattleConfig.config.isCardDestroyedExplosion

            else -> return
        }
    }

    @EventHandler
    fun onDespawn(event: ItemDespawnEvent) {
        if (event.entity.isCard && !BattleConfig.config.isCardsDespawn)
            event.isCancelled = true
    }

    @EventHandler
    fun onExplode(event: EntityExplodeEvent) {
        val entity = event.entity
        if (entity.isCard || entity.hasMetadata("battlecards:nointeract"))
            event.isCancelled = true
    }

    // Item Events

    @EventHandler
    fun onPlace(event: BlockPlaceEvent) {
        val item = event.itemInHand
        val nbt = NBTWrapper.of(item)
        val id = item.id ?: return
        if (!item.isCardBlock) return

        val block = event.block
        block["card_block"] = id

        if (nbt.getBoolean("container"))
            block["container"] = id

        block["success"] = nbt.getBoolean("success")

        if (nbt.getString("attach").isNotEmpty()) {
            val attachments = mutableListOf<UUID>()

            val attach = Material.matchMaterial(nbt.getString("attach"))
            val small = nbt.getBoolean("attach.small")

            val modX = nbt.getDouble("attach.mod.x")
            val modY = nbt.getDouble("attach.mod.y")
            val modZ = nbt.getDouble("attach.mod.z")

            attachments.add(block.world.spawn(block.location.add(0.5 + modX, 0.5 + modY, 0.5 + modZ), ArmorStand::class.java).apply {
                isSmall = small
                isVisible = false
                setGravity(false)
                setBasePlate(false)
                helmet = ItemStack(attach)
                this["battlecards:block_attachment"] = true
            }.uniqueId)

            block["attachments"] = attachments
        }
    }

    @EventHandler
    fun onBreak(event: BlockBreakEvent) {
        val p = event.player
        val block = event.block
        if (!block.isCardBlock) return

        event.isCancelled = true
        block.type = Material.AIR

        (block["attachments"] as List<UUID>)
            .mapNotNull { id -> block.world.entities.firstOrNull { it.uniqueId == id } }
            .filterIsInstance<ArmorStand>()
            .forEach { it.remove() }

        val id = block["card_block"]

        if (p.gameMode != GameMode.CREATIVE && Items.PUBLIC_ITEMS.containsKey(id))
            block.world.dropItemNaturally(block.location, Items.PUBLIC_ITEMS[id])

        BLOCK_DATA.remove(block.location)
    }

    @EventHandler
    fun onClickBlock(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) return

        val p = event.player
        val block = event.clickedBlock ?: return
        if (!block.isCardBlock) return

        if (block["container"]?.toString()?.isNotEmpty() == true) {
            event.isCancelled = true
            p.openInventory(CONTAINERS_CARD_BLOCKS[block["container"].toString()])

            if (block["success"] as? Boolean == true) p.playSuccess()
        }
    }

}