package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.Material
import org.bukkit.entity.Husk
import org.bukkit.inventory.ItemStack

@Attributes(150.0, 6.3, 25.5, 0.35, 0.4)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 25.0)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 2.7)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 5.0)

@ListedCardAbility("card.prince_husk.ability.fire_immune")
class IPrinceHusk : IBattleCard<Husk>(BattleCardType.PRINCE_HUSK) {

    override fun init() {
        super.init()

        en.equipment.helmet = ItemStack(Material.GOLD_HELMET).apply {
            itemMeta = itemMeta.apply {
                isUnbreakable = true
            }
        }
    }

    @CardAbility("card.prince_husk.ability.royal_guard")
    @Passive(600, CardOperation.SUBTRACT, 5.0, 600.0, 100.0)
    private fun royalGuard() {
        // TODO Add Minion Spawning
    }

}