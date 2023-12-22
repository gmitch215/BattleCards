package me.gamercoder215.battlecards

import com.google.common.collect.ImmutableMap
import me.gamercoder215.battlecards.api.card.item.CardEquipment
import me.gamercoder215.battlecards.util.inventory.Items.randomCumulative
import me.gamercoder215.battlecards.util.isCard
import me.gamercoder215.battlecards.util.isMinion
import me.gamercoder215.battlecards.util.itemStack
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.r
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import kotlin.math.pow

internal class BattleSpawner(private val plugin: BattleCards) : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    val NO_SPECIAL: (CardEquipment) -> Boolean = { eq -> eq.rarity != CardEquipment.Rarity.SPECIAL }
    val CLASSES_TO_EQUIMENT = ImmutableMap.builder<(LivingEntity) -> Boolean, (CardEquipment) -> Boolean>()
        .put(
            { en -> en is Monster },
            { eq -> eq.damageModifier > 1 && NO_SPECIAL(eq) }
        )
        .put(
            { en -> en !is Monster && en !is Animals },
            { eq -> eq.healthModifier > 1 && NO_SPECIAL(eq) }
        )
        .put(
            { en -> en.type == EntityType.ENDERMAN },
            { eq -> eq.speedModifier > 1 && NO_SPECIAL(eq) }
        )
        .put(
            { en -> en is Animals },
            { eq -> eq.defenseModifier > 1 && NO_SPECIAL(eq) }
        )
        .put(
            { en -> en is Golem },
            { eq -> eq.defenseModifier > 1 && eq.rarity.ordinal > 0 && NO_SPECIAL(eq) }
        )
        .put(
            { en -> en is WaterMob },
            { eq -> eq.defenseModifier > 1 && eq.healthModifier > 1 && NO_SPECIAL(eq) }
        )
        .put(
            { en -> en.type == EntityType.WITHER || en.type == EntityType.ENDER_DRAGON },
            { eq -> eq.rarity.ordinal > 2 } // Mythological & Special
        )
        .build()!!

    val EQUIPMENT_RARITY_CHANCES = (CardEquipment.Rarity.entries - CardEquipment.Rarity.SPECIAL)
        .associateWith { rarity -> 10.0.pow(rarity.ordinal) }

    @EventHandler
    fun onDeath(event: EntityDamageEvent) {
        val entity = event.entity as? LivingEntity ?: return
        if (entity is Player) return
        if (entity.isCard || entity.isMinion) return

        if (entity.health - event.finalDamage > 0) return

        if (r.nextDouble() > 0.1) return

        var rerolls = 0
        var count = 1
        if (event is EntityDamageByEntityEvent && event.damager is Player) {
            val p = event.damager as Player
            rerolls += p.itemInHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS).coerceAtMost(7)
            count += (p.activePotionEffects
                .filter { effect -> effect.type.name == "LUCK" }
                .sumOf { effect -> effect.amplifier + 1 } / 2)
                .coerceAtMost(3)
        }

        val predicates = CLASSES_TO_EQUIMENT.filter { entry -> entry.key(entity) }.entries
        val equipments = plugin.registeredEquipment
            .filter { eq -> predicates.any { entry -> entry.value(eq) } }
            .associateWith { eq -> EQUIPMENT_RARITY_CHANCES[eq.rarity]!! }

        for (i in 1..count)
            entity.world.dropItemNaturally(entity.location, equipments.randomCumulative(rerolls)?.itemStack ?: continue)
    }

}