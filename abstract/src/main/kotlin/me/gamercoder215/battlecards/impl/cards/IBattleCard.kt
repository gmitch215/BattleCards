package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCard
import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.IBattleStatistics
import me.gamercoder215.battlecards.util.CardUtils
import me.gamercoder215.battlecards.util.getEntity
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.w
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Creature
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import java.util.function.Supplier

abstract class IBattleCard<T : Creature>(
    protected val cardType: BattleCardType
) : BattleCard<T> {

    companion object {
        @JvmStatic
        private val spawned: MutableMap<UUID, IBattleCard<*>> = mutableMapOf()

        @JvmStatic
        fun byEntity(entity: Creature): IBattleCard<*>? {
            return spawned[entity.uniqueId]
        }
    }

    val stats: MutableMap<String, Any> = mutableMapOf()

    protected val creation: Long = System.currentTimeMillis()
    protected var last: Long? = null
    protected var lastPlayer: Player? = null

    protected lateinit var en: T
    lateinit var p: Player

    val attachments: MutableMap<UUID, Supplier<Location>> = mutableMapOf()

    fun spawn(player: Player, location: Location): T {
        if (!en.isDead) throw IllegalStateException("Entity already spawned")

        lastPlayer = player
        last = System.currentTimeMillis()
        p = player

        en = location.world?.spawn(location, getEntityClass()) ?: throw IllegalStateException("Could not spawn entity")

        en.isCustomNameVisible = true
        en.customName = "${getRarity().getColor()}${player.displayName ?: player.name}'s ${getRarity().getColor()}${getName()}"

        en.equipment.helmetDropChance = 0.0f
        en.equipment.chestplateDropChance = 0.0f
        en.equipment.leggingsDropChance = 0.0f
        en.equipment.bootsDropChance = 0.0f

        CardUtils.createAttachments(this)
        object : BukkitRunnable() {
            override fun run() {
                if (en.isDead) {
                    cancel()
                    return
                }

                attachments.forEach { (key, value) ->
                    val entity = Bukkit.getServer().getEntity(key) ?: return@forEach
                    entity.teleport(value.get())
                }
            }
        }.runTaskTimer(BattleConfig.getPlugin(), 0, 1)

        w.loadProperties(en, this)
        init()
        spawned[en.uniqueId] = this
        return en
    }

    fun despawn() {
        uninit()
        en.remove()
        spawned.remove(en.uniqueId)
    }

    // Implementation

    open fun init() {
        if (!::en.isInitialized) throw IllegalStateException("Entity not spawned")
    }

    open fun uninit() {
        if (!::en.isInitialized) throw IllegalStateException("Entity not spawned")
    }

    final override fun getType(): BattleCardType = cardType

    final override fun getStatistics(): IBattleStatistics {
        return IBattleStatistics(this)
    }

    final override fun getEntity(): T = en

    final override fun getCreationDate(): Date = Date(creation)

    override fun getLastUsed(): Date? = last?.let { Date(it) }

    override fun getLastUsedPlayer(): Player? = lastPlayer

}