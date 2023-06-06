package me.gamercoder215.battlecards.util.inventory

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCard
import me.gamercoder215.battlecards.util.CardUtils
import me.gamercoder215.battlecards.util.nbt
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object Generator {

    @JvmStatic
    fun createLine(level: Int): String {
        val builder = StringBuilder()

        for (i in 1..20) {
            if (i * 5 <= level) builder.append("=")
            else builder.append("-")
        }

        return builder.toString()
    }

    @JvmStatic
    fun generateCard(card: BattleCard<*>): ItemStack {
        val config = BattleConfig.getConfiguration()

        return ItemStack(Material.PAPER).apply {
            itemMeta = itemMeta.apply {
                displayName = "${card.getRarity().getColor()}${card.getName()} Card"

                val cardL = mutableListOf<String>()
                cardL.addAll(listOf(card.getRarity().toString()))

                if (config.getBoolean("Card.Display.ShowLevel"))
                    cardL.addAll(listOf(
                        " ",
                        "Level ${card.getLevel()} | ${card.getDeployTime()}s Deploy",
                        "${createLine(card.getLevel())} | ${(CardUtils.toRoman(card.getRemainingExperience().toLong()))} to Next Level"
                    ))

                cardL.addAll(listOf(
                    " ",
                    "${ChatColor.YELLOW}Left Click to View",
                    "${ChatColor.YELLOW}Right Click to Deploy"
                ))

                lore = CardUtils.color(cardL)
            }

            nbt { nbt ->
                nbt.setID("battlecard")
                nbt["card"] = card.getCardID()
            }
        }
    }

}