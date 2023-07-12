package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Phantom
import org.bukkit.entity.Stray
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

@Type(BattleCardType.PHANTOM_RIDER)
@Attributes(100.0, 6.5, 20.0, 0.3, 30.0, 256.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 2.0)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 0.65)
class IPhantomRider(data: ICard) : IBattleCard<Stray>(data) {

    private lateinit var phantom: Phantom

    override fun init() {
        super.init()

        entity.equipment!!.apply {
            setItemInMainHand(ItemStack(Material.BOW).apply {
                itemMeta = itemMeta!!.apply {
                    isUnbreakable = true
                    addEnchant(Enchantment.ARROW_DAMAGE, 1 + (level / 4), true)
                    addEnchant(Enchantment.ARROW_FIRE, 1, true)

                    if (level >= 10)
                        addEnchant(Enchantment.ARROW_KNOCKBACK, (level / 10).coerceAtMost(6), true)

                }
            })

            helmet = ItemStack(Material.IRON_HELMET).apply {
                itemMeta = itemMeta!!.apply {
                    isUnbreakable = true
                    addEnchant(Enchantment.PROTECTION_PROJECTILE, 1 + (level / 10), true)

                    if (level >= 5)
                        addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, (level / 5).coerceAtMost(15), true)
                }
            }

            if (level >= 15)
                chestplate = ItemStack(Material.IRON_CHESTPLATE).apply {
                    itemMeta = itemMeta!!.apply {
                        isUnbreakable = true
                        addEnchant(Enchantment.PROTECTION_PROJECTILE, (level - 2) / 5, true)
                    }
                }
        }

        phantom = world.spawn(location, Phantom::class.java).apply {
            target = entity.target
            getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = statistics.maxHealth / 2
            getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)!!.baseValue = statistics.speed + 0.04
        }
    }

    override fun uninit() {
        phantom.remove()
        super.uninit()
    }

    @CardAbility("card.phantom_rider.ability.phantom_bow")
    @Offensive
    private fun phantomBow(event: EntityDamageByEntityEvent) {
        val target = event.entity as? LivingEntity ?: return
        target.addPotionEffect(PotionEffect(PotionEffectType.LEVITATION, 20 * r.nextInt(5, 11), r.nextInt(0, 2)))
    }

}