package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.util.BattleMaterial
import me.gamercoder215.battlecards.util.CardAttackType
import me.gamercoder215.battlecards.util.attackType
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Creature
import org.bukkit.inventory.ItemStack

@Type(BattleCardType.BASIC)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 2.15)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 0.9)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 0.45)
@Rideable
class IBasicCard<T : Creature>(data: ICard) : IBattleCard<T>(data) {

    override fun init() {
        super.init()

        val type = when (entity.equipment.itemInHand.type) {
            Material.BOW -> CardAttackType.BOW
            Material.matchMaterial("CROSSBOW") -> CardAttackType.CROSSBOW
            else -> CardAttackType.MELEE
        }
        attackType = type

        entity.equipment.itemInHand = ItemStack(
            when (type) {
                CardAttackType.BOW -> Material.BOW
                CardAttackType.CROSSBOW -> Material.matchMaterial("CROSSBOW")
                else ->
                    when (level) {
                        in 15..45 -> BattleMaterial.WOODEN_SWORD.find()
                        in 46..75 -> Material.STONE_SWORD
                        in 76..125 -> Material.IRON_SWORD
                        in 126..175 -> Material.DIAMOND_SWORD
                        in 176..200 -> Material.matchMaterial("NETHERITE_SWORD") ?: Material.DIAMOND_SWORD
                        else -> Material.AIR
                    }
            }
        ).apply {
            val amount = (level - 10) / 5

            if (level >= 15) {
                when (type) {
                    CardAttackType.MELEE -> addUnsafeEnchantment(Enchantment.DAMAGE_ALL, amount)
                    CardAttackType.BOW -> addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, amount)
                    CardAttackType.CROSSBOW -> addUnsafeEnchantment(Enchantment.values().firstOrNull { it.name == "PIERCING" }, amount)
                }
            }
        }

        entity.equipment.helmet = ItemStack(
            when (level) {
                in 10..25 -> Material.LEATHER_HELMET
                in 26..55 -> Material.CHAINMAIL_HELMET
                in 56..100 -> Material.IRON_HELMET
                in 100..160 -> Material.DIAMOND_HELMET
                in 161..200 -> Material.matchMaterial("NETHERITE_HELMET") ?: Material.DIAMOND_HELMET
                else -> Material.AIR
            }
        ).apply {
            if (level >= 30)
                addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, (level - 30) / 10)
        }

        entity.equipment.chestplate = ItemStack(
            when (level) {
                in 20..40 -> Material.LEATHER_CHESTPLATE
                in 41..70 -> Material.CHAINMAIL_CHESTPLATE
                in 71..120 -> Material.IRON_CHESTPLATE
                in 121..200 -> Material.DIAMOND_CHESTPLATE
                else -> Material.AIR
            }
        )
    }

}