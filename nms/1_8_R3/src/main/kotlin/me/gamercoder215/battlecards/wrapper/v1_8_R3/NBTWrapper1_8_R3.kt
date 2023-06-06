package me.gamercoder215.battlecards.wrapper.v1_8_R3

import me.gamercoder215.battlecards.wrapper.NBTWrapper
import net.minecraft.server.v1_8_R3.NBTTagCompound
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import java.util.*

internal class NBTWrapper1_8_R3(item: ItemStack) : NBTWrapper(item) {

    override fun set(key: String, value: String) {
        val nms = CraftItemStack.asNMSCopy(item)
        val tag = nms.tag ?: NBTTagCompound()
        val battlecards = tag.getCompound(ROOT)

        battlecards.setString(key, value)
        tag[ROOT] = battlecards
        nms.tag = tag
        item = CraftItemStack.asBukkitCopy(nms)
    }

    override fun set(key: String, value: Boolean) {
        val nms = CraftItemStack.asNMSCopy(item)
        val tag = nms.tag ?: NBTTagCompound()
        val battlecards = tag.getCompound(ROOT)

        battlecards.setBoolean(key, value)
        tag[ROOT] = battlecards
        nms.tag = tag
        item = CraftItemStack.asBukkitCopy(nms)
    }

    override fun set(key: String, value: Int) {
        val nms = CraftItemStack.asNMSCopy(item)
        val tag = nms.tag ?: NBTTagCompound()
        val battlecards = tag.getCompound(ROOT)

        battlecards.setInt(key, value)
        tag[ROOT] = battlecards
        nms.tag = tag
        item = CraftItemStack.asBukkitCopy(nms)
    }

    override fun set(key: String, value: Double) {
        val nms = CraftItemStack.asNMSCopy(item)
        val tag = nms.tag ?: NBTTagCompound()
        val battlecards = tag.getCompound(ROOT)

        battlecards.setDouble(key, value)
        tag[ROOT] = battlecards
        nms.tag = tag
        item = CraftItemStack.asBukkitCopy(nms)
    }

    override fun set(key: String, value: Long) {
        val nms = CraftItemStack.asNMSCopy(item)
        val tag = nms.tag ?: NBTTagCompound()
        val battlecards = tag.getCompound(ROOT)

        battlecards.setLong(key, value)
        tag[ROOT] = battlecards
        nms.tag = tag
        item = CraftItemStack.asBukkitCopy(nms)
    }

    override fun set(key: String, value: Float) {
        val nms = CraftItemStack.asNMSCopy(item)
        val tag = nms.tag ?: NBTTagCompound()
        val battlecards = tag.getCompound(ROOT)

        battlecards.setFloat(key, value)
        tag[ROOT] = battlecards
        nms.tag = tag
        item = CraftItemStack.asBukkitCopy(nms)
    }

    override fun set(key: String, value: UUID) = set(key, value.toString())

    override fun getString(key: String): String = (CraftItemStack.asNMSCopy(item).tag ?: NBTTagCompound()).getCompound(ROOT).getString(key)
    override fun getBoolean(key: String): Boolean = (CraftItemStack.asNMSCopy(item).tag ?: NBTTagCompound()).getCompound(ROOT).getBoolean(key)
    override fun getInt(key: String): Int = (CraftItemStack.asNMSCopy(item).tag ?: NBTTagCompound()).getCompound(ROOT).getInt(key)
    override fun getDouble(key: String): Double = (CraftItemStack.asNMSCopy(item).tag ?: NBTTagCompound()).getCompound(ROOT).getDouble(key)
    override fun getLong(key: String): Long = (CraftItemStack.asNMSCopy(item).tag ?: NBTTagCompound()).getCompound(ROOT).getLong(key)
    override fun getFloat(key: String): Float = (CraftItemStack.asNMSCopy(item).tag ?: NBTTagCompound()).getCompound(ROOT).getFloat(key)
    override fun getUUID(key: String): UUID = UUID.fromString(getString(key))

}