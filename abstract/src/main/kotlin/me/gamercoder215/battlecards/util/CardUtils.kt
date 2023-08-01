package me.gamercoder215.battlecards.util

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.Card
import me.gamercoder215.battlecards.impl.BlockAttachment
import me.gamercoder215.battlecards.impl.MinionBlockAttachment
import me.gamercoder215.battlecards.impl.cards.IBattleCard
import me.gamercoder215.battlecards.wrapper.Wrapper.Companion.w
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.util.Vector
import java.text.SimpleDateFormat
import java.util.*

object CardUtils {

    // Entity Utils

    @JvmStatic
    fun createAttachments(card: IBattleCard<*>) {
        val attachments = card.javaClass.getAnnotationsByType(BlockAttachment::class.java)
        if (attachments.isEmpty()) return

        for (attachment in attachments) {
            val newLocation = local(card.location, Vector(attachment.offsetX, attachment.offsetY, attachment.offsetZ))
            newLocation.yaw = w.getYBodyRot(card.entity)
            newLocation.pitch = 0f

            val entity = card.world.spawn(newLocation, ArmorStand::class.java).apply {
                isSmall = attachment.small
                isVisible = false
                setGravity(false)
                setMetadata("battlecards:block_attachment", FixedMetadataValue(BattleConfig.plugin, true))
                helmet = ItemStack(attachment.material)
            }

            card.attachments[entity.uniqueId] = {
                local(card.location, Vector(attachment.offsetX, attachment.offsetY, attachment.offsetZ)).apply {
                    yaw = w.getYBodyRot(card.entity)
                    pitch = 0f
                }
            }
        }
    }

    @JvmStatic
    fun createMinionAttachments(minion: LivingEntity, card: IBattleCard<*>) {
        val attachments = card.javaClass.getAnnotationsByType(MinionBlockAttachment::class.java).filter { it.type == minion.type }
        if (attachments.isEmpty()) return

        val reference = minion.location

        val map = mutableMapOf<UUID, () -> Location>()

        for (attachment in attachments) {
            val newLocation = local(reference, Vector(attachment.offsetX, attachment.offsetY, attachment.offsetZ))
            newLocation.yaw = w.getYBodyRot(card.entity)
            newLocation.pitch = 0f

            val entity = minion.world.spawn(newLocation, ArmorStand::class.java).apply {
                isSmall = attachment.small
                isVisible = false
                setGravity(false)
                setMetadata("battlecards:block_attachment", FixedMetadataValue(BattleConfig.plugin, true))
                helmet = ItemStack(attachment.material)
            }

            map[entity.uniqueId] = {
                local(minion.location, Vector(attachment.offsetX, attachment.offsetY, attachment.offsetZ)).apply {
                    yaw = w.getYBodyRot(minion)
                    pitch = 0f
                }
            }
        }

        card.minionAttachments[minion.uniqueId] = map
    }

    // String Utils / Extensions

    @JvmStatic
    fun format(string: String, vararg args: Any): String {
        return String.format(BattleConfig.config.locale, string, *args)
    }

    @JvmStatic
    fun color(s: String): String {
        val array = s.trim().split("\\s".toRegex()).toTypedArray()

        val list: MutableList<String> = mutableListOf()

        for (i in array.indices) {
            var str = array[i].replace("&", "${ChatColor.COLOR_CHAR}")

            if (!str.startsWith(ChatColor.COLOR_CHAR)) {
                val strC = str.replace("[.,!]".toRegex(), "")
                str = when {
                    strC.endsWith("%") -> "${ChatColor.DARK_AQUA}$str"
                    strC.endsWith("s") && str.substringBeforeLast("s").toDoubleOrNull() != null -> "${ChatColor.GOLD}$str"
                    strC.endsWith("x") && str.substringBeforeLast("x").toDoubleOrNull() != null -> "${ChatColor.RED}$str"
                    strC.toDoubleOrNull() != null -> "${ChatColor.BLUE}$str"
                    else -> "${ChatColor.GRAY}$str"
                }
            }

            list.add("$str${ChatColor.RESET}")
        }

        return list.joinToString(" ")
    }

    @JvmStatic
    fun dateFormat(date: Date?, time: Boolean = false): String? {
        if (date == null || date.time == 0L) return null

        val pattern = if (time) "MMM dd, yyyy '|' h:mm a" else "MMM dd, yyyy"
        return SimpleDateFormat(pattern, BattleConfig.config.locale).format(date)
    }

    // Other

    @JvmStatic
    fun local(reference: Location, local: Vector): Location {
        val base = Vector(0, 0, 1)
        val left: Vector = base.clone().rotateAroundY(Math.toRadians(-reference.yaw + 90.0))
        val up: Vector = reference.direction.clone().rotateAroundNonUnitAxis(left, Math.toRadians(-90.0))

        val sway: Vector = left.clone().normalize() * local.x
        val heave: Vector = up.clone().normalize() * local.y
        val surge: Vector = reference.direction.clone() * local.z

        val loc = (Vector(reference.x, reference.y, reference.z) + sway + heave + surge).toLocation(reference.world)
        loc.yaw = reference.yaw

        return loc
    }

    @JvmStatic
    fun createLine(card: Card): String {
        val builder = StringBuilder()

        val nextExp = Card.toExperience(card.level + 1, card.rarity)
        val add = (nextExp - Card.toExperience(card.level, card.rarity)) / 20

        for (i in 1..20) {
            if (nextExp - card.remainingExperience >= add * (i + 1)) builder.append("=")
            else builder.append("-")
        }

        return builder.toString()
    }


}