package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.LargeFireball
import org.bukkit.entity.TNTPrimed
import org.bukkit.entity.Zombie
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.atan2

@Type(BattleCardType.BOMBERMAN)
@Attributes(90.0, 3.5, 30.0, 0.22, 45.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 3.3)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 3.95)
@AttributesModifier(CardAttribute.KNOCKBACK_RESISTANCE, CardOperation.ADD, 5.1)
class IBomberman(data: ICard) : IBattleCard<Zombie>(data) {

    override fun init() {
        super.init()

        entity.isBaby = false

        entity.equipment!!.helmet = ItemStack(Material.TNT).apply {
            itemMeta = itemMeta!!.apply {
                addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 1 + (level / 5).coerceAtMost(9), true)
            }
        }

        if (level >= 25) {
            entity.equipment!!.chestplate = ItemStack(Material.LEATHER_CHESTPLATE).apply {
                itemMeta = (itemMeta as LeatherArmorMeta).apply {
                    isUnbreakable = true
                    setColor(Color.BLACK)
                    addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 10, true)
                }
            }

            entity.equipment!!.leggings = ItemStack(Material.LEATHER_LEGGINGS).apply {
                itemMeta = (itemMeta as LeatherArmorMeta).apply {
                    isUnbreakable = true
                    setColor(Color.BLACK)

                    if (level >= 50)
                        addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 10, true)
                }
            }

            entity.equipment!!.boots = ItemStack(Material.LEATHER_BOOTS).apply {
                itemMeta = (itemMeta as LeatherArmorMeta).apply {
                    isUnbreakable = true
                    setColor(Color.BLACK)

                    if (level >= 50)
                        addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 10, true)
                }
            }
        }
    }

    @CardAbility("card.bomberman.ability.exploding", ChatColor.RED)
    @Offensive(0.45, CardOperation.ADD, 0.02, 0.75)
    private fun exploding(event: EntityDamageByEntityEvent) {
        if (event.cause == DamageCause.ENTITY_EXPLOSION || event.cause == DamageCause.BLOCK_EXPLOSION) return

        val power = (2F + (level / 15F)).coerceAtMost(6F)
        event.entity.world.createExplosion(event.entity.location, power, false, false, entity)
    }

    @CardAbility("card.bomberman.ability.primed", ChatColor.YELLOW)
    @Defensive(0.4, CardOperation.ADD, 0.02, 0.8)
    @UnlockedAt(5)
    private fun primed(event: EntityDamageByEntityEvent) {
        event.damager.world.spawn(event.damager.location, TNTPrimed::class.java).apply {
            yield = 3F
            fuseTicks = 80
            setIsIncendiary(true)
        }
    }

    @CardAbility("card.bomberman.ability.tntlings", ChatColor.RED)
    @Passive(600, CardOperation.SUBTRACT, 10, min = 200)
    @UnlockedAt(15)
    private fun tntLings() {
        if (entity.target == null) return

        val amount = (1 + r.nextInt(1 + (level / 15)).coerceAtMost(6))
        for (i in 0..amount)
            minion(Zombie::class.java) {
                isBaby = true
                val health = 20.0 + (level / 5.0)
                getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = health
                this.health = health

                entity.equipment!!.helmet = ItemStack(Material.TNT).apply {
                    itemMeta = itemMeta!!.apply {
                        addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true)
                        addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 5, true)
                    }
                }

                object : BukkitRunnable() {
                    override fun run() {
                        if (isDead) return cancel()
                        remove()
                        world.createExplosion(location, 2F, false, false, entity)
                    }
                }.runTaskLater(BattleConfig.plugin, 120L)
            }
    }

    @CardAbility("card.bomberman.ability.meteor")
    @Passive(1200, CardOperation.SUBTRACT, 20, min = 300)
    @UnlockedAt(35)
    private fun meteor() {
        val target = entity.target ?: return
        entity.world.spawn(target.location.add(0.0, 3.5, 0.0), LargeFireball::class.java).apply {
            setIsIncendiary(true)
            shooter = entity
            yield = 5F + ((level - 35) / 7F).coerceAtMost(1.5F)

            val x = location.x - target.location.x

            val yaw = (atan2(location.z - target.location.z, x) * 180 / Math.PI).toFloat()
            val pitch = (atan2(location.y - target.location.y, x) * 180 / Math.PI).toFloat()

            direction = Location(null, 0.0, 0.0, 0.0, yaw, pitch).direction
        }
        entity.world.playSound(entity.location, Sound.ENTITY_GHAST_SHOOT, 2F, 1F)
    }

}