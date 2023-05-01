package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCard
import me.gamercoder215.battlecards.api.card.BattleStatistics
import me.gamercoder215.battlecards.api.card.Rarity
import me.gamercoder215.battlecards.impl.CardDetails
import me.gamercoder215.battlecards.impl.IBattleStatistics
import me.gamercoder215.battlecards.wrapper.Wrapper
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.w
import org.bukkit.Location
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import java.util.*

abstract class IBattleCard<T : Mob>(
    protected val creation: Long = System.currentTimeMillis(),
    internal val stats: MutableMap<String, Any> = mutableMapOf(),
    protected var last: Long? = null,
    protected var lastPlayer: Player? = null
) : BattleCard<T> {

    protected lateinit var en: T

    fun spawn(location: Location): T {
        en = location.world?.spawn(location, getEntityClass()) ?: throw IllegalStateException("Could not spawn entity")

        en.isCustomNameVisible = true
        en.customName = "${getRarity().getColor()}${getLocalizedName()}"

//        w.loadProperties(en, this)

        init()
        return en
    }

    // Implementation

    open fun init() {
        if (!this::en.isInitialized) throw IllegalStateException("Entity not spawned")
    }

    final override fun getStatistics(): IBattleStatistics {
        return IBattleStatistics(this)
    }

    final override fun getEntity(): T = en

    final override fun getCardID(): String = this::class.java.annotations.find { it is CardDetails }?.let { (it as CardDetails).id } ?: "unknown"

    final override fun getLocalizedName(): String = this::class.java.annotations.find { it is CardDetails }?.let { BattleConfig.getLocalizedString((it as CardDetails).name) } ?: "Unknown"

    final override fun getRarity(): Rarity = this::class.java.annotations.find { it is CardDetails }?.let { (it as CardDetails).rarity } ?: Rarity.COMMON

    final override fun getCreationDate(): Date = Date(creation)

    override fun getLastUsed(): Date? = last?.let { Date(it) }

    override fun getLastUsedPlayer(): Player? = lastPlayer

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