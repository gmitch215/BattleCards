package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Illusioner
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Spellcaster
import org.bukkit.entity.Vex
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntitySpellCastEvent
import org.bukkit.inventory.ItemStack

@Type(BattleCardType.GOLDEN_WIZARD)
@Attributes(275.0, 5.0, 30.0, 0.31, 35.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 3.55)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 3.65)
@AttributesModifier(CardAttribute.SPEED, CardOperation.MULTIPLY, 1.001)
class IGoldenWizard(data: ICard) : IBattleCard<Illusioner>(data) {

    override fun init() {
        super.init()

        entity.equipment!!.setItemInMainHand(ItemStack(Material.BOW).apply {
            itemMeta = itemMeta!!.apply {
                addEnchant(Enchantment.ARROW_DAMAGE, (1 + (level / 10)).coerceAtMost(10), true)

                if (level >= 5)
                    addEnchant(Enchantment.ARROW_KNOCKBACK, ((level - 5) / 10).coerceAtMost(4), true)
            }
        })
    }

    @CardAbility("card.golden_wizard.ability.channeling")
    @Offensive(0.4, CardOperation.ADD, 0.03)
    private fun channeling(event: EntityDamageByEntityEvent) {
        val target = event.entity as? LivingEntity ?: return

        target.world.strikeLightning(event.entity.location)
        event.damage += 9.0 + r.nextDouble().times(13.5 + (level / 15.0))
    }

    @EventHandler
    private fun castSpell(event: EntitySpellCastEvent) {
        if (event.entity != entity) return

        when (event.spell) {
            Spellcaster.Spell.SUMMON_VEX -> {
                event.isCancelled = true

                val count = r.nextInt(3, 8)
                for (i in 0 until count)
                    minion(Vex::class.java) {
                        equipment!!.setItemInMainHand(ItemStack(Material.GOLDEN_SWORD).apply {
                            itemMeta = itemMeta!!.apply {
                                isUnbreakable = true
                            }
                        })

                        getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)!!.baseValue = 3.0 + (level / 10.0)
                    }
            }
            else -> return
        }
    }

}