package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.BattleCardType
import org.bukkit.entity.LivingEntity

class IBasicCard<T : LivingEntity> : IBattleCard<T>(BattleCardType.BASIC)