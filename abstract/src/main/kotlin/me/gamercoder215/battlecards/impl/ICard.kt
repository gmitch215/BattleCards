package me.gamercoder215.battlecards.impl

import com.google.common.collect.ImmutableSet
import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.BattleCard
import me.gamercoder215.battlecards.api.card.BattleCardType
import me.gamercoder215.battlecards.api.card.Card
import me.gamercoder215.battlecards.api.card.Rarity
import me.gamercoder215.battlecards.api.card.item.CardEquipment
import me.gamercoder215.battlecards.api.events.entity.CardSpawnEvent
import me.gamercoder215.battlecards.impl.cards.IBattleCard
import me.gamercoder215.battlecards.util.call
import me.gamercoder215.battlecards.util.formatName
import me.gamercoder215.battlecards.util.inventory.CardGenerator
import me.gamercoder215.battlecards.util.spawnedCards
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
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

    var storedEntityType: EntityType? = null

    val itemStack: ItemStack
        get() = CardGenerator.toItem(this)

    val stats: MutableMap<String, Number> = mutableMapOf()

    val cardEquipment: MutableMap<Int, CardEquipment> = mutableMapOf()

    override val equipment: Set<CardEquipment>
        get() = ImmutableSet.copyOf(cardEquipment.values)

    override val creationDate: Date
        get() = Date(creation)

    override val statistics: IBattleStatistics
        get() = IBattleStatistics(this)

    override val lastUsed: Date
        get() = Date(last ?: 0)

    override fun spawnCard(owner: Player): IBattleCard<*> = spawnCard(owner, itemStack)

    fun spawnCard(owner: Player, itemUsed: ItemStack): IBattleCard<*> {
        if (owner.spawnedCards.size >= BattleConfig.config.maxCardsSpawned) throw IllegalStateException("Player already has ${BattleConfig.config.maxCardsSpawned} spawned cards")

        val constr = entityCardClass.asSubclass(IBattleCard::class.java).getDeclaredConstructor(ICard::class.java)
        constr.isAccessible = true

        val card = constr.newInstance(this)
        card.spawn(owner, itemUsed, owner.location)

        CardSpawnEvent(card).apply { call() }
        
        object : BukkitRunnable() {
            override fun run() {
                if (!card.entity.isDead)
                    card.despawn()
            }
        }.runTaskLater(BattleConfig.plugin, card.deployTime * 20L)

        return card
    }

    override fun serialize(): MutableMap<String, Any?> {
        return mutableMapOf(
            "clazz" to entityCardClass.name,
            "type" to type.name,
            "creation_date" to creation,
            "last_used" to last,
            "last_used_player" to lastUsedPlayer?.uniqueId.toString(),
            "stats" to stats,
            "stored_entity_type" to storedEntityType?.name,
            "equipment" to cardEquipment.map { it.key to it.value.name }.toMap()
        )
    }

    override fun toByteArray(): ByteArray {
        val bOs = ByteArrayOutputStream()
        val os = BukkitObjectOutputStream(BufferedOutputStream(bOs))
        os.writeObject(this)
        os.close()

        return bOs.toByteArray()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ICard) return false

        return other.toByteArray().contentEquals(toByteArray())
    }

    override val entityCardType: EntityType?
        get() = storedEntityType ?: super.entityCardType

    override val entityClass: Class<out LivingEntity>?
        get() {
            return try {
                storedEntityType?.entityClass?.asSubclass(LivingEntity::class.java)
            } catch (ignored: ClassCastException) {
                null
            } ?: super.entityClass
        }

    override fun hashCode(): Int = toByteArray().hashCode()

    override val name: String
        get() {
            if (rarity == Rarity.BASIC) return entityCardType!!.formatName()
            return super.name
        }

    companion object {

        private const val serialVersionUID: Long = 193409138419023815L

        @JvmStatic
        fun fromByteArray(array: ByteArray): ICard? {
            if (array.isEmpty()) return null

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
            val storedEntityTypeS = map["stored_entity_type"] as String?
            val equipment = map["equipment"] as Map<Int, String>

            val card = ICard(clazz, type, creation, last, lastPlayer)
            card.stats.putAll(map["stats"] as MutableMap<String, Number>)
            card.storedEntityType = storedEntityTypeS?.let {
                try {
                    EntityType.valueOf(it)
                } catch (ignored: IllegalArgumentException) {
                    null
                }
            }
            card.cardEquipment.putAll(equipment.map { entry -> entry.key to BattleConfig.config.registeredEquipment.first { it.name == entry.value } }.toMap())

            return card
        }

    }

}