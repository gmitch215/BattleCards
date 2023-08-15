package me.gamercoder215.battlecards.wrapper

import org.bukkit.inventory.Inventory

@Suppress("unchecked_cast")
interface BattleInventory : Inventory {

    val attributes: Map<String, Any?>

    override fun getTitle(): String = get("_name", String::class.java, "Inventory")

    val id: String?
        get() = get("_id", String::class.java)

    var isCancelled: Boolean
        get() = get("_cancel", Boolean::class.java, false)
        set(value) = set("_cancel", value)

    operator fun set(key: String, value: Any?)

    operator fun get(key: String): Any? = attributes[key]

    operator fun get(key: String, def: Any): Any = attributes[key] ?: def

    operator fun <T> get(key: String, cast: Class<T>): T? = (if (cast in wrappers) (cast as Class<*>).kotlin.javaObjectType else cast).cast(get(key)) as? T

    operator fun <T> get(key: String, cast: Class<T>, def: T) = get(key, cast) ?: def

    private companion object {

        private val wrappers = setOf(
            Double::class,
            Float::class,
            Long::class,
            Int::class,
            Short::class,
            Byte::class,
            Char::class,
            Boolean::class,
        ).map { it.java }

    }
}