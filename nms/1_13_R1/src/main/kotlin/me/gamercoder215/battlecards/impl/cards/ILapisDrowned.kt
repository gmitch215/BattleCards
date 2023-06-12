package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Drowned
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack

@Type(BattleCardType.LAPIS_DROWNED)
@Attributes(45.0, 3.1, 5.5, 0.32, 0.18)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 11.0)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 0.34)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 0.22)
class ILapisDrowned(data: ICard) : IBattleCard<Drowned>(data) {

    override fun init() {
        super.init()

        en.equipment.helmet = ItemStack(Material.LAPIS_BLOCK)
    }

    @CardAbility("card.lapis_drowned.ability.disenchantment", ChatColor.DARK_AQUA)
    @Defensive
    private fun disenchantment(event: EntityDamageByEntityEvent) {
        TODO("Not yet implemented")
    }

}