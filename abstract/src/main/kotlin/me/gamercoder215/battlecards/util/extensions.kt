package me.gamercoder215.battlecards.util

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.impl.cards.IBattleCard
import me.gamercoder215.battlecards.util.CardUtils.BLOCK_DATA
import me.gamercoder215.battlecards.util.CardUtils.format
import me.gamercoder215.battlecards.wrapper.NBTWrapper
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.get
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.w
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.Creature
import org.bukkit.entity.Entity
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.Vector
import java.util.*
import kotlin.math.cos
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin

fun ItemStack.nbt(nbt: (NBTWrapper) -> Unit): ItemStack {
    val w = this.nbt
    nbt(w)
    return w.item
}

inline val Entity.isCard: Boolean
    get() {
        if (this !is Creature) return false
        return card != null
    }

inline val Item.isCard: Boolean
    get() = itemStack.isCard

inline val Entity.isMinion: Boolean
    get() {
        if (this !is Creature) return false
        return IBattleCard.byMinion(this) != null
    }

inline val Entity.card: IBattleCard<*>?
    get() = IBattleCard.byEntity(this as Creature)

inline val Entity.cardByMinion: IBattleCard<*>?
    get() {
        if (!isMinion) return null
        return IBattleCard.byMinion(this as Creature)
    }

inline val ItemStack.card: ICard?
    get() {
        val bytes = nbt.getByteArray("card")
        if (bytes.isEmpty()) return null

        return ICard.fromByteArray(bytes)
    }

inline val ItemStack.isCard: Boolean
    get() = nbt.getByteArray("card").isNotEmpty()

inline val ItemStack.id: String?
    get() {
        val id = nbt.id
        if (id.isEmpty()) return null

        return id
    }

inline val ItemStack.isCardBlock: Boolean
    get() = nbt.getBoolean("card_block")

inline val Player.spawnedCards: List<IBattleCard<*>>
    get() = IBattleCard.spawned.values.filter { it.p == this }

inline val PlayerInventory.cards: Map<Int, ICard>
    get() {
        val cards = mutableMapOf<Int, ICard>()
        for (i in 0 until size) {
            val item = this[i] ?: continue
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

inline val ItemStack.nbt
    get() = NBTWrapper.of(this)

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

operator fun Entity.set(key: String, value: Any) {
    w.setEntityNBT(this, key, value)
}

operator fun Entity.get(key: String): Any? {
    return w.getEntityNBT(this, key)
}

inline val Player.cardInHand: ICard?
    get() = inventory.itemInHand.card

inline val Player.attackable: Boolean
    get() {
        return gameMode != GameMode.CREATIVE && gameMode != GameMode.SPECTATOR
    }

inline val Player.gameName: String
    get() = displayName ?: name

fun Event.call() {
    Bukkit.getPluginManager().callEvent(this)
}

operator fun Block.get(key: String): Any? {
    return BLOCK_DATA[this.location]?.attributes?.get(key)
}

operator fun Block.get(key: String, def: Any?): Any? {
    return get(key) ?: def
}

operator fun <T> Block.get(key: String, clazz: Class<T>): T? {
    return clazz.cast(get(key))
}

operator fun <T> Block.get(key: String, clazz: Class<T>, def: T): T {
    return clazz.cast(get(key, def))
}

operator fun Block.set(key: String, value: Any) {
    var data = BLOCK_DATA[this.location]
    if (data == null) {
        data = BattleBlockData(this)
        BLOCK_DATA[this.location] = data
    }

    data.attributes[key] = value
}

inline val Block.isCardBlock: Boolean
    get() = get("card_block")?.toString()?.isNotEmpty() == true

inline val Chunk.blocks: Set<Block>
    get() {
        val blocks = mutableSetOf<Block>()

        for (x in 0..15)
            for (y in 0..255)
                for (z in 0..15)
                    blocks.add(getBlock(x, y, z))

        return blocks
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

fun UserDamage.getChance(level: Int, unlockedAt: Int = 0): Double {
    var chance = this.chance
    if (!value.isNaN())
        for (i in 1 until (level - unlockedAt)) chance = operation(chance, value)

    return chance.coerceAtMost(max)
}

fun Passive.getChance(level: Int, unlockedAt: Int = 0): Long {
    var interval = this.interval

    if (value != Long.MIN_VALUE)
        for (i in 1 until (level - unlockedAt)) interval = operation(interval.toDouble(), value.toDouble()).toLong()

    return interval.coerceIn(min, max)
}

fun sync(block: () -> Unit): BukkitTask = Bukkit.getScheduler().runTask(BattleConfig.plugin, block)
fun async(block: () -> Unit): BukkitTask = Bukkit.getScheduler().runTaskAsynchronously(BattleConfig.plugin, block)

// Bukkit Extensions from Newer Version & Utils

fun Server.getEntity(id: UUID): Entity? {
    for (world in worlds)
        if (world.entities.map { it.uniqueId }.contains(id))
            return world.entities.first { it.uniqueId == id }

    return null
}

fun Vector.rotateAroundY(angle: Double): Vector {
    val cos = cos(angle); val sin = sin(angle)

    val nx: Double = cos * x + sin * z
    val nz: Double = -sin * x + cos * z
    return setX(nx).setZ(nz)
}

fun Vector.rotateAroundNonUnitAxis(axis: Vector, angle: Double): Vector {
    val x2 = axis.x; val y2 = axis.y; val z2 = axis.z

    val cos = cos(angle); val sin = sin(angle)
    val dot: Double = dot(axis)

    val xPrime = x2 * dot * (1.0 - cos) + x * cos + (-z2 * y + y2 * z) * sin
    val yPrime = y2 * dot * (1.0 - cos) + y * cos + (z2 * x - x2 * z) * sin
    val zPrime = z2 * dot * (1.0 - cos) + z * cos + (-y2 * x + x2 * y) * sin

    return setX(xPrime).setY(yPrime).setZ(zPrime)
}

operator fun Location.plus(other: Location): Location = add(other)
operator fun Location.plus(other: Vector): Location = add(other)

operator fun Vector.plus(other: Vector): Vector = add(other)
operator fun Vector.plus(other: Location): Vector = add(other.toVector())
operator fun Vector.times(other: Vector): Vector = multiply(other)
operator fun Vector.times(other: Location): Vector = multiply(other.toVector())
operator fun Vector.times(other: Number): Vector = multiply(other.toDouble())

operator fun Inventory.get(index: Int): ItemStack? = getItem(index)
operator fun Inventory.set(index: Int, item: ItemStack?) = setItem(index, item)

// Kotlin Util

fun <K, V> MutableCollection<Pair<K, V>>.put(first: K, second: V) = add(first to second)

fun Number.format(): String {
    return when (this) {
        is Int, is Long -> format("%,d", this)
        else -> format("%,.2f", this).dropLastWhile { it == '0' }.dropLastWhile { it == '.' }
    }
}

fun Number.formatInt(): String {
    return when (this) {
        is Int, is Long -> format()
        else -> format("%,.0f", this)
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

fun Long.formatTime(): String {
    val seconds = this / 1000; val minutes = seconds / 60; val hours = minutes / 60
    val days = hours / 24; val weeks = days / 7
    val months = weeks / 4; val years = months / 12

    return when {
        years > 0 -> format(get("time.years"), years)
        months > 0 -> format(get("time.months"), months)
        weeks > 0 -> format(get("time.weeks"), weeks)
        days > 0 -> format(get("time.days"), days)
        hours > 0 -> format(get("time.hours"), hours)
        minutes > 0 -> format(get("time.minutes"), minutes)
        else -> format(get("time.seconds"), seconds)
    }
}

private const val SUFFIXES = "KMBTQEXSON"

fun Number.withSuffix(): String {
    val num = toDouble()
    if (num.isNaN()) return num.toString()
    if (num < 0) return "-" + (-num).withSuffix()
    if (num < 1000) return format()

    val index = (log10(num) / 3).toInt()
    val suffix = SUFFIXES[index - 1].toString()

    return format("%.1f%s", num / 1000.0.pow(index), suffix)
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