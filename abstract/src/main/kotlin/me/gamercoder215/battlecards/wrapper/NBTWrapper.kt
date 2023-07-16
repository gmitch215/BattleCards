package me.gamercoder215.battlecards.wrapper

import me.gamercoder215.battlecards.util.BattleMaterial
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.w
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.*

abstract class NBTWrapper(
    var item: ItemStack
) {

    companion object {
        @JvmStatic
        protected val ROOT = "BattleCards"

        @JvmStatic
        protected val TAGS_KEY = "tags"

        @JvmStatic
        fun of(item: ItemStack) = w.getNBTWrapper(item)

        @JvmStatic
        fun builder(item: ItemStack, action: (NBTWrapper) -> Unit): ItemStack {
            return of(item.clone()).apply {
                action(this)
            }.item
        }

        @JvmStatic
        fun builder(material: Material, action: (NBTWrapper) -> Unit): ItemStack = builder(ItemStack(material), action)

        @JvmStatic
        fun builder(material: BattleMaterial, action: (NBTWrapper) -> Unit): ItemStack = builder(ItemStack(material.findStack()), action)

        @JvmStatic
        fun builder(item: ItemStack, meta: (ItemMeta) -> Unit, nbt: (NBTWrapper) -> Unit): ItemStack {
            val item0 = item.clone()
            item0.itemMeta = item0.itemMeta.apply { meta(this) }

            return of(item0).apply { nbt(this) }.item
        }

        @JvmStatic
        fun builder(material: Material, meta: (ItemMeta) -> Unit, nbt: (NBTWrapper) -> Unit): ItemStack = builder(ItemStack(material), meta, nbt)

        @JvmStatic
        fun builder(material: BattleMaterial, meta: (ItemMeta) -> Unit, nbt: (NBTWrapper) -> Unit): ItemStack = builder(ItemStack(material.findStack()), meta, nbt)
    }


    // NBT Methods/Implementation

    var id: String
        get() = getString("id")
        set(value) = set("id", value)

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

    abstract fun getByteArray(key: String): ByteArray

    abstract operator fun set(key: String, value: ByteArray)

    abstract fun getTags(): Set<String>

    abstract fun addTag(tag: String)

    fun hasTag(tag: String): Boolean = getTags().contains(tag)

    abstract fun removeTag(tag: String)

    fun removeTags(vararg tags: String) = removeTags(tags.toList())

    abstract fun removeTags(tags: Collection<String>)

    operator fun set(key: String, value: Number) {
        when (value) {
            is Int -> set(key, value)
            is Double -> set(key, value)
            is Long -> set(key, value)
            is Float -> set(key, value)
            else -> set(key, value.toDouble())
        }
    }

}