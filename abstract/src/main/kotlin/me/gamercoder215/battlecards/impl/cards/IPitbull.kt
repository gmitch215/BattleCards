package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.ChatColor
import org.bukkit.entity.Wolf
import org.bukkit.event.entity.EntityDamageByEntityEvent

@Type(BattleCardType.PITBULL)
@Attributes(80.0, 9.45, 10.0, 0.245, 2.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 3.5)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 0.4)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 0.35)
class IPitbull(data: ICard) : IBattleCard<Wolf>(data) {

    @CardAbility("card.pitbull.ability.bite", ChatColor.DARK_GRAY)
    @Offensive(0.2, CardOperation.ADD, 0.005, 0.75)
    private fun bite(event: EntityDamageByEntityEvent) {
        event.damage *= 1.05
    }

}