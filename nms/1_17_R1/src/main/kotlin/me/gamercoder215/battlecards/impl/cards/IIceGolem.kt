package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Snowman
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

@Type(BattleCardType.ICE_GOLEM)
@Attributes(1000.0, 5.0, 100.0, 0.29, 5.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 4.2)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 1.1)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 3.55)
@BlockAttachment(Material.PACKED_ICE, 0.0, -0.1, 0.0)
@Rideable
class IIceGolem(data: ICard) : IBattleCard<Snowman>(data) {

    override fun init() {
        super.init()

        entity.isDerp = true
    }

    @CardAbility("card.ice_golem.ability.frost", ChatColor.AQUA)
    @UserOffensive(0.35, CardOperation.ADD, 0.045)
    @Offensive
    private fun frost(event: EntityDamageByEntityEvent) {
        val target = event.entity as? LivingEntity ?: return
        target.freezeTicks += 20 * 2
        if (r.nextDouble() < 0.4)
            target.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 10 * (level.coerceAtMost(38) + 2), 1))
    }

}