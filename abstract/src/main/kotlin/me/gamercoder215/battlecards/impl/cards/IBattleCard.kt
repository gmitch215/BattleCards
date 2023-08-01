package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCard
import me.gamercoder215.battlecards.api.events.entity.CardUseAbilityEvent
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.util.*
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.w
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Ageable
import org.bukkit.entity.Creature
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
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
        @JvmStatic
        val spawned: MutableMap<UUID, IBattleCard<*>> = mutableMapOf()

        @JvmStatic
        fun byEntity(entity: Creature): IBattleCard<*>? = spawned[entity.uniqueId]

        @JvmStatic
        fun byMinion(minion: LivingEntity): IBattleCard<*>? {
            spawned.forEach { (_, card) ->
                if (card.minions.contains(minion))
                    return card
            }

            return null
        }

        @JvmStatic
        protected val r = SecureRandom()
    }

    override lateinit var entity: T
    override lateinit var itemUsed: ItemStack
    override lateinit var currentItem: ItemStack
    lateinit var p: Player

    val attachments: MutableMap<UUID, () -> Location> = mutableMapOf()
    val minions: MutableSet<LivingEntity> = mutableSetOf()
    val minionAttachments: MutableMap<UUID, MutableMap<UUID, () -> Location>> = mutableMapOf()

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
            (entity as Ageable).ageLock = true

        w.loadProperties(entity, this)
        entity.health = entity.maxHealth
        init()
        return entity
    }

    fun despawn() {
        if (!this::entity.isInitialized) throw IllegalStateException("Entity not spawned")

        uninit()
        entity.remove()
        w.spawnParticle(BattleParticle.CLOUD, p.location, 30, 0.0, 1.0, 0.0, 0.2)
    }

    // Implementation

    open fun init() {
        if (!this::entity.isInitialized) throw IllegalStateException("Entity not spawned")
        p.inventory.removeItem(itemUsed)

        // Attachments
        CardUtils.createAttachments(this)
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

                minions.iterator().apply {
                    while (hasNext()) {
                        val minion = next()
                        if (minion.isDead) {
                            remove()
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
                }

                if (entity is Ageable)
                    (entity as Ageable).setBreed(false)

                if ((entity.world.uid != p.world.uid || entity.location.distanceSquared(p.location) > (30 * 30)) && entity.isOnGround) {
                    val target = p.location
                    if (p.location.subtract(0.0, 1.0, 0.0).block.type.isSolid && !target.block.type.isSolid)
                        entity.teleport(target)
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
        spawned[entity.uniqueId] = this
    }

    open fun uninit() {
        if (!this::entity.isInitialized) throw IllegalStateException("Entity not spawned")

        minions.forEach {
            it.remove()
            minionAttachments[it.uniqueId]?.forEach attachments@{ (key, _) ->
                val entity = Bukkit.getServer().getEntity(key) ?: return@attachments
                entity.remove()
            }
        }
        minions.clear()

        p.inventory.addItem(currentItem)
        spawned.remove(entity.uniqueId)
    }

    final override val statistics: IBattleStatistics
        get() = data.statistics

    final override val isRideable: Boolean
        get() = this::class.java.isAnnotationPresent(Rideable::class.java)

    // Utilities

    val target: LivingEntity?
        get() = entity.target

    val location: Location
        get() = entity.location

    val world: World
        get() = entity.world

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