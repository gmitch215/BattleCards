package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.util.isCard
import me.gamercoder215.battlecards.util.isMinion
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ArmorMeta
import org.bukkit.inventory.meta.trim.ArmorTrim
import org.bukkit.inventory.meta.trim.TrimMaterial
import org.bukkit.inventory.meta.trim.TrimPattern
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable

@Type(BattleCardType.SEALORD)
@Attributes(1500.0, 35.0, 145.0, 0.3, 5.0, 128.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 10.5)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 5.45)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 3.5)
class ISeaLord(data: ICard) : IBattleCard<Drowned>(data) {

    private companion object {
        @JvmStatic
        val dolphinKey = NamespacedKey(BattleConfig.plugin, "sealord_dolphin")
    }
    
    private lateinit var dolphin: Dolphin

    override fun init() {
        super.init()

        dolphin = minion(Dolphin::class.java) {
            isCustomNameVisible = false
            addPotionEffect(PotionEffect(PotionEffectType.WATER_BREATHING, -1, 0, false, false))

            getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = this@ISeaLord.statistics.maxHealth / 20.0
            
            persistentDataContainer[dolphinKey, PersistentDataType.BOOLEAN] = true
            addPassenger(entity)
        }

        entity.equipment!!.helmet = ItemStack(Material.DIAMOND_HELMET).apply {
            itemMeta = (itemMeta as ArmorMeta).apply {
                isUnbreakable = true
                trim = ArmorTrim(TrimMaterial.LAPIS, TrimPattern.TIDE)
            }
        }

        if (level >= 10)
            entity.equipment!!.chestplate = ItemStack(Material.DIAMOND_CHESTPLATE).apply {
                itemMeta = (itemMeta as ArmorMeta).apply {
                    isUnbreakable = true
                    trim = ArmorTrim(TrimMaterial.LAPIS, TrimPattern.TIDE)
                }
            }

        entity.equipment!!.setItemInMainHand(ItemStack(Material.TRIDENT).apply {
            itemMeta = itemMeta!!.apply {
                isUnbreakable = true

                addEnchant(Enchantment.IMPALING, 2 + (level / 2), true)
                addEnchant(Enchantment.CHANNELING, 1, true)
            }
        })
    }

    @CardAbility("card.sealord.ability.sea_military", ChatColor.DARK_AQUA)
    @Passive(1500, CardOperation.SUBTRACT, 20, Long.MAX_VALUE, 400)
    private fun seaMilitary() {
        val amount = r.nextInt(2, 6)

        val health = (dolphin.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue / 10.0).coerceAtLeast(20.0)

        for (i in 0 until amount) {
            val militaryDolphin = minion(Dolphin::class.java) {
                isCustomNameVisible = false
                addPotionEffect(PotionEffect(PotionEffectType.WATER_BREATHING, -1, 0, false, false))

                getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = (health / 20.0).coerceAtLeast(20.0)
                persistentDataContainer[dolphinKey, PersistentDataType.BOOLEAN] = true
            }

            militaryDolphin.addPassenger(minion(if (r.nextBoolean()) Drowned::class.java else Zombie::class.java) {
                conversionTime = Int.MAX_VALUE
                getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = health

                setBaby()
                equipment!!.helmet = ItemStack(Material.IRON_HELMET).apply {
                    itemMeta = (itemMeta as ArmorMeta).apply {
                        isUnbreakable = true
                        trim = ArmorTrim(TrimMaterial.DIAMOND, TrimPattern.TIDE)
                    }
                }
                
                equipment!!.setItemInMainHand(ItemStack(if (r.nextDouble() < 0.4) Material.TRIDENT else Material.IRON_SWORD).apply {
                    itemMeta = itemMeta!!.apply {
                        isUnbreakable = true

                        if (type == Material.TRIDENT) addEnchant(Enchantment.IMPALING, (1 + (level / 2)).coerceAtMost(7), true)
                        else addEnchant(Enchantment.DAMAGE_ALL, (1 + (level / 2)).coerceAtMost(7), true)
                    }
                })
            })
        }
    }

    @CardAbility("card.sealord.ability.channeling")
    @Offensive(0.8, CardOperation.ADD, 0.02)
    private fun channeling(event: EntityDamageByEntityEvent) {
        event.entity.world.strikeLightning(event.entity.location)
    }

    @CardAbility("card.sealord.ability.wet", ChatColor.BLUE)
    @Damage
    @UnlockedAt(5)
    private fun wet(event: EntityDamageEvent) {
        if (event.cause == DamageCause.FIRE || event.cause == DamageCause.LAVA || event.cause == DamageCause.FIRE_TICK)
            event.isCancelled = true
    }

    @CardAbility("card.sealord.ability.thundering", ChatColor.AQUA)
    @Passive(1200)
    @UnlockedAt(15)
    private fun thundering() {
        if (world.getGameRuleValue(GameRule.DO_WEATHER_CYCLE.name) != "false" && !world.isThundering) {
            world.isThundering = true
            world.thunderDuration = 1200
        }

        object : BukkitRunnable() {
            override fun run() {
                world.playSound(entity.location, Sound.ENTITY_ENDER_DRAGON_GROWL, 3F, 0.65F)

                (entity.getNearbyEntities(10.0, 10.0, 10.0) + listOf(target))
                    .filterIsInstance<LivingEntity>()
                    .filter { it.isCard && !it.isMinion(this@ISeaLord) }
                    .filter { if (BattleConfig.config.cardAttackPlayers) it is Player else true }
                    .distinctBy { it.uniqueId }
                    .forEach {
                        it.damage(5.0)
                        it.world.strikeLightning(it.location)
                    }
            }
        }.runTaskLater(BattleConfig.plugin, 60)
    }
    
    @EventHandler
    private fun cancelDryout(event: EntityDamageEvent) {
        if (event.cause != DamageCause.DRYOUT) return
        val entity = event.entity as? Dolphin ?: return

        if (entity.persistentDataContainer[dolphinKey, PersistentDataType.BOOLEAN] == true)
            event.isCancelled = true
    }


}