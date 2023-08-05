package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.util.BattleMaterial
import org.bukkit.ChatColor
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.ZombieVillager
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

@Type(BattleCardType.MINER)
@Attributes(140.0, 9.5, 13.75, 0.22, 5.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 5.0)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 1.5)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 3.0, 250.0)
@AttributesModifier(CardAttribute.KNOCKBACK_RESISTANCE, CardOperation.MULTIPLY, 1.04)
class IMiner(data: ICard) : IBattleCard<ZombieVillager>(data) {

    override fun init() {
        super.init()

        entity.equipment.helmet = ItemStack(BattleMaterial.GOLDEN_HELMET.find()).apply {
            itemMeta = itemMeta.apply {
                addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true)
                addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 20, true)

                isUnbreakable = true
            }
        }

        entity.equipment.itemInMainHand = ItemStack(BattleMaterial.GOLDEN_PICKAXE.find()).apply {
            itemMeta = itemMeta.apply {
                isUnbreakable = true
            }
        }
    }

    @CardAbility("card.miner.ability.haste", ChatColor.YELLOW)
    @Passive(1)
    private fun userHaste() {
        p.addPotionEffect(PotionEffect(PotionEffectType.FAST_DIGGING, 3, 1, true))
    }

}