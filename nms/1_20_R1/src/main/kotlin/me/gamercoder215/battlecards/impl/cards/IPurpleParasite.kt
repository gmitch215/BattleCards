package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.DyeColor
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Shulker
import org.bukkit.entity.ShulkerBullet
import org.bukkit.entity.Silverfish
import org.bukkit.entity.Sniffer
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

@Type(BattleCardType.PURPLE_PARASITE)
@Attributes(110.0, 16.7, 65.7, 0.36, 750.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 6.32)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 2.97)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 3.645)
class IPurpleParasite(data: ICard) : IBattleCard<Shulker>(data) {

    private lateinit var host: Sniffer

    override fun init() {
        super.init()

        host = entity.world.spawn(entity.location, Sniffer::class.java).apply {
            passengers.add(entity)
            minions.add(this)

            val health = statistics.maxHealth * 1.1
            getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = health
            this.health = health

            getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)!!.baseValue = statistics.speed
        }

        entity.color = DyeColor.PURPLE
    }

    @CardAbility("card.purple_parasite.ability.poisoning")
    @Offensive
    @UserOffensive(0.35, CardOperation.ADD, 0.05)
    private fun poisoning(event: EntityDamageByEntityEvent) {
        val target = event.entity as? LivingEntity ?: return

        target.addPotionEffect(PotionEffect(PotionEffectType.POISON, 110, r.nextInt(0, 3)))
        if (r.nextDouble() < 0.2)
            target.addPotionEffect(PotionEffect(PotionEffectType.DARKNESS, 30, 0))
    }

    @CardAbility("card.purple_parasite.ability.stonemites")
    @Passive(380, CardOperation.SUBTRACT, 7, min = 120)
    private fun stonemites() {
        if (minions.size >= 21) return

        val count = r.nextInt(1, 5)
        for (i in 0 until count)
            minion(Silverfish::class.java) {
                getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)!!.baseValue = (statistics.attackDamage / 20.0).coerceIn(1.0, 6.5)
                getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = (statistics.maxHealth / 20.0).coerceIn(5.0, 40.0)
            }
    }

    @EventHandler
    private fun useAttackDamage(event: EntityDamageByEntityEvent) {
        val entity = event.entity as? ShulkerBullet ?: return
        if (entity.shooter != this.entity) return

        event.damage = statistics.attackDamage
    }

    @EventHandler
    private fun onHostDie(event: EntityDeathEvent) {
        if (event.entity != host) return

        entity.health = 0.0
    }
}