package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCard
import me.gamercoder215.battlecards.impl.IBattleStatistics
import me.gamercoder215.battlecards.impl.ICard
import me.gamercoder215.battlecards.impl.Passive
import me.gamercoder215.battlecards.util.BattleParticle
import me.gamercoder215.battlecards.util.CardUtils
import me.gamercoder215.battlecards.util.getEntity
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.w
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Creature
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
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
            }
        }.runTaskTimer(BattleConfig.plugin, 0, 1)

        // Passive Abilities
        this.javaClass.declaredMethods.filter { it.isAnnotationPresent(Passive::class.java) }.forEach { m ->
            m.isAccessible = true
            val passive = m.getAnnotation(Passive::class.java)

            val base = passive.interval
            var interval = base

            if (passive.value != Long.MIN_VALUE)
                interval = passive.operation.apply(interval.toDouble(), passive.value.toDouble()).toLong()

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
}