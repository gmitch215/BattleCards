package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.impl.ICard
import me.gamercoder215.battlecards.impl.Type
import org.bukkit.entity.Creature

@Type(BattleCardType.BASIC)
class IBasicCard<T : Creature>(data: ICard) : IBattleCard<T>(data)