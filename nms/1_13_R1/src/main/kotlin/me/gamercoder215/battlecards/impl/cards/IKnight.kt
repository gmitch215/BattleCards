package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.block.Banner
import org.bukkit.block.banner.Pattern
import org.bukkit.block.banner.PatternType
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Horse
import org.bukkit.entity.Vindicator
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BlockStateMeta

@Type(BattleCardType.KNIGHT)
@Attributes(90.0, 6.75, 20.0, 0.27, 10.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 3.5)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 0.3)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 0.45)
class IKnight(data: ICard) : IBattleCard<Vindicator>(data) {

    private lateinit var horse: Horse

    override fun init() {
        super.init()

        entity.equipment.itemInMainHand = ItemStack(Material.DIAMOND_SWORD).apply {
            itemMeta = itemMeta.apply {
                isUnbreakable = true

                if (level >= 10)
                    addEnchant(Enchantment.DAMAGE_ALL, ((level - 10) / 3).coerceAtMost(6), true)
            }
        }

        entity.equipment.itemInOffHand = ItemStack(Material.SHIELD).apply {
            itemMeta = (itemMeta as BlockStateMeta).apply {
                blockState = (blockState as Banner).apply {
                    baseColor = DyeColor.LIME
                    patterns = listOf(
                        Pattern(DyeColor.GREEN, PatternType.GRADIENT),
                        Pattern(DyeColor.BLACK, PatternType.BORDER),
                        Pattern(DyeColor.BLACK, PatternType.TRIANGLE_BOTTOM),
                        Pattern(DyeColor.BLACK, PatternType.CIRCLE_MIDDLE),
                        Pattern(DyeColor.BLACK, PatternType.TRIANGLES_TOP),
                        Pattern(DyeColor.YELLOW, PatternType.MOJANG),
                    )

                    update()
                }
            }
        }

        horse = world.spawn(location, Horse::class.java)
        horse.style = Horse.Style.BLACK_DOTS
        horse.color = Horse.Color.entries.random()
        horse.isTamed = true
        horse.jumpStrength = r.nextDouble(0.3, 0.8)
        horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)!!.baseValue = statistics.speed + 0.06

        horse.inventory.saddle = ItemStack(Material.SADDLE)
        horse.inventory.armor = ItemStack(Material.IRON_HORSE_ARMOR)
        horse.addPassenger(entity)
        minions.add(horse)
    }

    override fun uninit() {
        horse.remove()
        super.uninit()
    }

}