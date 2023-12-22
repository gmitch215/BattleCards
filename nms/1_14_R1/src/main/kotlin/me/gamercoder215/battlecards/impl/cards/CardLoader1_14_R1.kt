package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.api.card.Rarity
import me.gamercoder215.battlecards.api.card.item.CardEquipment
import me.gamercoder215.battlecards.api.card.item.CardEquipment.Rarity.*
import me.gamercoder215.battlecards.util.inventory.CardGenerator
import me.gamercoder215.battlecards.util.inventory.Items
import me.gamercoder215.battlecards.util.itemStack
import me.gamercoder215.battlecards.util.nbt
import me.gamercoder215.battlecards.util.set
import me.gamercoder215.battlecards.util.sync
import me.gamercoder215.battlecards.wrapper.CardLoader
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.r
import org.bukkit.Material
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.VillagerAcquireTradeEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.GrindstoneInventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe

internal class CardLoader1_14_R1 : CardLoader, Listener {

    override fun loadedCards(): Collection<Class<out IBattleCard<*>>> = listOf(
        IBomberman::class.java,
        IMercenary::class.java,
        IPhantomRider::class.java,
        IRaider::class.java
    )

    override fun loadedEquipment(): Collection<CardEquipment> = CardEquipments1_14_R1.entries

    private companion object {
        private val equipmentToPrice = mutableMapOf(
            AVERAGE to 26..48,
            FREQUENT to 39..61,
            HISTORICAL to 52..84,
            MYTHOLOGICAL to 65..128
        )

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

        private fun recipe(card: BattleCardType): MerchantRecipe? {
            if (card.entityClass == null) return null

            val shards = (5..8).random()
            return MerchantRecipe(CardGenerator.toItem(card()), 3).apply {
                setExperienceReward(true)
                villagerExperience = 15

                ingredients = listOf(
                    Items.cardShard(card.rarity).apply { amount = shards }
                )
            }
        }

        private fun equipment(filter: (CardEquipment) -> Boolean): MerchantRecipe = recipe(BattleConfig.config.registeredEquipment
            .filter(filter)
            .filter { it.rarity in equipmentToPrice.keys }.random())

        private val villagerTrades: Map<Villager.Profession, Set<Pair<Int, () -> MerchantRecipe?>>> = mapOf(
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
                    equipment { it.healthModifier in 1.0..1.1 }
                },
                3 to {
                    equipment { it.healthModifier in 1.0..1.3 }
                },
                3 to {
                    MerchantRecipe(Items.SMALL_EXPERIENCE_BOOK, 5).apply {
                        setExperienceReward(true)
                        villagerExperience = 15

                        ingredients = listOf(
                            ItemStack(Material.EMERALD, 64),
                            ItemStack(Material.EMERALD, r.nextInt(52, 65))
                        )
                    }
                },
                3 to {
                    recipe(listOf(
                        BattleCardType.REDSTONE_ZOMBIE,
                        BattleCardType.LAPIS_DROWNED,
                    ).random())
                },
                4 to {
                    MerchantRecipe(Items.MEDIUM_EXPERIENCE_BOOK, 3).apply {
                        setExperienceReward(true)
                        villagerExperience = 35

                        ingredients = listOf(
                            ItemStack(Material.EMERALD, 64),
                            ItemStack(Material.EMERALD, 64)
                        )
                    }
                },
                4 to {
                    recipe(BattleCardType.SILVERFISH_HIVE)
                }
            ),
            Villager.Profession.ARMORER to setOf(
                2 to {
                    equipment { it.defenseModifier in 1.0..1.1 }
                },
                3 to {
                    equipment { it.defenseModifier in 1.0..1.3 }
                },
                3 to {
                    recipe(listOf(
                        BattleCardType.STONE_ARCHER,
                        BattleCardType.SKELETON_SOLDIER,
                    ).random())
                },
                4 to {
                    recipe(listOf(
                        BattleCardType.GOLD_SKELETON,
                        BattleCardType.DIAMOND_GOLEM,
                    ).random())
                }
            ),
            Villager.Profession.WEAPONSMITH to setOf(
                2 to {
                    equipment { it.damageModifier in 1.0..1.1 }
                },
                3 to {
                    recipe(listOf(
                        BattleCardType.SKELETON_SOLDIER,
                        BattleCardType.STONE_ARCHER,
                        BattleCardType.BANDIT
                    ).random())
                },
                4 to {
                    equipment { it.damageModifier in 1.0..1.4 && it.ability != null }
                },
                4 to {
                    recipe(listOf(
                        BattleCardType.LAPIS_DROWNED,
                        BattleCardType.KNIGHT,
                        BattleCardType.UNDEAD_LUMBERJACK,
                        BattleCardType.MERCENARY
                    ).random())
                }
            ),
            Villager.Profession.TOOLSMITH to setOf(
                2 to {
                    equipment { (it.speedModifier in 1.0..1.1 || it.knockbackResistanceModifier in 1.0..1.2) }
                },
                3 to {
                    equipment { (it.speedModifier in 1.0..1.3 || it.knockbackResistanceModifier in 1.0..1.5) }
                },
                3 to {
                    recipe(listOf(
                        BattleCardType.KNIGHT,
                        BattleCardType.MERCENARY,
                        BattleCardType.MINER
                    ).random())
                }
            ),
            Villager.Profession.BUTCHER to setOf(
                3 to {
                    recipe(BattleCardType.PITBULL)
                }
            ),
            Villager.Profession.MASON to setOf(
                3 to {
                    recipe(BattleCardType.MESA_ZOMBIE)
                }
            ),
            Villager.Profession.FLETCHER to setOf(
                3 to {
                    recipe(BattleCardType.SNIPER)
                }
            ),
            Villager.Profession.LIBRARIAN to setOf(
                1 to {
                    MerchantRecipe(Items.cardShard(Rarity.COMMON), 8).apply {
                        setExperienceReward(true)
                        villagerExperience = 5

                        ingredients = listOf(
                            ItemStack(Material.EMERALD, r.nextInt(31, 47))
                        )
                    }
                },
                2 to {
                    MerchantRecipe(Items.cardShard(Rarity.UNCOMMON), 8).apply {
                        setExperienceReward(true)
                        villagerExperience = 10

                        ingredients = listOf(
                            Items.cardShard(Rarity.COMMON).apply { amount = r.nextInt(3, 6) },
                            ItemStack(Material.EMERALD, r.nextInt(28, 51))
                        )
                    }
                },
                3 to {
                    MerchantRecipe(Items.cardShard(Rarity.RARE), 8).apply {
                        setExperienceReward(true)
                        villagerExperience = 25

                        ingredients = listOf(
                            Items.cardShard(Rarity.UNCOMMON).apply { amount = r.nextInt(3, 6) },
                            ItemStack(Material.EMERALD, r.nextInt(38, 65))
                        )
                    }
                },
                4 to {
                    MerchantRecipe(Items.cardShard(Rarity.EPIC), 8).apply {
                        setExperienceReward(true)
                        villagerExperience = 40

                        ingredients = listOf(
                            Items.cardShard(Rarity.RARE).apply { amount = r.nextInt(3, 6) },
                            ItemStack(Material.EMERALD, r.nextInt(49, 65))
                        )
                    }
                },
                4 to {
                    MerchantRecipe(Items.SMALL_EXPERIENCE_BOOK, 10).apply {
                        setExperienceReward(true)
                        villagerExperience = 10

                        ingredients = listOf(
                            ItemStack(Material.EMERALD, 64),
                            ItemStack(Material.EMERALD, r.nextInt(31, 54))
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
            val r = (villagerTrades[villager.profession] ?: return).filter { it.first == villager.villagerLevel }.randomOrNull()?.second?.invoke() ?: return

            if (villager.recipes.any { it.result.isSimilar(r.result) }) return

            e.recipe = r
        }
    }

    @EventHandler
    fun grindstone(event: InventoryClickEvent) {
        val inv = event.view.topInventory as? GrindstoneInventory ?: return

        sync {
            if (inv.filterNotNull().any { it.nbt.hasTag("nointeract") })
                inv[2] = null
        }
    }

}