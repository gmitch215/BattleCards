package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.util.CardAttackType
import me.gamercoder215.battlecards.util.attackType
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Pillager
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

@Type(BattleCardType.MERCENARY)
@Attributes(100.0, 6.45, 20.0, 0.26, 15.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 4.0)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 1.35)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 2.45)
class IMercenary(data: ICard) : IBattleCard<Pillager>(data) {

    override fun init() {
        super.init()

        attackType = CardAttackType.MELEE

        entity.equipment!!.setItemInMainHand(ItemStack(Material.DIAMOND_SWORD).apply {
            itemMeta = itemMeta!!.apply {
                isUnbreakable = true

                addEnchant(Enchantment.SWEEPING_EDGE, (1 + (level / 5)).coerceAtMost(7), true)
            }
        })
    }

    @CardAbility("card.mercenary.ability.rage")
    @Passive(1200, CardOperation.SUBTRACT, 20, Long.MAX_VALUE, 200)
    private fun rage() {
        entity.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 20, 2))
        entity.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 20 * 15, 1))
    }

}