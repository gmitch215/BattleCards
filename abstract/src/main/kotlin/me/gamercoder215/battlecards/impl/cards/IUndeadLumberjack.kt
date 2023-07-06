package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Skeleton
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

@Type(BattleCardType.UNDEAD_LUMBERJACK)
@Attributes(230.0, 11.4, 6.5, 0.25, 2.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 9.0)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 1.2)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.MULTIPLY, 1.04)
@AttributesModifier(CardAttribute.KNOCKBACK_RESISTANCE, CardOperation.ADD, 0.03)
class IUndeadLumberjack(data: ICard) : IBattleCard<Skeleton>(data) {

    override fun init() {
        super.init()

        entity.equipment.helmet = ItemStack(Material.IRON_HELMET).apply {
            itemMeta = itemMeta.apply {
                addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true)
                addEnchant(Enchantment.DURABILITY, 32767, true)
                addEnchant(Enchantment.THORNS, 1, true)
            }
        }

        entity.equipment.itemInHand = ItemStack(Material.DIAMOND_AXE).apply {
            itemMeta = itemMeta.apply {
                addEnchant(Enchantment.DAMAGE_ALL, 2, true)
            }
        }

        if (level > 45)
            entity.equipment.chestplate = ItemStack(Material.DIAMOND_CHESTPLATE).apply {
                itemMeta = itemMeta.apply {
                    addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3, true)
                    addEnchant(Enchantment.THORNS, 2, true)
                }
            }
    }

    @CardAbility("card.undead_lumberjack.ability.bleeding", ChatColor.DARK_RED)
    @Offensive(0.2, CardOperation.ADD, 0.02, 0.75)
    private fun bleeding(event: EntityDamageByEntityEvent) {
        val target = event.entity as? LivingEntity ?: return
        var i = 0

        object : BukkitRunnable() {
            override fun run() {
                if (i > level / 5)
                    return cancel()

                target.damage(statistics.attackDamage / 3, entity)
                i++
            }
        }.runTaskTimer(BattleConfig.plugin, 0L, 65L)
    }

}