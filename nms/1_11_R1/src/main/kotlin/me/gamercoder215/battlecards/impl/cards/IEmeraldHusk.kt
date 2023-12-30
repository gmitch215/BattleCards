package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Husk
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta

@Type(BattleCardType.EMERALD_HUSK)
@Attributes(100.0, 5.5, 10.0, 0.2, 100.0, 128.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 0.55)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 2.72)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 3.175)
class IEmeraldHusk(data: ICard) : IBattleCard<Husk>(data) {

    override fun init() {
        super.init()

        entity.equipment.apply {
            helmet = ItemStack(Material.EMERALD_BLOCK)
            chestplate = ItemStack(Material.LEATHER_CHESTPLATE).apply {
                itemMeta = (itemMeta as LeatherArmorMeta).apply {
                    isUnbreakable = true
                    color = Color.GREEN
                }
            }
            leggings = ItemStack(Material.LEATHER_LEGGINGS).apply {
                itemMeta = (itemMeta as LeatherArmorMeta).apply {
                    isUnbreakable = true
                    color = Color.GREEN
                }
            }
            boots = ItemStack(Material.LEATHER_BOOTS).apply {
                itemMeta = (itemMeta as LeatherArmorMeta).apply {
                    isUnbreakable = true
                    color = Color.GREEN
                }
            }
        }
    }

}