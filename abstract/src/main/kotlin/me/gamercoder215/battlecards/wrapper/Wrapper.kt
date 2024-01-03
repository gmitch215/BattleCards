package me.gamercoder215.battlecards.wrapper

import me.gamercoder215.battlecards.api.BattleConfig
import me.gamercoder215.battlecards.api.card.item.CardEquipment
import me.gamercoder215.battlecards.api.card.item.CardEquipments
import me.gamercoder215.battlecards.impl.CardAttribute
import me.gamercoder215.battlecards.impl.cards.*
import me.gamercoder215.battlecards.util.BattleParticle
import me.gamercoder215.battlecards.util.CardAttackType
import me.gamercoder215.battlecards.wrapper.commands.CommandWrapper
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.*
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import java.security.SecureRandom

interface Wrapper {

    fun sendActionbar(player: Player, component: BaseComponent)

    fun sendActionbar(player: Player, message: String)

    fun setBossBarVisibility(boss: Wither, visible: Boolean)

    fun loadProperties(en: Creature, card: IBattleCard<*>)

    fun getNBTWrapper(item: ItemStack): NBTWrapper

    fun isCard(en: Creature): Boolean

    fun createInventory(id: String, name: String, size: Int): BattleInventory

    fun getCommandVersion(): Int = 2

    fun spawnParticle(particle: BattleParticle, location: Location, count: Int, dX: Double = 0.0, dY: Double = 0.0, dZ: Double = 0.0, speed: Double = 0.0, force: Boolean = false)

    fun <T : Creature> spawnMinion(clazz: Class<T>, ownerCard: IBattleCard<*>): T

    fun getDefaultAttribute(type: EntityType, attribute: CardAttribute): Double

    fun setAttackType(entity: Creature, attackType: CardAttackType)

    fun getAttackType(entity: Creature): CardAttackType

    fun getYBodyRot(entity: LivingEntity): Float

    fun addPacketInjector(p: Player)

    fun removePacketInjector(p: Player)

    fun addFollowGoal(entity: LivingEntity, ownerCard: IBattleCard<*>)

    companion object {
        val w = getWrapper()

        const val PACKET_INJECTOR_ID = "battlecards:packet_injector"

        val r = SecureRandom()

        val versions = listOf(
            "1_8_R1",
            "1_8_R2",
            "1_8_R3",
            "1_9_R1",
            "1_9_R2",
            "1_10_R1",
            "1_11_R1",
            "1_12_R1",
            "1_13_R1",
            "1_13_R2",
            "1_14_R1",
            "1_15_R1",
            "1_16_R1",
            "1_16_R2",
            "1_16_R3",
            "1_17_R1",
            "1_18_R1",
            "1_18_R2",
            "1_19_R1",
            "1_19_R2",
            "1_19_R3",
            "1_20_R1",
            "1_20_R2",
            "1_20_R3"
        )

        fun getWrapper(): Wrapper {
            val constr = Class.forName("${Wrapper::class.java.`package`.name}.v${getServerVersion()}.Wrapper${getServerVersion()}")
                .asSubclass(Wrapper::class.java)
                .getDeclaredConstructor()

            constr.isAccessible = true
            return constr.newInstance()
        }

        fun getCommandWrapper(): CommandWrapper {
            val cmdV: Int = when (BattleConfig.configuration.getString("Functionality.CommandVersion", "auto")) {
                "1" -> 1
                "2" -> 2
                "auto" -> w.getCommandVersion()
                else -> throw IllegalStateException("Invalid Command Version '${BattleConfig.configuration.getString("Functionality.CommandVersion")}'")
            }

            val constr = Class.forName("me.gamercoder215.battlecards.wrapper.commands.CommandWrapperV${cmdV}")
                .asSubclass(CommandWrapper::class.java)
                .getDeclaredConstructor(Plugin::class.java)

            constr.isAccessible = true
            return constr.newInstance(BattleConfig.plugin)
        }

        fun loadCards() {
            val current = getServerVersion()
            val cards: MutableList<Class<out IBattleCard<*>>> = mutableListOf(
                IBasicCard::class.java,
                IDiamondGolem::class.java,
                IWitherKing::class.java,
                IWitherman::class.java,
                ISniper::class.java,
                IRedstoneZombie::class.java,
                IUndeadLumberjack::class.java,
                IPitbull::class.java,
                ISkeletonSoldier::class.java,
                ISilverfishHive::class.java,
                ISpiderHive::class.java,
                ISpiderQueen::class.java,
                IStoneArcher::class.java,
                IThunderRevenant::class.java,
            )

            val equipment: MutableList<CardEquipment> = mutableListOf()
            equipment.addAll(CardEquipments.entries)

            versions.subList(0, versions.indexOf(current) + 1).forEach {
                try {
                    val constr = Class.forName("${IBattleCard::class.java.`package`.name}.CardLoader$it")
                        .asSubclass(CardLoader::class.java)
                        .getDeclaredConstructor()
                    constr.isAccessible = true

                    val loader = constr.newInstance()
                    cards.addAll(loader.loadedCards())
                    equipment.addAll(loader.loadedEquipment())

                    if (loader is Listener)
                        Bukkit.getPluginManager().registerEvents(loader, BattleConfig.plugin)
                } catch (ignored: ClassNotFoundException) {}
            }

            cards.forEach {
                try {
                    BattleConfig.config.registerCard(it)
                } catch (e: IllegalStateException) {
                    BattleConfig.logger.warning("Failed to register card '${it.name}'")
                    BattleConfig.print(e)
                }
            }
            equipment.forEach(BattleConfig.config::registerEquipment)
        }

        fun getServerVersion(): String {
            return Bukkit.getServer().javaClass.`package`.name.split(".")[3].substring(1)
        }

        fun get(key: String): String {
            return BattleConfig.config.get(key)
        }

        fun getMessage(key: String): String {
            return BattleConfig.config.getMessage(key)
        }

        val legacy: Boolean
            get() = w.getCommandVersion() == 1

    }

}