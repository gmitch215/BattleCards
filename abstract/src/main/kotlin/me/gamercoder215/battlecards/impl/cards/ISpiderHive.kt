package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.Material
import org.bukkit.entity.CaveSpider

@Type(BattleCardType.SPIDER_HIVE)
@Attributes(50.0, 2.5, 7.0, 0.26, 7.5)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 0.09)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 0.1)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 0.102)
@AttributesModifier(CardAttribute.KNOCKBACK_RESISTANCE, CardOperation.MULTIPLY, 1.007)
@BlockAttachment(Material.IRON_BLOCK, 0.0, -0.4, 0.2, true)
class ISpiderHive(data: ICard) : IBattleCard<CaveSpider>(data) {

    @CardAbility("card.spider_hive.ability.colony")
    @Passive(200, CardOperation.SUBTRACT, 5, Long.MAX_VALUE, 140)
    private fun colony() {
        if (minions.size >= 50) return

        val count = r.nextInt(3, 8)

        for (i in 0 until count)
            minion(CaveSpider::class.java) {
                maxHealth = statistics.maxHealth * 0.6
            }

    }

}