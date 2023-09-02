package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.util.CardAttackType
import me.gamercoder215.battlecards.util.attackType
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Goat
import org.bukkit.entity.Skeleton
import org.bukkit.inventory.ItemStack

@Type(BattleCardType.GOAT_GLADIATOR)
@Attributes(190.0, 7.0, 10.0, 0.27, 10.0, 96.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 4.6)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 3.8)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 3.95)
@AttributesModifier(CardAttribute.SPEED, CardOperation.MULTIPLY, 1.005, 0.335)
class IGoatGladiator(data: ICard) : IBattleCard<Skeleton>(data) {

    private lateinit var goat: Goat

    override fun init() {
        super.init()

        attackType = CardAttackType.BOW

        entity.equipment!!.apply {
            helmet = ItemStack(Material.CHAINMAIL_HELMET).apply {
                itemMeta = itemMeta!!.apply {
                    isUnbreakable = true

                    addEnchant(Enchantment.PROTECTION_PROJECTILE, 2 + (level / 5).coerceAtMost(8), true)
                }
            }

            chestplate = ItemStack(Material.CHAINMAIL_CHESTPLATE).apply {
                itemMeta = itemMeta!!.apply {
                    isUnbreakable = true

                    addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1 + (level / 8).coerceAtMost(9), true)
                }
            }

            leggings = ItemStack(Material.CHAINMAIL_LEGGINGS).apply {
                itemMeta = itemMeta!!.apply {
                    isUnbreakable = true

                    addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1 + (level / 8).coerceAtMost(9), true)
                }
            }

            boots = ItemStack(Material.CHAINMAIL_BOOTS).apply {
                itemMeta = itemMeta!!.apply {
                    isUnbreakable = true

                    addEnchant(Enchantment.PROTECTION_FALL, 10 + (level / 2).coerceAtMost(15), true)
                }
            }

            setItemInMainHand(ItemStack(Material.BOW).apply {
                itemMeta = itemMeta!!.apply {
                    isUnbreakable = true

                    addEnchant(Enchantment.ARROW_DAMAGE, 3 + (level / 5).coerceAtMost(27), true)

                    if (level >= 15)
                        addEnchant(Enchantment.ARROW_KNOCKBACK, 1 + (level.minus(15) / 7).coerceAtMost(3), true)

                    if (level >= 25)
                        addEnchant(Enchantment.ARROW_FIRE, 1, true)
                }
            })
        }

        goat = entity.world.spawn(entity.location, Goat::class.java).apply {
            val hp = statistics.maxHealth * 0.75
            getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = hp
            health = hp

            getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)!!.baseValue = statistics.attackDamage
            getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)!!.baseValue = statistics.speed * 1.1
            getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK)!!.baseValue = 1.0 + (level.div(5) * 0.1).coerceAtMost(2.0)

            addPassenger(entity)
            minions.add(this)
        }

    }

    @Passive(10)
    private fun goatAi() {
        if (!this::goat.isInitialized) return

        if (goat.target == null && entity.target != null)
            goat.target = entity.target
    }

}