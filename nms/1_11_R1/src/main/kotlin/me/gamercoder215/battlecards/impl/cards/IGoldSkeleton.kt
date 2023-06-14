package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.Material
import org.bukkit.entity.WitherSkeleton
import org.bukkit.inventory.ItemStack

@Type(BattleCardType.GOLD_SKELETON)

@Attributes(170.0, 6.5, 20.25, 0.4, 0.32)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 22.0)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 2.7)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 3.2)
@AttributesModifier(CardAttribute.KNOCKBACK_RESISTANCE, CardOperation.MULTIPLY, 1.02)
class IGoldSkeleton(data: ICard) : IBattleCard<WitherSkeleton>(data) {

    override fun init() {
        super.init()

        entity.equipment.helmet = ItemStack(Material.GOLD_HELMET).apply {
            itemMeta = itemMeta.apply {
                isUnbreakable = true
            }
        }

        entity.equipment.itemInMainHand = ItemStack(Material.GOLD_AXE).apply {
            itemMeta = itemMeta.apply {
                isUnbreakable = true
            }
        }
    }

}