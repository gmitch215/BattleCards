package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.ChatColor
import org.bukkit.block.Biome
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.PolarBear
import org.bukkit.event.entity.EntityDamageByEntityEvent

@Type(BattleCardType.FROST_BEAR)
@Attributes(350.0, 15.0, 20.0, 0.27, 10.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 3.35)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.MULTIPLY, 1.015)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 1.15, 115.5)
@AttributesModifier(CardAttribute.KNOCKBACK_RESISTANCE, CardOperation.MULTIPLY, 1.013)
@Rideable
class IFrostBear(data: ICard) : IBattleCard<PolarBear>(data) {

    @CardAbility("card.frost_bear.ability.frostbite")
    @Offensive(0.2, CardOperation.ADD, 0.025, 0.75)
    private fun frostbite(event: EntityDamageByEntityEvent) {
        val target = event.entity as? LivingEntity ?: return
        target.freezeTicks += 20 * 3
    }

    @CardAbility("card.frost_bear.ability.ice_defense", ChatColor.AQUA)
    @UserDefensive(0.5, CardOperation.ADD, 0.01)
    @Defensive
    private fun iceDefense(event: EntityDamageByEntityEvent) {
        if (event.entity.location.block.biome.takeIf {
            it == Biome.FROZEN_OCEAN || it == Biome.FROZEN_RIVER || it == Biome.DEEP_FROZEN_OCEAN ||
            it == Biome.ICE_SPIKES || it == Biome.SNOWY_TAIGA
        } == null) return

        event.damage *= 0.75
    }

    @CardAbility("card.frost_bear.ability.ice_aspect", ChatColor.DARK_AQUA)
    @UserOffensive(0.1, CardOperation.MULTIPLY, 1.03)
    @UnlockedAt(30)
    private fun iceAspect(event: EntityDamageByEntityEvent) {
        val target = event.entity as? LivingEntity ?: return
        target.freezeTicks += 15
    }

}