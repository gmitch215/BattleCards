package me.gamercoder215.battlecards.wrapper.v1_20_R3

import me.gamercoder215.battlecards.wrapper.BattleInventory
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftInventoryCustom

// https://youtrack.jetbrains.com/issue/KT-59638
@Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED")
internal class BattleInventory1_20_R3(
    id: String,
    name: String,
    size: Int
) : CraftInventoryCustom(null, size, name), BattleInventory {

    override val attributes = mutableMapOf<String, Any?>()

    init {
        set("_id", id)
        set("_name", name)
    }

    override fun set(key: String, value: Any?) {
        attributes[key] = value
    }

}