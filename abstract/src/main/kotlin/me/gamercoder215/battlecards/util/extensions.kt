package me.gamercoder215.battlecards.util

import me.gamercoder215.battlecards.impl.cards.IBattleCard
import me.gamercoder215.battlecards.wrapper.NBTWrapper
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.w
import org.bukkit.Server
import org.bukkit.entity.Creature
import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

fun ItemStack.nbt(nbt: (NBTWrapper) -> Unit): ItemStack {
    val w = NBTWrapper.of(this)
    nbt(w)
    return w.item
}

fun Entity.isCard(): Boolean {
    if (this !is Creature) return false
    return w.isCard(this)
}

fun Entity.getCard(): IBattleCard<*>? {
    if (!isCard()) return null
    return IBattleCard.byEntity(this as Creature)
}

// Bukkit Extensions from Newer Version

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

// Kotlin Util

fun Double.format(): String {
    return CardUtils.format("%,.2f", this)
}

fun Float.format(): String {
    return CardUtils.format("%,.2f", this)
}

fun Double.formatInt(): String {
    return CardUtils.format("%,.0f", this)
}

fun Float.formatInt(): String {
    return CardUtils.format("%,.0f", this)
}

fun Int.format(): String {
    return CardUtils.format("%,d", this)
}

fun Long.format(): String {
    return CardUtils.format("%,d", this)
}