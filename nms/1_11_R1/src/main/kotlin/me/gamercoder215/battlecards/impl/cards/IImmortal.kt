package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.WitherSkeleton
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

@Type(BattleCardType.THE_IMMORTAL)
@Attributes(1400.0, 29.5, 85.5, 0.38, 50.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 5.92, 6150.0)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 3.72)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 2.17)
@CardAbility("card.the_immortal.ability.immortality", ChatColor.YELLOW)
class IImmortal(data: ICard) : IBattleCard<WitherSkeleton>(data) {

    override fun init() {
        super.init()

        val regen = PotionEffect(PotionEffectType.REGENERATION, deployTime, 0, false, false)
        entity.addPotionEffect(regen)
        p.addPotionEffect(regen)

        entity.equipment.apply {
            itemInOffHand = ItemStack(Material.matchMaterial("TOTEM_OF_UNDYING") ?: Material.matchMaterial("TOTEM")).apply {
                amount = 1 + (level / 10).coerceAtMost(2)
            }

            if (level > 10)
                helmet = ItemStack(Material.DIAMOND_HELMET).apply {
                    itemMeta = itemMeta.apply {
                        isUnbreakable = true

                        if (level > 15)
                            addEnchant(Enchantment.THORNS, 3, true)

                        addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, (level / 3).coerceAtMost(10), true)
                    }
                }

            chestplate = ItemStack(Material.DIAMOND_CHESTPLATE).apply {
                itemMeta = itemMeta.apply {
                    isUnbreakable = true

                    if (level > 5)
                        addEnchant(Enchantment.THORNS, 2, true)

                    if (level > 10)
                        addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, (level / 5).coerceAtMost(10), true)
                }
            }

            leggings = ItemStack(Material.DIAMOND_LEGGINGS).apply {
                itemMeta = itemMeta.apply {
                    isUnbreakable = true

                    if (level > 10)
                        addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, (level / 4).coerceAtMost(10), true)
                }
            }

            boots = ItemStack(Material.DIAMOND_BOOTS).apply {
                itemMeta = itemMeta.apply {
                    isUnbreakable = true

                    if (level > 5)
                        addEnchant(Enchantment.PROTECTION_FALL, 3, true)

                    if (level > 10)
                        addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, (level / 4).coerceAtMost(10), true)
                }
            }
        }
    }

    @CardAbility("card.the_immortal.ability.hardening", ChatColor.GREEN)
    @UnlockedAt(5)
    @Defensive(0.05, CardOperation.ADD, 0.01, 0.15)
    private fun hardening(event: EntityDamageByEntityEvent) {
        event.isCancelled = true
        entity.world.playSound(entity.location, Sound.ITEM_SHIELD_BLOCK, 1F, 1F)
    }

    @CardAbility("card.the_immortal.ability.soft_landing", ChatColor.WHITE)
    @UnlockedAt(10)
    @UserDamage
    @Damage
    private fun softLanding(event: EntityDamageEvent) {
        if (event.cause == EntityDamageEvent.DamageCause.FALL || event.cause == EntityDamageEvent.DamageCause.FLY_INTO_WALL)
            event.isCancelled = true
    }

}