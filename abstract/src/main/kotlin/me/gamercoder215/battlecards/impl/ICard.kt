package me.gamercoder215.battlecards.impl

import me.gamercoder215.battlecards.api.card.BattleCard
import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.api.card.Card
import me.gamercoder215.battlecards.impl.cards.IBattleCard
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.*
import java.util.*

@Suppress("unchecked_cast")
class ICard(
    override val entityCardClass: Class<out BattleCard<*>>,
    override val type: BattleCardType,
    val creation: Long,
    var last: Long? = null,
    override var lastUsedPlayer: OfflinePlayer? = null
) : Card, Serializable {

    val stats: MutableMap<String, Number> = mutableMapOf()

    override val creationDate: Date
        get() = Date(creation)

    override val statistics: IBattleStatistics
        get() = IBattleStatistics(this)

    override val lastUsed: Date
        get() = Date(last ?: 0)

    override fun spawnCard(owner: Player): IBattleCard<*> {
        val constr = entityCardClass.asSubclass(IBattleCard::class.java).getDeclaredConstructor(ICard::class.java)
        constr.isAccessible = true

        val card = constr.newInstance(this)
        card.spawn(owner, owner.location)

        return card
    }

    override fun serialize(): MutableMap<String, Any?> {
        return mutableMapOf(
            "clazz" to entityCardClass.name,
            "type" to type.name,
            "creation_date" to creation,
            "last_used" to last,
            "last_used_player" to lastUsedPlayer?.uniqueId.toString(),
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