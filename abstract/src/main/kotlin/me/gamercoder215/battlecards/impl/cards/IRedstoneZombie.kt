package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.Material
import org.bukkit.entity.Zombie
import org.bukkit.inventory.ItemStack

@Type(BattleCardType.WITHER_KING)
@Attributes(150.0, 2.5, 2.0, 0.18, 80.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 8.0)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 1.5)
class IRedstoneZombie(data: ICard): IBattleCard<Zombie>(data) {

    override fun init() {
        super.init()

        entity.equipment.helmet = ItemStack(Material.REDSTONE_BLOCK)
    }

}