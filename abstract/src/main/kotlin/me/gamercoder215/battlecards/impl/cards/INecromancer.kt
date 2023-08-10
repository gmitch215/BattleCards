package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Skeleton
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta

@Type(BattleCardType.NECROMANCER)
@Attributes(900.0, 0.0, 93.5, 0.21, 15.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 8.32)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 4.57)
@AttributesModifier(CardAttribute.KNOCKBACK_RESISTANCE, CardOperation.ADD, 1.58)
class INecromancer(data: ICard) : IBattleCard<Skeleton>(data) {

    override fun init() {
        super.init()

        entity.equipment.helmet = ItemStack(Material.LEATHER_HELMET).apply {
            itemMeta = (itemMeta as LeatherArmorMeta).apply {
                color = Color.GREEN
            }
        }

        entity.equipment.itemInHand = ItemStack(Material.BLAZE_ROD)
    }

    // TODO Add Abilities

}