package me.gamercoder215.battlecards.util.inventory

import me.gamercoder215.battlecards.util.BattleMaterial
import me.gamercoder215.battlecards.wrapper.NBTWrapper.Companion.builder
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

object Items {

    @JvmStatic
    val GUI_BACKGROUND: ItemStack = builder(BattleMaterial.BLACK_STAINED_GLASS_PANE,
        { meta -> meta.displayName = " " },
        { nbt -> nbt.id = "gui_background" }
    )

    @JvmStatic
    fun builder(material: Material, action: (ItemMeta) -> Unit): ItemStack {
        return ItemStack(material).apply {
            itemMeta = itemMeta.apply {
                action(this)
            }
        }
    }

}