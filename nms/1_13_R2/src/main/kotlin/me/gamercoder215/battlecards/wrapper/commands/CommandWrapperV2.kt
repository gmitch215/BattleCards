package me.gamercoder215.battlecards.wrapper.commands

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.api.card.Card
import me.gamercoder215.battlecards.util.cardInHand
import me.gamercoder215.battlecards.util.inventory.Items
import me.gamercoder215.battlecards.wrapper.commands.CommandWrapper.Companion.getError
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import revxrsal.commands.annotation.*
import revxrsal.commands.autocomplete.SuggestionProvider
import revxrsal.commands.bukkit.BukkitCommandHandler
import revxrsal.commands.bukkit.annotation.CommandPermission

internal class CommandWrapperV2(private val plugin: Plugin) : CommandWrapper {

    companion object {
        private lateinit var handler: BukkitCommandHandler

        @JvmStatic
        fun hasHandler(): Boolean = ::handler.isInitialized
    }

    init {
        run {
            if (hasHandler()) return@run
            handler = BukkitCommandHandler.create(plugin)

            handler.autoCompleter
                .registerParameterSuggestions(BattleCardType::class.java, SuggestionProvider.of { BattleCardType.entries.filter { it != BattleCardType.BASIC }.map { it.name.lowercase() } })
                .registerParameterSuggestions(EntityType::class.java, SuggestionProvider.of { EntityType.entries.map { it.name.lowercase() } })
                .registerSuggestion("items", SuggestionProvider.of { Items.PUBLIC_ITEMS.keys })

            handler.register(this)
            handler.register(CardCommands(this))
            handler.registerBrigadier()
            handler.locale = BattleConfig.config.locale
        }
    }

    @Command("cardreload", "creload")
    @Description("Reloads the BattleCards Plugin")
    @Usage("/cardreload")
    @CommandPermission("battlecards.admin.reload")
    override fun reloadPlugin(sender: CommandSender) = super.reloadPlugin(sender)

    @Command("bcard", "card", "battlecard")
    @Description("Main BattleCards Card Command")
    @Usage("/bcard [args]")
    @CommandPermission("battlecards.user.card")
    private class CardCommands(private val wrapper: CommandWrapperV2) {

        @Subcommand("info")
        @DefaultFor("bcard", "card", "battlecard")
        fun cardInfo(p: Player) = wrapper.cardInfo(p)

        @Subcommand("create")
        @CommandPermission("battlecards.admin.card.create")
        fun cardCreate(p: Player, type: BattleCardType) = wrapper.createCard(p, type)

        @Subcommand("basic")
        @CommandPermission("battlecards.admin.card.create")
        fun cardCreateBasic(p: Player, entityType: EntityType) = wrapper.createCard(p, BattleCardType.BASIC, entityType)

        @Subcommand("query")
        @CommandPermission("battlecards.user.query")
        fun cardQuery(p: Player, type: BattleCardType) = wrapper.queryCard(p, type)

        // Card Editing

        @Subcommand("edit level set", "edit lvl set")
        @CommandPermission("battlecards.admin.card.edit")
        fun setCardLevel(p: Player, @Range(min = 1.0, max = Card.MAX_LEVEL.toDouble()) level: Int) {
            val card = p.cardInHand ?: return p.sendMessage(getError("error.argument.held.item.card"))

            if (level < 1 || level > card.maxCardLevel)
                return p.sendMessage(getError("error.argument.card.level"))

            wrapper.editCard(p) { it.level = level }
        }

        @Subcommand("edit level add", "edit lvl add")
        @CommandPermission("battlecards.admin.card.edit")
        fun addCardLevel(p: Player, @Range(min = 1.0, max = Card.MAX_LEVEL.toDouble()) level: Int) {
            val card = p.cardInHand ?: return p.sendMessage(getError("error.argument.held.item.card"))
            wrapper.editCard(p) { it.level = (card.level + level).coerceAtMost(card.maxCardLevel) }
        }

        @Subcommand("edit level remove", "edit lvl remove")
        @CommandPermission("battlecards.admin.card.edit")
        fun removeCardLevel(p: Player, @Range(min = 1.0, max = Card.MAX_LEVEL.toDouble()) level: Int) {
            val card = p.cardInHand ?: return p.sendMessage(getError("error.argument.held.item.card"))
            setCardLevel(p, card.level - level)
        }

        @Subcommand("edit experience set", "edit exp set")
        @CommandPermission("battlecards.admin.card.edit")
        fun setCardExperience(p: Player, @Range(min = 0.0) experience: Double) {
            val card = p.cardInHand ?: return p.sendMessage(getError("error.argument.held.item.card"))

            if (experience < 0 || experience > card.maxCardExperience)
                return p.sendMessage(getError("error.argument.card.experience"))

            wrapper.editCard(p) { it.experience = experience }
        }

        @Subcommand("edit experience add", "edit exp add")
        @CommandPermission("battlecards.admin.card.edit")
        fun addCardExperience(p: Player, @Range(min = 0.0) experience: Double) {
            val card = p.cardInHand ?: return p.sendMessage(getError("error.argument.held.item.card"))
            wrapper.editCard(p) { it.experience = (card.experience + experience).coerceAtMost(card.maxCardExperience) }
        }

        @Subcommand("edit experience remove", "edit exp remove")
        @CommandPermission("battlecards.admin.card.edit")
        fun removeCardExperience(p: Player, @Range(min = 0.0) experience: Double) {
            val card = p.cardInHand ?: return p.sendMessage(getError("error.argument.held.item.card"))
            setCardExperience(p, card.experience - experience)
        }

        @Subcommand("item")
        @CommandPermission("battlecards.admin.items")
        @AutoComplete("@items")
        fun giveCardItem(p: Player, id: String) = wrapper.giveItem(p, id)

    }

}