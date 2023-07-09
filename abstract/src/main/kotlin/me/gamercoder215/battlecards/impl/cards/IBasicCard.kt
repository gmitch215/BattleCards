package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.*
import org.bukkit.entity.Creature

@Type(BattleCardType.BASIC)
@AttributesModifier(CardAttribute.MAX_HEALTH, CardOperation.ADD, 5.0)
@AttributesModifier(CardAttribute.DEFENSE, CardOperation.ADD, 1.0)
@AttributesModifier(CardAttribute.ATTACK_DAMAGE, CardOperation.ADD, 0.5)
class IBasicCard<T : Creature>(data: ICard) : IBattleCard<T>(data)