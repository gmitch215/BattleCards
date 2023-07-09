package me.gamercoder215.battlecards.util

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.impl.BlockAttachment
import me.gamercoder215.battlecards.impl.cards.IBattleCard
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.ArmorStand
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.util.ChatPaginator
import org.bukkit.util.Vector
import java.text.SimpleDateFormat
import java.util.*
import java.util.function.Supplier

object CardUtils {

    // Entity Utils

    @JvmStatic
    fun createAttachments(card: IBattleCard<*>) {
        val attachments = card.javaClass.getAnnotationsByType(BlockAttachment::class.java)
        val reference = card.location

        for (attachment in attachments) {
            val newLocation = reference.add(getLocal(reference, Vector(attachment.offsetX, attachment.offsetY, attachment.offsetZ)))
            val entity: ArmorStand = card.world.spawn(newLocation, ArmorStand::class.java)

            entity.isSmall = attachment.small
            entity.isVisible = false
            entity.setGravity(false)
            entity.setMetadata("battlecards:block_attachment", FixedMetadataValue(BattleConfig.plugin, true))

            entity.helmet = ItemStack(attachment.material)

            card.attachments[entity.uniqueId] = Supplier{
                val ref = card.location
                ref.add(getLocal(ref, Vector(attachment.offsetX, attachment.offsetY, attachment.offsetZ)))
            }
        }
    }

    // String Utils / Extensions

    @JvmStatic
    fun format(string: String, vararg args: Any): String {
        return String.format(BattleConfig.config.locale, string, *args)
    }

    @JvmStatic
    fun color(s: String): Array<String> {
        val array = s.trim().split("\\s".toRegex()).toTypedArray()

        val list: MutableList<String> = mutableListOf()

        for (i in array.indices) {
            var str = array[i].replace("&", "${ChatColor.COLOR_CHAR}")

            if (!str.startsWith(ChatColor.COLOR_CHAR)) {
                str = when {
                    str.toDoubleOrNull() != null -> "${ChatColor.RED}$str"
                    str.endsWith("s") -> "${ChatColor.GOLD}$str"
                    str.endsWith("x") -> "${ChatColor.BLUE}$str"
                    else -> "${ChatColor.GRAY}$str"
                }
            }

            list.add(str)
        }

        return ChatPaginator.wordWrap(list.joinToString(" "), 40)
    }

    @JvmStatic
    fun color(s: Collection<String>): List<String> {
        val list = mutableListOf<String>()
        for (i in s) list.addAll(color(i))

        return list
    }

    @JvmStatic
    fun dateFormat(date: Date?): String? {
        if (date == null) return null
        return SimpleDateFormat("MMM dd, yyyy", BattleConfig.config.locale).format(date)
    }

    // Other

    @JvmStatic
    fun getLocal(reference: Location, local: Vector): Vector {
        val base = Vector(0, 0, 1)
        val left: Vector = base.clone().rotateAroundY(Math.toRadians(-reference.yaw + 90.0))
        val up: Vector = reference.direction.clone().rotateAroundNonUnitAxis(left, Math.toRadians(-90.0))

        val sway: Vector = left.clone().normalize().multiply(local.x)
        val heave: Vector = up.clone().normalize().multiply(local.y)
        val surge: Vector = reference.direction.clone().multiply(local.z)

        return Vector(reference.x, reference.y, reference.z).add(sway).add(heave).add(surge)
    }

    @JvmStatic
    fun createLine(level: Int): String {
        val builder = StringBuilder()

        for (i in 1..20) {
            if (i * 5 <= level) builder.append("=")
            else builder.append("-")
        }

        return builder.toString()
    }


}