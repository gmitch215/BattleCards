package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCard
import me.gamercoder215.battlecards.api.card.item.CardEquipment.Potion
import me.gamercoder215.battlecards.api.events.entity.CardUseAbilityEvent
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.util.*
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.w
import me.gamercoder215.battlecards.wrapper.commands.CommandWrapper.Companion.getError
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.*
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.potion.PotionEffect
import org.bukkit.scheduler.BukkitRunnable
import java.lang.reflect.Method
import java.security.SecureRandom
import java.util.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

abstract class IBattleCard<T : Creature>(
    final override val data: ICard,
) : BattleCard<T> {

    companion object {
        val spawned: MutableMap<UUID, IBattleCard<*>> = mutableMapOf()

        fun byEntity(entity: Creature): IBattleCard<*>? = spawned[entity.uniqueId]

        fun byMinion(minion: LivingEntity): IBattleCard<*>? {
            spawned.forEach { (_, card) ->
                if (card.minions.any { it.uniqueId == minion.uniqueId })
                    return card
            }

            return null
        }

        protected val r = SecureRandom()
    }

    override lateinit var entity: T
    override lateinit var itemUsed: ItemStack
    override lateinit var currentItem: ItemStack
    lateinit var p: Player

    lateinit var healthHologram: ArmorStand
    val attachments: MutableMap<UUID, () -> Location> = mutableMapOf()
    val minions: MutableSet<LivingEntity> = mutableSetOf()
    val minionAttachments: MutableMap<UUID, MutableMap<UUID, () -> Location>> = mutableMapOf()
    val attachmentMods: MutableSet<Pair<(ArmorStand) -> Boolean, ArmorStand.() -> Unit>> = mutableSetOf()

    fun spawn(player: Player, card: ItemStack, location: Location): T {
        if (this::entity.isInitialized) throw IllegalStateException("Entity already spawned")

        data.lastUsedPlayer = player
        data.last = System.currentTimeMillis()
        p = player

        entity = location.world?.spawn(location, entityClass) ?: throw IllegalStateException("Could not spawn entity")
        itemUsed = card
        currentItem = card

        entity.isCustomNameVisible = true
        entity.customName = "${rarity.color}${player.displayName ?: player.name}'s ${rarity.color}$name"

        entity.equipment.helmetDropChance = 0.0f
        entity.equipment.chestplateDropChance = 0.0f
        entity.equipment.leggingsDropChance = 0.0f
        entity.equipment.bootsDropChance = 0.0f

        entity.removeWhenFarAway = false
        entity.canPickupItems = false

        if (entity is Ageable)
            (entity as Ageable).apply {
                ageLock = true
                setAdult()
                setBreed(false)
            }

        w.loadProperties(entity, this)
        entity.health = entity.maxHealth
        init()
        return entity
    }

    fun despawn(animation: Boolean = false) {
        if (!this::entity.isInitialized) throw IllegalStateException("Entity not spawned")

        uninit()

        if (animation)
            entity.health = 0.0
        else {
            entity.remove()
            w.spawnParticle(BattleParticle.CLOUD, entity.location, 30, 0.0, 1.0, 0.0, 0.2)
        }
    }

    // Implementation

    open fun particles() {}

    open fun loadAttachmentMods() {}

    open fun init() {
        if (!this::entity.isInitialized) throw IllegalStateException("Entity not spawned")
        p.inventory.removeItem(itemUsed)

        // Attachments
        healthHologram = entity.world.spawn(entity.eyeLocation.subtract(0.0, 1.25, 0.0), ArmorStand::class.java).apply {
            customName = "${ChatColor.GREEN}${entity.health.format()} HP"
            isCustomNameVisible = true
            isVisible = false
            setGravity(false)
            setMetadata("battlecards:block_attachment", FixedMetadataValue(BattleConfig.plugin, true))
        }

        attachments[healthHologram.uniqueId] = { entity.eyeLocation.subtract(0.0, 1.25, 0.0) }

        loadAttachmentMods()
        CardUtils.createAttachments(this)

        // Attachments & Util Runnable
        object : BukkitRunnable() {
            override fun run() {
                if (entity.isDead) {
                    attachments.forEach {
                        val entity = Bukkit.getServer().getEntity(it.key) ?: return@forEach
                        entity.remove()
                    }
                    attachments.clear()

                    cancel()
                    return
                }

                attachments.forEach { (key, value) ->
                    val entity = Bukkit.getServer().getEntity(key) ?: return@forEach
                    entity.teleport(value())
                }

                for (minion in minions) {
                    if (minion.isDead) {
                        minionAttachments[minion.uniqueId]?.forEach { (key, _) ->
                            val entity = Bukkit.getServer().getEntity(key) ?: return@forEach
                            entity.remove()
                        }
                        continue
                    }

                    minionAttachments[minion.uniqueId]?.forEach { (key, value) ->
                        val entity = Bukkit.getServer().getEntity(key) ?: return@forEach
                        entity.teleport(value())
                    }
                }

                particles()


                if ((entity.world.uid != p.world.uid || entity.location.distanceSquared(p.location) > (30 * 30)) && entity.isOnGround) {
                    val target = p.location
                    if (p.location.subtract(0.0, 1.0, 0.0).block.type.isSolid && !target.block.type.isSolid) {
                        if (entity.vehicle == null)
                            entity.teleport(target)
                        else {
                            var vehicle = entity.vehicle
                            while (vehicle?.vehicle != null)
                                vehicle = vehicle.vehicle

                            vehicle?.teleport(target)
                        }
                    }
                }
            }
        }.runTaskTimer(BattleConfig.plugin, 0, 1)

        // Passive Abilities
        this.javaClass.declaredMethods.filter { it.isAnnotationPresent(Passive::class.java) && checkUnlockedAt(it) }.forEach { m ->
            m.isAccessible = true
            val interval = m.getAnnotation(Passive::class.java).getChance(level, unlockedAt(m))
            object : BukkitRunnable() {
                override fun run() {
                    if (entity.isDead) {
                        cancel()
                        return
                    }

                    val use = CardUseAbilityEvent(this@IBattleCard, CardUseAbilityEvent.AbilityType.PASSIVE).apply { call() }
                    if (!use.isCancelled)
                        m.invoke(this@IBattleCard)
                }
            }.runTaskTimer(BattleConfig.plugin, interval, interval)
        }

        // Card Equipment Effects
        for (equipment in this.equipment) {
            val effects = equipment.effects
            if (effects.isEmpty()) continue

            for (effect in effects) {
                when (effect.status) {
                    Potion.Status.CARD_ONLY -> entity.addPotionEffect(PotionEffect(effect.type, Int.MAX_VALUE, effect.amplifier, true, false))
                    Potion.Status.USER_ONLY -> p.addPotionEffect(PotionEffect(effect.type, Int.MAX_VALUE, effect.amplifier, true, false))
                    Potion.Status.BOTH -> {
                        entity.addPotionEffect(PotionEffect(effect.type, Int.MAX_VALUE, effect.amplifier, true, false))
                        p.addPotionEffect(PotionEffect(effect.type, Int.MAX_VALUE, effect.amplifier, true, false))
                    }
                }
            }
        }

        spawned[entity.uniqueId] = this
    }

    open fun uninit() {
        if (!this::entity.isInitialized) throw IllegalStateException("Entity not spawned")
        statistics.checkQuestCompletions()

        attachments.forEach {
            val entity = Bukkit.getServer().getEntity(it.key) ?: return@forEach
            entity.remove()
        }
        attachments.clear()

        minions.forEach {
            it.remove()
            minionAttachments[it.uniqueId]?.forEach attachments@{ (key, _) ->
                val entity = Bukkit.getServer().getEntity(key) ?: return@attachments
                entity.remove()
            }
        }
        minions.clear()

        // Card Equipment Effects
        for (equipment in this.equipment) {
            val effects = equipment.effects
            if (effects.isEmpty()) continue

            for (effect in effects) {
                when (effect.status) {
                    Potion.Status.CARD_ONLY -> if (entity.activePotionEffects.any { it.type == effect.type && it.isAmbient }) entity.removePotionEffect(effect.type)
                    Potion.Status.USER_ONLY -> if (p.activePotionEffects.any { it.type == effect.type && it.isAmbient }) p.removePotionEffect(effect.type)
                    Potion.Status.BOTH -> {
                        if (entity.activePotionEffects.any { it.type == effect.type && it.isAmbient })
                            entity.removePotionEffect(effect.type)

                        if (p.activePotionEffects.any { it.type == effect.type && it.isAmbient })
                            p.removePotionEffect(effect.type)
                    }
                }
            }
        }

        currentItem = data.itemStack

        if (p.inventory.firstEmpty() == -1) {
            p.sendMessage(getError("error.inventory.full.card_dropped"))
            p.world.dropItemNaturally(p.location, currentItem)
        } else
            p.inventory.addItem(currentItem)
        spawned.remove(entity.uniqueId)
    }

    final override val statistics: IBattleStatistics
        get() = data.statistics

    final override val isRideable: Boolean
        get() = this::class.java.isAnnotationPresent(Rideable::class.java)

    // Utilities

    private fun checkUnlockedAt(method: Method): Boolean =
        level >= unlockedAt(method)

    private fun unlockedAt(method: Method): Int {
        val annotation = method.getAnnotation(UnlockedAt::class.java) ?: return 0
        return annotation.level
    }

    fun <E : Creature> minion(clazz: Class<E>, action: E.() -> Unit = {}): E {
        val minion = w.spawnMinion(clazz, this)
        action(minion)
        CardUtils.createMinionAttachments(minion, this)

        if (minions.size >= 100)
            minion.remove()

        return minion
    }

    fun spawn(l: Location, o: BattleParticle) =
        w.spawnParticle(o, l, 1, 0.0, 0.0, 0.0, 0.0, false)

    fun circle(l: Location, o: BattleParticle, points: Int, radius: Double) {
        for (i in 0 until points) {
            val angle = 2 * Math.PI * i / points
            l.add(radius * sin(angle), 0.0, radius * cos(angle))
            spawn(l, o)
            l.subtract(radius * sin(angle), 0.0, radius * cos(angle))
        }
    }

    fun polygon(l: Location, o: BattleParticle, points: Int, radius: Double) {
        for (i in 0 until points) {
            val a = Math.toRadians(360.0 / points * i); val a2 = Math.toRadians(360.0 / points * (i + 1))

            val x = cos(a) * radius; val z = sin(a) * radius
            val x2 = cos(a2) * radius; val z2 = sin(a2) * radius
            val dX = x2 - x; val dZ = z2 - z

            val dist = sqrt((dX - x) * (dX - x) + (dZ - z) * (dZ - z)) / radius; var d = 0.0

            while (d < dist - (2.0 - 2 * (points.toDouble() / 10))) {
                l.add(x + dX * d, 0.0, z + dZ * d)
                spawn(l, o)
                l.subtract(x + dX * d, 0.0, z + dZ * d)
                d += .1
            }
        }
    }

    // Hashing

    override fun hashCode(): Int = entity.uniqueId.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other !is IBattleCard<*>) return false
        return other.entity.uniqueId == entity.uniqueId || data == other.data
    }
}