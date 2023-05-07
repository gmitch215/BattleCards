package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import org.bukkit.entity.Creature

class IBasicCard<T : Creature> : IBattleCard<T>(BattleCardType.BASIC)