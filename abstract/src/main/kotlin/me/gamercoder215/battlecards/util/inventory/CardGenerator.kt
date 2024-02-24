package me.gamercoder215.battlecards.util.inventory

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.api.card.Card
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.messages.color
import me.gamercoder215.battlecards.messages.dateFormat
import me.gamercoder215.battlecards.messages.format
import me.gamercoder215.battlecards.messages.get
import me.gamercoder215.battlecards.util.*
import me.gamercoder215.battlecards.util.CardUtils.createLine
import me.gamercoder215.battlecards.util.CardUtils.power
import org.bukkit.ChatColor.*
import org.bukkit.Material
import org.bukkit.entity.Creature
import org.bukkit.inventory.ItemStack
import org.bukkit.util.ChatPaginator
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

object CardGenerator {

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
                        "$YELLOW${format(get("constants.level"), card.level)} $WHITE| $GOLD${format(get("constants.card.deploy"), card.deployTime)}",
                        if (card.isMaxed) "$AQUA$BOLD${get("constants.maxed")}" else "$GRAY${createLine(card).replace("=", "$GREEN=$GRAY")} $WHITE| $DARK_AQUA${format(get("constants.card.next_level"), card.remainingExperience.withSuffix())}"
                    ))

                cardL.addAll(listOf(
                    " ",
                    "$YELLOW${get("constants.card.left_click_view")}",
                    "$YELLOW${get("constants.card.right_click_deploy")}",
                ))

                lore = cardL
            }

        }.nbt { nbt -> nbt["card"] = card.toByteArray() }
    }

    fun createBasicCard(entity: Creature): ItemStack {
        if (!BattleConfig.getValidBasicCards().contains(entity.type)) throw IllegalArgumentException("Invalid Entity Type: ${entity.type}")
        val card = BattleCardType.BASIC() as ICard
        card.storedEntityType = entity.type
        return toItem(card)
    }

    internal val generationColors = arrayOf(
        RESET, // Generation 0
        AQUA,
        GREEN,
        DARK_BLUE,
        YELLOW,
        DARK_PURPLE,
        GOLD,
        LIGHT_PURPLE
    )

    fun generateCardInfo(card: Card): ItemStack {
        val config = BattleConfig.configuration

        return ItemStack(BattleMaterial.MAP.find()).apply {
            itemMeta = itemMeta.apply {
                displayName = "${format(get("constants.card"), "${card.rarity.color}${card.name}")} $WHITE| ${generationColors[card.generation]}${format(get("constants.card.generation"), card.generation.toRoman())}"

                val cardL = mutableListOf<String>()
                cardL.addAll(listOf(
                    "${card.rarity} ${card.cardClass}",
                    " ",
                    "$YELLOW${format(get("constants.level"), card.level)} $WHITE| $GOLD${format(get("constants.card.deploy"), card.deployTime)}",
                    if (card.isMaxed) "$AQUA$BOLD${get("constants.maxed")}" else "$GRAY${createLine(card).replace("=", "$GREEN=$GRAY")} $WHITE| $DARK_AQUA${format(get("constants.card.next_level"), card.remainingExperience.withSuffix())}"
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
                                val unlockedAt = if (isAnnotationPresent(UnlockedAt::class.java)) getAnnotation(UnlockedAt::class.java).level else 0

                                return@run "$GREEN${(
                                    if (isAnnotationPresent(Defensive::class.java)) getAnnotation(Defensive::class.java).getChance(card.level, unlockedAt)
                                    else if (isAnnotationPresent(Offensive::class.java)) getAnnotation(Offensive::class.java).getChance(card.level, unlockedAt)
                                    else if (isAnnotationPresent(UserDefensive::class.java)) getAnnotation(UserDefensive::class.java).getChance(card.level, unlockedAt)
                                    else if (isAnnotationPresent(UserOffensive::class.java)) getAnnotation(UserOffensive::class.java).getChance(card.level, unlockedAt)
                                    else if (isAnnotationPresent(Damage::class.java)) getAnnotation(Damage::class.java).getChance(card.level, unlockedAt)
                                    else if (isAnnotationPresent(UserDamage::class.java)) getAnnotation(UserDamage::class.java).getChance(card.level, unlockedAt)
                                    else 1.0
                                ).times(100.0).format()}%$GRAY"
                            }

                            placeholders["%bcu"] = it.run {
                                val unlockedAt = if (isAnnotationPresent(UnlockedAt::class.java)) getAnnotation(UnlockedAt::class.java).level else 0

                                return@run "$GREEN${(
                                        if (isAnnotationPresent(UserDefensive::class.java)) getAnnotation(UserDefensive::class.java).getChance(card.level, unlockedAt)
                                        else if (isAnnotationPresent(UserOffensive::class.java)) getAnnotation(UserOffensive::class.java).getChance(card.level, unlockedAt)
                                        else if (isAnnotationPresent(UserDamage::class.java)) getAnnotation(UserDamage::class.java).getChance(card.level, unlockedAt)
                                        else 1.0
                                ).times(100.0).format()}%$GRAY"
                            }

                            placeholders["%bcint"] = it.run {
                                val unlockedAt = if (isAnnotationPresent(UnlockedAt::class.java)) getAnnotation(UnlockedAt::class.java).level else 0
                                return@run if (isAnnotationPresent(Passive::class.java)) "$GOLD${getAnnotation(Passive::class.java).getChance(card.level, unlockedAt).div(20.0).format()}s$GRAY" else ""
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
                            ).map { s -> "$GRAY$s" }

                            abilityL.addAll(listOf("${ability.color}${get(ability.name)}") + desc + listOf(""))
                        }

                        cardL.addAll(abilityL)
                    }
                }

                cardL.addAll(listOf(
                    " ",
                    format("$AQUA${get("constants.card.creation_date")}", "$GOLD${dateFormat(card.creationDate)}"),
                    format("$AQUA${get("constants.card.last_used_by")}", "$GOLD${card.lastUsedPlayer?.name ?: "N/A"}"),
                    format("$AQUA${get("constants.card.last_used_on")}", "$GOLD${dateFormat(card.lastUsed, true) ?: "N/A"}")
                ))

                lore = cardL
            }
        }
    }

    fun generateCardStatistics(card: Card): ItemStack? {
        if (!BattleConfig.configuration.getBoolean("Cards.Display.Info.ShowStatistics")) return null

        val statistics = card.statistics
        
        return ItemStack(BattleMaterial.MAP.find()).apply {
            itemMeta = itemMeta.apply {
                displayName = "${format(get("constants.card"), "${card.rarity.color}${card.name}")} $WHITE| $YELLOW${get("constants.statistics")}"

                val cardL = mutableListOf<String>()
                cardL.addAll(listOf(
                    " ",
                    "$RED${format(get("constants.card.statistics.max_health"), "$GOLD${statistics.maxHealth.withSuffix()}")}",
                    "$RED${format(get("constants.card.statistics.attack_damage"), "$GOLD${statistics.attackDamage.withSuffix()}")}",
                    "$GREEN${format(get("constants.card.statistics.defense"), "$GOLD${statistics.defense.withSuffix()}")}",
                    "$AQUA${format(get("constants.card.statistics.movement_speed"), "$GOLD${statistics.speed.withSuffix()}")}",
                    "$DARK_PURPLE${format(get("constants.card.statistics.knockback_resistance"), "$GOLD${statistics.knockbackResistance.withSuffix()}")}",
                    " ",
                    "$RED${format(get("constants.card.statistics.player_kills"), "$DARK_RED${statistics.playerKills.withSuffix()}")}",
                    "$RED${format(get("constants.card.statistics.card_kills"), "$DARK_RED${statistics.cardKills.withSuffix()}")}",
                    "$RED${format(get("constants.card.statistics.entity_kills"),"$DARK_RED${statistics.entityKills.withSuffix()}")}",
                    "$RED${format(get("constants.card.statistics.total_kills"), "$DARK_RED${statistics.kills.withSuffix()}")}",
                    "$RED${format(get("constants.card.statistics.total_deaths"), "$DARK_RED${statistics.deaths.withSuffix()}")}",
                    " ",
                    "$DARK_RED${format(get("constants.card.statistics.total_damage_dealt"), "$BLUE${statistics.damageDealt.withSuffix()}")}",
                    "$DARK_RED${format(get("constants.card.statistics.total_damage_received"), "$BLUE${statistics.damageReceived.withSuffix()}")}",
                    " ",
                    "$GREEN${format(get("constants.card.statistics.card_experience"), "$YELLOW${statistics.cardExperience.withSuffix()}")}",
                    "$DARK_GREEN${format(get("constants.card.statistics.max_card_experience"), "$YELLOW${statistics.maxCardExperience.withSuffix()}")}",
                    "$GREEN${format(get("constants.card.statistics.card_level"), "$YELLOW${statistics.cardLevel.format()}")}",
                    "$DARK_GREEN${format(get("constants.card.statistics.max_card_level"), "$YELLOW${statistics.maxCardLevel.format()}")}",
                    " ",
                    "$GOLD${format(get("constants.card_power"), "$YELLOW${card.power.format()}")}"
                ))

                lore = cardL
            }
        }
    }

}