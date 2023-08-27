package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.util.BattleMaterial
import me.gamercoder215.battlecards.util.spawnedCards
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Drowned
import org.bukkit.entity.Guardian
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

@Type(BattleCardType.AQUATIC_ASSASSIN)
@Attributes(200.0, 9.2, 15.4, 0.31, 50.0, 128.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 6.65)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 8.75)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 7.13)
class IAquaticAssassin(data: ICard) : IBattleCard<Drowned>(data) {

    private lateinit var guardian: Guardian

    override fun init() {
        super.init()

        entity.equipment.apply {
            helmet = ItemStack(Material.DIAMOND_HELMET).apply {
                itemMeta = itemMeta.apply {
                    isUnbreakable = true

                    addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2 + (level / 4).coerceAtMost(8), true)
                    addEnchant(Enchantment.PROTECTION_PROJECTILE, 20, true)
                }
            }

            itemInMainHand = ItemStack(BattleMaterial.GOLDEN_SWORD.find()).apply {
                itemMeta = itemMeta.apply {
                    isUnbreakable = true

                    addEnchant(Enchantment.DAMAGE_ALL, 3 + (level / 5).coerceAtMost(12), true)
                }
            }

            if (level >= 15)
                chestplate = ItemStack(Material.DIAMOND_CHESTPLATE).apply {
                    itemMeta = itemMeta.apply {
                        isUnbreakable = true

                        addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4 + (level.minus(15) / 3).coerceAtMost(8), true)
                        addEnchant(Enchantment.PROTECTION_PROJECTILE, 25, true)
                    }
                }
        }

        guardian = minion(Guardian::class.java) {
            val health = statistics.maxHealth / 3.0
            getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = health
            this.health = health

            getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)!!.baseValue = (statistics.attackDamage / 2.0).coerceAtMost(75.0)
            getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)!!.baseValue = statistics.speed * 1.15

            addPassenger(entity)
        }
    }

    @CardAbility("card.aquatic_assassin.ability.super_conduit", ChatColor.AQUA)
    @Passive(1)
    private fun conduit() {
        (minions + listOf(p, entity) + p.spawnedCards.map { it.entity }).forEach {
            it.addPotionEffect(PotionEffect(PotionEffectType.CONDUIT_POWER, 4, 1, true))
        }
    }

    @CardAbility("card.aquatic_assassin.ability.paralysis", ChatColor.YELLOW)
    @EventHandler
    @UnlockedAt(20)
    private fun guardianDamage(event: EntityDamageByEntityEvent) {
        if (event.damager != guardian) return
        val entity = event.entity as? LivingEntity ?: return

        entity.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 20 * 4, 1))
        entity.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, 20 * 6, 2))
    }

    @CardAbility("card.aquatic_assassin.ability.airbending")
    @Offensive(0.7, CardOperation.ADD, 0.02)
    @UserOffensive(0.25, CardOperation.ADD, 0.025, 0.5)
    @UnlockedAt(40)
    private fun airbending(event: EntityDamageByEntityEvent) {
        val entity = event.entity as? LivingEntity ?: return
        entity.remainingAir -= 20 * 3
    }

}