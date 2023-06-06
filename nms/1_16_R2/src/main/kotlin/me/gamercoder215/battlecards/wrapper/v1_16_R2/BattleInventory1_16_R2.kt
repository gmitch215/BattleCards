package me.gamercoder215.battlecards.wrapper.v1_16_R2

import me.gamercoder215.battlecards.wrapper.BattleInventory
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftInventoryCustom

// https://youtrack.jetbrains.com/issue/KTIJ-25766/
internal class BattleInventory1_16_R2(
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

}