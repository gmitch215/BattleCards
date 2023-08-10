package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.util.BattleMaterial
import me.gamercoder215.battlecards.util.isCard
import me.gamercoder215.battlecards.util.isMinion
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Creature
import org.bukkit.entity.EntityType
import org.bukkit.entity.Husk
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta

@Type(BattleCardType.ETERNAL_HUSK)
@Attributes(1500.0, 28.9, 127.8, 0.17, 200.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 6.24, 7500.0)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 4.3)
@AttributesModifier(CardAttribute.KNOCKBACK_RESISTANCE, CardOperation.MULTIPLY, 1.04, 3000.0)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 2.65)
class IEternalHusk(data: ICard) : IBattleCard<Husk>(data) {

    override fun init() {
        super.init()

        entity.equipment.helmet = ItemStack(Material.LEATHER_HELMET).apply {
            itemMeta = (itemMeta as LeatherArmorMeta).apply {
                color = Color.GREEN
                isUnbreakable = true

                addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, level / 7, true)
            }
        }

        entity.equipment.itemInMainHand = ItemStack(BattleMaterial.GOLDEN_HOE.find()).apply {
            itemMeta = itemMeta.apply {
                isUnbreakable = true

                addEnchant(Enchantment.DAMAGE_ALL, (level / 5) + 2, true)
                addEnchant(Enchantment.DAMAGE_UNDEAD, (level / 4) + 4, true)
            }
        }

        if (level >= 10)
            entity.equipment.chestplate = ItemStack(Material.LEATHER_CHESTPLATE).apply {
                itemMeta = (itemMeta as LeatherArmorMeta).apply {
                    color = Color.GREEN
                    isUnbreakable = true

                    addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, (level - 10) / 5, true)
                }
            }
    }

    private var chargedDamage: Double = 0.0

    @CardAbility("card.eternal_husk.ability.charge")
    @Defensive(0.7, CardOperation.ADD, 0.02)
    private fun charge(event: EntityDamageByEntityEvent) {
        chargedDamage += event.finalDamage / 2.0
    }

    @Offensive(0.1)
    private fun releaseCharge(event: EntityDamageByEntityEvent) {
        if (chargedDamage <= 0.0) return

        event.damage += chargedDamage
        chargedDamage = 0.0

        entity.world.playSound(entity.location, Sound.ENTITY_WITHER_BREAK_BLOCK, 2F, 1F)
    }

    @CardAbility("card.eternal_husk.ability.advisors")
    @Passive(140)
    @UnlockedAt(5)
    private fun advisors() {
        if (minions.size >= 4) return

        while (minions.size < 4)
            minion(Husk::class.java) {
                equipment.helmet = ItemStack(Material.IRON_HELMET).apply {
                    itemMeta = itemMeta.apply {
                        isUnbreakable = true
                    }
                }

                equipment.itemInMainHand = ItemStack(BattleMaterial.GOLDEN_SWORD.find()).apply {
                    itemMeta = itemMeta.apply {
                        isUnbreakable = true
                    }
                }

                target = entity.target
            }
    }

    private companion object {
        @JvmStatic
        private val undeadTypes: List<EntityType> = listOf<Any>(
            EntityType.ZOMBIE,
            EntityType.SKELETON,
            EntityType.HUSK,
            EntityType.STRAY,
            EntityType.WITHER_SKELETON,

            "zombified_piglin",
            "zoglin",
            "drowned"
        ).mapNotNull {
            when (it) {
                is EntityType -> it
                is String -> try { EntityType.valueOf(it.uppercase()) } catch (e: IllegalArgumentException) { null }
                else -> null
            }
        }
    }

    @CardAbility("card.eternal_husk.ability.undead_supreme")
    @Passive(360, CardOperation.SUBTRACT, 10, min = 200)
    @UnlockedAt(20)
    private fun undeadSupreme() {
        val distance = (15.0 + (level - 20) * 2.25).coerceAtMost(50.0)
        entity.getNearbyEntities(distance, distance, distance)
            .filterIsInstance<Creature>()
            .filter { !it.isCard && !it.isMinion }
            .filter { it.type in undeadTypes }
            .forEach {
                it.target = entity.target
            }

        entity.world.playSound(entity.location, Sound.ENTITY_WITHER_AMBIENT, 5F, 0F)
    }

}