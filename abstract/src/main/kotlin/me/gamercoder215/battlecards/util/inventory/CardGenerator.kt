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
    fun toItem(card: Card): ItemStack {
        val config = BattleConfig.getConfiguration()

        return ItemStack(Material.PAPER).apply {
            itemMeta = itemMeta.apply {
                displayName = format(get("constants.card"), "${card.rarity.color}${card.name}")

                val cardL = mutableListOf<String>()
                cardL.add(card.rarity.toString())

                if (config.getBoolean("Cards.Display.Inventory.ShowLevel"))
                    cardL.addAll(listOf(
                        " ",
                        "${format(get("constants.level"), card.level)} | ${format(get("constants.card.deploy"), card.deployTime)}",
                        "${createLine(card.level)} | ${format(get("constants.card.next_level"), card.remainingExperience.withSuffix())}"
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
        return toItem(card)
    }

    @JvmStatic
    fun generateCardInfo(card: Card): ItemStack {
        val config = BattleConfig.getConfiguration()

        return ItemStack(Material.EMPTY_MAP).apply {
            itemMeta = itemMeta.apply {
                displayName = "${format(get("constants.card"), "${card.rarity.color}${card.name}")} | ${format(get("constants.card.generation"), card.generation.toRoman())}"

                val cardL = mutableListOf<String>()
                cardL.addAll(listOf(
                    card.rarity.toString(),
                    " ",
                    "${format(get("constants.level"), card.level)} | ${format(get("constants.card.deploy"), card.deployTime)}",
                    "${createLine(card.level)} | ${format(get("constants.card.next_level"), card.remainingExperience.withSuffix())}"
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
                    format("${ChatColor.AQUA}${get("constants.card.creation_date")}", "${ChatColor.GOLD}${dateFormat(card.creationDate)}"),
                    format("${ChatColor.AQUA}${get("constants.card.last_used_by")}", "${ChatColor.GOLD}${card.lastUsedPlayer?.name ?: "N/A"}"),
                    format("${ChatColor.AQUA}${get("constants.card.last_used_on")}", "${ChatColor.GOLD}${dateFormat(card.lastUsed) ?: "N/A"}")
                ))

                lore = CardUtils.color(cardL)
            }
        }
    }

    @JvmStatic
    fun generateCardStatistics(card: Card): ItemStack? {
        if (!BattleConfig.getConfiguration().getBoolean("Card.Display.Info.ShowStatistics")) return null

        val statistics = card.statistics
        
        return ItemStack(Material.EMPTY_MAP).apply {
            itemMeta = itemMeta.apply {
                displayName = "${format(get("constants.card"), "${card.rarity.color}${card.name}")} | ${get("constants.statistics")}"

                val cardL = mutableListOf<String>()
                cardL.addAll(listOf(
                    " ",
                    "${ChatColor.RED}${format(get("constants.card.statistics.max_health"), "${ChatColor.GOLD}${statistics.maxHealth.format()}")}",
                    "${ChatColor.RED}${format(get("constants.card.statistics.attack_damage"), "${ChatColor.GOLD}${statistics.attackDamage.format()}")}",
                    "${ChatColor.GREEN}${format(get("constants.card.statistics.defense"), "${ChatColor.GOLD}${statistics.defense.format()}")}",
                    "${ChatColor.AQUA}${format(get("constants.card.statistics.movement_speed"), "${ChatColor.GOLD}${statistics.speed.format()}")}",
                    "${ChatColor.DARK_PURPLE}${format(get("constants.card.statistics.knockback_resistance"), "${ChatColor.GOLD}${statistics.knockbackResistance.format()}")}",
                    " ",
                    "${ChatColor.RED}${format(get("constants.card.statistics.player_kills"), "${ChatColor.DARK_RED}${statistics.playerKills.format()}")}",
                    "${ChatColor.RED}${format(get("constants.card.statistics.card_kills"), "${ChatColor.DARK_RED}${statistics.cardKills.format()}")}",
                    "${ChatColor.RED}${format(get("constants.card.statistics.entity_kills"),"${ChatColor.DARK_RED}${statistics.entityKills.format()}")}",
                    "${ChatColor.RED}${format(get("constants.card.statistics.total_kills"), "${ChatColor.DARK_RED}${statistics.kills.format()}")}",
                    "${ChatColor.RED}${format(get("constants.card.statistics.total_deaths"), "${ChatColor.DARK_RED}${statistics.deaths.format()}")}",
                    " ",
                    "${ChatColor.DARK_RED}${format(get("constants.card.statistics.total_damage_dealt"), "${ChatColor.BLUE}${statistics.damageDealt.format()}")}",
                    "${ChatColor.DARK_RED}${format(get("constants.card.statistics.total_damage_received"), "${ChatColor.BLUE}${statistics.damageReceived.format()}")}",
                    " ",
                    "${ChatColor.GREEN}${format(get("constants.card.statistics.card_experience"), "${ChatColor.YELLOW}${statistics.cardExperience.format()}")}",
                    "${ChatColor.DARK_GREEN}${format(get("constants.card.statistics.max_card_experience"), "${ChatColor.YELLOW}${statistics.maxCardExperience.format()}")}",
                    "${ChatColor.GREEN}${format(get("constants.card.statistics.card_level"), "${ChatColor.YELLOW}${statistics.cardLevel.format()}")}",
                    "${ChatColor.DARK_GREEN}${format(get("constants.card.statistics.max_card_level"), "${ChatColor.YELLOW}${statistics.maxCardLevel.format()}")}",
                ))

                lore = cardL
            }
        }
    }

}