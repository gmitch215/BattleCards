package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCard
import me.gamercoder215.battlecards.impl.IBattleStatistics
import me.gamercoder215.battlecards.impl.ICard
import me.gamercoder215.battlecards.impl.Passive
import me.gamercoder215.battlecards.impl.UnlockedAt
import me.gamercoder215.battlecards.util.BattleParticle
import me.gamercoder215.battlecards.util.CardUtils
import me.gamercoder215.battlecards.util.getChance
import me.gamercoder215.battlecards.util.getEntity
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.w
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Creature
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.lang.reflect.Method
import java.security.SecureRandom
import java.util.*
import java.util.function.Supplier
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
        fun byMinion(minion: Creature): IBattleCard<*>? {
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

    val attachments: MutableMap<UUID, Supplier<Location>> = mutableMapOf()
    val minions: MutableList<Creature> = mutableListOf()

    fun spawn(player: Player, card: ItemStack, location: Location): T {
        if (!entity.isDead) throw IllegalStateException("Entity already spawned")

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
        w.loadProperties(entity, this)
        init()
        return entity
    }

    fun despawn() {
        uninit()
        minions.forEach { it.health = 0.0 }
        minions.clear()

        entity.remove()
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
                    entity.teleport(value.get())
                }

                minions.removeIf { it.isDead }
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

                    m.invoke(this@IBattleCard)
                }
            }.runTaskTimer(BattleConfig.plugin, interval, interval)
        }
        spawned[entity.uniqueId] = this
    }

    open fun uninit() {
        if (!this::entity.isInitialized) throw IllegalStateException("Entity not spawned")
        p.inventory.addItem(currentItem)
        spawned.remove(entity.uniqueId)
    }

    final override val statistics: IBattleStatistics
        get() = data.statistics

    // Utilities

    val target: LivingEntity
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

    fun <T : Creature> minion(clazz: Class<T>, action: T.() -> Unit = {}): T {
        val minion = w.spawnMinion(clazz, this)
        action(minion)
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
        return other.entity.uniqueId == entity.uniqueId
    }
}