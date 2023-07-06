package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Skeleton
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.inventory.ItemStack

@Type(BattleCardType.SNIPER)
@Attributes(25.0, 3.5, 5.5, 0.4, 0.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 5.0)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 1.5)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 1.55)

@CardAbility("card.sniper.ability.triple_shot", ChatColor.GREEN)
class ISniper(data: ICard) : IBattleCard<Skeleton>(data) {

    override fun init() {
        super.init()

        entity.equipment.helmet = ItemStack(Material.IRON_HELMET).apply {
            itemMeta = itemMeta.apply {
                try {
                    spigot().isUnbreakable = true
                } catch (ignored: UnsupportedOperationException) {
                    addEnchant(Enchantment.DURABILITY, 32767, true)
                }
            }
        }
    }

    @Listener
    fun onShoot(event: EntityShootBowEvent) {
        if (event.entity != entity) return

        event.projectile.velocity.multiply(1 + (level / 50.0))

        if (r.nextInt(100) < (20 + (level * 5))) {
            entity.world.spawn(event.projectile.location.apply { yaw += 30 }, event.projectile::class.java)
            entity.world.spawn(event.projectile.location.apply { yaw -= 30 }, event.projectile::class.java)
        }
    }

}