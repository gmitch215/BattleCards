package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.util.CardAttackType
import me.gamercoder215.battlecards.util.attackType
import org.bukkit.Material
import org.bukkit.entity.Skeleton
import org.bukkit.inventory.ItemStack

@Type(BattleCardType.SKELETON_SOLDIER)
@Attributes(80.0, 3.5, 5.0, 0.2, 5.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 2.5)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 0.55)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 0.4)
class ISkeletonSoldier(data: ICard) : IBattleCard<Skeleton>(data) {

    override fun init() {
        super.init()

        attackType = CardAttackType.MELEE

        entity.equipment.itemInHand = ItemStack(Material.IRON_SWORD)
        entity.equipment.helmet = ItemStack(Material.IRON_HELMET)
        entity.equipment.chestplate = ItemStack(Material.IRON_CHESTPLATE)
        entity.equipment.leggings = ItemStack(Material.IRON_LEGGINGS)
        entity.equipment.boots = ItemStack(Material.IRON_BOOTS)
    }

}