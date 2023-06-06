package me.gamercoder215.battlecards.util

import me.gamercoder215.battlecards.wrapper.NBTWrapper
import org.bukkit.inventory.ItemStack

fun ItemStack.nbt(nbt: (NBTWrapper) -> Unit): ItemStack {
    val w = NBTWrapper.of(this)
    nbt(w)
    return w.getItem()
}