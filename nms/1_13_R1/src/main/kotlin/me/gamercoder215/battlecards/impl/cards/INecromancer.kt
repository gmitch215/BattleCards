package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.util.attackable
import me.gamercoder215.battlecards.util.isCard
import me.gamercoder215.battlecards.util.isMinion
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.ShulkerBullet
import org.bukkit.entity.Skeleton
import org.bukkit.entity.WitherSkeleton
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta

@Type(BattleCardType.NECROMANCER)
@Attributes(900.0, 0.0, 93.5, 0.21, 15.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 8.32)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 4.57)
@AttributesModifier(CardAttribute.KNOCKBACK_RESISTANCE, CardOperation.ADD, 1.58)
@BlockAttachment(Material.GREEN_BANNER, 0.0, -2.0, 0.0, 180.0F, local = false)
class INecromancer(data: ICard) : IBattleCard<Skeleton>(data) {

    override fun init() {
        super.init()

        entity.equipment.helmet = ItemStack(Material.LEATHER_HELMET).apply {
            itemMeta = (itemMeta as LeatherArmorMeta).apply {
                color = Color.GREEN
            }
        }

        entity.equipment.itemInMainHand = ItemStack(Material.BLAZE_ROD)
    }

    @CardAbility("card.necromancer.ability.bullet")
    @Passive(100, CardOperation.SUBTRACT, 2, 70)
    private fun bullet() {
        val target = (entity.getNearbyEntities(20.0, 20.0, 20.0) + listOf(entity.target))
            .filterIsInstance<LivingEntity>()
            .filter { (it.isCard || (it is Player && it.attackable)) && !it.isMinion(this) }
            .minByOrNull { it.location.distanceSquared(entity.location) } ?: return

        entity.world.spawn(entity.eyeLocation, ShulkerBullet::class.java).apply {
            this.target = target
            shooter = entity
        }
    }

    @CardAbility("card.necromancer.ability.rise_of_the_undead")
    @Passive(180, CardOperation.SUBTRACT, 4, 140)
    private fun riseUndead() {
        val minionCap = (15 + level).coerceAtMost(30)
        if (minions.size >= minionCap) return

        val count = r.nextInt(2, 5).coerceAtMost( minionCap - minions.size)
        for (i in 0 until count)
            minion(Skeleton::class.java) {
                entity.equipment.helmet = ItemStack(Material.IRON_BLOCK)

                if (r.nextBoolean())
                    entity.equipment.itemInMainHand = ItemStack(Material.IRON_SWORD).apply {
                        itemMeta = itemMeta.apply {
                            isUnbreakable = true

                            addEnchant(Enchantment.DAMAGE_ALL, 1 + (level / 8), true)
                        }
                    }
                else
                    entity.equipment.itemInMainHand = ItemStack(Material.BOW).apply {
                        itemMeta = itemMeta.apply {
                            isUnbreakable = true

                            addEnchant(Enchantment.ARROW_DAMAGE, 1 + (level / 8), true)
                        }
                    }
            }
    }

    @CardAbility("card.necromancer.ability.lightning")
    @Offensive(0.6, CardOperation.ADD, 0.035)
    @UnlockedAt(10)
    private fun lightning(event: EntityDamageByEntityEvent) {
        val entity = event.entity as? LivingEntity ?: return
        entity.world.strikeLightning(entity.location)
        event.damage += 9.0 + (0.25 * level).coerceAtMost(11.0)
    }

    @CardAbility("card.necromancer.ability.undead_monster")
    @Passive(300, CardOperation.SUBTRACT, 5, 200)
    @UnlockedAt(25)
    private fun undeadMonster() {
        val minionCap = (15 + level).coerceAtMost(30)
        if (minions.size >= minionCap) return

        minion(WitherSkeleton::class.java) {
            entity.equipment.helmet = ItemStack(Material.DIAMOND_BLOCK)

            getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = 100.0 + ((level - 25) * 1.5).coerceAtMost(25.0)
            getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)!!.baseValue = 9.5 + ((level - 25) * 0.1).coerceAtMost(1.5)
            getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)!!.baseValue = 150.0
        }
    }

}