package me.gamercoder215.battlecards.util.inventory

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.api.card.Card
import me.gamercoder215.battlecards.impl.CardAbility
import me.gamercoder215.battlecards.util.*
import me.gamercoder215.battlecards.util.CardUtils.createLine
import me.gamercoder215.battlecards.util.CardUtils.dateFormat
import me.gamercoder215.battlecards.util.CardUtils.format
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.get
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Creature
import org.bukkit.inventory.ItemStack
import java.util.stream.Collectors

object CardGenerator {

    @JvmStatic
    fun createItem(card: Card): ItemStack {
        val config = BattleConfig.getConfiguration()

        return ItemStack(Material.PAPER).apply {
            itemMeta = itemMeta.apply {
                displayName = format(get("constants.card"), "${card.getRarity().getColor()}${card.getName()}")

                val cardL = mutableListOf<String>()
                cardL.add(card.getRarity().toString())

                if (config.getBoolean("Cards.Display.Inventory.ShowLevel"))
                    cardL.addAll(listOf(
                        " ",
                        "${format(get("constants.level"), card.getLevel())} | ${format(get("constants.card.deploy"), card.getDeployTime())}",
                        "${createLine(card.getLevel())} | ${format(get("constants.card.next_level"), card.getRemainingExperience().withSuffix())}"
                    ))

                cardL.addAll(listOf(
                    " ",
                    get("constants.card.left_click_view"),
                    get("constants.card.right_click_deploy")
                ))

                lore = CardUtils.color(cardL)
            }

            nbt { nbt -> nbt["card"] = card.toByteArray() }
        }
    }

    @JvmStatic
    fun createBasicCard(entity: Creature): ItemStack {
        if (!BattleConfig.getValidBasicCards().contains(entity.type)) throw IllegalArgumentException("Invalid Entity Type: ${entity.type}")
        val card = BattleCardType.BASIC.createCardData()
        return createItem(card)
    }

    @JvmStatic
    fun generateCardInfo(card: Card): ItemStack {
        val config = BattleConfig.getConfiguration()

        return ItemStack(Material.EMPTY_MAP).apply {
            itemMeta = itemMeta.apply {
                displayName = "${format(get("constants.card"), "${card.getRarity().getColor()}${card.getName()}")} | ${format(get("constants.card.generation"), card.getGeneration().toRoman())}"

                val cardL = mutableListOf<String>()
                cardL.addAll(listOf(
                    card.getRarity().toString(),
                    " ",
                    "${format(get("constants.level"), card.getLevel())} | ${format(get("constants.card.deploy"), card.getDeployTime())}",
                    "${createLine(card.getLevel())} | ${format(get("constants.card.next_level"), card.getRemainingExperience().withSuffix())}"
                ))

                if (config.getBoolean("Card.Display.Info.ShowAbilities")) {
                    val abilityL = mutableListOf<String>()
                    abilityL.add(" ")

                    val abilities = card::class.java.getAnnotationsByType(CardAbility::class.java)
                        .plus(card::class.java.methods.toList().stream()
                            .map { it.getAnnotationsByType(CardAbility::class.java) }
                            .flatMap { it.toList().stream() }
                            .collect(Collectors.toList())
                        )

                    for (ability in abilities)
                        abilityL.addAll(listOf(
                            "${ability.color}${get(ability.name)}",
                            if (ability.desc.equals("<desc>", ignoreCase = true))
                                get("${ability.name}.desc")
                            else
                                get(ability.desc),
                        ))

                    cardL.addAll(abilityL)
                }

                cardL.addAll(listOf(
                    " ",
                    format("${ChatColor.AQUA}${get("constants.card.creation_date")}", "${ChatColor.GOLD}${dateFormat(card.getCreationDate())}"),
                    format("${ChatColor.AQUA}${get("constants.card.last_used_by")}", "${ChatColor.GOLD}${card.getLastUsedPlayer()?.name ?: "N/A"}"),
                    format("${ChatColor.AQUA}${get("constants.card.last_used_on")}", "${ChatColor.GOLD}${dateFormat(card.getLastUsed()) ?: "N/A"}")
                ))

                lore = CardUtils.color(cardL)
            }
        }
    }

    @JvmStatic
    fun generateCardStatistics(card: Card): ItemStack? {
        if (!BattleConfig.getConfiguration().getBoolean("Card.Display.Info.ShowStatistics")) return null

        return ItemStack(Material.EMPTY_MAP).apply {
            itemMeta = itemMeta.apply {
                displayName = "${format(get("constants.card"), "${card.getRarity().getColor()}${card.getName()}")} | ${get("constants.statistics")}"

                val cardL = mutableListOf<String>()
                cardL.addAll(listOf(
                    " ",
                    "${ChatColor.RED}${format(get("constants.card.statistics.max_health"), "${ChatColor.GOLD}${card.getStatistics().getMaxHealth().format()}")}",
                    "${ChatColor.RED}${format(get("constants.card.statistics.attack_damage"), "${ChatColor.GOLD}${card.getStatistics().getAttackDamage().format()}")}",
                    "${ChatColor.GREEN}${format(get("constants.card.statistics.defense"), "${ChatColor.GOLD}${card.getStatistics().getDefense().format()}")}",
                    "${ChatColor.AQUA}${format(get("constants.card.statistics.movement_speed"), "${ChatColor.GOLD}${card.getStatistics().getSpeed().format()}")}",
                    "${ChatColor.DARK_PURPLE}${format(get("constants.card.statistics.knockback_resistance"), "${ChatColor.GOLD}${card.getStatistics().getKnockbackResistance().format()}")}",
                    " ",
                    "${ChatColor.RED}${format(get("constants.card.statistics.player_kills"), "${ChatColor.DARK_RED}${card.getStatistics().getPlayerKills().format()}")}",
                    "${ChatColor.RED}${format(get("constants.card.statistics.card_kills"), "${ChatColor.DARK_RED}${card.getStatistics().getCardKills().format()}")}",
                    "${ChatColor.RED}${format(get("constants.card.statistics.entity_kills"),"${ChatColor.DARK_RED}${card.getStatistics().getEntityKills().format()}")}",
                    "${ChatColor.RED}${format(get("constants.card.statistics.total_kills"), "${ChatColor.DARK_RED}${card.getStatistics().getKills().format()}")}",
                    " ",
                    "${ChatColor.DARK_RED}${format(get("constants.card.statistics.total_damage_dealt"), "${ChatColor.BLUE}${card.getStatistics().getDamageDealt().format()}")}",
                    "${ChatColor.DARK_RED}${format(get("constants.card.statistics.total_damage_received"), "${ChatColor.BLUE}${card.getStatistics().getDamageReceived().format()}")}",
                    " ",
                    "${ChatColor.GREEN}${format(get("constants.card.statistics.card_experience"), "${ChatColor.YELLOW}${card.getStatistics().getCardExperience().format()}")}",
                    "${ChatColor.DARK_GREEN}${format(get("constants.card.statistics.max_card_experience"), "${ChatColor.YELLOW}${card.getStatistics().getMaxCardExperience().format()}")}",
                    "${ChatColor.GREEN}${format(get("constants.card.statistics.card_level"), "${ChatColor.YELLOW}${card.getStatistics().getCardLevel().format()}")}",
                    "${ChatColor.DARK_GREEN}${format(get("constants.card.statistics.max_card_level"), "${ChatColor.YELLOW}${card.getStatistics().getMaxCardLevel().format()}")}",
                ))

                lore = cardL
            }
        }
    }

}