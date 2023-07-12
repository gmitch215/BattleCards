package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import me.gamercoder215.battlecards.util.BattleMaterial
import me.gamercoder215.battlecards.util.BattleSound
import org.bukkit.ChatColor
import org.bukkit.entity.CaveSpider
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Spider
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable

@Type(BattleCardType.SPIDER_QUEEN)
@Attributes(600.0, 8.5, 20.0, 0.33, 20.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 9.5)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 1.25)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 10.0)
class ISpiderQueen(data: ICard) : IBattleCard<Spider>(data) {

    private lateinit var child: CaveSpider

    override fun init() {
        super.init()

        child = world.spawn(location, CaveSpider::class.java)
        child.maxHealth = data.statistics.maxHealth / 15
        entity.passenger = child
    }

    override fun uninit() {
        if (!child.isDead) child.health = 0.0
        super.uninit()
    }

    @CardAbility("card.spider_queen.ability.poisoning", ChatColor.DARK_GREEN)
    @Offensive
    private fun poisoning(event: EntityDamageByEntityEvent) {
        val target = event.entity as? LivingEntity ?: return
        target.addPotionEffect(PotionEffect(PotionEffectType.POISON, ((level / 5) + 2) * 20, level / 25))
    }

    @CardAbility("card.spider_queen.ability.webbing", ChatColor.GRAY)
    @Offensive(0.2, CardOperation.ADD, 0.01, 0.5)
    @UnlockedAt(15)
    private fun webbing(event: EntityDamageByEntityEvent) {
        val target = event.entity as? LivingEntity ?: return
        val time = ((level - 15) / 5) + 1

        target.addPotionEffect(PotionEffect(PotionEffectType.SLOW, time * 20, (level - 15) / 30))
        if (target.location.block.isEmpty && r.nextBoolean()) target.location.block.type = BattleMaterial.COBWEB.find()
    }

    @CardAbility("card.spider_queen.ability.matriarchy", ChatColor.RED)
    @EventHandler
    private fun childDeath(event: EntityDeathEvent) {
        if (event.entity != child) return

        entity.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, 20 * 10, 1))
        entity.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 20, 3))

        object : BukkitRunnable() {
            override fun run() {
                world.playSound(location, BattleSound.ENTITY_ENDER_DRAGON_GROWL.findOrNull() ?: return, 2F, 1.5F)
            }
        }.runTaskLater(BattleConfig.plugin, 30)
    }

    @CardAbility("card.spider_queen.ability.spiderlings", ChatColor.DARK_AQUA)
    @Defensive(0.1, CardOperation.ADD, 0.02, 0.4)
    @UnlockedAt(25)
    private fun spiderlings(event: EntityDamageByEntityEvent) {
        if (child.isDead) return

        event.isCancelled = true
        BattleSound.ITEM_SHIELD_BLOCK.play(location, 3F, 1F)

        minion(CaveSpider::class.java) {
            maxHealth = child.health
        }
    }

    @CardAbility("card.spider_queen.ability.fangs", ChatColor.DARK_PURPLE)
    @UserOffensive
    @UnlockedAt(50)
    private fun fangs(event: EntityDamageByEntityEvent) {
        val target = event.entity as? LivingEntity ?: return
        target.addPotionEffect(PotionEffect(PotionEffectType.POISON, 20 * 4, 0))
    }

}