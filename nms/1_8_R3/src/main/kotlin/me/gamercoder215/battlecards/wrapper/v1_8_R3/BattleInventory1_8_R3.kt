package me.gamercoder215.battlecards.wrapper.v1_8_R3

import me.gamercoder215.battlecards.wrapper.BattleInventory
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryCustom

internal class BattleInventory1_8_R3(
    id: String,
    name: String,
    size: Int
) : CraftInventoryCustom(null, size, name), BattleInventory {

    private val attributes = mutableMapOf<String, Any>()

    init {
        set("_id", id)
        set("_name", name)
    }

    override fun set(key: String, value: Any) {
        attributes[key] = value
    }

    override fun getAttributes(): Map<String, Any> {
        return attributes
    }

    override fun getTitle() = super<BattleInventory>.getTitle()

}