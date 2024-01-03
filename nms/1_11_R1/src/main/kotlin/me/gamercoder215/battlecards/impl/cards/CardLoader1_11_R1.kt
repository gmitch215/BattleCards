package me.gamercoder215.battlecards.impl.cards

import me.gamercoder215.battlecards.api.card.item.CardEquipment
import me.gamercoder215.battlecards.wrapper.CardLoader

internal class CardLoader1_11_R1 : CardLoader {

    override fun loadedCards(): Collection<Class<out IBattleCard<*>>> = listOf(
        IMesaZombie::class.java,
        IPrinceHusk::class.java,
        IGoldSkeleton::class.java,
        IBandit::class.java,
        IMiner::class.java,
        ISuspiciousZombie::class.java,
        IEmeraldHusk::class.java,
        IEternalHusk::class.java,
        IWarriorHusk::class.java,
        IImmortal::class.java,
    )

    override fun loadedEquipment(): Collection<CardEquipment> = CardEquipments1_11_R1.entries

}