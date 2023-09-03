package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.util.*
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.block.data.Levelled
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.PiglinBrute
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Type(BattleCardType.PIGLIN_TITAN)
@Attributes(1250.0, 38.7, 105.5, 0.36, 100.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 5.82, 6150.0)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 4.13)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 2.79)
@Rideable
class IPiglinTitan(data: ICard) : IBattleCard<PiglinBrute>(data) {

    override fun init() {
        super.init()

        attackType = CardAttackType.MELEE

        entity.addPotionEffect(PotionEffect(PotionEffectType.FIRE_RESISTANCE, Int.MAX_VALUE, 0, false, false))

        entity.equipment!!.apply {
            chestplate = ItemStack(Material.GOLDEN_CHESTPLATE).apply {
                itemMeta = itemMeta!!.apply {
                    isUnbreakable = true

                    addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5 + (level / 3).coerceAtMost(10), true)
                    addEnchant(Enchantment.PROTECTION_PROJECTILE, 20, true)
                }
            }

            leggings = ItemStack(Material.GOLDEN_LEGGINGS).apply {
                itemMeta = itemMeta!!.apply {
                    isUnbreakable = true

                    addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5 + (level / 3).coerceAtMost(5), true)
                    addEnchant(Enchantment.PROTECTION_PROJECTILE, 20, true)
                }
            }

            boots = ItemStack(Material.GOLDEN_BOOTS).apply {
                itemMeta = itemMeta!!.apply {
                    isUnbreakable = true

                    addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5 + (level / 3).coerceAtMost(5), true)
                    addEnchant(Enchantment.PROTECTION_PROJECTILE, 15, true)
                    addEnchant(Enchantment.PROTECTION_FALL, 10, true)
                }
            }

            setItemInMainHand(ItemStack(Material.NETHERITE_AXE).apply {
                itemMeta = itemMeta!!.apply {
                    isUnbreakable = true

                    addEnchant(Enchantment.DAMAGE_ALL, 5 + (level / 3).coerceAtMost(15), true)
                    addEnchant(Enchantment.DAMAGE_UNDEAD, 15 + (level / 2).coerceAtMost(15), true)
                    addEnchant(Enchantment.KNOCKBACK, 1 + (level / 5).coerceAtMost(4), true)

                    if (level >= 5)
                        addEnchant(Enchantment.DAMAGE_ARTHROPODS, 10, true)

                    if (level >= 15)
                        addEnchant(Enchantment.FIRE_ASPECT, 1 + (level.minus(15) / 4), true)
                }
            })

            if (level >= 10)
                helmet = ItemStack(Material.NETHERITE_HELMET).apply {
                    itemMeta = itemMeta!!.apply {
                        isUnbreakable = true

                        addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5 + (level.minus(10) / 2).coerceAtMost(5), true)
                        addEnchant(Enchantment.PROTECTION_PROJECTILE, 15, true)
                    }
                }
        }
    }

    private var lastMoveLocation: Location? = null

    @CardAbility("card.piglin_titan.ability.lava_walker", ChatColor.GOLD)
    @UnlockedAt(5)
    @Passive(1)
    private fun lavaWalker() {
        if (lastMoveLocation == entity.location || !entity.isOnGround || entity.location.block.isLiquid) return
        lastMoveLocation = entity.location

        val water = BoundingBox(
            entity.location.x - 3.0,
            entity.location.y - 1.0,
            entity.location.z - 3.0,
            entity.location.x + 3.0,
            entity.location.y - 1.0,
            entity.location.z + 3.0
        ).run {
            val blocks = mutableListOf<Block>()

            for (x in minX.toInt()..maxX.toInt())
                for (y in minY.toInt()..maxY.toInt())
                    for (z in minZ.toInt()..maxZ.toInt())
                        blocks.add(entity.world.getBlockAt(x, y, z))

            blocks
        }.filter { it.type == Material.WATER && (it.blockData as? Levelled).run { this != null && level == 0} }

        if (water.isEmpty()) return

        water.forEach {
            it.type = Material.MAGMA_BLOCK

            sync({ it.type = Material.WATER }, 100)
        }
        entity.world.playSound(entity.location, Sound.BLOCK_LAVA_EXTINGUISH, 1F, 1F)
    }

    @CardAbility("card.piglin_titan.ability.leap", ChatColor.GREEN)
    @UnlockedAt(10)
    @Passive(400, CardOperation.SUBTRACT, 4, min = 200)
    private fun leap() {
        val target = entity.target ?: return
        val loc = target.location

        val yaw = atan2(loc.z, loc.x).toDegrees()
        val pitch = atan2(loc.y, loc.x).toDegrees()

        val xz = cos(pitch.toRadians())
        val direction = Vector(
            -xz * sin(yaw.toRadians()),
            -sin(pitch.toRadians()),
            xz * cos(yaw.toRadians())
        )

        entity.velocity = direction.multiply(3.0 + (level / 5.0).coerceAtMost(2.0))

        entity.swingMainHand()

        if (target.location.distanceSquared(entity.location) <= 16.0)
            target.damage(statistics.attackDamage * 1.02, entity)
    }

    @Damage
    private fun fallDamage(event: EntityDamageEvent) {
        if (event.cause == EntityDamageEvent.DamageCause.FALL)
            event.isCancelled = true
    }

    @CardAbility("card.piglin_titan.ability.landing", ChatColor.AQUA)
    @UnlockedAt(25)
    @UserDamage
    private fun ownerFallDamage(event: EntityDamageEvent) = fallDamage(event)

}