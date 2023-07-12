package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Husk
import org.bukkit.inventory.ItemStack

@Type(BattleCardType.WARRIOR_HUSK)
@Attributes(125.0, 14.5, 70.0, 0.24, 10.0, 192.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 0.475)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 0.7)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 0.85)
class IWarriorHusk(data: ICard) : IBattleCard<Husk>(data) {

    override fun init() {
        super.init()

        entity.equipment.apply {
            helmet = ItemStack(Material.CHAINMAIL_HELMET).apply {
                itemMeta = itemMeta.apply { isUnbreakable = true }
            }
            chestplate = ItemStack(Material.CHAINMAIL_CHESTPLATE).apply {
                itemMeta = itemMeta.apply {
                    isUnbreakable = true
                    addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1 + (level / 5), true)
                }
            }
            leggings = ItemStack(Material.CHAINMAIL_LEGGINGS).apply {
                itemMeta = itemMeta.apply { isUnbreakable = true }
            }
            boots = ItemStack(Material.CHAINMAIL_HELMET).apply {
                itemMeta = itemMeta.apply { isUnbreakable = true }
            }

            itemInMainHand = ItemStack(Material.IRON_SWORD).apply {
                itemMeta = itemMeta.apply {
                    isUnbreakable = true
                    addEnchant(Enchantment.DAMAGE_ALL, 1 + (level / 10), true)
                }
            }

            itemInOffHand = ItemStack(Material.SHIELD).apply {
                itemMeta = itemMeta.apply { isUnbreakable = true }
            }
        }
    }

}