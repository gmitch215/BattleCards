package me.gamercoder215.battlecards.wrapper

import org.bukkit.inventory.Inventory

interface BattleInventory : Inventory {

    fun getAttributes(): Map<String, Any>

    override fun getTitle(): String = get("_name", String::class.java, "Inventory")

    val id: String?
        get() = get("_id", String::class.java)

    var isCancelled: Boolean
        get() = get("_cancel", Boolean::class.java, false)
        set(value) = set("_cancel", value)

    operator fun set(key: String, value: Any)

    operator fun get(key: String): Any? = getAttributes()[key]

    operator fun get(key: String, def: Any): Any = getAttributes()[key] ?: def

    operator fun <T> get(key: String, cast: Class<T>): T? = cast.cast(get(key))

    operator fun <T> get(key: String, cast: Class<T>, def: T) = get(key, cast) ?: def

}