package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Skeleton
import org.bukkit.inventory.ItemStack

@Type(BattleCardType.STONE_ARCHER)
@Attributes(40.0, 3.5, 10.0, 0.2, 5.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 0.35)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 0.5)
@AttributesModifier(CardAttribute.KNOCKBACK_RESISTANCE, CardOperation.ADD, 0.15)
class IStoneArcher(data: ICard) : IBattleCard<Skeleton>(data) {

    override fun init() {
        super.init()

        entity.equipment.apply {
            helmet = ItemStack(Material.STONE)
            itemInHand = ItemStack(Material.BOW).apply {
                itemMeta = itemMeta.apply {
                    addEnchant(Enchantment.ARROW_DAMAGE, 1 + (level / 10), true)
                }
            }
        }
    }

}