package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.util.BattleMaterial
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Husk
import org.bukkit.inventory.ItemStack

@Type(BattleCardType.PRINCE_HUSK)
@Attributes(150.0, 6.3, 25.5, 0.25, 0.32)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 7.08)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 3.7)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 6.2)

@CardAbility("card.prince_husk.ability.fire_immune", ChatColor.GOLD)
class IPrinceHusk(data: ICard) : IBattleCard<Husk>(data) {

    override fun init() {
        super.init()

        entity.equipment.helmet = ItemStack(BattleMaterial.GOLDEN_HELMET.find()).apply {
            itemMeta = itemMeta.apply {
                isUnbreakable = true
            }
        }
        entity.equipment.chestplate = ItemStack(BattleMaterial.GOLDEN_CHESTPLATE.find()).apply {
            itemMeta = itemMeta.apply {
                isUnbreakable = true
            }
        }
    }

    @CardAbility("card.prince_husk.ability.royal_guard", ChatColor.DARK_BLUE)
    @Passive(600, CardOperation.SUBTRACT, 5, min = 100)
    private fun royalGuard() {
        minion(Husk::class.java) {
            equipment.itemInMainHand = ItemStack(if (r.nextDouble() < 0.25) Material.IRON_AXE else Material.IRON_SWORD)

            equipment.helmet = ItemStack(Material.IRON_BLOCK)
            equipment.chestplate = ItemStack(Material.IRON_CHESTPLATE).apply {
                itemMeta = itemMeta.apply {
                    isUnbreakable = true

                    if (level >= 20)
                        addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, level / 20, true)
                }
            }
        }
    }

}