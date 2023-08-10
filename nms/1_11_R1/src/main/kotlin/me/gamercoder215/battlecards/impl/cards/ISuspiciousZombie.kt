package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.util.plus
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EvokerFangs
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Zombie
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector

@Type(BattleCardType.SUSPICIOUS_ZOMBIE)
@Attributes(300.0, 2.4, 20.0, 0.28, 20.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 1.2)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 2.0)
@AttributesModifier(CardAttribute.KNOCKBACK_RESISTANCE, CardOperation.ADD, 0.5)
class ISuspiciousZombie(data: ICard) : IBattleCard<Zombie>(data) {

    override fun init() {
        super.init()

        entity.isBaby = false

        entity.equipment.helmet = ItemStack(Material.LEATHER_HELMET).apply {
            itemMeta = (itemMeta as LeatherArmorMeta).apply {
                addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, (1 + (level / 4)).coerceAtMost(10), true)
                addEnchant(Enchantment.DURABILITY, 32767, true)

                color = Color.RED
            }
        }

        entity.equipment.itemInMainHand = ItemStack(Material.STICK)
    }

    @CardAbility("card.suspicious_zombie.ability.fangs", ChatColor.DARK_GRAY)
    @Passive(400, CardOperation.SUBTRACT, 5, min = 220)
    private fun fangs() {
        val target = entity.target ?: return
        val locs = listOf<Location>(
            target.location,
            target.location + Vector(0, 0, 1),
            target.location + Vector(1, 0, 0),
            target.location + Vector(-1, 0, 0),
            target.location + Vector(0, 0, -1),
            target.location + Vector(1, 0, 1),
            target.location + Vector(1, 0, -1),
            target.location + Vector(-1, 0, 1),
            target.location + Vector(-1, 0, -1)
        )

        locs.forEach { loc -> loc.world.spawn(loc, EvokerFangs::class.java) { it.owner = entity } }
    }

    @CardAbility("card.suspicious_zombie.ability.effect", ChatColor.DARK_GREEN)
    @Offensive
    private fun effect(event: EntityDamageByEntityEvent) {
        val target = event.entity as? LivingEntity ?: return
        val effects = listOfNotNull(
            PotionEffectType.WEAKNESS,
            PotionEffectType.SLOW,
            PotionEffectType.UNLUCK,
            if (level >= 5) PotionEffectType.POISON else null,
            if (level >= 10) PotionEffectType.WITHER else null,
            if (level >= 10) PotionEffectType.BLINDNESS else null,
            if (level >= 15) PotionEffectType.CONFUSION else null,
            if (level >= 15) PotionEffectType.HUNGER else null,
            if (level >= 15) PotionEffectType.LEVITATION else null,
        )

        effects.random().apply {
            target.addPotionEffect(PotionEffect(this, 20 * r.nextInt(4, 11), r.nextInt(0, 2 + (level / 15).coerceAtMost(3)), true))
        }
    }

    @CardAbility("card.suspicious_zombie.ability.lightning")
    @Defensive(0.5, CardOperation.ADD, 0.05)
    @UserDefensive(0.25, CardOperation.ADD, 0.025, 0.75)
    @UnlockedAt(10)
    private fun lightning(event: EntityDamageByEntityEvent) {
        val target = event.damager as? LivingEntity ?: return
        target.world.strikeLightning(target.location)
        target.damage(6.0.plus((level - 11) * 8.0).coerceAtMost(25.0), entity)
    }

}