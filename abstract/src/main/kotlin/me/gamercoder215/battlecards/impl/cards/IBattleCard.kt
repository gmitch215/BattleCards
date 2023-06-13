package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCard
import me.gamercoder215.battlecards.impl.ICard
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
    override val data: ICard
) : BattleCard<T> {

    companion object {
        @JvmStatic
        val spawned: MutableMap<UUID, IBattleCard<*>> = mutableMapOf()

        @JvmStatic
        fun byEntity(entity: Creature): IBattleCard<*>? {
            return spawned[entity.uniqueId]
        }
    }

    override lateinit var entity: T
    lateinit var p: Player

    val attachments: MutableMap<UUID, Supplier<Location>> = mutableMapOf()

    fun spawn(player: Player, location: Location): T {
        if (!entity.isDead) throw IllegalStateException("Entity already spawned")

        data.lastUsedPlayer = player
        data.last = System.currentTimeMillis()
        p = player

        entity = location.world?.spawn(location, entityClass) ?: throw IllegalStateException("Could not spawn entity")

        entity.isCustomNameVisible = true
        entity.customName = "${rarity.color}${player.displayName ?: player.name}'s ${rarity.color}$name"

        entity.equipment.helmetDropChance = 0.0f
        entity.equipment.chestplateDropChance = 0.0f
        entity.equipment.leggingsDropChance = 0.0f
        entity.equipment.bootsDropChance = 0.0f

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
        }.runTaskTimer(BattleConfig.getPlugin(), 0, 1)

        w.loadProperties(entity, this)
        init()
        spawned[entity.uniqueId] = this
        return entity
    }

    fun despawn() {
        uninit()
        entity.remove()
        spawned.remove(entity.uniqueId)
    }

    // Implementation

    open fun init() {
        if (!this::entity.isInitialized) throw IllegalStateException("Entity not spawned")
    }

    open fun uninit() {
        if (!this::entity.isInitialized) throw IllegalStateException("Entity not spawned")
    }

}