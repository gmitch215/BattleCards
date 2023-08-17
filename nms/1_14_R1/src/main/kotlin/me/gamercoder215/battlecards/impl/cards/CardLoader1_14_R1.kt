package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.util.inventory.Items
import me.gamercoder215.battlecards.wrapper.CardLoader
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.r
import org.bukkit.Material
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.VillagerAcquireTradeEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import kotlin.math.min

internal class CardLoader1_14_R1 : CardLoader, Listener {

    override fun loadedCards(): Collection<Class<out IBattleCard<*>>> = listOf(
        IBomberman::class.java,
        IMercenary::class.java,
        IPhantomRider::class.java,
        IRaider::class.java
    )

    private companion object {
        @JvmStatic
        private val villagerTrades: Map<Villager.Profession, Set<Pair<Int, () -> MerchantRecipe>>> = mapOf(
            Villager.Profession.CLERIC to setOf(
                2 to {
                    MerchantRecipe(Items.TINY_EXPERIENCE_BOOK, 15).apply {
                        setExperienceReward(true)
                        villagerExperience = 5

                        ingredients = listOf(
                            ItemStack(Material.EMERALD, r.nextInt(30, 41))
                        )
                    }
                }
            )
        )
    }

    @EventHandler
    fun onAquire(e: VillagerAcquireTradeEvent) {
        val villager = e.entity as? Villager ?: return
        if (villager.profession !in villagerTrades.keys) return

        if (r.nextDouble() < BattleConfig.config.cardTradesChance) {
            val (minLevel, recipe) = (villagerTrades[villager.profession] ?: return).random()

            if (villager.villagerLevel >= minLevel)
                e.recipe = recipe()
        }
    }

}