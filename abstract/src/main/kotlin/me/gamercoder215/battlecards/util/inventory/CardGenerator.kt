package me.gamercoder215.battlecards.util.inventory

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.api.card.Card
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.util.*
import me.gamercoder215.battlecards.util.CardUtils.color
import me.gamercoder215.battlecards.util.CardUtils.createLine
import me.gamercoder215.battlecards.util.CardUtils.dateFormat
import me.gamercoder215.battlecards.util.CardUtils.format
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.get
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Creature
import org.bukkit.inventory.ItemStack
import org.bukkit.util.ChatPaginator

object CardGenerator {

    @JvmStatic
    fun toItem(card: Card): ItemStack {
        val config = BattleConfig.configuration

        return ItemStack(Material.PAPER).apply {
            itemMeta = itemMeta.apply {
                displayName = format(get("constants.card"), "${card.rarity.color}${card.name}")

                val cardL = mutableListOf<String>()
                cardL.add(card.rarity.toString())

                if (config.getBoolean("Cards.Display.Inventory.ShowLevel"))
                    cardL.addAll(listOf(
                        " ",
                        "${ChatColor.YELLOW}${format(get("constants.level"), card.level)} ${ChatColor.WHITE}| ${ChatColor.GOLD}${format(get("constants.card.deploy"), card.deployTime)}",
                        if (card.isMaxed) "${ChatColor.AQUA}${ChatColor.BOLD}${get("constants.maxed")}" else "${ChatColor.GRAY}${createLine(card).replace("=", "${ChatColor.GREEN}=${ChatColor.GRAY}")} ${ChatColor.WHITE}| ${ChatColor.DARK_AQUA}${format(get("constants.card.next_level"), card.remainingExperience.withSuffix())}"
                    ))

                cardL.addAll(listOf(
                    " ",
                    "${ChatColor.YELLOW}${get("constants.card.left_click_view")}",
                    "${ChatColor.YELLOW}${get("constants.card.right_click_deploy")}",
                ))

                lore = cardL
            }

        }.nbt { nbt -> nbt["card"] = card.toByteArray() }
    }

    @JvmStatic
    fun createBasicCard(entity: Creature): ItemStack {
        if (!BattleConfig.getValidBasicCards().contains(entity.type)) throw IllegalArgumentException("Invalid Entity Type: ${entity.type}")
        val card = BattleCardType.BASIC.createCardData() as ICard
        card.storedEntityType = entity.type
        return toItem(card)
    }

    private val generationColors = listOf(
        ChatColor.GREEN,
        ChatColor.AQUA,
        ChatColor.DARK_BLUE,
        ChatColor.YELLOW,
        ChatColor.DARK_PURPLE,
        ChatColor.GOLD,
        ChatColor.LIGHT_PURPLE
    )

    @JvmStatic
    fun generateCardInfo(card: Card): ItemStack {
        val config = BattleConfig.configuration

        return ItemStack(BattleMaterial.MAP.find()).apply {
            itemMeta = itemMeta.apply {
                displayName = "${format(get("constants.card"), "${card.rarity.color}${card.name}")} ${ChatColor.WHITE}| ${generationColors[card.generation]}${format(get("constants.card.generation"), card.generation.toRoman())}"

                val cardL = mutableListOf<String>()
                cardL.addAll(listOf(
                    card.rarity.toString(),
                    " ",
                    "${ChatColor.YELLOW}${format(get("constants.level"), card.level)} ${ChatColor.WHITE}| ${ChatColor.GOLD}${format(get("constants.card.deploy"), card.deployTime)}",
                    if (card.isMaxed) "${ChatColor.AQUA}${ChatColor.BOLD}${get("constants.maxed")}" else "${ChatColor.GRAY}${createLine(card).replace("=", "${ChatColor.GREEN}=${ChatColor.GRAY}")} ${ChatColor.WHITE}| ${ChatColor.DARK_AQUA}${format(get("constants.card.next_level"), card.remainingExperience.withSuffix())}"
                ))

                if (config.getBoolean("Cards.Display.Info.ShowAbilities")) {
                    val abilityL = mutableListOf<String>()
                    abilityL.add(" ")

                    val abilities = card.entityCardClass.getAnnotationsByType(CardAbility::class.java).associateWith { mapOf<String, String>() }.toMutableMap()
                    abilities.putAll(card.entityCardClass.declaredMethods.toList()
                        .map { it.isAccessible = true; it }
                        .filter {
                            if (it.isAnnotationPresent(UnlockedAt::class.java))
                                card.level >= it.getAnnotation(UnlockedAt::class.java).level
                            else true
                        }.associate {
                            val placeholders = mutableMapOf<String, String>()

                            placeholders["%bch"] = it.run {
                                return@run "${ChatColor.GREEN}${(
                                    if (isAnnotationPresent(Defensive::class.java)) getAnnotation(Defensive::class.java).getChance(card.level)
                                    else if (isAnnotationPresent(Offensive::class.java)) getAnnotation(Offensive::class.java).getChance(card.level)
                                    else if (isAnnotationPresent(UserDefensive::class.java)) getAnnotation(UserDefensive::class.java).getChance(card.level)
                                    else if (isAnnotationPresent(UserOffensive::class.java)) getAnnotation(UserOffensive::class.java).getChance(card.level)
                                    else if (isAnnotationPresent(Damage::class.java)) getAnnotation(Damage::class.java).getChance(card.level)
                                    else 1.0
                                ).times(100.0).format()}%${ChatColor.GRAY}"
                            }

                            placeholders["%bcu"] = it.run {
                                return@run "${ChatColor.GREEN}${(
                                        if (isAnnotationPresent(UserDefensive::class.java)) getAnnotation(UserDefensive::class.java).getChance(card.level)
                                        else if (isAnnotationPresent(UserOffensive::class.java)) getAnnotation(UserOffensive::class.java).getChance(card.level)
                                        else 1.0
                                ).times(100.0).format()}%${ChatColor.GRAY}"
                            }

                            placeholders["%bcint"] = it.run {
                                return@run if (isAnnotationPresent(Passive::class.java)) "${ChatColor.GOLD}${getAnnotation(Passive::class.java).interval.div(20.0).format()}s${ChatColor.GRAY}" else ""
                            }

                            it.getAnnotation(CardAbility::class.java) to placeholders
                        }.filter { it.key != null }.toMap())

                    if (abilities.isNotEmpty()) {
                        for ((ability, placeholders) in abilities) {
                            val desc = ChatPaginator.wordWrap(
                                if (ability.desc.equals("<desc>", ignoreCase = true))
                                    color(get("${ability.name}.desc").replace(placeholders))
                                else
                                    color(get(ability.desc).replace(placeholders)),
                                35
                            ).map { s -> "${ChatColor.GRAY}$s" }

                            abilityL.addAll(listOf("${ability.color}${get(ability.name)}") + desc + listOf(""))
                        }

                        cardL.addAll(abilityL)
                    }
                }

                cardL.addAll(listOf(
                    " ",
                    format("${ChatColor.AQUA}${get("constants.card.creation_date")}", "${ChatColor.GOLD}${dateFormat(card.creationDate)}"),
                    format("${ChatColor.AQUA}${get("constants.card.last_used_by")}", "${ChatColor.GOLD}${card.lastUsedPlayer?.name ?: "N/A"}"),
                    format("${ChatColor.AQUA}${get("constants.card.last_used_on")}", "${ChatColor.GOLD}${dateFormat(card.lastUsed, true) ?: "N/A"}")
                ))

                lore = cardL
            }
        }
    }

    @JvmStatic
    fun generateCardStatistics(card: Card): ItemStack? {
        if (!BattleConfig.configuration.getBoolean("Cards.Display.Info.ShowStatistics")) return null

        val statistics = card.statistics
        
        return ItemStack(BattleMaterial.MAP.find()).apply {
            itemMeta = itemMeta.apply {
                displayName = "${format(get("constants.card"), "${card.rarity.color}${card.name}")} ${ChatColor.WHITE}| ${ChatColor.YELLOW}${get("constants.statistics")}"

                val cardL = mutableListOf<String>()
                cardL.addAll(listOf(
                    " ",
                    "${ChatColor.RED}${format(get("constants.card.statistics.max_health"), "${ChatColor.GOLD}${statistics.maxHealth.withSuffix()}")}",
                    "${ChatColor.RED}${format(get("constants.card.statistics.attack_damage"), "${ChatColor.GOLD}${statistics.attackDamage.withSuffix()}")}",
                    "${ChatColor.GREEN}${format(get("constants.card.statistics.defense"), "${ChatColor.GOLD}${statistics.defense.withSuffix()}")}",
                    "${ChatColor.AQUA}${format(get("constants.card.statistics.movement_speed"), "${ChatColor.GOLD}${statistics.speed.withSuffix()}")}",
                    "${ChatColor.DARK_PURPLE}${format(get("constants.card.statistics.knockback_resistance"), "${ChatColor.GOLD}${statistics.knockbackResistance.withSuffix()}")}",
                    " ",
                    "${ChatColor.RED}${format(get("constants.card.statistics.player_kills"), "${ChatColor.DARK_RED}${statistics.playerKills.withSuffix()}")}",
                    "${ChatColor.RED}${format(get("constants.card.statistics.card_kills"), "${ChatColor.DARK_RED}${statistics.cardKills.withSuffix()}")}",
                    "${ChatColor.RED}${format(get("constants.card.statistics.entity_kills"),"${ChatColor.DARK_RED}${statistics.entityKills.withSuffix()}")}",
                    "${ChatColor.RED}${format(get("constants.card.statistics.total_kills"), "${ChatColor.DARK_RED}${statistics.kills.withSuffix()}")}",
                    "${ChatColor.RED}${format(get("constants.card.statistics.total_deaths"), "${ChatColor.DARK_RED}${statistics.deaths.withSuffix()}")}",
                    " ",
                    "${ChatColor.DARK_RED}${format(get("constants.card.statistics.total_damage_dealt"), "${ChatColor.BLUE}${statistics.damageDealt.withSuffix()}")}",
                    "${ChatColor.DARK_RED}${format(get("constants.card.statistics.total_damage_received"), "${ChatColor.BLUE}${statistics.damageReceived.withSuffix()}")}",
                    " ",
                    "${ChatColor.GREEN}${format(get("constants.card.statistics.card_experience"), "${ChatColor.YELLOW}${statistics.cardExperience.withSuffix()}")}",
                    "${ChatColor.DARK_GREEN}${format(get("constants.card.statistics.max_card_experience"), "${ChatColor.YELLOW}${statistics.maxCardExperience.withSuffix()}")}",
                    "${ChatColor.GREEN}${format(get("constants.card.statistics.card_level"), "${ChatColor.YELLOW}${statistics.cardLevel.format()}")}",
                    "${ChatColor.DARK_GREEN}${format(get("constants.card.statistics.max_card_level"), "${ChatColor.YELLOW}${statistics.maxCardLevel.format()}")}",
                ))

                lore = cardL
            }
        }
    }

}