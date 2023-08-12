package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.util.BattleSound
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Blaze
import org.bukkit.entity.LargeFireball
import org.bukkit.event.entity.EntityDamageByEntityEvent

@Type(BattleCardType.INFERNO_BLAZE)
@Attributes(300.0, 25.0, 65.0, 0.28, 10.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 5.375)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 1.255)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 2.87)
@BlockAttachment(Material.MAGMA_BLOCK, 0.0, 0.7, 0.0, small = true)
class IInfernoBlaze(data: ICard) : IBattleCard<Blaze>(data) {

    @CardAbility("card.inferno_blaze.ability.fire_thorns", ChatColor.GOLD)
    @UserDefensive(0.75, CardOperation.ADD, 0.025)
    @Defensive
    private fun fireThorns(event: EntityDamageByEntityEvent) {
        event.entity.fireTicks += 20 * 3
    }

    @CardAbility("card.inferno_blaze.ability.heat_aspect", ChatColor.RED)
    @UserOffensive(0.2, CardOperation.ADD, 0.05, 0.5)
    @Offensive
    private fun heatAspect(event: EntityDamageByEntityEvent) {
        if (event.damager.world.environment == World.Environment.NETHER)
            event.damage *= 1.25
    }

    @CardAbility("card.inferno_blaze.ability.heat_protection", ChatColor.YELLOW)
    @UserDefensive(0.15, CardOperation.ADD, 0.075, 0.55)
    @Defensive
    @UnlockedAt(5)
    private fun heatProtection(event: EntityDamageByEntityEvent) {
        if (event.damager.world.environment == World.Environment.NETHER)
            event.damage *= 0.85
    }

    @CardAbility("card.inferno_blaze.ability.heat_shield", ChatColor.DARK_RED)
    @Defensive(0.05, CardOperation.ADD, 0.02, 0.2)
    @UnlockedAt(30)
    private fun heatSheild(event: EntityDamageByEntityEvent) {
        if (event.damager.world.environment == World.Environment.NETHER) {
            event.isCancelled = true
            entity.world.playSound(entity.eyeLocation, BattleSound.ITEM_SHIELD_BLOCK.findOrNull() ?: return, 3F, 1F)
        }
    }

    @CardAbility("card.inferno_blaze.ability.ghast")
    @Passive(300, CardOperation.SUBTRACT, 10, min = 100)
    @UnlockedAt(15)
    private fun ghast() {
        val fireball = entity.world.spawn(entity.eyeLocation, LargeFireball::class.java)
        fireball.direction = entity.eyeLocation.direction
        fireball.yield = 1 + (level / 0.2F).coerceAtMost(3F)
        fireball.velocity = fireball.velocity.multiply(1 + (level / 50))
        fireball.shooter = entity

        entity.world.playSound(entity.location, BattleSound.ENTITY_GHAST_SCREAM.find(), 4F, 1.5F)
    }


}