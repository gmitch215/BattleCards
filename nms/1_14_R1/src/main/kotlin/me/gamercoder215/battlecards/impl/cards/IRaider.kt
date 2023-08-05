package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.ChatColor
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.block.Banner
import org.bukkit.block.banner.Pattern
import org.bukkit.block.banner.PatternType
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.*
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BannerMeta
import org.bukkit.inventory.meta.BlockStateMeta

@Type(BattleCardType.RAIDER)
@Attributes(175.0, 7.5, 40.0, 0.3, 20.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 2.2)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 2.8)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 4.06)
@MinionBlockAttachment(EntityType.IRON_GOLEM, Material.LAPIS_BLOCK, 0.0, 0.69, 0.2)
class IRaider(data: ICard) : IBattleCard<Pillager>(data) {

    private lateinit var ravager: Ravager

    private companion object {
        @JvmStatic
        val bannerPattern = listOf(
            Pattern(DyeColor.ORANGE, PatternType.DIAGONAL_RIGHT_MIRROR),
            Pattern(DyeColor.YELLOW, PatternType.CROSS),
            Pattern(DyeColor.GRAY, PatternType.CIRCLE_MIDDLE),
            Pattern(DyeColor.CYAN, PatternType.SKULL)
        )
    }

    override fun init() {
        super.init()

        entity.equipment!!.apply {
            setItemInMainHand(ItemStack(Material.CROSSBOW).apply {
                itemMeta = itemMeta!!.apply {
                    addEnchant(Enchantment.QUICK_CHARGE, 2, true)
                    addEnchant(Enchantment.MULTISHOT, 1, true)
                }
            })

            setItemInOffHand(ItemStack(Material.SHIELD).apply {
                itemMeta = (itemMeta as BlockStateMeta).apply {
                    blockState = (blockState as Banner).apply { baseColor = DyeColor.RED; patterns = bannerPattern }
                }
            })

            if (level >= 20)
                helmet = ItemStack(Material.RED_BANNER).apply {
                    itemMeta = (itemMeta as BannerMeta).apply { patterns = bannerPattern }
                }
        }

        ravager = minion(Ravager::class.java) {
            addPassenger(entity)
        }
    }

    @CardAbility("card.raider.ability.raid_golems", ChatColor.BLUE)
    @Passive(780, CardOperation.SUBTRACT, 10, Long.MAX_VALUE, 300)
    @UnlockedAt(25)
    private fun raidGolems() {
        val count = r.nextInt(1, 3)
        for (i in 0 until count)
            minion(IronGolem::class.java)
    }

    @CardAbility("card.raider.ability.bombs", ChatColor.YELLOW)
    @Offensive(0.2, CardOperation.ADD, 0.03, 0.5)
    private fun bombs(event: EntityDamageByEntityEvent) {
        if (event.cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) return

        val target = event.entity as? LivingEntity ?: return
        target.world.createExplosion(target.location, r.nextFloat(2.0F, 4.0F), false, true, entity)
    }

    @CardAbility("card.raider.ability.illusioner", ChatColor.DARK_PURPLE)
    @Passive(900, CardOperation.SUBTRACT, 15, Long.MAX_VALUE, 450)
    @UnlockedAt(40)
    private fun illusioner() = minion(Illusioner::class.java)


}