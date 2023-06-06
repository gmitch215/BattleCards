package me.gamercoder215.battlecards.wrapper

import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.w
import org.bukkit.inventory.ItemStack
import java.util.*

abstract class NBTWrapper(
    protected var item: ItemStack
) {

    companion object {
        @JvmStatic
        protected val ROOT = "BattleCards"

        fun of(item: ItemStack) = w.getNBTWrapper(item)
    }

    fun getItem(): ItemStack = item

    // NBT Methods/Implementation

    fun getID(): String = getString("id")

    fun setID(id: String) = set("id", id)

    fun hasID(): Boolean = getString("id").isNotEmpty()

    fun getClass(key: String): Class<*> = Class.forName(getString(key))

    fun <T> getClass(key: String, cast: Class<T>): Class<out T> = getClass(key).asSubclass(cast)

    abstract fun getString(key: String): String

    abstract operator fun set(key: String, value: String)

    abstract fun getBoolean(key: String): Boolean

    abstract operator fun set(key: String, value: Boolean)

    abstract fun getInt(key: String): Int

    abstract operator fun set(key: String, value: Int)

    abstract fun getDouble(key: String): Double

    abstract operator fun set(key: String, value: Double)

    abstract fun getLong(key: String): Long

    abstract operator fun set(key: String, value: Long)

    abstract fun getFloat(key: String): Float

    abstract operator fun set(key: String, value: Float)

    abstract fun getUUID(key: String): UUID

    abstract operator fun set(key: String, value: UUID)

}