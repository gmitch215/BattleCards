package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Camel
import org.bukkit.entity.Husk
import org.bukkit.entity.LargeFireball
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

@Type(BattleCardType.SAND_TRAVELER)
@Attributes(155.0, 13.7, 31.7, 0.29, 25.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 3.81)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 3.15)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 2.545)

@CardAbility("card.sand_traveler.ability.blazing_hot", ChatColor.GOLD)
class ISandTraveler(data: ICard) : IBattleCard<Husk>(data) {

    private lateinit var camel: Camel

    override fun init() {
        super.init()

        val fireResistance = PotionEffect(PotionEffectType.FIRE_RESISTANCE, data.deployTime, 0, true, false)
        entity.addPotionEffect(fireResistance)
        p.addPotionEffect(fireResistance)

        camel = entity.world.spawn(entity.location, Camel::class.java).apply {
            addPassenger(entity)
            minions.add(this)

            isTamed = true
            inventory.saddle = ItemStack(Material.SADDLE)

            addPotionEffect(fireResistance)

            val health = statistics.maxHealth * 0.75
            getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = health
            this.health = health

            getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)!!.baseValue = statistics.speed
        }

        entity.equipment!!.apply {
            helmet = ItemStack(Material.GOLDEN_HELMET).apply {
                itemMeta = itemMeta!!.apply {
                    isUnbreakable = true

                    addEnchant(Enchantment.PROTECTION_PROJECTILE, 1 + (level / 5).coerceAtMost(6), true)
                }
            }

            if (level > 15)
                chestplate = ItemStack(Material.GOLDEN_CHESTPLATE).apply {
                    itemMeta = itemMeta!!.apply {
                        isUnbreakable = true

                        addEnchant(Enchantment.PROTECTION_PROJECTILE, 1 + (level.minus(15) / 5).coerceAtMost(8), true)
                        addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1 + (level.minus(15) / 7).coerceAtMost(6), true)
                    }
                }
        }
    }

    @UnlockedAt(3)
    @CardAbility("card.sand_traveler.ability.firecracker", ChatColor.RED)
    @Passive(7 * 20)
    private fun firecracker() {
        entity.world.spawn(entity.eyeLocation, LargeFireball::class.java).apply {
            direction = entity.eyeLocation.direction
            yield = 0.75F
            setIsIncendiary(true)
            shooter = entity
        }
        entity.world.playSound(entity.eyeLocation, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 3.0F, 0.5F)
    }

}