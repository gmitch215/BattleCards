package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.ChatColor
import org.bukkit.entity.Silverfish

@Type(BattleCardType.SILVERFISH_HIVE)
@Attributes(60.0, 1.2, 2.0, 0.27, 10.0)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 0.05)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 0.006)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 0.01)
class ISilverfishHive(data: ICard) : IBattleCard<Silverfish>(data) {

    private lateinit var bottom: Silverfish

    override fun init() {
        super.init()

        bottom = minion(Silverfish::class.java) {
            passenger = entity
            maxHealth = statistics.maxHealth * 0.75
        }
    }

    @CardAbility("card.silverfish_hive.ability.hivemind", ChatColor.YELLOW)
    @Passive(200, CardOperation.SUBTRACT, 5, 80)
    private fun hivemind() {
        val count = r.nextInt(2, 7)
        for (i in 0 until count)
            minion(Silverfish::class.java) {
                maxHealth = statistics.maxHealth / 2
            }
    }

}