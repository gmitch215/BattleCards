package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Husk
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack

@Type(BattleCardType.MESA_ZOMBIE)
@Attributes(50.0, 2.3, 5.5, 0.3, 0.2)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 15.0)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 1.1)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 0.4)
class IMesaZombie(data: ICard) : IBattleCard<Husk>(data) {

    override fun init() {
        super.init()

        entity.equipment.helmet = ItemStack(Material.RED_SANDSTONE)
    }

    @CardAbility("card.mesa_zombie.ability.fire_aspect", ChatColor.GOLD)
    @Offensive(0.5)
    private fun fireAspect(event: EntityDamageByEntityEvent) {
        val target = event.entity as? LivingEntity ?: return
        val ticks: Int = (level * 4) + 16

        target.fireTicks += ticks
    }

}