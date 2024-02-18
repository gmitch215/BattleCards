package me.gamercoder215.battlecards.messages

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.util.inventory.Generator
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.r
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.event.ClickCallback
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.ChatColor.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.regex.Pattern


internal class AdventureMessageHandler(plugin: Plugin) : MessageHandler {

    init {
        plugin.logger.info("Loaded Adventure MessageHandler")
    }

    companion object {
        private val LEGACY_SERIALIZER = LegacyComponentSerializer
            .legacySection()
            .toBuilder()
            .extractUrls(Style.style()
                .decorate(TextDecoration.UNDERLINED)
                .build()
            )
            .useUnusualXRepeatedCharacterHexFormat()
            .build()

        private val LIFETIME = ClickCallback.Options.builder()
            .uses(ClickCallback.UNLIMITED_USES)
            .build()

        private val TEXT_REPLACERS = {
            setOf(
                // Online Players
                Bukkit.getOnlinePlayers().map { p ->
                    val name = p.name
                    val displayName = PlainTextComponentSerializer.plainText().serialize(p.displayName())

                    TextReplacementConfig.builder()
                        .match(Pattern.compile("$name|$displayName", Pattern.LITERAL))
                        .replacement(
                            Component.text(name)
                                .hoverEvent(HoverEvent.showEntity(Key.key("minecraft:player"), p.uniqueId))
                        )
                        .build()
                },
                // Card Names
                BattleCardType.entries.map { card ->
                    TextReplacementConfig.builder()
                        .match(Pattern.compile(card.name, Pattern.LITERAL))
                        .replacement(
                            Component.text(card.name)
                                .hoverEvent(HoverEvent.showText(
                                    Component.text("${card.rarity.color}$BOLD${card.name}")
                                ))
                                .clickEvent(ClickEvent.callback { p ->
                                    if (p is Player)
                                        p.openInventory(Generator.generateCatalogue(card()))
                                })
                        )
                        .build()
                }
            ).flatten()
        }

        private fun fromLegacy(legacy: String): Component {
            var base: Component = LEGACY_SERIALIZER.deserialize(legacy)

            for (replacer in TEXT_REPLACERS())
                base = base.replaceText(replacer)

            return base
        }

        private fun map(component: Component, key: String): Component {
            var component0 = component

            if (ERROR_EXAMPLES.containsKey(key)) {
                val text = fromLegacy(
                    "${EXAMPLE_COLORS[r.nextInt(EXAMPLE_COLORS.size)]}${format(
                        get("constants.example"),
                        "$GOLD${ERROR_EXAMPLES[key]!!()}"
                    )}"
                )
                component0 = component0.hoverEvent(HoverEvent.showText(text))
            }

            return component0
        }
    }

    private val prefix = fromLegacy(me.gamercoder215.battlecards.messages.prefix)
            .hoverEvent(
                HoverEvent.showText(
                    Component.text("BattleCards v" + plugin.description.version).color(TextColor.color(0xFD00DF))
                )
            )
            .clickEvent(ClickEvent.openUrl("https://github.com/GamerCoder215/BattleCards"))

    // Impl

    private fun sendComponents(sender: CommandSender, vararg components: Component) {
        sender.sendMessage(Component.empty().children(listOf(*components)))
    }

    override fun send(sender: CommandSender, key: String, vararg args: Any) {
        val message = map(fromLegacy(format(get(key), args)), key)
        sender.sendMessage(message)
    }

    override fun sendMessage(sender: CommandSender, key: String, vararg args: Any) {
        val message = map(fromLegacy(format(get(key), args)), key)
        sendComponents(sender, prefix, message)
    }

    override fun sendError(sender: CommandSender, key: String, vararg args: Any) {
        val message = map(fromLegacy("$RED${format(get(key), args)}"), key)
        sendComponents(sender, prefix, message)
    }

    override fun sendSuccess(sender: CommandSender, key: String, vararg args: Any) {
        val message = map(fromLegacy("$GREEN${format(get(key), args)}"), key)
        sendComponents(sender, prefix, message)
    }

    override fun sendRaw(sender: CommandSender, message: String) = sender.sendMessage(fromLegacy(message))
    override fun sendRaw(sender: CommandSender, vararg messages: String) = sendComponents(sender, *messages.map { fromLegacy(it) }.toTypedArray())
    override fun sendRawMessage(sender: CommandSender, message: String) = sendComponents(sender, prefix, fromLegacy(message))

}