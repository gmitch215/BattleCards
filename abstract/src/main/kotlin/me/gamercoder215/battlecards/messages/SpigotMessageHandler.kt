package me.gamercoder215.battlecards.messages

import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.r
import net.md_5.bungee.api.ChatColor.GOLD
import net.md_5.bungee.api.ChatColor.LIGHT_PURPLE
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin


internal class SpigotMessageHandler(private val plugin: Plugin) : MessageHandler {

    init {
        plugin.logger.info("Loaded Spigot MessageHandler")
    }

    companion object {
        val CONSOLE_SPIGOT = object : Player.Spigot() {
            override fun sendMessage(component: BaseComponent) = Bukkit.getConsoleSender().sendMessage(component.toLegacyText())

            override fun sendMessage(vararg components: BaseComponent) = Bukkit.getConsoleSender().sendMessage(TextComponent.toLegacyText(*components))
        }

        private fun spigot(sender: CommandSender) = if (sender is ConsoleCommandSender) CONSOLE_SPIGOT else (sender as Player).spigot()

        private fun map(components: Array<BaseComponent>, key: String): Array<BaseComponent> {
            if (ERROR_EXAMPLES.containsKey(key)) {
                val example = TextComponent.fromLegacyText(
                    "${EXAMPLE_COLORS[r.nextInt(EXAMPLE_COLORS.size)]}${format(
                        get("constants.example"),
                        "$GOLD${ERROR_EXAMPLES[key]!!()}"
                    )}"
                )
                val hover = HoverEvent(HoverEvent.Action.SHOW_TEXT, example)

                components.forEach { c -> c.hoverEvent = hover }
            }

            return components
        }
    }

    private val prefix = TextComponent(me.gamercoder215.battlecards.messages.prefix).apply {
        hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, arrayOf(
            TextComponent("BattleCards v" + plugin.description.version).apply {
                color = LIGHT_PURPLE
            })
        )

        clickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/GamerCoder215/BattleCards")
    }

    // Impl

    private fun sendComponents(sender: CommandSender, message: Array<BaseComponent>) = spigot(sender).sendMessage(*message)

    override fun send(sender: CommandSender, key: String, vararg args: Any) {
        val message = map(TextComponent.fromLegacyText(format(get(key), *args)), key)
        sendComponents(sender, message)
    }

    override fun sendMessage(sender: CommandSender, key: String, vararg args: Any) {
        val message = map(TextComponent.fromLegacyText(format(get(key), *args)), key)
        sendComponents(sender, message.toMutableList().apply { add(0, prefix) }.toTypedArray())
    }

    override fun sendError(sender: CommandSender, key: String, vararg args: Any) {
        val message = map(TextComponent.fromLegacyText("${ChatColor.RED}${format(get(key), *args)}"), key)
        sendComponents(sender, message.toMutableList().apply { add(0, prefix) }.toTypedArray())
    }

    override fun sendSuccess(sender: CommandSender, key: String, vararg args: Any) {
        val message = map(TextComponent.fromLegacyText("${ChatColor.GREEN}${format(get(key), *args)}"), key)
        sendComponents(sender, message.toMutableList().apply { add(0, prefix) }.toTypedArray())
    }

    override fun sendRaw(sender: CommandSender, message: String) =
        sendComponents(sender, TextComponent.fromLegacyText(message))

    override fun sendRaw(sender: CommandSender, vararg messages: String) =
        sendComponents(sender, messages.map { TextComponent.fromLegacyText(it).toList() }.flatten().toTypedArray())

    override fun sendRawMessage(sender: CommandSender, message: String) {
        val message0 = TextComponent.fromLegacyText(message)
        sendComponents(sender, message0.toMutableList().apply { add(0, prefix) }.toTypedArray())
    }
}