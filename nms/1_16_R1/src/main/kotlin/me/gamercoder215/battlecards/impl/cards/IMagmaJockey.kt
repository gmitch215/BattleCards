package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.util.CardAttackType
import me.gamercoder215.battlecards.util.attackType
import me.gamercoder215.battlecards.util.card
import me.gamercoder215.battlecards.util.isCard
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.w
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Arrow
import org.bukkit.entity.MagmaCube
import org.bukkit.entity.Piglin
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.entity.SlimeSplitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

@Type(BattleCardType.MAGMA_JOCKEY)
@Attributes(175.0, 6.0, 5.0, 0.22, 65.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 5.3)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 2.6)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 2.475)
class IMagmaJockey(data: ICard) : IBattleCard<Piglin>(data) {

    private lateinit var magma: MagmaCube

    override fun init() {
        super.init()

        attackType = CardAttackType.CROSSBOW

        entity.equipment!!.apply {
            helmet = ItemStack(Material.GOLDEN_HELMET).apply {
                itemMeta = itemMeta!!.apply {
                    isUnbreakable = true

                    addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5, true)
                }
            }

            chestplate = ItemStack(Material.GOLDEN_CHESTPLATE).apply {
                itemMeta = itemMeta!!.apply {
                    isUnbreakable = true

                    addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6 + (level / 9).coerceAtMost(9), true)
                }
            }

            setItemInMainHand(ItemStack(Material.CROSSBOW).apply {
                itemMeta = itemMeta!!.apply {
                    isUnbreakable = true

                    addEnchant(Enchantment.PIERCING, 3, true)

                    if (level >= 15)
                        addEnchant(Enchantment.MULTISHOT, 1, true)
                }
            })
        }

        magma = entity.world.spawn(entity.location, MagmaCube::class.java).apply {
            size = 3 + (level / 15).coerceAtMost(3)

            val hp = statistics.maxHealth * 0.7

            getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = hp
            health = hp

            getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)!!.baseValue = statistics.attackDamage / 3
            getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK)!!.baseValue = 2.0 + (level * 0.03)

            addPassenger(entity)
            minions.add(this)

            w.addFollowGoal(this, this@IMagmaJockey)
        }
    }

    @Passive(1)
    private fun magmaAI() {
        if (!this::magma.isInitialized) return

        if (magma.target == p || (magma.target?.isCard == true && magma.target?.card?.p == p))
            magma.target == null

        if (entity.target != null && magma.target == null)
            magma.target = entity.target
    }

    @CardAbility("card.magma_jockey.ability.nether_magic", ChatColor.DARK_RED)
    @EventHandler
    private fun netherMagic(event: ProjectileLaunchEvent) {
        if (event.entity.shooter != entity) return

        val target = entity.target ?: return
        val proj = event.entity as? Arrow ?: return

        object : BukkitRunnable() {
            override fun run() {
                if (proj.isDead || !proj.isValid || proj.isOnGround || proj.isInBlock) return cancel()

                val loc = target.location.add(0.0, 0.6, 0.0)

                if (loc.distanceSquared(proj.location) > 9.0) return
                if (loc.distanceSquared(proj.location) < 0.0625) return cancel()

                val direction = loc.toVector().subtract(proj.location.toVector()).normalize()
                proj.velocity = direction.multiply(1.25 + (level * 0.05).coerceAtMost(1.75))
            }
        }.runTaskTimer(BattleConfig.plugin, 0L, 1L)
    }

    @EventHandler
    private fun arrowDamage(event: ProjectileHitEvent) {
        if (event.hitEntity == null) return

        val proj = event.entity as? Arrow ?: return
        println(proj.shooter)

        if (proj.shooter != entity) return

        println("called")

        proj.knockbackStrength = (level / 10).coerceAtMost(5)
        proj.damage = statistics.attackDamage
    }

    @EventHandler
    private fun onSplit(event: SlimeSplitEvent) {
        if (event.entity.uniqueId == magma.uniqueId)
            event.isCancelled = true
    }

}