package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Stray
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta

@Type(BattleCardType.BANDIT)
@Attributes(75.0, 6.5, 8.5, 0.3, 4.5)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 7.0)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 1.55)
class IBandit(data: ICard) : IBattleCard<Stray>(data) {

    override fun init() {
        super.init()

        entity.equipment.helmet = ItemStack(Material.LEATHER_HELMET).apply {
            itemMeta = (itemMeta as LeatherArmorMeta).apply {
                isUnbreakable = true
                color = Color.WHITE

                if (level >= 40)
                    addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true)
            }
        }

        entity.equipment.boots = ItemStack(Material.LEATHER_BOOTS).apply {
            itemMeta = (itemMeta as LeatherArmorMeta).apply {
                isUnbreakable = true
                color = Color.WHITE

                if (level >= 20)
                    addEnchant(Enchantment.PROTECTION_FALL, 2, true)
            }
        }

        entity.equipment.itemInMainHand = ItemStack(Material.BOW).apply {
            itemMeta = itemMeta.apply {
                isUnbreakable = true
                if (level >= 15)
                    addEnchant(Enchantment.ARROW_FIRE, 1, true)
            }
        }

        if (level >= 65)
            entity.equipment.chestplate = ItemStack(Material.IRON_CHESTPLATE).apply {
                itemMeta = itemMeta.apply { isUnbreakable = true }
            }
    }

    @CardAbility("card.bandit.ability.bullet", ChatColor.GRAY)
    @EventHandler
    @UnlockedAt(5)
    private fun bullet(event: EntityShootBowEvent) {
        if (event.entity != entity) return

        event.projectile.velocity = event.projectile.velocity.multiply(1.5)
    }

}