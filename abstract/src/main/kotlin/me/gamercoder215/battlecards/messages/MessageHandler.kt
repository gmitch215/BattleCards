package me.gamercoder215.battlecards.messages

import com.google.common.collect.ImmutableList
import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.util.isDisabled
import org.bukkit.ChatColor.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.plugin.Plugin
import java.lang.reflect.Constructor
import java.text.SimpleDateFormat
import java.util.*

// Static Util

fun <T> any(iterable: () -> Iterable<T?>, toString: (T) -> String = { it.toString().lowercase() }, def: String, filter: (T?) -> Boolean = { true }): () -> String = {
    iterable()
        .filter(filter)
        .map { if (it != null) toString(it) else null }
        .randomOrNull() ?: def
}

fun get(key: String) = BattleConfig.config.get(key)

fun format(format: String, vararg args: Any): String {
    if (args.isEmpty()) return format

    return String.format(BattleConfig.config.locale, format, *args)
        .replace('\u00a0', ' ') // Replace non-breaking space
}

fun color(s: String): String {
    val array = s.trim().split("\\s".toRegex()).toTypedArray()
    val list = mutableListOf<String>()

    for (i in array.indices) {
        var str = array[i].replace("&", "$COLOR_CHAR")

        if (!str.startsWith(COLOR_CHAR)) {
            val strC = str.replace("[.,!+]".toRegex(), "")
            str = when {
                strC.contains("-") && strC.split("-").size == 2 -> {
                    val split = strC.split("-").toTypedArray()
                    "${color(split[0])}-${color(split[1])}"
                }
                strC.endsWith("%") -> "$DARK_AQUA$str"
                strC.endsWith("s") && str.substringBeforeLast("s").toDoubleOrNull() != null -> "$GOLD$str"
                strC.endsWith("x") && str.substringBeforeLast("x").toDoubleOrNull() != null -> "$RED$str"
                strC.toDoubleOrNull() != null -> "$BLUE$str"
                else -> "$GRAY$str"
            }
        }

        list.add(str)
    }

    return list.joinToString(" ")
}

fun dateFormat(date: Date?, time: Boolean = false): String? {
    if (date == null || date.time == 0L) return null

    val pattern = if (time) "MMM dd, yyyy '|' h:mm a" else "MMM dd, yyyy"
    return SimpleDateFormat(pattern, BattleConfig.config.locale).format(date)
}

// Statics

val prefix = "${get("plugin.prefix")} "

val EXAMPLE_COLORS = arrayOf(YELLOW, LIGHT_PURPLE, BLUE, DARK_PURPLE)

val ERROR_EXAMPLES = mapOf<String, () -> Any>(
    "error.argument.basic_type" to any({ BattleConfig.getValidBasicCards() }, def = "wither_skeleton"),
    "error.argument.card" to any({ BattleCardType.entries.filter { !it.isDisabled && it != BattleCardType.BASIC } }, def = "witherman"),
    "error.argument.entity_type" to any({ EntityType.entries }, def = "pig"),
).mapValues { (_, v) -> { v().toString() } }

// Fetcher

val messages = getMessageHandler()

fun getMessageHandler(): MessageHandler {
    val plugin = BattleConfig.plugin

    when (LoadedMessageType.find()) {
        LoadedMessageType.SPIGOT -> return SpigotMessageHandler(plugin)
        LoadedMessageType.ADVENTURE -> {
            try {
                val adventure = Class.forName("me.gamercoder215.battlecards.messages.AdventureMessageHandler")
                    .asSubclass(MessageHandler::class.java)

                val constructor: Constructor<out MessageHandler> = adventure.getDeclaredConstructor(Plugin::class.java)
                constructor.setAccessible(true)
                return constructor.newInstance(plugin)
            } catch (e: ReflectiveOperationException) {
                BattleConfig.print(e)
                return BukkitMessageHandler(plugin)
            }
        }
        else -> return BukkitMessageHandler(plugin)
    }
}

internal enum class LoadedMessageType {
    BUKKIT,
    SPIGOT,
    ADVENTURE;

    companion object {
        fun find(): LoadedMessageType {
            val type: String = BattleConfig.configuration.getString("Functionality.MessageHandler", "auto").lowercase()

            return when (type) {
                "bukkit" -> BUKKIT
                "spigot" -> SPIGOT
                "adventure" -> ADVENTURE
                else -> findAuto()
            }
        }

        fun findAuto(): LoadedMessageType {
            try {
                Class.forName("net.kyori.adventure.text.Component")
                return ADVENTURE
            } catch (e: ClassNotFoundException) {
                try {
                    Class.forName("org.bukkit.entity.Player\$Spigot")
                    return SPIGOT
                } catch (e2: ClassNotFoundException) {
                    return BUKKIT
                }
            }
        }
    }
}

// MessageHandler
interface MessageHandler {

    fun send(sender: CommandSender, key: String, vararg args: Any)

    fun sendMessage(sender: CommandSender, key: String, vararg args: Any)

    fun sendError(sender: CommandSender, key: String, vararg args: Any)

    fun sendSuccess(sender: CommandSender, key: String, vararg args: Any)

    fun sendRaw(sender: CommandSender, message: String)

    fun sendRaw(sender: CommandSender, vararg messages: String)

    fun sendRaw(sender: CommandSender, messages: Iterable<String>) = sendRaw(sender, *ImmutableList.copyOf(messages).toTypedArray<String>())

    fun sendRawMessage(sender: CommandSender, message: String)

}

// Extensions

fun CommandSender.send(key: String, vararg args: Any) = messages.send(this, key, *args)
fun CommandSender.sendMessage(key: String, vararg args: Any) = messages.sendMessage(this, key, *args)
fun CommandSender.sendError(key: String, vararg args: Any) = messages.sendError(this, key, *args)
fun CommandSender.sendSuccess(key: String, vararg args: Any) = messages.sendSuccess(this, key, *args)

fun CommandSender.sendRaw(message: String) = messages.sendRaw(this, message)
fun CommandSender.sendRaw(messages: Iterable<String>) = messages.forEach { sendRaw(it) }
fun CommandSender.sendRaw(vararg messages: String) = messages.forEach { sendRaw(it) }
fun CommandSender.sendRawMessage(message: String) = messages.sendRawMessage(this, message)