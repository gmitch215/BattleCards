package me.gamercoder215.battlecards.wrapper.v1_19_R3

import me.gamercoder215.battlecards.api.card.BattleCard
import me.gamercoder215.battlecards.impl.IBattleCard
import me.gamercoder215.battlecards.wrapper.Wrapper
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import net.minecraft.world.entity.ai.goal.FloatGoal
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class Wrapper1_19_R3 : Wrapper {

    override fun sendActionbar(player: Player, component: BaseComponent) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component)
    }

    override fun sendActionbar(player: Player, message: String) {
        sendActionbar(player, TextComponent(message))
    }

    // Card Editing

    private fun addCardPathfinders(entity: net.minecraft.world.entity.Mob, card: IBattleCard<*>) {
        val goal = entity.goalSelector
        val target = entity.targetSelector

        goal.removeAllGoals { true }
        target.removeAllGoals { true }

        goal.addGoal(0, FloatGoal(entity))
    }

    override fun editCard(entity: LivingEntity, card: IBattleCard<*>) {

    }
}