package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCard
import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.IBattleStatistics
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.util.*

abstract class IBattleCard<T : LivingEntity>(
    protected val cardType: BattleCardType
) : BattleCard<T> {

    val stats: MutableMap<String, Any> = mutableMapOf()

    protected val creation: Long = System.currentTimeMillis()
    protected var last: Long? = null
    protected var lastPlayer: Player? = null

    protected lateinit var en: T
    protected lateinit var p: Player

    fun spawn(player: Player, location: Location): T {
        if (!en.isDead) throw IllegalStateException("Entity already spawned")

        lastPlayer = player
        last = System.currentTimeMillis()
        p = player

        en = location.world?.spawn(location, getEntityClass()) ?: throw IllegalStateException("Could not spawn entity")

        en.isCustomNameVisible = true
        en.customName = "${getRarity().getColor()}${player.displayName ?: player.name}'s ${getRarity().getColor()}${getLocalizedName()}"

        en.equipment.helmetDropChance = 0.0f
        en.equipment.chestplateDropChance = 0.0f
        en.equipment.leggingsDropChance = 0.0f
        en.equipment.bootsDropChance = 0.0f

//        w.loadProperties(en, this)

        init()
        return en
    }

    fun despawn() {
        uninit()
        en.remove()
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

    // Util

    fun getAnnotations(): Set<Annotation> {
        val annotations = mutableSetOf<Annotation>()
        var superClass: Class<*> = this::class.java

        while (superClass.superclass != null) {
            annotations.addAll(superClass.annotations)
            superClass = superClass.superclass
        }

        return annotations
    }

}