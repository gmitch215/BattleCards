package me.gamercoder215.battlecards.util

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.Card
import me.gamercoder215.battlecards.api.card.Rarity
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
import kotlin.math.abs
import kotlin.math.pow

object CardUtils {

    @JvmStatic
    val BLOCK_DATA: MutableMap<Location, BattleBlockData> = mutableMapOf()

    // Entity Utils

    @JvmStatic
    fun createAttachments(card: IBattleCard<*>) {
        val attachments = card.javaClass.getAnnotationsByType(BlockAttachment::class.java)
        if (attachments.isEmpty()) return

        for (attachment in attachments) {
            val newLocation =
                if (attachment.local)
                    local(card.entity.location, Vector(attachment.offsetX, attachment.offsetY, attachment.offsetZ))
                else
                    card.entity.location.apply {
                        x += attachment.offsetX
                        y += attachment.offsetY
                        z += attachment.offsetZ
                    }

            newLocation.yaw = w.getYBodyRot(card.entity) + attachment.offsetYaw
            newLocation.pitch = 0F

            val entity = card.entity.world.spawn(newLocation, ArmorStand::class.java).apply {
                isSmall = attachment.small
                isVisible = false
                setGravity(false)
                setMetadata("battlecards:block_attachment", FixedMetadataValue(BattleConfig.plugin, true))
                helmet = ItemStack(attachment.material)
            }

            card.attachmentMods.forEach { if (it.first(entity)) it.second(entity) }

            card.attachments[entity.uniqueId] = {
                if (attachment.local)
                    local(card.entity.location, Vector(attachment.offsetX, attachment.offsetY, attachment.offsetZ)).apply {
                        yaw = w.getYBodyRot(card.entity) + attachment.offsetYaw
                        pitch = 0F
                    }
                else
                    card.entity.location.apply {
                        yaw = w.getYBodyRot(card.entity) + attachment.offsetYaw
                        pitch = 0F
                        x += attachment.offsetX
                        y += attachment.offsetY
                        z += attachment.offsetZ
                    }
            }
        }
    }

    @JvmStatic
    fun createMinionAttachments(minion: LivingEntity, card: IBattleCard<*>) {
        val attachments = card.javaClass.getAnnotationsByType(MinionBlockAttachment::class.java).filter { it.type == minion.type }
        if (attachments.isEmpty()) return

        val map = mutableMapOf<UUID, () -> Location>()

        for (attachment in attachments) {
            val newLocation =
                if (attachment.local)
                    local(minion.location, Vector(attachment.offsetX, attachment.offsetY, attachment.offsetZ))
                else
                    minion.location.apply {
                        x += attachment.offsetX
                        y += attachment.offsetY
                        z += attachment.offsetZ
                    }

            newLocation.yaw = w.getYBodyRot(minion) + attachment.offsetYaw
            newLocation.pitch = 0F

            val entity = minion.world.spawn(newLocation, ArmorStand::class.java).apply {
                isSmall = attachment.small
                isVisible = false
                setGravity(false)
                setMetadata("battlecards:block_attachment", FixedMetadataValue(BattleConfig.plugin, true))
                helmet = ItemStack(attachment.material)
            }

            map[entity.uniqueId] = {
                if (attachment.local)
                    local(minion.location, Vector(attachment.offsetX, attachment.offsetY, attachment.offsetZ)).apply {
                        yaw = w.getYBodyRot(minion) + attachment.offsetYaw
                        pitch = 0F
                    }
                else
                    minion.location.apply {
                        yaw = w.getYBodyRot(minion) + attachment.offsetYaw
                        pitch = 0F
                        x += attachment.offsetX
                        y += attachment.offsetY
                        z += attachment.offsetZ
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
                val strC = str.replace("[.,!+]".toRegex(), "")
                str = when {
                    strC.contains("-") && strC.split("-").size == 2 -> {
                        val split = strC.split("-").toTypedArray()
                        "${color(split[0])}-${color(split[1])}"
                    }
                    strC.endsWith("%") -> "${ChatColor.DARK_AQUA}$str"
                    strC.endsWith("s") && str.substringBeforeLast("s").toDoubleOrNull() != null -> "${ChatColor.GOLD}$str"
                    strC.endsWith("x") && str.substringBeforeLast("x").toDoubleOrNull() != null -> "${ChatColor.RED}$str"
                    strC.toDoubleOrNull() != null -> "${ChatColor.BLUE}$str"
                    else -> "${ChatColor.GRAY}$str"
                }
            }

            list.add(str)
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

        val min = Card.toExperience(card.level, card.rarity)
        val next = Card.toExperience(card.level + 1, card.rarity)

        val add = (next - min) / 20.0

        for (i in 1..20) {
            if ((next - min) - card.remainingExperience >= add * (i + 1)) builder.append("=")
            else builder.append("-")
        }

        return builder.toString()
    }

    val Card.power: Long
        get() = (level.toDouble().pow(rarity.experienceModifier) * rarity.ordinal.plus(1)).toLong()

    @JvmStatic
    fun getCardPower(cards: Iterable<ItemStack>)
        = cards.map { it to it.card!! }.sumOf { it.second.power * it.first.amount }

    @JvmStatic
    private val intervalCardChances = listOf(
        250,
        1500,
        13500,
        31525
    )

    @JvmStatic
    fun calculateCardChances(cards: Iterable<ItemStack>): Map<Rarity, Double> {
        val power = getCardPower(cards)
        if (power < 50) return emptyMap()

        val map = mutableMapOf<Rarity, Double>()
        val p = power - 50

        for ((i, interval) in intervalCardChances.withIndex())
            if (p <= interval) {
                map[Rarity.entries[i + 1]] = 1.0 - (p / interval.toDouble())
                map[Rarity.entries[i + 2]] = 1.0 - (abs(p - interval.div(2.0)) / interval.div(2.0))
                map[Rarity.entries[i + 3]] = p / interval.toDouble()
                break
            }

        val sum = map.values.sum()

        for (rarity in Rarity.entries) {
            if (rarity == Rarity.BASIC) continue

            map.putIfAbsent(rarity, 0.0)
            map[rarity] = (map[rarity]!! / sum).coerceAtLeast(0.0)
        }

        return map
    }


}