package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.item.CardEquipment
import me.gamercoder215.battlecards.api.card.item.CardEquipment.Rarity.*
import me.gamercoder215.battlecards.util.inventory.Items
import me.gamercoder215.battlecards.util.itemStack
import me.gamercoder215.battlecards.wrapper.CardLoader
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.r
import org.bukkit.Material
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.VillagerAcquireTradeEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe

internal class CardLoader1_14_R1 : CardLoader, Listener {

    override fun loadedCards(): Collection<Class<out IBattleCard<*>>> = listOf(
        IBomberman::class.java,
        IMercenary::class.java,
        IPhantomRider::class.java,
        IRaider::class.java
    )

    private companion object {
        @JvmStatic
        private val equipmentToPrice = mutableMapOf(
            AVERAGE to 26..48,
            FREQUENT to 39..61,
            HISTORICAL to 52..84,
            MYTHOLOGICAL to 65..128
        )

        @JvmStatic
        private fun recipe(equipment: CardEquipment): MerchantRecipe {
            val count = equipmentToPrice[equipment.rarity]!!.random()
            return MerchantRecipe(equipment.itemStack, 5).apply {
                setExperienceReward(true)
                villagerExperience = 10

                ingredients =
                    if (count > 64)
                        listOf(ItemStack(Material.EMERALD, 64), ItemStack(Material.EMERALD, count - 64))
                    else
                        listOf(ItemStack(Material.EMERALD, count))
            }
        }

        @JvmStatic
        private val villagerTrades: Map<Villager.Profession, Set<Pair<Int, () -> MerchantRecipe>>> = mapOf(
            Villager.Profession.CLERIC to setOf(
                2 to {
                    MerchantRecipe(Items.TINY_EXPERIENCE_BOOK, 10).apply {
                        setExperienceReward(true)
                        villagerExperience = 5

                        ingredients = listOf(
                            ItemStack(Material.EMERALD, r.nextInt(40, 53))
                        )
                    }
                },
                2 to {
                    recipe(BattleConfig.config.registeredEquipment.filter { it.healthModifier in 1.0..1.1 && it.rarity in equipmentToPrice.keys }.random())
                },
                3 to {
                    recipe(BattleConfig.config.registeredEquipment.filter { it.healthModifier in 1.0..1.3 && it.rarity in equipmentToPrice.keys }.random())
                },
                4 to {
                    MerchantRecipe(Items.SMALL_EXPERIENCE_BOOK, 5).apply {
                        setExperienceReward(true)
                        villagerExperience = 15

                        ingredients = listOf(
                            ItemStack(Material.EMERALD, 64),
                            ItemStack(Material.EMERALD, r.nextInt(52, 65))
                        )
                    }
                }
            ),
            Villager.Profession.ARMORER to setOf(
                2 to {
                    recipe(BattleConfig.config.registeredEquipment.filter { it.defenseModifier in 1.0..1.1 && it.rarity in equipmentToPrice.keys }.random())
                },
                3 to {
                    recipe(BattleConfig.config.registeredEquipment.filter { it.defenseModifier in 1.0..1.3 && it.rarity in equipmentToPrice.keys }.random())
                },
            ),
            Villager.Profession.WEAPONSMITH to setOf(
                2 to {
                    recipe(BattleConfig.config.registeredEquipment.filter { it.damageModifier in 1.0..1.1 && it.rarity in equipmentToPrice.keys }.random())
                },
                4 to {
                    recipe(BattleConfig.config.registeredEquipment.filter { it.damageModifier in 1.0..1.4 && it.ability != null && it.rarity in equipmentToPrice.keys }.random())
                }
            ),
            Villager.Profession.TOOLSMITH to setOf(
                2 to {
                    recipe(BattleConfig.config.registeredEquipment.filter { (it.speedModifier in 1.0..1.1 || it.knockbackResistanceModifier in 1.0..1.2) && it.rarity in equipmentToPrice.keys }.random())
                },
                3 to {
                    recipe(BattleConfig.config.registeredEquipment.filter { (it.speedModifier in 1.0..1.3 || it.knockbackResistanceModifier in 1.0..1.5) && it.rarity in equipmentToPrice.keys }.random())
                }
            ),
        )
    }

    @EventHandler
    fun onAquire(e: VillagerAcquireTradeEvent) {
        val villager = e.entity as? Villager ?: return
        if (villager.profession !in villagerTrades.keys) return

        if (r.nextDouble() < BattleConfig.config.cardTradesChance) {
            val r = (villagerTrades[villager.profession] ?: return).filter { it.first == villager.villagerLevel }.randomOrNull()?.second?.invoke() ?: return

            if (villager.recipes.any { it.result.isSimilar(r.result) }) return

            e.recipe = r
        }
    }

}