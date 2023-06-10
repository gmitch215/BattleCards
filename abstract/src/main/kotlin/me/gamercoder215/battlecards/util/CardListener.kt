package me.gamercoder215.battlecards.util

import me.gamercoder215.battlecards.impl.Defensive
import me.gamercoder215.battlecards.impl.Offensive
import me.gamercoder215.battlecards.util.inventory.Generator
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.get
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.r
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.w
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityTargetEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.Plugin

class CardListener(plugin: Plugin) : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (!event.hasItem()) return
        val p = event.player
        val item = event.item!!

        if (!item.isCard()) return
        val card = item.getCard()!!

        when (event.action) {
            Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> {
                val info = Generator.genGUI(27, get("menu.card_info"))
                info.isCancelled = true

                p.openInventory(info)
                p.playSuccess()
            }
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> {
                card.spawnCard(p)
                w.spawnParticle(BattleParticle.CLOUD, p.location, 30, 0.0, 1.0, 0.0, 0.2)
            }
            else -> return
        }
    }

    @EventHandler
    fun onTarget(event: EntityTargetEvent) {
        if (event.entity.isCard() && event.entity.getCard()!!.p.uniqueId == event.target.uniqueId)
            event.isCancelled = true
    }

    @EventHandler
    fun onHitAttachment(event: EntityDamageEvent) {
        val entity = event.entity
        if (!entity.hasMetadata("battlecards:block_attachment")) return
        event.isCancelled = true
    }

    @EventHandler
    fun onAttack(event: EntityDamageByEntityEvent) {
        if (event.entity.isCard()) {
            val card = event.entity.getCard()
            if (card != null) {
                val defensive = card.javaClass.declaredMethods.filter { it.isAnnotationPresent(Defensive::class.java) }
                for (method in defensive) {
                    val annotation = method.getDeclaredAnnotation(Defensive::class.java)
                    var chance = annotation.chance

                    if (!annotation.value.isNaN())
                        for (i in 1 until card.getLevel()) chance = annotation.operation.apply(chance, annotation.value)

                    chance = chance.coerceAtMost(annotation.max)
                    if ((r.nextDouble() * annotation.max) <= chance)
                        method.invoke(card, event)
                }
            }
        }

        if (event.damager.isCard()) {
            val card = event.damager.getCard()
            if (card != null) {
                val offensive = card.javaClass.declaredMethods.filter { it.isAnnotationPresent(Offensive::class.java) }
                for (method in offensive) {
                    val annotation = method.getDeclaredAnnotation(Defensive::class.java)
                    var chance = annotation.chance

                    if (!annotation.value.isNaN())
                        for (i in 1 until card.getLevel()) chance = annotation.operation.apply(chance, annotation.value)

                    chance = chance.coerceAtMost(annotation.max)
                    if ((r.nextDouble() * annotation.max) <= chance)
                        method.invoke(card, event)
                }
            }
        }
    }

}