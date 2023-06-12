package me.gamercoder215.battlecards.wrapper.v1_13_R1

import com.google.common.collect.ImmutableSet
import me.gamercoder215.battlecards.wrapper.NBTWrapper
import net.minecraft.server.v1_13_R1.NBTTagString
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import java.util.*

internal class NBTWrapper1_13_R1(item: ItemStack) : NBTWrapper(item) {

    override fun set(key: String, value: String) {
        val nms = CraftItemStack.asNMSCopy(item)
        val tag = nms.orCreateTag
        val battlecards = tag.getCompound(ROOT)

        battlecards.setString(key, value)
        tag[ROOT] = battlecards
        nms.tag = tag
        item = CraftItemStack.asBukkitCopy(nms)
    }

    override fun set(key: String, value: Boolean) {
        val nms = CraftItemStack.asNMSCopy(item)
        val tag = nms.orCreateTag
        val battlecards = tag.getCompound(ROOT)

        battlecards.setBoolean(key, value)
        tag[ROOT] = battlecards
        nms.tag = tag
        item = CraftItemStack.asBukkitCopy(nms)
    }

    override fun set(key: String, value: Int) {
        val nms = CraftItemStack.asNMSCopy(item)
        val tag = nms.orCreateTag
        val battlecards = tag.getCompound(ROOT)

        battlecards.setInt(key, value)
        tag[ROOT] = battlecards
        nms.tag = tag
        item = CraftItemStack.asBukkitCopy(nms)
    }

    override fun set(key: String, value: Double) {
        val nms = CraftItemStack.asNMSCopy(item)
        val tag = nms.orCreateTag
        val battlecards = tag.getCompound(ROOT)

        battlecards.setDouble(key, value)
        tag[ROOT] = battlecards
        nms.tag = tag
        item = CraftItemStack.asBukkitCopy(nms)
    }

    override fun set(key: String, value: Long) {
        val nms = CraftItemStack.asNMSCopy(item)
        val tag = nms.orCreateTag
        val battlecards = tag.getCompound(ROOT)

        battlecards.setLong(key, value)
        tag[ROOT] = battlecards
        nms.tag = tag
        item = CraftItemStack.asBukkitCopy(nms)
    }

    override fun set(key: String, value: Float) {
        val nms = CraftItemStack.asNMSCopy(item)
        val tag = nms.orCreateTag
        val battlecards = tag.getCompound(ROOT)

        battlecards.setFloat(key, value)
        tag[ROOT] = battlecards
        nms.tag = tag
        item = CraftItemStack.asBukkitCopy(nms)
    }

    override fun set(key: String, value: UUID) {
        val nms = CraftItemStack.asNMSCopy(item)
        val tag = nms.orCreateTag
        val battlecards = tag.getCompound(ROOT)

        battlecards.a(key, value)
        tag[ROOT] = battlecards
        nms.tag = tag
        item = CraftItemStack.asBukkitCopy(nms)
    }

    override fun set(key: String, value: ByteArray) {
        val nms = CraftItemStack.asNMSCopy(item)
        val tag = nms.orCreateTag
        val battlecards = tag.getCompound(ROOT)

        battlecards.setByteArray(key, value)
        tag.set(ROOT, battlecards)
        nms.tag = tag
        item = CraftItemStack.asBukkitCopy(nms)
    }

    override fun getString(key: String): String = CraftItemStack.asNMSCopy(item).orCreateTag.getCompound(ROOT).getString(key)
    override fun getBoolean(key: String): Boolean = CraftItemStack.asNMSCopy(item).orCreateTag.getCompound(ROOT).getBoolean(key)
    override fun getInt(key: String): Int = CraftItemStack.asNMSCopy(item).orCreateTag.getCompound(ROOT).getInt(key)
    override fun getDouble(key: String): Double = CraftItemStack.asNMSCopy(item).orCreateTag.getCompound(ROOT).getDouble(key)
    override fun getLong(key: String): Long = CraftItemStack.asNMSCopy(item).orCreateTag.getCompound(ROOT).getLong(key)
    override fun getFloat(key: String): Float = CraftItemStack.asNMSCopy(item).orCreateTag.getCompound(ROOT).getFloat(key)
    override fun getUUID(key: String): UUID = CraftItemStack.asNMSCopy(item).orCreateTag.getCompound(ROOT).a(key) ?: throw NullPointerException()
    override fun getByteArray(key: String): ByteArray = CraftItemStack.asNMSCopy(item).orCreateTag.getCompound(ROOT).getByteArray(key)
    override fun getTags(): Set<String> = ImmutableSet.copyOf(CraftItemStack.asNMSCopy(item).orCreateTag.getCompound(ROOT).getList(TAGS_KEY, 8).map { it.b_() })

    override fun addTag(tag: String) {
        val nms = CraftItemStack.asNMSCopy(item)
        val nmsT = nms.orCreateTag
        val battlecards = nmsT.getCompound(ROOT)

        val tags = battlecards.getList(TAGS_KEY, 8)
        tags.add(NBTTagString(tag))
        battlecards[TAGS_KEY] = tags
        nmsT[ROOT] = battlecards
        nms.tag = nmsT
        item = CraftItemStack.asBukkitCopy(nms)
    }

    override fun removeTag(tag: String) {
        val nms = CraftItemStack.asNMSCopy(item)
        val nmsT = nms.orCreateTag
        val battlecards = nmsT.getCompound(ROOT)

        val tags = battlecards.getList(TAGS_KEY, 8)
        tags.removeIf { it.b_() == tag }
        battlecards[TAGS_KEY] = tags
        nmsT[ROOT] = battlecards
        nms.tag = nmsT
        item = CraftItemStack.asBukkitCopy(nms)
    }

    override fun removeTags(tags: Collection<String>) {
        val nms = CraftItemStack.asNMSCopy(item)
        val nmsT = nms.orCreateTag
        val battlecards = nmsT.getCompound(ROOT)

        val btags = battlecards.getList(TAGS_KEY, 8)
        btags.removeIf { tags.contains(it.b_()) }
        battlecards[TAGS_KEY] = btags
        nmsT[ROOT] = battlecards
        nms.tag = nmsT
        item = CraftItemStack.asBukkitCopy(nms)
    }
}