package me.gamercoder215.battlecards.impl

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCard
import me.gamercoder215.battlecards.api.card.BattleStatistics
import me.gamercoder215.battlecards.api.card.Rarity
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.util.*

abstract class IBattleCard<T : LivingEntity>(
    protected val creationDate: Long = System.currentTimeMillis(),
    internal val statistics: MutableMap<String, Any> = mutableMapOf(),
    protected var lastUsed: Long? = null,
    protected var lastUsedPlayer: Player? = null
) : BattleCard<T> {

    protected var entity: T? = null

    open fun spawn(location: Location): T {
        val bukkit: T = location.world.spawn(location, getEntityClass())
        entity = bukkit

        return bukkit
    }

    // Implementation

    open fun init() {}

    final override fun getStatistics(): BattleStatistics {
        return IBattleStatistics(this)
    }

    override fun getCardID(): String = this::class.java.annotations.find { it is CardDetails }?.let { (it as CardDetails).id } ?: "unknown"

    override fun getLocalizedName(): String = this::class.java.annotations.find { it is CardDetails }?.let { BattleConfig.getLocalizedString((it as CardDetails).name) } ?: "Unknown"

    override fun getRarity(): Rarity = this::class.java.annotations.find { it is CardDetails }?.let { (it as CardDetails).rarity } ?: Rarity.COMMON

    override fun getEntity(): T? = entity

    override fun getCreationDate(): Date = Date(creationDate)

    override fun getLastUsed(): Date? = lastUsed?.let { Date(it) }

    override fun getLastUsedPlayer(): Player? = lastUsedPlayer

    // Util

    fun getAnnotations(): List<Annotation> {
        val annotations = mutableListOf<Annotation>()
        var superClass: Class<*> = this::class.java

        while (superClass.superclass != null) {
            annotations.addAll(superClass.annotations)
            superClass = superClass.superclass
        }

        return annotations
    }

}