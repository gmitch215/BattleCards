package me.gamercoder215.battlecards.impl

import me.gamercoder215.battlecards.api.card.BattleCard
import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.api.card.BattleStatistics
import me.gamercoder215.battlecards.api.card.Card
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.*
import java.util.*

@Suppress("unchecked_cast")
class ICard(
    val clazz: Class<out BattleCard<*>>,
    val type: BattleCardType,
    val creation: Long,
    var last: Long? = null,
    var lastPlayer: OfflinePlayer? = null
) : Card, Serializable {

    val stats: MutableMap<String, Number> = mutableMapOf()

    override fun getCreationDate(): Date = Date(creation)

    override fun getStatistics(): BattleStatistics = IBattleStatistics(this)

    override fun getLastUsed(): Date = Date(last ?: 0)

    override fun getLastUsedPlayer(): OfflinePlayer? = lastPlayer

    override fun getType(): BattleCardType = type

    override fun getEntityCardClass(): Class<out BattleCard<*>> = clazz

    override fun serialize(): MutableMap<String, Any?> {
        return mutableMapOf(
            "clazz" to clazz.name,
            "type" to type.name,
            "creation_date" to creation,
            "last_used" to last,
            "last_used_player" to lastPlayer?.uniqueId.toString(),
            "stats" to stats
        )
    }

    override fun toByteArray(): ByteArray {
        val bOs = ByteArrayOutputStream()
        val os = BukkitObjectOutputStream(BufferedOutputStream(bOs))
        os.writeObject(this)
        os.close()

        return bOs.toByteArray()
    }

    companion object {

        @JvmStatic
        fun fromByteArray(array: ByteArray): ICard {
            val bIs = ByteArrayInputStream(array)
            val iS = BukkitObjectInputStream(BufferedInputStream(bIs))
            val card = iS.readObject() as ICard
            iS.close()

            return card
        }

        @JvmStatic
        fun deserialize(map: Map<String, Any>): ICard {
            val clazz = Class.forName(map["clazz"] as String).asSubclass(BattleCard::class.java)
            val type = BattleCardType.valueOf((map["type"] as String).uppercase())
            val creation = map["creation_date"] as Long
            val last = map["last_used"] as Long?
            val lastPlayerS = map["last_used_player"] as String
            val lastPlayer: OfflinePlayer? = lastPlayerS.let { if (it == "null") null else Bukkit.getOfflinePlayer(UUID.fromString(it)) }

            val card = ICard(clazz, type, creation, last, lastPlayer)
            card.stats.putAll(map["stats"] as MutableMap<String, Number>)

            return card
        }

    }

}