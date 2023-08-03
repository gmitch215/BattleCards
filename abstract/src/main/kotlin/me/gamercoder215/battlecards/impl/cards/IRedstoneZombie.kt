package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.Material
import org.bukkit.entity.Zombie
import org.bukkit.inventory.ItemStack

@Type(BattleCardType.REDSTONE_ZOMBIE)
@Attributes(40.0, 2.5, 2.0, 0.18, 80.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 0.65)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 0.425)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 0.7)
class IRedstoneZombie(data: ICard): IBattleCard<Zombie>(data) {

    override fun init() {
        super.init()

        entity.equipment.helmet = ItemStack(Material.REDSTONE_BLOCK)
    }

}