package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.Material
import org.bukkit.entity.Husk
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack

@Attributes(50.0, 2.3, 5.5, 0.3, 0.2)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 15.0)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD)
class IMesaZombie : IBattleCard<Husk>(BattleCardType.MESA_ZOMBIE) {

    override fun init() {
        super.init()

        en.equipment.helmet = ItemStack(Material.RED_SANDSTONE)
    }

    @CardAbility("Fire Aspect")
    @Offensive(0.5)
    private fun fireAspect(event: EntityDamageByEntityEvent) {
        val target = event.entity as? LivingEntity ?: return
        val ticks: Int = (getLevel() * 4) + 16

        target.fireTicks += ticks
    }

}