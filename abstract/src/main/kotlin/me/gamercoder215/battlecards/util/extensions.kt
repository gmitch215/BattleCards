package me.gamercoder215.battlecards.util

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.impl.cards.IBattleCard
import me.gamercoder215.battlecards.wrapper.NBTWrapper
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.w
import org.bukkit.Location
import org.bukkit.Server
import org.bukkit.entity.Creature
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.util.Vector
import java.util.*
import kotlin.math.cos
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin

fun ItemStack.nbt(nbt: (NBTWrapper) -> Unit): ItemStack {
    val w = NBTWrapper.of(this)
    nbt(w)
    return w.item
}

inline val Entity.isCard: Boolean
    get() {
        if (this !is Creature) return false
        return w.isCard(this) || IBattleCard.spawned.containsKey(uniqueId)
    }

inline val Entity.isMinion: Boolean
    get() {
        if (this !is Creature) return false
        return IBattleCard.byMinion(this) != null
    }

inline val Entity.card: IBattleCard<*>?
    get() {
        if (!isCard) return null
        return IBattleCard.byEntity(this as Creature)
    }

inline val Entity.cardByMinion: IBattleCard<*>?
    get() {
        if (!isMinion) return null
        return IBattleCard.byMinion(this as Creature)
    }

inline val ItemStack.card: ICard?
    get() {
        val nbt = NBTWrapper.of(this)
        val bytes = nbt.getByteArray("card")
        if (bytes.isEmpty()) return null

        return ICard.fromByteArray(bytes)
    }

inline val ItemStack.isCard: Boolean
    get() = NBTWrapper.of(this).getByteArray("card").isNotEmpty()

inline val ItemStack.id: String?
    get() {
        val id = NBTWrapper.of(this).getID()
        if (id.isEmpty()) return null

        return id
    }

inline val Player.spawnedCards: List<IBattleCard<*>>
    get() = IBattleCard.spawned.values.filter { it.p == this }

inline val PlayerInventory.cards: Map<Int, ICard>
    get() {
        val cards = mutableMapOf<Int, ICard>()
        for (i in 0 until size) {
            val item = getItem(i)
            cards[i] = item.card ?: continue
        }

        return cards
    }

inline var Creature.attackType: CardAttackType
    get() = w.getAttackType(this)
    set(value) = w.setAttackType(this, value)

inline var IBattleCard<*>.attackType: CardAttackType
    get() = this.entity.attackType
    set(value) { this.entity.attackType = value }

fun Entity.isMinion(card: IBattleCard<*>): Boolean {
    if (this !is Creature) return false
    return IBattleCard.byMinion(this) == card
}

fun Player.playSuccess() {
    playSound(location, BattleSound.ENTITY_ARROW_HIT_PLAYER.find(), 1F, 2F)
}

fun Player.playFailure() {
    playSound(location, BattleSound.BLOCK_NOTE_BLOCK_PLING.find(), 1F, 0F)
}

fun Defensive.getChance(level: Int, unlockedAt: Int = 0): Double {
    var chance = this.chance
    if (!value.isNaN())
        for (i in 1 until (level - unlockedAt)) chance = operation(chance, value)

    return chance.coerceAtMost(max)
}

fun UserDefensive.getChance(level: Int, unlockedAt: Int = 0): Double {
    var chance = this.chance
    if (!value.isNaN())
        for (i in 1 until (level - unlockedAt)) chance = operation(chance, value)

    return chance.coerceAtMost(max)
}

fun Offensive.getChance(level: Int, unlockedAt: Int = 0): Double {
    var chance = this.chance
    if (!value.isNaN())
        for (i in 1 until (level - unlockedAt)) chance = operation(chance, value)

    return chance.coerceAtMost(max)
}

fun UserOffensive.getChance(level: Int, unlockedAt: Int = 0): Double {
    var chance = this.chance
    if (!value.isNaN())
        for (i in 1 until (level - unlockedAt)) chance = operation(chance, value)

    return chance.coerceAtMost(max)
}

fun Damage.getChance(level: Int, unlockedAt: Int = 0): Double {
    var chance = this.chance
    if (!value.isNaN())
        for (i in 1 until (level - unlockedAt)) chance = operation(chance, value)

    return chance.coerceAtMost(max)
}

fun Passive.getChance(level: Int, unlockedAt: Int = 0): Long {
    var interval = this.interval

    if (value != Long.MIN_VALUE)
        for (i in 1 until (level - unlockedAt)) interval = operation(interval.toDouble(), value.toDouble()).toLong()

    return interval
}

// Bukkit Extensions from Newer Version & Utils

fun Server.getEntity(id: UUID): Entity? {
    for (world in worlds)
        if (world.entities.map { it.uniqueId }.contains(id))
            return world.entities.first { it.uniqueId == id }

    return null
}

fun Vector.rotateAroundY(angle: Double): Vector {
    val cos = cos(angle)
    val sin = sin(angle)
    val nx: Double = cos * x + sin * z
    val nz: Double = -sin * x + cos * z
    return setX(nx).setZ(nz)
}

fun Vector.rotateAroundNonUnitAxis(axis: Vector, angle: Double): Vector {
    val x2 = axis.x
    val y2 = axis.y
    val z2 = axis.z

    val cos = cos(angle)
    val sin = sin(angle)
    val dot: Double = dot(axis)

    val xPrime = x2 * dot * (1.0 - cos) + x * cos + (-z2 * y + y2 * z) * sin
    val yPrime = y2 * dot * (1.0 - cos) + y * cos + (z2 * x - x2 * z) * sin
    val zPrime = z2 * dot * (1.0 - cos) + z * cos + (-y2 * x + x2 * y) * sin

    return setX(xPrime).setY(yPrime).setZ(zPrime)
}

operator fun Location.plus(other: Location): Location = add(other)
operator fun Location.plus(other: Vector): Location = add(other)

// Kotlin Util

fun Number.format(): String {
    return when (this) {
        is Int, is Long -> CardUtils.format("%,d", this)
        else -> CardUtils.format("%,.2f", this)
    }
}

fun Number.formatInt(): String {
    return when (this) {
        is Int, is Long -> format()
        else -> CardUtils.format("%,.0f", this)
    }
}

private val ROMAN_NUMERALS = TreeMap<Long, String>().apply {
    putAll(mutableMapOf(
        1000L to "M",
        900L to "CM",
        500L to "D",
        400L to "CD",
        100L to "C",
        90L to "XC",
        50L to "L",
        40L to "XL",
        10L to "X",
        9L to "IX",
        5L to "V",
        4L to "IV",
        1L to "I"
    ))
}

fun Number.toRoman(): String {
    val number = toLong()
    val l: Long = ROMAN_NUMERALS.floorKey(number)
    return if (number == l) ROMAN_NUMERALS[number]!! else ROMAN_NUMERALS[l] + (number - l).toRoman()
}

private const val SUFFIXES = "KMBTQEXSON"

fun Number.withSuffix(): String {
    val num = toDouble()
    if (num < 0) return "-" + (-num).withSuffix()
    if (num < 1000) return format()

    val index = (log10(num) / 3).toInt()
    val suffix = SUFFIXES[index - 1].toString()

    return CardUtils.format("%.1f%s", num / 1000.0.pow(index), suffix)
}

fun Enum<*>.formatName(): String {
    return name.lowercase(BattleConfig.config.locale).split("_").joinToString(" ") { s -> s.replaceFirstChar { it.uppercase() } }
}

fun String.replace(vararg replacements: Pair<String, String>): String {
    var result = this
    for (replacement in replacements)
        result = result.replace(replacement.first, replacement.second)

    return result
}

fun String.replace(replacements: Map<String, String>): String =
    replace(*replacements.toList().toTypedArray())

