package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.util.isCard
import me.gamercoder215.battlecards.util.isMinion
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.*
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack

@Type(BattleCardType.NETHER_PRINCE)
@Attributes(900.0, 24.0, 60.0, 0.28, 100.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 10.0)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 3.7)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 11.0)
@AttributesModifier(CardAttribute.KNOCKBACK_RESISTANCE, CardOperation.ADD, 4.3)
class INetherPrince(data: ICard) : IBattleCard<WitherSkeleton>(data) {

    private lateinit var hoglin: Hoglin

    override fun init() {
        super.init()

        entity.equipment!!.apply {
            helmet = ItemStack(Material.NETHERITE_HELMET).apply {
                itemMeta = itemMeta!!.apply {
                    addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5 + (level / 5), true)
                    addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 10 + (level / 10), true)
                }
            }

            if (level >= 20)
                chestplate = ItemStack(Material.NETHERITE_CHESTPLATE).apply {
                    itemMeta = itemMeta!!.apply {
                        addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5 + ((level - 20) / 5), true)
                        addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 12 + ((level - 20) / 8), true)
                    }
                }
        }

        hoglin = minion(Hoglin::class.java).apply {
            isImmuneToZombification = true
            getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = 100.0 + (level * 5.5)
            getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK)!!.baseValue = (statistics.knockbackResistance / 20).coerceAtMost(13.5)

            addPassenger(entity)
        }
    }

    @CardAbility("card.ability.nether_prince.firepower", ChatColor.YELLOW)
    @Passive(500, CardOperation.SUBTRACT, 5, Long.MAX_VALUE, 300)
    private fun firepower() = minion(Blaze::class.java) {
        getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = 30.0 + (level * 0.5)
    }

    @CardAbility("card.ability.nether_prince.nether_aspect", ChatColor.DARK_RED)
    @UserOffensive
    @UnlockedAt(20)
    private fun netherAspect(event: EntityDamageByEntityEvent) {
        val target = event.entity as? LivingEntity ?: return
        target.fireTicks += 200
    }

    private companion object {
        @JvmStatic
        private val netherTypes: List<EntityType> = listOf<Any>(
            EntityType.WITHER_SKELETON,
            EntityType.BLAZE,
            EntityType.PIGLIN,
            EntityType.ZOMBIFIED_PIGLIN,
            EntityType.HOGLIN,
            EntityType.ZOGLIN,
            EntityType.GHAST,
            EntityType.ENDERMAN,

            "piglin_brute"
        ).mapNotNull {
            when (it) {
                is EntityType -> it
                is String -> try { EntityType.valueOf(it.uppercase()) } catch (e: IllegalArgumentException) { null }
                else -> null
            }
        }
    }

    @CardAbility("card.ability.nether_prince.decree", ChatColor.DARK_AQUA)
    @Passive(300, CardOperation.SUBTRACT, 2, Long.MAX_VALUE)
    @UnlockedAt(50)
    private fun decree() {
        val distance = (20.0 + (level - 50) * 2.0).coerceAtMost(40.0)
        entity.getNearbyEntities(distance, distance, distance)
            .filterIsInstance<Mob>()
            .filter { !it.isCard && !it.isMinion }
            .filter { it.type in netherTypes }
            .forEach {
                it.target = entity.target
            }

        entity.world.playSound(entity.location, Sound.ENTITY_WITHER_AMBIENT, 5F, 1.25F)
    }

}